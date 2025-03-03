package com.example.apigatewayservice.filter;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
//import org.springframework.http.server.ServerHttpRequest; 이거 아니고 아래 것으로 해야 함
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
//@Slf4j
public class CustomFilter extends AbstractGatewayFilterFactory<CustomFilter.Config> {

    private static final Logger log = LoggerFactory.getLogger(CustomFilter.class);

    public CustomFilter () {
        super(Config.class);
    }

    // 마우스 우클릭 generate implement methods에서 추가할 수 있음
    @Override
    public GatewayFilter apply(Config config) {
        // Custom Pre Filter
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            log.info("Custom Pre Filter: request id = {}", request.getId());

            //Custom Post Filter
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                log.info("Custom Post filter: response code -> {}", response.getStatusCode());
            }));
        };
    }

    public static class Config {
        //Put the configuration properties
    }
}
