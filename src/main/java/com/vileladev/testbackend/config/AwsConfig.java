package com.vileladev.testbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.providers.AwsRegionProvider;
import software.amazon.awssdk.regions.providers.AwsProfileRegionProvider;


@Configuration
public class AwsConfig {
    @Bean
    public AwsRegionProvider awsRegionProvider() {
        return new AwsProfileRegionProvider();
    }
}