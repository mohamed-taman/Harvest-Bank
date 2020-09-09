package org.siriusxi.blueharvest.bank.cs.integration;

import lombok.extern.log4j.Log4j2;
import org.siriusxi.blueharvest.bank.common.api.composite.account.Account;
import org.siriusxi.blueharvest.bank.common.api.dto.AccountDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.springframework.http.HttpMethod.GET;

@Component
@Log4j2
public class AccountIntegration {

    private final RestTemplate restTemplate;

    private final String accountServiceUrl;

    private static final  String BASE_URL = "/bank/api/v1/";

    private static final  String QUERY_PARAM = "?customerId=";

    @Autowired
    public AccountIntegration(
            RestTemplate restTemplate,
            @Value("${app.account-service.host}") String accountServiceHost,
            @Value("${app.account-service.port}") int    accountServicePort) {

        this.restTemplate = restTemplate;

        accountServiceUrl = "http://"
                .concat(accountServiceHost)
                .concat(":")
                .concat(String.valueOf(accountServicePort))
                .concat(BASE_URL)
                .concat("accounts");
    }

    public List<Account> getCustomerAccounts(int customerId){

        String url = accountServiceUrl.concat(QUERY_PARAM).concat(String.valueOf(customerId));
        log.debug("Will call getCustomerAccounts API on URL: {}", url);

        return restTemplate.exchange(url, GET, null,
                new ParameterizedTypeReference<List<Account>>() {}).getBody();
    }

    public void createAccount(AccountDTO account) {
        restTemplate.postForObject(accountServiceUrl,account, AccountDTO.class);
    }
}
