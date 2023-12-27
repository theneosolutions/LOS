package com.seulah.los.config;

import feign.Contract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Muhammad Mansoor
 */
@Configuration
public class FeignBean {
    @Bean
    public Contract feignContract() {
        return new Contract.Default();

    }
}
