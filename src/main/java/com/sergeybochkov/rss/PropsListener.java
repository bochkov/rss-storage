package com.sergeybochkov.rss;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

@Slf4j
public final class PropsListener implements ApplicationListener<ContextRefreshedEvent> {

    @Value("${rss.proxy-host}")
    private String proxyHost;

    @Value("${rss.proxy-port}")
    private String proxyPort;

    @Value("${rss.user-agent}")
    private String userAgent;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("started with proxyHost={}, proxyPort={} and userAgent={}", proxyHost, proxyPort, userAgent);
    }
}
