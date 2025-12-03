package com.ramble.uavjingan.config;

import com.ramble.uavjingan.properties.JinganProperties;
import com.ramble.uavjingan.service.UavService;
import com.ramble.uavjingan.support.AuthenticationSupport;
import com.ramble.uavjingan.support.DeviceSupport;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Project     ramble-uav
 * Package     com.ramble.uavjingan.config
 * Class       JinganAutoConfiguration
 * date        2025/12/2 10:19
 * author      cml
 * Email       liangchen_beijing@163.com
 * Description 
 */

@Configuration
@ConditionalOnProperty(name = "uav.jingan.enable", havingValue = "true")
@EnableConfigurationProperties(JinganProperties.class)
public class JinganAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public RestTemplate restTemplate(ClientHttpRequestFactory factory) {
        return new RestTemplate(factory);
    }

    @Bean
    @ConditionalOnMissingBean
    public ClientHttpRequestFactory simpleClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(150000); // ms
        factory.setConnectTimeout(150000); // ms
        return factory;
    }

    @Bean
    public AuthenticationSupport authenticationSupport(JinganProperties properties) {
        return new AuthenticationSupport(properties);
    }

    @Bean
    public DeviceSupport deviceSupport(RestTemplate restTemplate,
                                       JinganProperties properties,
                                       AuthenticationSupport authenticationSupport) {
        return new DeviceSupport(restTemplate, properties, authenticationSupport);
    }

    @Bean
    @ConditionalOnProperty(name = "uav.jingan.enable", havingValue = "true")
    public UavService uavService(DeviceSupport deviceSupport,
                                 ApplicationEventPublisher publisher) {
        return new UavService(deviceSupport, publisher);
    }
}
