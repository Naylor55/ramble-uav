package com.ramble.uavjingan.dto;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * Project     ngh-aircraft
 * Package     com.nghsmart.nghaircraft.adapter.qingpu.dto
 * Class       DeviceDataDto
 * date        2025/9/11 11:28
 * author      cml
 * Email       liangchen_beijing@163.com
 * Description 
 */


@Data
@ToString
public class DeviceDataDto implements Serializable {


    private Integer total;


    private List<DevicePropertiesDto> rows;
}
