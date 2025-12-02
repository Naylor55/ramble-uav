package com.ramble.uavjingan.support;

import com.alibaba.fastjson2.JSONObject;
import com.ramble.uavjingan.properties.JinganProperties;
import com.ramble.uavjingan.util.GenerateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Project     ngh-aircraft
 * Package     com.nghsmart.nghaircraft.adapter.qingpu.support
 * Class       AuthenticationSupport
 * date        2025/9/11 10:44
 * author      cml
 * Email       liangchen_beijing@163.com
 * Description 
 */


@Slf4j
@RequiredArgsConstructor
public class AuthenticationSupport {

    private final JinganProperties jinganProperties;


    public <T> HttpEntity<T> buildHttpEntity(T body, HttpHeaders headers) {
        HttpHeaders h = buildHeader();
        if (headers != null) {
            h.addAll(headers);
        }
        return new HttpEntity<>(body, h);
    }

    public HttpHeaders buildHeader() {
        return new HttpHeaders() {{
//            put("token", getToken());
            put("content-type", List.of("application/json;charset=UTF-8"));
        }};
    }

    public List<String> getToken() {
        return List.of("");
    }

    /**
     * 构造签名，并追加到url中
     * @param url
     * @param body
     * @return
     */
    public String buildSignature(String url, JSONObject body) {
        log.debug("ak={},body={}", jinganProperties.getAccessKeyId(), body.toJSONString());
        String signature = GenerateUtils.generate(jinganProperties.getAccessKeyId(), jinganProperties.getSecretKey(), body);
        log.debug("signature={}", signature);
        String realUrl = String.format("%s?AccessKeyId=%s&Signature=%s", url, jinganProperties.getAccessKeyId(), signature);
        log.debug("realUrl={}", realUrl);
        return realUrl;
    }

}
