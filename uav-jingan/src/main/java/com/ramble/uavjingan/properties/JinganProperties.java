package com.ramble.uavjingan.properties;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;



@Slf4j
@Data
@ConfigurationProperties(prefix = "uav.jingan")
public class JinganProperties {


    private String prefix = "http://test.4a.jing-an.com:32711";

    private Integer deviceListPageSize = Integer.MAX_VALUE;

    private String accessKeyId = "acuvkfacfcswxblm";

    private String secretKey = "EU2ibcJXpfjoviY7sV9T5A==";
}
