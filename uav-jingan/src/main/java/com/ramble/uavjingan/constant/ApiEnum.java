package com.ramble.uavjingan.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpMethod;

/**
 * Project     ngh-aircraft
 * Package     com.nghsmart.nghaircraft.adapter.qingpu.constant
 * Class       ApiEnum
 * date        2025/9/10 16:09
 * author      cml
 * Email       liangchen_beijing@163.com
 * Description 三方接口地址枚举
 */
@AllArgsConstructor
@Getter
public enum ApiEnum {

    DEVICE_LIST("/TTPService/controlServer/openapi/device/list", HttpMethod.POST, "设备列表"),

    /**
     * 直播地址   /TTPService/controlServer/v3/service/{product_key}/{device_id}/live/post
     */
    LIVE_URL("/TTPService/controlServer/v3/service/%s/%s/live/post", HttpMethod.POST, "直播地址"),
    ;

    private final String url;
    private final HttpMethod method;
    private final String desc;

    public static ApiEnum getByUrl(String url) {
        for (ApiEnum apiEnum : ApiEnum.values()) {
            if (apiEnum.getUrl().equals(url)) {
                return apiEnum;
            }
        }
        return null;
    }
}
