package com.quatex.evaproxy;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

@EnableScheduling
@org.springframework.context.annotation.Configuration
public class Configuration {

    @Bean
    public RestTemplate restTemplate() throws NoSuchAlgorithmException, KeyManagementException {
        RestTemplateBuilder builder = new RestTemplateBuilder();
return builder.build();
//        TrustManager[] trustAllCerts = new TrustManager[] {
//
//        };
//        SSLContext ssl = SSLContext.getInstance("SSL");
//        ssl.init(null, trustAllCerts, new SecureRandom());
//
//        CloseableHttpClient httpClient = HttpClients.custom()
//                .setSSLContext(ssl).setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();
//        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
//        factory.setHttpClient(httpClient);
//        return builder.requestFactory(() -> factory).build();
    }
}
