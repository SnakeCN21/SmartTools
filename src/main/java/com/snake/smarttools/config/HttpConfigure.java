package com.snake.smarttools.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;

@Configuration
@Slf4j
public class HttpConfigure {
    private static final Integer connectTimeoutInMillis = 60 * 1000; //最初是15秒
    private static final Integer readTimeoutInMillis = 60 * 1000;
    private static final Integer connectPoolSize = 120;

    private HttpClientConnectionManager poolingConnectionManager() {
        PoolingHttpClientConnectionManager poolingConnectionManager = new PoolingHttpClientConnectionManager();
        poolingConnectionManager.setMaxTotal(connectPoolSize);
        poolingConnectionManager.setDefaultMaxPerRoute(connectPoolSize);
        return poolingConnectionManager;
    }

    @Bean
    public ClientHttpRequestFactory httpComponentsClientHttpRequestFactory() {
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(poolingConnectionManager()).build();
        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory(HttpClients.createDefault());
        httpRequestFactory.setConnectionRequestTimeout(connectTimeoutInMillis);
        httpRequestFactory.setConnectTimeout(connectTimeoutInMillis);
        httpRequestFactory.setReadTimeout(readTimeoutInMillis);
        httpRequestFactory.setHttpClient(httpClient);
        return httpRequestFactory;
    }

    @Bean("restTemplate")
    public RestTemplate drtRestTemplate(@Autowired ClientHttpRequestFactory clientHttpRequestFactory) {
        RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory);
        restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        return restTemplate;
    }
}
