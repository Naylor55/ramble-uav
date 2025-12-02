package com.ramble.uavjingan.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.filter.ValueFilter;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.*;

import static com.alibaba.fastjson2.JSONWriter.Feature.WriteMapNullValue;
import static com.alibaba.fastjson2.JSONWriter.Feature.WriteNullNumberAsZero;


/**
 * Project     ngh-aircraft
 * Package     com.nghsmart.nghaircraft.adapter.qingpu.util
 * Class       GenerateUtils
 * date        2025/9/23 10:01
 * author      cml
 * Email       liangchen_beijing@163.com
 * Description 靖安科技加签名工具类
 */
@Slf4j
public class GenerateUtils {
    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
    //创建 SerializeConfig    实例
    private static ValueFilter valueFilter = (o, name, value) -> {
        if (value instanceof BigDecimal && ((BigDecimal) value).intValue(
        ) == ((BigDecimal) value).doubleValue()) {
            return ((BigDecimal) value).intValue();
        }
        return value;
    };

    public static String generate(String ak, String secretKey, Map<String, Object> params) {
        Map<String, Object> paramsClone = new HashMap<>(params);
        paramsClone.put("AccessKeyId", ak);
        String paramStr = paramsClone.keySet().stream().sorted(String::compareTo).reduce("",
                (l, r) -> {
                    try {
                        return ("".equals(l) ? "" : l + "&") +
                                URLEncoder.encode(r, "UTF-8") + "=" + URLEncoder.encode(convertParam2Str(paramsClone.get(r)), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        return "";
                    }
                });
        //        paramStr = paramStr.replaceAll("%2F", "/");
        paramStr = paramStr.replaceAll("\\+", "%20");
        paramStr = paramStr.replaceAll("\\*", "%2A");
        paramStr = paramStr.replaceAll("%7e", "~");
        paramStr = paramStr.replaceAll("%27", "'");
        log.debug("paramStr:{}", paramStr);
        try {
            return URLEncoder.encode(genHMAC(secretKey, paramStr), "UTF8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    private static String convertParam2Str(Object o) {
        if (o instanceof Map) {
            return JSON.toJSONString(o, valueFilter, WriteNullNumberAsZero, WriteMapNullValue);
        }
        if (o instanceof String) {
            return (String) o;
        }
        if (o instanceof Collection) {
            return JSON.toJSONString(o, valueFilter, WriteNullNumberAsZero, WriteMapNullValue);
        }
        return o + "";
    }

    private static String genHMAC(String data, String key) {
        byte[] result = null;
        try {
            SecretKeySpec signinKey = new SecretKeySpec(key.getBytes(), HMAC_SHA1_ALGORITHM);
            Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
            mac.init(signinKey);
            byte[] rawHmac = mac.doFinal(data.getBytes());
            result = Base64.getEncoder().encode(rawHmac);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Objects.isNull(result) ? null : new String(result);
    }


//    public static void main(String[] args) {
//        JSONObject param = JSON.parseObject("{\"deviceIds\":\"330102330102011508316830232581012242840175514529857\",\"taskTemplateInfo\":{\"defaultDeviceId\":\"330102330102011508316830232581012242840175514529857\",\"parameters\":{\"spaces\":[{\"spaceId\":\"MAP\",\"spaceType\":\"MAP\",\"positions\":[{\"positionName\":\"南湖园区5号⻔\",\"pointX\":\"119.89782894953204\",\"pointY\":\"30.249822096988698\",\"pointZ\":\"0.0\",\"positionIndex\":\"0\",\"id\":\"87056440897605\",\"actions\":[{\"type\":\"GET_PICTURE\",\"config\":{\"useGlobalPayloadLensIndex\":\"0\",\"payloadLensIndex\":[\"wide\"]}}]}]}]},\"taskBasic\":\"{\\\"actionTriggerParam\\\":0.0,\\\"coverage\\\":0.0,\\\"deviceModel\\\":\\\"M300\\\",\\\"finishAction\\\":\\\"NO_ACTION\\\",\\\"flyToWaylineMode\\\":\\\"safely\\\",\\\"gimbalPitchMode\\\":\\\"manual\\\",\\\"globalRTHHeight\\\":100.0,\\\"globalTransitionalSpeed\\\":15.0,\\\"globalWaypointTurnMode\\\":\\\"toPointAndPassWithContinuityCurvature\\\",\\\"height\\\":100.0,\\\"mainK\\\":0.0,\\\"payloadEnumValue\\\":\\\"42\\\",\\\"payloadInfo\\\":{\\\"payloadEnumValue\\\":53,\\\"payloadPositionIndex\\\":0,\\\"payloadSubEnumValue\\\":2},\\\"payloadPositionIndex\\\":0,\\\"photoWaylineCoverage\\\":0.0,\\\"record\\\":\\\"NOT_RECORD\\\",\\\"speed\\\":10.0,\\\"takeOffRefPoint\\\":[],\\\"takeOffRefPointAGLHeight\\\":0.0,\\\"takeOffSecurityHeight\\\":20.0,\\\"waypointHeadingMode\\\":\\\"followWayline\\\",\\\"wideGSD\\\":0.0}\"},\"actionName\":\"1945347179640766464\"}"
//        );
//        String accessKeyId = "ajfcudajyjgugkby";
//        String secretAccessKey = "2Ohy3L1XKyxXZt5TdoQ9Vw==";
//        String Signature = generate(accessKeyId, secretAccessKey, param);
//        System.out.println(Signature);
//    }

    public static void main(String[] args) {
        JSONObject param = JSON.parseObject("{\"page\":1,\"size\":100}");
        String accessKeyId = "hgkpnhqucomaqmws";
        String secretAccessKey = "wxMlYm4Xg13VVBMekScZsQ==";
        String Signature = generate(accessKeyId, secretAccessKey, param);
        System.out.println(Signature);
    }

}
