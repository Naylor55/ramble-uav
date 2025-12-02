package com.ramble.uavjingan.dto;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * Project     ngh-aircraft
 * Package     com.nghsmart.nghaircraft.adapter.qingpu.dto
 * Class       LiveInfoDto
 * date        2025/9/11 14:43
 * author      cml
 * Email       liangchen_beijing@163.com
 * Description 
 */

@Data
@ToString
public class LiveInfoDto implements Serializable {

    private String protocol;

    private String playUrl = "";

    private String sourceAccessProtocol;
}
