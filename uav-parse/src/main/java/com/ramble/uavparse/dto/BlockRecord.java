package com.ramble.uavparse.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Project     uav-parse
 * Package     com.ramble.uavparse
 * Class       BlockRecord
 * date        2025/12/10 09:42
 * author      cml
 * Email       liangchen_beijing@163.com
 * Description 
 */

@Data
public class BlockRecord {
    final LocalDateTime time;
    final Double lat, lon, relAlt, absAlt, gbYaw, gbPitch;

    public BlockRecord(LocalDateTime time, Double lat, Double lon, Double relAlt, Double absAlt, Double gbYaw, Double gbPitch) {
        this.time = time;
        this.lat = lat;
        this.lon = lon;
        this.relAlt = relAlt;
        this.absAlt = absAlt;
        this.gbYaw = gbYaw;
        this.gbPitch = gbPitch;
    }

    @Override
    public String toString() {
        return String.format("time=%s lat=%s lon=%s rel=%s abs=%s yaw=%s pitch=%s",
                time, lat, lon, relAlt, absAlt, gbYaw, gbPitch);
    }
}
