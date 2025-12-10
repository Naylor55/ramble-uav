package com.ramble.uavparse.parse;

import com.ramble.uavparse.dto.BlockRecord;

import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Project     uav-parse
 * Package     com.ramble.uavparse.parse
 * Class       DjiM300ParseImpl
 * date        2025/12/10 16:49
 * author      cml
 * Email       liangchen_beijing@163.com
 * Description
 *
 * 示例格式：
 * 1
 * 00:00:00,000 --> 00:00:00,033
 * <font size="28">FrameCnt: 1, DiffTime: 33ms
 * 2025-12-04 16:36:51.746
 * [iso: 150] [shutter: 1/30.0] [fnum: 2.8] [ev: 0.7] [focal_len: 24.00] [dzoom: 1.00]
 * [latitude: 30.319154] [longitude: 120.142543] [rel_alt: -0.000 abs_alt: 17.337]
 * [drone_speedx: 0.0 drone_speedy: 0.0 drone_speedz: 0.0]
 * [drone_yaw: 1.9 drone_pitch: 0.0 drone_roll: -1.0]
 * [gb_yaw: -3.6 gb_pitch: 0.0 gb_roll: 0.0]
 *
 * 0
 * [ae_meter_md : 0] [dzoom_ratio: 10000, delta:0] [color_md : default] [ct : 6410]
 * </font>
 *
 * 2
 * 00:00:00,033 --> 00:00:00,066
 * <font size="28">FrameCnt: 2, DiffTime: 33ms
 * 2025-12-04 16:36:51.778
 * [iso: 150] [shutter: 1/30.0] [fnum: 2.8] [ev: 0.7] [focal_len: 24.00] [dzoom: 1.00]
 * [latitude: 30.319154] [longitude: 120.142543] [rel_alt: -0.000 abs_alt: 17.337]
 * [drone_speedx: 0.0 drone_speedy: 0.0 drone_speedz: 0.0]
 * [drone_yaw: 1.9 drone_pitch: 0.0 drone_roll: -1.0]
 * [gb_yaw: -3.6 gb_pitch: 0.0 gb_roll: 0.0]
 *
 * 0
 * [ae_meter_md : 0] [dzoom_ratio: 10000, delta:0] [color_md : default] [ct : 6410]
 * </font>
 *
 * 3
 * 00:00:00,066 --> 00:00:00,100
 * <font size="28">FrameCnt: 3, DiffTime: 34ms
 * 2025-12-04 16:36:51.812
 * [iso: 150] [shutter: 1/30.0] [fnum: 2.8] [ev: 0.7] [focal_len: 24.00] [dzoom: 1.00]
 * [latitude: 30.319154] [longitude: 120.142543] [rel_alt: -0.000 abs_alt: 17.337]
 * [drone_speedx: 0.0 drone_speedy: 0.0 drone_speedz: 0.0]
 * [drone_yaw: 1.9 drone_pitch: 0.0 drone_roll: -1.0]
 * [gb_yaw: -3.6 gb_pitch: 0.0 gb_roll: 0.0]
 *
 * 0
 *
 */
public class DjiM300ParseImpl implements TelemetryParser {

    private static final Pattern TIME_PATTERN = Pattern.compile("(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2})");
    private static final Pattern LAT_PATTERN = Pattern.compile("\\blatitude:\\s*([+-]?\\d+(\\.\\d+)?)");
    private static final Pattern LON_PATTERN = Pattern.compile("\\blongitude:\\s*([+-]?\\d+(\\.\\d+)?)");
    private static final Pattern REL_ALT_PATTERN = Pattern.compile("\\brel_alt:\\s*([+-]?\\d+(\\.\\d+)?)");
    private static final Pattern ABS_ALT_PATTERN = Pattern.compile("\\babs_alt:\\s*([+-]?\\d+(\\.\\d+)?)");
    private static final Pattern GB_YAW_PATTERN = Pattern.compile("\\bgb_yaw:\\s*([+-]?\\d+(\\.\\d+)?)");
    private static final Pattern GB_PITCH_PATTERN = Pattern.compile("\\bgb_pitch:\\s*([+-]?\\d+(\\.\\d+)?)");

    @Override
    public boolean supports(String block) {
        return block.contains("rel_alt")
                && block.contains("gb_yaw")
                && block.contains("drone_yaw")
                && !block.contains("Drone:");
    }

    @Override
    public BlockRecord parse(String block) {
        return new BlockRecord(
                parseTime(block),
                parseLat(block),
                parseLon(block),
                parseRelAlt(block),
                parseAbsAlt(block),
                parseGimbalYaw(block),
                parseGimbalPitch(block)
        );
    }


    private LocalDateTime parseTime(String block) {
        Matcher m = TIME_PATTERN.matcher(block);
        if (!m.find()) return null;

        return LocalDateTime.parse(m.group(1), DATE_TIME_SEC);
    }


    private Double parseLat(String block) {
        return findDouble(LAT_PATTERN, block);
    }

    private Double parseLon(String block) {
        return findDouble(LON_PATTERN, block);
    }

    private Double parseRelAlt(String block) {
        return findDouble(REL_ALT_PATTERN, block);
    }

    private Double parseAbsAlt(String block) {
        return findDouble(ABS_ALT_PATTERN, block);
    }

    private Double parseGimbalYaw(String block) { // 对应 gb_yaw
        return findDouble(GB_YAW_PATTERN, block);
    }

    private Double parseGimbalPitch(String block) { // 对应 gb_pitch
        return findDouble(GB_PITCH_PATTERN, block);
    }


    /**
     * 根据给定的 Pattern 在文本中查找并解析第一个 double 值。
     *
     * @param p 用于匹配和捕获数值的正则表达式 Pattern。
     * @param text    要搜索的文本块。
     * @return 解析出的 Double 值，如果未找到或解析失败则返回 null。
     */
    private Double findDouble(Pattern p, String text) {
        Matcher m = p.matcher(text);
        if (!m.find()) return 0D;

        try {
            return Double.parseDouble(m.group(1));
        } catch (Exception e) {
            return 0D;
        }
    }
}
