package com.ramble.uavparse.parse;

import com.ramble.uavparse.dto.BlockRecord;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Project     uav-parse
 * Package     com.ramble.uavparse
 * Class       DjiDock2ParseImpl
 * date        2025/12/10 09:49
 * author      cml
 * Email       liangchen_beijing@163.com
 * Description
 *
 * 示例格式：
 * 1
 * 00:00:00,000 --> 00:00:00,033
 * FrameCnt: 0 2025-11-26 10:58:25.417
 * [iso: 170] [shutter: 1/3200.0] [fnum: 1.7] [ev: 0] [color_md: default] [ae_meter_md: 1] [focal_len: 24.00] [dzoom_ratio: 1.00], [latitude: 28.941126] [longitude: 106.921302] [rel_alt: 134.974 abs_alt: 428.593] [gb_yaw: 4.5 gb_pitch: -20.0 gb_roll: 0.0]
 *
 * 2
 * 00:00:00,033 --> 00:00:00,066
 * FrameCnt: 1 2025-11-26 10:58:25.452
 * [iso: 170] [shutter: 1/3200.0] [fnum: 1.7] [ev: 0] [color_md: default] [ae_meter_md: 1] [focal_len: 24.00] [dzoom_ratio: 1.00], [latitude: 28.941126] [longitude: 106.921302] [rel_alt: 134.974 abs_alt: 428.593] [gb_yaw: 4.5 gb_pitch: -20.0 gb_roll: 0.0]
 *
 * 3
 *
 */
public class DjiDock2ParseImpl implements TelemetryParser {


    private static final Pattern TIME_PATTERN =
            Pattern.compile("(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2})");

    @Override
    public boolean supports(String block) {
        // 1. 包含 rel_alt
        // 2. 包含 gb_yaw
        // 3. 不包含 <font> 标签
        // 4. FrameCnt 行以 FrameCnt: 开头
        return block.contains("rel_alt")
                && block.contains("gb_yaw")
                && !block.contains("<font>")
                && block.lines().anyMatch(l -> l.trim().startsWith("FrameCnt:"));
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

        String text = m.group(1);
        try {
            return LocalDateTime.parse(text, DATE_TIME_SEC);
        } catch (Exception e) {
            return null;
        }
    }

    private Double parseLat(String block) {
        Pattern latPattern = Pattern.compile("\\[latitude:\\s*([\\-\\d\\.]+)]");
        return findDouble(latPattern, block);
    }

    private Double parseLon(String block) {
        Pattern lonPattern = Pattern.compile("\\[longitude:\\s*([\\-\\d\\.]+)]");
        return findDouble(lonPattern, block);
    }

    private Double parseRelAlt(String block) {
        return findDouble(Pattern.compile("rel_alt:\\s*([\\-\\d\\.]+)"), block);
    }

    private Double parseAbsAlt(String block) {
        return findDouble(Pattern.compile("abs_alt:\\s*([\\-\\d\\.]+)"), block);
    }

    private Double parseYaw(String block) {
        return findDouble(Pattern.compile("gb_yaw:\\s*([\\-\\d\\.]+)"), block);
    }

    private Double parsePitch(String block) {
        return findDouble(Pattern.compile("gb_pitch:\\s*([\\-\\d\\.]+)"), block);
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
