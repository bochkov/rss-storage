package com.sergeybochkov.rss;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "rss")
public class RssProps {

    private String userAgent;
    private String proxyHost;
    private Integer proxyPort;

}
