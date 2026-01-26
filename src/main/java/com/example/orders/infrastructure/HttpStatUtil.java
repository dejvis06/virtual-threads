package com.example.orders.infrastructure;

import org.jspecify.annotations.NonNull;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public final class HttpStatUtil implements ApplicationContextAware {

    private static ApplicationContext ctx;

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) {
        HttpStatUtil.ctx = applicationContext;
    }

    public static String okWithServerDelay(long seconds) {
        RestTemplate restTemplate = ctx.getBean(RestTemplate.class);

        return restTemplate.getForObject(
                "https://postman-echo.com/delay/" + seconds,
                String.class
        );
    }
}
