package com.ramble.uavjingan.dto;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * Project     ngh-aircraft
 * Package     com.nghsmart.nghaircraft.adapter.qingpu.dto
 * Class       DeviceStateDto
 * date        2025/9/8 17:15
 * author      cml
 * Email       liangchen_beijing@163.com
 * Description 设备属性
 */

@Data
@ToString
public class DevicePropertiesDto implements Serializable {

    /**
     * 产品key，设备物模型产品主键
     */
    private String productKey;

    /**
     * 设备id，平台设备唯⼀标识
     */
    private String deviceId;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 详情⻅枚举定义[设备状态]
     */
    private String status;

    private Long registerTime;

    private Long lastOnlineTime;

    /**
     * ⽗设备id，平台设备唯⼀标识
     */
    private String parentId;

    /**
     * 详情⻅[设备属性实体定义]
     */
    private UavPropertiesDto properties;

}
