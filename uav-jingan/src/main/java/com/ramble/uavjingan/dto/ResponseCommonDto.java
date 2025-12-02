package com.ramble.uavjingan.dto;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * Project     ngh-aircraft
 * Package     com.nghsmart.nghaircraft.adapter.qingpu.dto
 * Class       ResponseCommonDto
 * date        2025/9/11 14:39
 * author      cml
 * Email       liangchen_beijing@163.com
 * Description 
 */

@Data
@ToString
public class ResponseCommonDto<T> implements Serializable {

    private String code;

    private String message;

    private T data;


}
