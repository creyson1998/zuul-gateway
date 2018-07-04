package com.qclass.zuulgateway.filter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by fanwenwen on 2018/6/5.
 */
@Component
@ConfigurationProperties(prefix = "filter")
public class FilterConfig {

    private String ignores;

    public String getIgnores() {
        return ignores;
    }

    public void setIgnores(String ignores) {
        this.ignores = ignores;

    }


}

