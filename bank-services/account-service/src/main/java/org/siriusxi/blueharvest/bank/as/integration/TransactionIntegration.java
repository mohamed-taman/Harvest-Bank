package org.siriusxi.blueharvest.bank.as.integration;

import lombok.extern.log4j.Log4j2;
import org.siriusxi.blueharvest.bank.common.api.composite.trx.Transaction;
import org.siriusxi.blueharvest.bank.common.api.dto.TransactionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.springframework.http.HttpMethod.GET;

@Component
@Log4j2
public class TransactionIntegration {

    private final RestTemplate restTemplate;

    private final String transactionServiceUrl;

    private final String BASE_URL = "/bank/api/v1/";

    private final String QUERY_PARAM = "?accountId=";

    @Autowired
    public TransactionIntegration(
            RestTemplate restTemplate,
            @Value("${app.transaction-service.host}") String transactionServiceHost,
            @Value("${app.transaction-service.port}") int    transactionServicePort) {

        this.restTemplate = restTemplate;

        transactionServiceUrl = "http://"
                .concat(transactionServiceHost)
                .concat(":")
                .concat(String.valueOf(transactionServicePort))
                .concat(BASE_URL)
                .concat("transactions");
    }

    public List<Transaction> getAccountTransactions(int accountId){

        String url = transactionServiceUrl.concat(QUERY_PARAM).concat(String.valueOf(accountId));
        log.debug("Will call getCustomerAccounts API on URL: {}", url);

        return restTemplate.exchange(url, GET, null,
                new ParameterizedTypeReference<List<Transaction>>() {}).getBody();
    }

    public void createTransaction(TransactionDTO transaction) {
        restTemplate.postForObject(transactionServiceUrl,transaction, TransactionDTO.class);
    }


}
