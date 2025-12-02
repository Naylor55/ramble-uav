package com.ramble.uavjingan.service;

import com.alibaba.fastjson2.JSON;
import com.ramble.uavjingan.dto.JinganFlightData;
import com.ramble.uavjingan.dto.UavPropertiesDto;
import com.ramble.uavjingan.support.DeviceSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;


/**
 * Project     ngh-aircraft
 * Package     com.nghsmart.nghaircraft.adapter.qingpu.service
 * Class       DeviceService
 * date        2025/9/23 11:20
 * author      cml
 * Email       liangchen_beijing@163.com
 * Description 
 */
@Slf4j
@RequiredArgsConstructor
public class UavService {

    private final DeviceSupport deviceSupport;
    private final ApplicationEventPublisher publisher;


    /**
     * 同步无人机状态信息，上一次任务结束后，延迟 1 秒再执行
     */
    @Scheduled(fixedDelay = 1000)
    public void syncUavState() {
        try {
            log.debug("syncUavState_start");
            deviceSupport.findDeviceList().forEach(item -> {
                JinganFlightData data = new JinganFlightData();
                UavPropertiesDto uav = item.getProperties();
                log.debug("syncUavState_uavProperties={}", JSON.toJSONString(uav));
                if (null != uav.getSn() && null != uav.getLongitude() && null != uav.getLatitude() && null != uav.getAltitude() && null != uav.getHeight()
                        && null != uav.getGimbalYaw() && null != uav.getGimbalPitch()) {
                    data.setSn(uav.getSn());
                    long timeMillis = System.currentTimeMillis();
                    data.setTimestamp(Long.valueOf(timeMillis));
                    data.setTime(new Timestamp(timeMillis).toLocalDateTime());
                    data.setLongitude(uav.getLongitude());
                    data.setLatitude(uav.getLatitude());
                    data.setElevation(uav.getHeight());
                    data.setHeight(uav.getAltitude());
                    data.setHead(Double.valueOf(uav.getGimbalYaw()));
                    data.setPitch(Double.valueOf(uav.getGimbalPitch()));
                    //广角的时候设置云台聚焦系数为1
                    if (StringUtils.hasText(uav.getLensType()) && "wide".equals(uav.getLensType())) {
                        data.setZoom(Double.valueOf(1.0D));
                    } else {
                        data.setZoom(Double.valueOf(uav.getZoomFactor()));
                    }
                    data.setExtend("靖安科技独有扩展字段");
                    data.setPayload(JSON.toJSONString(uav));
                    //发送 application  event
                    publisher.publishEvent(data);
                } else {
                    log.debug("syncUavState_uav_properties_is_null,sn={}", uav.getSn());
                }
            });
            log.debug("syncUavState_end");
        } catch (Exception e) {
            log.error("syncUavState_exception", e);
        } finally {
            log.debug("syncUavState_complete");
        }

    }

}
