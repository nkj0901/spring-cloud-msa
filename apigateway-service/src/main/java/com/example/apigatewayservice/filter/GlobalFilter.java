package com.example.apigatewayservice.filter;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class GlobalFilter extends AbstractGatewayFilterFactory<GlobalFilter.Config> {

//    private static final Logger log = LoggerFactory.getLogger(GlobalFilter.class);

    public GlobalFilter() {
        super(Config.class);
    }

    // 마우스 우클릭 generate implement methods에서 추가할 수 있음
    @Override
    public GatewayFilter apply(Config config) {
        // Custom Pre Filter
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            log.info("Global Filter baseMessage: {}", config.getBaseMessage());

            //boolean type은 getter가 is로 시작함
            if(config.isPreLogger()) {
                log.info("Global Filter start: request id -> {}", request.getId());
            }

            //Custom Post Filter
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                if(config.isPostLogger()) {
                    if(config.isPostLogger()) {
                        log.info("Global Filter start: request id -> {}", response.getStatusCode());
                    }
                }
            }));
        };
    }

    @Data
    public static class Config {
        //Put the configuration properties
        private String baseMessage;
        private boolean preLogger;
        private boolean postLogger;
    }
}
