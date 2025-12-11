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
import java.util.regex.Pattern;

@Slf4j
@SpringBootApplication
public class UavParseApplication {

    // 用于判断 SRT 块序号行的正则表达式，匹配一个数字
    private static final Pattern BLOCK_NUMBER_PATTERN = Pattern.compile("^\\d+$");

    // 用于判断 SRT 时间码行的正则表达式 (关键!)
    // 格式: hh:mm:ss,mmm --> hh:mm:ss,mmm
    private static final Pattern TIMECODE_PATTERN = Pattern.compile("^\\d{2}:\\d{2}:\\d{2},\\d{3} --> \\d{2}:\\d{2}:\\d{2},\\d{3}$");

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
        List<Path> srtFiles = List.of(Paths.get("D:\\生产环境维护\\唐山高新分局\\DJI_0317.SRT"));

        //机场格式
        //List<Path> srtFiles = List.of(Paths.get("D:\\生产环境维护\\重庆\\万盛\\DJI_20251126105825_0001_W.srt"));
        //List<Path> srtFiles = List.of(Paths.get("D:\\生产环境维护\\天津\\20251209_富康路巡检-2-20251209171419\\20251209_富康路巡检.SRT"));

        //M30T 遥控器录制，无人机sd卡获取的srt
        //List<Path> srtFiles = List.of(Paths.get("D:\\生产环境维护\\黔西\\DJI_20250110150450_0001_S.SRT"));

        //M300
        //List<Path> srtFiles = List.of(Paths.get("D:\\生产环境维护\\杭州拱墅\\1205_0002_S\\DJI_20251204163651_0002_S.SRT"));

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
            String line;
            StringBuilder currentBlock = new StringBuilder();
            boolean blockStarted = false; // 标记是否已经开始构建一个块

            while ((line = br.readLine()) != null) {
                // 判断当前行是否是时间码行
                if (TIMECODE_PATTERN.matcher(line.trim()).matches()) {
                    // 遇到了时间码行
                    if (blockStarted) {
                        // 如果已经在构建一个块，则当前块结束
                        String blockToParse = currentBlock.toString().trim();
                        BlockRecord record = fileParser.parse(blockToParse);
                        if (record != null) {
                            records.add(record);
                        } else {
                            log.warn("Failed to parse block from file {}: {}", srtFile, blockToParse.substring(0, Math.min(100, blockToParse.length())) + "...");
                        }
                        // 重置 StringBuilder 为新块
                        currentBlock.setLength(0);
                    }
                    // 开始构建新块 (或为第一个块开始)
                    blockStarted = true;
                }

                // 如果已经开始构建块，则将当前行添加到块中
                // (这会自动包含序号行、时间码行、内容行和空行)
                if (blockStarted) {
                    currentBlock.append(line).append('\n');
                }
            }

            // 循环结束后，检查是否还有最后一个未处理的块
            if (blockStarted && currentBlock.length() > 0) {
                String blockToParse = currentBlock.toString().trim();
                BlockRecord record = fileParser.parse(blockToParse);
                if (record != null) {
                    records.add(record);
                } else {
                    log.warn("Failed to parse last block from file {}: {}", srtFile, blockToParse.substring(0, Math.min(100, blockToParse.length())) + "...");
                }
            }
        }
        return records;
    }

    /**
     * 为文件选择合适的解析器 按照空行分块
     * @param srtFile
     * @return
     * @throws IOException
     */
    private static TelemetryParser selectParserWithEmptyLine(Path srtFile) throws IOException {
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

    /**
     * 寻找合适的解析器，使用基于序号(0,1,2)的逻辑读取第一个完整块
     * @param srtFile SRT 文件路径
     * @return 匹配的 TelemetryParser，如果没有找到则返回 null
     * @throws IOException 文件读取异常
     */
    private static TelemetryParser selectParserWithSequenceNumber(Path srtFile) throws IOException {
        try (BufferedReader br = Files.newBufferedReader(srtFile)) {
            String line;
            StringBuilder firstBlock = new StringBuilder();

            // 读取第一个完整的数据块用于判断文件类型
            while ((line = br.readLine()) != null) {
                // 判断是否遇到了下一个块的序号（表示第一个块结束）
                if (BLOCK_NUMBER_PATTERN.matcher(line.trim()).matches() && firstBlock.length() > 0) {
                    // 如果已经读取了内容并且又遇到了序号行，则认为第一个块已经读完
                    break;
                }
                // 如果是第一个遇到的序号行，则忽略它，继续读取内容
                if (!(BLOCK_NUMBER_PATTERN.matcher(line.trim()).matches() && firstBlock.length() == 0)) {
                    firstBlock.append(line).append('\n');
                }
            }

            // 根据第一个数据块选择解析器
            if (firstBlock.length() > 0) {
                String firstBlockText = firstBlock.toString().trim(); // 清理末尾换行符再判断
                log.debug("First block sample for parser selection ({} chars):\n{}", firstBlockText.length(), firstBlockText);

                return parsers.stream()
                        .filter(p -> {
                            boolean supports = p.supports(firstBlockText);
                            log.debug("Parser {} supports first block: {}", p.getClass().getSimpleName(), supports);
                            return supports;
                        })
                        .findFirst()
                        .orElse(null); // 如果没找到，返回 null
            } else {
                log.warn("File appears to be empty or unreadable: {}", srtFile);
            }
        } catch (IOException e) {
            log.error("Error reading file for parser selection: {}", srtFile, e);
            throw e; // Re-throw to handle it upstream if needed
        }
        return null; // Default if no parser is found or file is empty
    }

    /**
     * 寻找合适的解析器，使用 00:00:00,000 --> 00:00:00,033 分割数据块
     * @param srtFile
     * @return
     * @throws IOException
     */
    private static TelemetryParser selectParser(Path srtFile) throws IOException {
        try (BufferedReader br = Files.newBufferedReader(srtFile)) {
            String line;
            StringBuilder firstBlock = new StringBuilder();
            boolean blockStarted = false;
            boolean firstBlockComplete = false;

            // 读取第一个完整的数据块用于判断该使用哪个解析器
            while ((line = br.readLine()) != null && !firstBlockComplete) {
                // 判断当前行是否是时间码行
                if (TIMECODE_PATTERN.matcher(line.trim()).matches()) {
                    if (blockStarted) {
                        // 如果已经开始构建第一个块，现在又遇到时间码，说明第一个块结束
                        firstBlockComplete = true;
                        // 注意：不把第二个时间码行加到 firstBlock 里
                        continue; // 跳过添加这行，直接进入下次循环判断是否退出
                    } else {
                        // 第一次遇到时间码行，开始构建第一个块
                        blockStarted = true;
                    }
                }

                // 如果已经开始构建第一个块，则添加行
                if (blockStarted) {
                    firstBlock.append(line).append('\n');
                }
            }

            // 根据第一个数据块选择解析器
            if (firstBlock.length() > 0) {
                String firstBlockText = firstBlock.toString().trim();
                log.debug("First block sample for parser selection ({} chars): {}", firstBlockText.length(), firstBlockText.substring(0, Math.min(200, firstBlockText.length())) + "...");

                return parsers.stream()
                        .filter(p -> {
                            boolean supports = p.supports(firstBlockText);
                            log.debug("Parser {} supports first block: {}", p.getClass().getSimpleName(), supports);
                            return supports;
                        })
                        .findFirst()
                        .orElse(null);
            } else {
                log.warn("Could not extract a complete block for parser selection from file: {}", srtFile);
            }
        } catch (IOException e) {
            log.error("Error reading file for parser selection: {}", srtFile, e);
            throw e;
        }
        return null;
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
