package com.ramble.uavjingan.dto;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * Project     ngh-aircraft
 * Package     com.nghsmart.nghaircraft.adapter.qingpu.dto
 * Class       UavPropertiesDto
 * date        2025/9/8 17:18
 * author      cml
 * Email       liangchen_beijing@163.com
 * Description 无人机属性
 */

@Data
@ToString
public class UavPropertiesDto implements Serializable {

    private Double longitude;

    private Double latitude;

    private String sn;

    private Float height;

    private Float altitude;

    private Float gimbalYaw;

    private Float gimbalPitch;

    private Float zoomFactor;

    /**
     * ⼤疆上云api OSD原数据实体详情参考
     * https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/aircraft/m4d-properties.html
     */
    private JSONObject cloudApiOsdData;


    /**
     * 当前镜头视角：wide = 广角 ； ir = 红外 ； zoom = 变焦
     */
    private  String lensType;


}
