package com.ramble.uavparse.parse;

import com.ramble.uavparse.dto.BlockRecord;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Project     uav-parse
 * Package     com.ramble.uavparse
 * Class       DjiM30tParseImpl
 * date        2025/12/10 10:06
 * author      cml
 * Email       liangchen_beijing@163.com
 * Description
 *
 * 示例格式：
 * 1
 * 00:00:00,000 --> 00:00:00,033
 * <font size="28">FrameCnt: 1, DiffTime: 33ms
 * 2025-01-10 15:04:50.857
 * [focal_len: 24.00] [dzoom_ratio: 1.00], [latitude: 0.000000] [longitude: 0.000000] [rel_alt: 0.000 abs_alt: 1114.790] [gb_yaw: 166.3 gb_pitch: 0.0 gb_roll: 0.0] </font>
 *
 * 2
 * 00:00:00,033 --> 00:00:00,066
 * <font size="28">FrameCnt: 2, DiffTime: 33ms
 * 2025-01-10 15:04:50.890
 * [focal_len: 24.00] [dzoom_ratio: 1.00], [latitude: 0.000000] [longitude: 0.000000] [rel_alt: 0.000 abs_alt: 1114.790] [gb_yaw: 166.3 gb_pitch: 0.0 gb_roll: 0.0] </font>
 *
 * 3
 */
public class DjiM30tParseImpl implements TelemetryParser {


    private static final Pattern TIME_PATTERN =
            Pattern.compile("(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2})");

    @Override
    public boolean supports(String block) {
        // 1. 包含 rel_alt 和 gb_yaw
        // 3. 不包含 Drone:
        return block.contains("rel_alt")
                && block.contains("gb_yaw")
                && !block.contains("Drone:")
                && !block.contains("drone_yaw");
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
        return findDouble(Pattern.compile("\\blatitude:\\s*([\\-+]?\\d+\\.\\d*|\\d*\\.\\d+)"), block);
    }

    private Double parseLon(String block) {
        return findDouble(Pattern.compile("\\blongitude:\\s*([+-]?\\d+(\\.\\d+)?)"), block);
    }

    private Double parseRelAlt(String block) {
        return findDouble(Pattern.compile("[,\\s]*rel_alt:\\s*([\\-\\d\\.]+)"), block);
    }

    private Double parseAbsAlt(String block) {
        return findDouble(Pattern.compile("[,\\s]*abs_alt:\\s*([\\-\\d\\.]+)"), block);
    }

    private Double parseYaw(String block) {
        return findDouble(Pattern.compile("[,\\s]*gb_yaw:\\s*([\\-\\d\\.]+)"), block);
    }

    private Double parsePitch(String block) {
        return findDouble(Pattern.compile("[,\\s]*gb_pitch:\\s*([\\-\\d\\.]+)"), block);
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
