package com.ramble.uavintegration.event;

import com.ramble.uavcommon.demos.dto.FlightData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Project     ramble-uav
 * Package     com.ramble.uavintegration.event
 * Class       FlightDataListener
 * date        2025/12/2 10:05
 * author      cml
 * Email       liangchen_beijing@163.com
 * Description 
 */
@Slf4j
@Component
public class FlightDataListener {

    @EventListener
    public void receive(FlightData flightData) {
        log.info("Received flight data: {}", flightData);
    }
}
