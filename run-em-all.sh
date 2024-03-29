#!/usr/bin/env bash
## Author: Mohamed Taman
## version: v0.7

echo -e "\nStarting [Harvest Bank] μServices ....\n\
---------------------------------------\n"

function runService(){
  java --enable-preview -jar "$1"/target/*.jar > /dev/null
}

for dir in $(find  bank-services/*-service -maxdepth 0 -type d)
do
    echo -e "Starting [${dir:14}] μService.... Done \n" && \
    runService "$dir" &
done
