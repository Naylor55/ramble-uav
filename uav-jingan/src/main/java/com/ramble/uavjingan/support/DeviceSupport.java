package com.ramble.uavjingan.support;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ramble.uavjingan.constant.ApiEnum;
import com.ramble.uavjingan.dto.DeviceDataDto;
import com.ramble.uavjingan.dto.DevicePropertiesDto;
import com.ramble.uavjingan.dto.ResponseCommonDto;
import com.ramble.uavjingan.properties.JinganProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Project     ngh-aircraft
 * Package     com.nghsmart.nghaircraft.adapter.qingpu.support
 * Class       DeviceSupport
 * date        2025/9/10 15:55
 * author      cml
 * Email       liangchen_beijing@163.com
 * Description  设备支持组件，用于适配对方设备相关接口
 */

@Slf4j
@RequiredArgsConstructor
public class DeviceSupport {

    private final RestTemplate restTemplate;

    private final JinganProperties jinganProperties;

    private final AuthenticationSupport authenticationSupport;

    /**
     * 设备列表
     * @return
     */
    public DeviceDataDto findDeviceData() {
        JSONObject param = new JSONObject();
        param.put("page", 1);
        param.put("size", jinganProperties.getDeviceListPageSize());
        String url = authenticationSupport.buildSignature(jinganProperties.getPrefix() + ApiEnum.DEVICE_LIST.getUrl(), param);
        ResponseEntity<ResponseCommonDto<DeviceDataDto>> exchange = restTemplate.exchange(url, ApiEnum.DEVICE_LIST.getMethod(),
                authenticationSupport.buildHttpEntity(param, null),
                new ParameterizedTypeReference<ResponseCommonDto<DeviceDataDto>>() {
                });
        log.debug("findDeviceList_response={}", JSON.toJSONString(exchange));
        if (exchange.getStatusCode().is2xxSuccessful() && exchange.getBody() != null) {
            return exchange.getBody().getData();
        }
        return null;
    }

    /**
     * 设备属性列表
     * @return
     */
    public List<DevicePropertiesDto> findDeviceList() {
        return Optional.ofNullable(findDeviceData()).map(DeviceDataDto::getRows).orElse(new ArrayList<>());
    }
}
