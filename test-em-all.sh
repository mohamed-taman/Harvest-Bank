#!/usr/bin/env bash
## Author: Mohamed Taman
## version: v0.7
### Sample usage:
#
#   for local run
#     HOST=localhost PORT=8090 ./test-em-all.bash start stop
#
echo -e "\nStarting 'Harvest Bank μServices' for [end-2-end] testing....\n"

: "${HOST=localhost}"
: "${PORT=8090}"
: "${CUST_ID_OK=1}"
: "${CUST_ID_NOT_FOUND=14}"
: "${CONTENT_TYPE=\"Content-Type: application/json\"}"

BASE_URL="/bank/api/v1/customers"

function assertCurl() {

  local expectedHttpCode=$1
  local curlCmd="$2 -w \"%{http_code}\""
  local result=$(eval "${curlCmd}")
  local httpCode="${result:(-3)}"
  RESPONSE='' && (( ${#result} > 3 )) && RESPONSE="${result%???}"

  if [[ "$httpCode" == "$expectedHttpCode" ]]
  then
    if [[ "$httpCode" == "200" ]]
    then
      echo "Test OK (HTTP Code: $httpCode)"
    else
      echo "Test OK (HTTP Code: $httpCode, $RESPONSE)"
    fi
    return 0
  else
      echo  "Test FAILED, EXPECTED HTTP Code: $expectedHttpCode, GOT: $httpCode, WILL ABORT!"
      echo  "- Failing command: $curlCmd"
      echo  "- Response Body: $RESPONSE"
      return 1
  fi
}

function assertEqual() {

  local expected=$1
  local actual=$2

  if [[ "$actual" == "$expected" ]]
  then
    echo "Test OK (actual value: $actual)"
    return 0
  else
    echo "Test FAILED, EXPECTED VALUE: $expected, ACTUAL VALUE: $actual, WILL ABORT"
    return 1
  fi
}

function testUrl() {
    url=$@
    if curl ${url} -ks -f -o /dev/null
    then
          return 0
    else
          return 1
    fi;
}

function waitForService() {
    url=$@
    echo -n "Wait for: $url ... "
    n=0
    until testUrl ${url}
    do
        n=$((n + 1))
        if [[ ${n} == 100 ]]
        then
            echo " Give up"
            exit 1
        else
            sleep 3
            echo -n ", retry #$n "
        fi
    done
    echo -e "\n DONE, continues...\n"
}

set -e

echo "Start Tests:" "$(date)"

echo "HOST=${HOST}"
echo "PORT=${PORT}"

if [[ $@ == *"start"* ]]
then
    echo "Firing up the test environment..."
    bash run-em-all.sh
fi

waitForService curl http://${HOST}:${PORT}/actuator/health

# Verify that a normal request works, expect seven customers
assertCurl 200 "curl http://$HOST:$PORT${BASE_URL} -s"
assertEqual 7 "$(echo "${RESPONSE}" | jq ". | length")"
assertEqual 0 "$(echo "${RESPONSE}" | jq ".[0].accounts | length")"
assertEqual 0 "$(echo "${RESPONSE}" | jq ".[0].accounts[0].transactions | length")"

# Verify that a 404 (Not Found) error is returned for a non existing customer (14)
assertCurl 404 "curl -H ${CONTENT_TYPE} -d '{\"initialCredit\": 10.0}' \
http://$HOST:$PORT${BASE_URL}/$CUST_ID_NOT_FOUND/accounts -s"

# Verify that a 422 (Unprocessable Entity) error is returned for minus Initial Credit
assertCurl 422 "curl -H ${CONTENT_TYPE} -d '{\"initialCredit\": -10.0}' \
http://$HOST:$PORT${BASE_URL}/$CUST_ID_NOT_FOUND/accounts -s"

# Verify that a 400 (Bad Request) error error is returned for a customerId that is not a number, i
# .e. invalid format
assertCurl 400 "curl -H ${CONTENT_TYPE} -d '{\"initialCredit\": -10.0}' \
http://$HOST:$PORT${BASE_URL}/invalidCustomerId/accounts -s"
assertEqual "\"Bad Request\"" "$(echo "${RESPONSE}" | jq .error)"

# Verify that no transactions only created customer Account for initial credit 0.0
assertCurl 200 "curl -H ${CONTENT_TYPE} -d '{\"initialCredit\": 0.0}' \
http://$HOST:$PORT${BASE_URL}/$CUST_ID_OK/accounts -s"
assertCurl 200 "curl http://$HOST:$PORT${BASE_URL} -s"
assertEqual 1 "$(echo "${RESPONSE}" | jq ".[0].accounts | length")"
assertEqual 0 "$(echo "${RESPONSE}" | jq ".[0].accounts[0].balance")"
assertEqual 0 "$(echo "${RESPONSE}" | jq ".[0].accounts[0].transactions | length")"

# Verify that transaction is created for customer account for initial credit 100.20
assertCurl 200 "curl -H ${CONTENT_TYPE} -d '{\"initialCredit\": 100.20}' \
http://$HOST:$PORT${BASE_URL}/$CUST_ID_OK/accounts -s"
assertCurl 200 "curl http://$HOST:$PORT${BASE_URL} -s"
assertEqual 100.2 "$(echo "${RESPONSE}" | jq ".[0].balance")"
assertEqual 2 "$(echo "${RESPONSE}" | jq ".[0].accounts | length")"
assertEqual 100.2 "$(echo "${RESPONSE}" | jq ".[0].accounts[1].balance")"
assertEqual 1 "$(echo "${RESPONSE}" | jq ".[0].accounts[1].transactions | length")"
assertEqual 100.2 "$(echo "${RESPONSE}" | jq ".[0].accounts[1].transactions[0].amount")"

# Verify customer balance is updated to 200.0
assertCurl 200 "curl -H ${CONTENT_TYPE} -d '{\"initialCredit\": 99.80}' \
http://$HOST:$PORT${BASE_URL}/$CUST_ID_OK/accounts -s"
assertCurl 200 "curl http://$HOST:$PORT${BASE_URL} -s"
assertEqual 200 "$(echo "${RESPONSE}" | jq ".[0].balance")"
assertEqual 3 "$(echo "${RESPONSE}" | jq ".[0].accounts | length")"
assertEqual 99.8 "$(echo "${RESPONSE}" | jq ".[0].accounts[2].balance")"
assertEqual 1 "$(echo "${RESPONSE}" | jq ".[0].accounts[2].transactions | length")"
assertEqual 99.8 "$(echo "${RESPONSE}" | jq ".[0].accounts[2].transactions[0].amount")"

echo -e "\nEnd, all tests OK: $(date) \n"

if [[ $@ == *"stop"* ]]
then
    echo "We are done, stopping the test environment..."
    bash stop-em-all.sh
fi