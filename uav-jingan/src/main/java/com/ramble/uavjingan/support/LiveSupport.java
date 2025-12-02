package com.ramble.uavjingan.support;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ramble.uavjingan.constant.ApiEnum;
import com.ramble.uavjingan.dto.LiveInfoDto;
import com.ramble.uavjingan.dto.ResponseCommonDto;
import com.ramble.uavjingan.properties.JinganProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Project     ngh-aircraft
 * Package     com.nghsmart.nghaircraft.adapter.qingpu.support
 * Class       LiveSupport
 * date        2025/9/9 16:44
 * author      cml
 * Email       liangchen_beijing@163.com
 * Description 直播支持类
 */

@Slf4j
@RequiredArgsConstructor
public class LiveSupport {

    private final JinganProperties jinganProperties;

    private final RestTemplate restTemplate;

    private final AuthenticationSupport authenticationSupport;


    /**
     * 获取直播地址 - 实况拉流
     * @param productKey
     * @param deviceId
     * @param videoId
     * @return
     */
    public LiveInfoDto getLiveUrl(String productKey, String deviceId, String videoId) {
        JSONObject json = new JSONObject();
        json.put("action", "START");
        json.put("protocol", "FLV");
        json.put("videoId", videoId);
        json.put("ssl", false);
        String url = authenticationSupport.buildSignature(jinganProperties.getPrefix() + String.format(ApiEnum.LIVE_URL.getUrl(), productKey, deviceId), json);
        log.debug("getLiveUrl_url={},param={}", url, json.toJSONString());
        ResponseEntity<ResponseCommonDto<LiveInfoDto>> exchange = restTemplate.exchange(url, ApiEnum.LIVE_URL.getMethod(),
                authenticationSupport.buildHttpEntity(json, null), new ParameterizedTypeReference<ResponseCommonDto<LiveInfoDto>>() {
                });
        log.info("getLiveUrl_response={}", JSON.toJSONString(exchange));
        if (exchange.getStatusCode().is2xxSuccessful() && exchange.getBody() != null) {
            return exchange.getBody().getData();
        }
        return new LiveInfoDto();
    }

}
