package org.siriusxi.blueharvest.bank.as.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ServiceConfiguration {

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
