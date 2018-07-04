package com.qclass.zuulgateway;

import com.qclass.zuulgateway.filter.AccessFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * Created by fanwenwen on 2018/6/21.
 */
@SpringBootApplication
@EnableZuulProxy
@EnableFeignClients(basePackages={"com.qclass"})
public class ZuulApplication {

    @Autowired
    private  AccessFilter accessFilter;

    public static void main(String[] args) {

        SpringApplication.run(ZuulApplication.class, args);
    }

}
