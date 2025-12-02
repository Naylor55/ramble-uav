package com.ramble.uavjingan.dto;

import com.ramble.uavcommon.demos.dto.FlightData;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Project     ramble-uav
 * Package     com.ramble.uavjingan.dto
 * Class       JinganFlightData
 * date        2025/12/2 11:15
 * author      cml
 * Email       liangchen_beijing@163.com
 * Description 
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class JinganFlightData extends FlightData {
    public JinganFlightData() {
        super();
        this.setSource("靖安科技");
    }

    /**
     * 扩展字段，仅靖安科技独有
     */
    private String extend;

}
