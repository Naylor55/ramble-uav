package com.ramble.uavjingan.dto;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * Project     ngh-aircraft
 * Package     com.nghsmart.nghaircraft.adapter.qingpu.dto
 * Class       MsgCommonDto
 * date        2025/9/8 17:14
 * author      cml
 * Email       liangchen_beijing@163.com
 * Description 警航kafka 消息公共属性
 */

@Data
@ToString
public class MsgCommonDto<T> implements Serializable {


    private String event;
    private Long time;

    private T data;
}
