package com.ramble.uavparse.parse;

import com.ramble.uavparse.dto.BlockRecord;

import java.time.format.DateTimeFormatter;

/**
 * Project     uav-parse
 * Package     com.ramble.uavparse
 * Class       TelemetryParser
 * date        2025/12/10 09:42
 * author      cml
 * Email       liangchen_beijing@163.com
 * Description 
 */
public interface TelemetryParser {

    DateTimeFormatter DATE_TIME_SEC = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    boolean supports(String block);   // 判断当前解析器是否支持该SRT格式

    BlockRecord parse(String block);  // 解析SRT block并返回BlockRecord

}
