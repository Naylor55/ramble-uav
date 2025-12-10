package com.ramble.uavparse;

import com.ramble.uavparse.dto.BlockRecord;
import com.ramble.uavparse.parse.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@SpringBootApplication
public class UavParseApplication {
    // 解析器列表
    private static final List<TelemetryParser> parsers = List.of(
            new DjiDock2ParseImpl(),
            new DjiM300ParseImpl(),
            new DjiM30tParseImpl(),
            new DjiYu2ParseImpl()
    );

    public static void main(String[] args) throws IOException {
        SpringApplication.run(UavParseApplication.class, args);

        // 输入多个 .srt 文件路径。解析出里面的时间，经纬度，云台俯仰角度等信息，然后归集到 List 中 ，并按照时间升序。
        //御2格式
        //List<Path> srtFiles = List.of(Paths.get("D:\\生产环境维护\\唐山高新分局\\DJI_0317.SRT"));

        //机场格式
        //List<Path> srtFiles = List.of(Paths.get("D:\\生产环境维护\\重庆\\万盛\\DJI_20251126105825_0001_W.srt"));
        //List<Path> srtFiles = List.of(Paths.get("D:\\生产环境维护\\天津\\20251209_富康路巡检-2-20251209171419\\20251209_富康路巡检.SRT"));

        //M30T 遥控器录制，无人机sd卡获取的srt
        //List<Path> srtFiles = List.of(Paths.get("D:\\生产环境维护\\黔西\\DJI_20250110150450_0001_S.SRT"));

        //M300
        List<Path> srtFiles = List.of(Paths.get("D:\\生产环境维护\\杭州拱墅\\1205_0002_S\\DJI_20251204163651_0002_S.SRT"));

        List<BlockRecord> allRecords = new ArrayList<>();

        // 逐个解析文件
        for (Path srtFile : srtFiles) {
            allRecords.addAll(parseSrtFile(srtFile));
        }

        // 按时间排序
        allRecords.sort(Comparator.comparing(BlockRecord::getTime));

        // 输出所有记录
        allRecords.forEach(System.out::println);
    }

    // 解析单个 SRT 文件
    public static List<BlockRecord> parseSrtFile(Path srtFile) throws IOException {
        List<BlockRecord> records = new ArrayList<>();
        // 为当前文件选择解析器（只需要选择一次）
        TelemetryParser fileParser = selectParser(srtFile);
        log.debug("matched_parser: {}", fileParser);
        try (BufferedReader br = Files.newBufferedReader(srtFile)) {
            StringBuilder block = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    if (block.length() > 0) {
                        records.add(fileParser.parse(block.toString()));
                        block.setLength(0);
                    }
                } else {
                    block.append(line).append('\n');
                }
            }
            if (block.length() > 0) {
                // 处理最后一个 block
                records.add(fileParser.parse(block.toString()));
            }
        }
        return records;
    }

    /**
     * 为文件选择合适的解析器
     * @param srtFile
     * @return
     * @throws IOException
     */
    private static TelemetryParser selectParser(Path srtFile) throws IOException {
        try (BufferedReader br = Files.newBufferedReader(srtFile)) {
            String line;
            StringBuilder firstBlock = new StringBuilder();

            // 读取第一个数据块用于判断文件类型
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    if (firstBlock.length() > 0) {
                        break; // 找到第一个完整的数据块
                    }
                } else {
                    firstBlock.append(line).append('\n');
                }
            }

            // 根据第一个数据块选择解析器
            if (firstBlock.length() > 0) {
                String firstBlockText = firstBlock.toString();
                return parsers.stream()
                        .filter(p -> p.supports(firstBlockText))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Unsupported SRT format for file: " + srtFile));
            }
        }
        throw new IllegalArgumentException("Cannot determine parser for empty file: " + srtFile);
    }

    // 根据不同格式解析每个 block
    private static BlockRecord parseBlock(String blockText) {
        // 选择合适的解析器
        TelemetryParser parser = parsers.stream()
                .filter(p -> p.supports(blockText))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported SRT format"));
        log.debug("matched_parser: {}", parser);
        return parser.parse(blockText);
    }


}
