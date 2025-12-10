package com.ramble.uavparse.parse;

import com.ramble.uavparse.dto.BlockRecord;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Project     uav-parse
 * Package     com.ramble.uavparse
 * Class       DjiYu2ParseImpl
 * date        2025/12/10 09:59
 * author      cml
 * Email       liangchen_beijing@163.com
 * Description
 *
 * 示例格式：
 * 1
 * 00:00:00,000 --> 00:00:00,033
 * <font size="36">FrameCnt : 1, DiffTime : 33ms
 * 2025-12-09 09:58:56,675,927
 * [iso : 100] [shutter : 1/400.0] [fnum : 280] [ev : 0] [ct : 5623] [color_md : default] [focal_len : 240] [dzoom_ratio: 10000, delta:0],[latitude: 39.674253] [longtitude: 118.162311] [altitude: 143.076004] [Drone: Yaw:90.4, Pitch:0.8, Roll:-1.6] </font>
 *
 * 2
 * 00:00:00,033 --> 00:00:00,066
 * <font size="36">FrameCnt : 2, DiffTime : 33ms
 * 2025-12-09 09:58:56,709,294
 * [iso : 100] [shutter : 1/400.0] [fnum : 280] [ev : 0] [ct : 5623] [color_md : default] [focal_len : 240] [dzoom_ratio: 10000, delta:0],[latitude: 39.674253] [longtitude: 118.162311] [altitude: 143.076004] [Drone: Yaw:90.4, Pitch:0.7, Roll:-1.6] </font>
 *
 * 3
 */
public class DjiYu2ParseImpl implements TelemetryParser {


    private static final Pattern TIME_PATTERN =
            Pattern.compile("(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2})");

    @Override
    public boolean supports(String block) {
        // 1. 包含 Drone:
        // 2. 包含 <font>
        return block.contains("Drone:");
    }

    @Override
    public BlockRecord parse(String block) {
        return new BlockRecord(
                parseTime(block), parseLat(block), parseLon(block),
                parseRelAlt(block), parseAbsAlt(block), parseYaw(block), parsePitch(block)
        );
    }

    private LocalDateTime parseTime(String block) {
        Matcher m = TIME_PATTERN.matcher(block);
        if (!m.find()) return null;
        return LocalDateTime.parse(m.group(1), DATE_TIME_SEC);
    }

    private Double parseLat(String block) {
        return findDouble(Pattern.compile("[,\\s]*\\[latitude:\\s*([\\-\\d\\.]+)]"), block);
    }

    private Double parseLon(String block) {
        // 注意 longtitude 拼写问题，不改，直接兼容
        return findDouble(Pattern.compile("[,\\s]*\\[longtitude:\\s*([\\-\\d\\.]+)]"), block);
    }

    /**
     * 解析相对高度
     * 御2当前没有相对高度
     * @param block
     * @return
     */
    private Double parseRelAlt(String block) {
        return 0D;
    }

    /**
     * 解析海拔高度
     * @param block
     * @return
     */
    private Double parseAbsAlt(String block) {
        return findDouble(Pattern.compile("\\[altitude:\\s*([\\-\\d\\.]+)]"), block);
    }

    private Double parseYaw(String block) {
        return findDouble(Pattern.compile("Drone:\\s*Yaw:\\s*([\\-\\d\\.]+)"), block);
    }

    private Double parsePitch(String block) {
        return findDouble(Pattern.compile("Pitch:\\s*([\\-\\d\\.]+)"), block);
    }

    private Double findDouble(Pattern p, String text) {
        Matcher m = p.matcher(text);
        if (!m.find()) return null;

        try {
            return Double.parseDouble(m.group(1));
        } catch (Exception e) {
            return null;
        }
    }
}
