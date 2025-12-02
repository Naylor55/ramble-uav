package com.ramble.uavcommon.demos.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Project     uav-common
 * Package     com.ramble.uavcommon.demos.dto
 * Class       FlightData
 * date        2025/11/26 16:50
 * author      cml
 * Email       liangchen_beijing@163.com
 * Description 
 */

@Data
public abstract class FlightData {


    /**
     * 飞控数据来源
     */
    private String source;

    /**
     * 原始数据
     */
    private Object payload;

    private String sn;

    private Long timestamp;

    /**
     * timestamp 格式化之后的时间（2025-02-20T12:51:12.980768200）
     */
    private LocalDateTime time;

    /**
     * 84坐标系
     */
    private Double longitude;

    /**
     * 84坐标系
     */
    private Double latitude;

    /**
     * 海拔高度
     */
    private Float height;

    /**
     * 相对高度，相对起飞点的高度
     */
    private Float elevation;

    /**
     * 偏航轴角度
     */
    private Double head;

    /**
     * 横滚轴角度
     */
    private Double roll;

    /**
     * 俯仰轴角度
     */
    private Double pitch;

    private Double zoom;

    /**
     * 高度（单位：米）
     */
    private double altitude;

    /**
     * 速度（单位：公里/小时）
     */
    private double speed;

    /**
     * 电池电量（单位：百分比）
     */
    private int batteryLevel;

    /**
     * 飞行时间（单位：秒）
     */
    private int flightTime;

    /**
     * 温度（单位：摄氏度）
     */
    private double temperature;

}
