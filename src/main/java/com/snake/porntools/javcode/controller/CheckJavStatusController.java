package com.snake.porntools.javcode.controller;

import com.snake.utils.Constants;
import com.snake.utils.HttpClientUtils;
import com.snake.utils.TxtWriterUtils;
import com.snake.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

public class CheckJavStatusController {
    private static final Logger logger = LoggerFactory.getLogger(CheckJavStatusController.class);

    private static List<String> javCodeList = new ArrayList<String>();
    private static List<String> duplicateJavCodeList = new ArrayList<String>();
    private static List<String> downloadableJavCodeList = new ArrayList<String>();
    private static List<String> chineseSubtitlesJavCodeList = new ArrayList<String>();

    /**
     * 任务的流程如下:
     * 1. 读取 txt 文件.
     * 2. 解析 txt 文件, 然后按行读取内容.
     * 3. 经过一些清洗之后获得真正的 JavCode.
     * 4. 依次将这些 JavCode 组装成 Http Request, 并进行请求.
     * 5. 解析返回的 Http Response, 判断当前 JavCode 是否 包含磁链 或 中文磁链
     * 6. 将所有 JavCode 都检索完毕之后, 将检索完的 JavCode 写入到一个新 txt 文件中.
     *
     * @throws IOException
     */
    public void taskStart() throws IOException {
        logger.info("开始执行 CheckJavStatusController.taskStart()...");
        long startTime = System.nanoTime();

        File file = new File(new Utils().getPropValue("input_javcode_file")); //设定为当前文件夹

        if (!file.exists()) {
            logger.info(file.getAbsolutePath() + " 文件不存在.");
        } else {
            try {
                javCodeList = parseJavCode(file);
                duplicateJavCodeList = new Utils().findCollectionDuplicateElements(javCodeList);

                if (!javCodeList.isEmpty()) {
                    for (String javCode : javCodeList) {
                        String url = Constants.JAVDB_URL.replace(Constants.JAV_CODE, javCode);

                        Map<String, String> htmlMap = new HttpClientUtils().getHTML(url, Constants.JAVDB_HEADERS);

                        String responseCode = htmlMap.get(Constants.RESPONSE_CODE);

                        if (responseCode.equalsIgnoreCase(Constants.HTTP_RESPONSE_CODE_200)) {
                            String html = htmlMap.get(Constants.HTML);

                            // 0: 没有磁链; 1: 有磁链; 2: 有中文磁链
                            Map<String, String> javCodeMap = new HttpClientUtils().parseJavDBHTML(javCode, html);

                            int resourceStatus = Integer.parseInt(javCodeMap.get(Constants.RESOURCE_STATUS));
                            String releaseDate = javCodeMap.get(Constants.RELEASE_DATE);

                            switch (resourceStatus) {
                                case 1:
                                    downloadableJavCodeList.add(javCode + Constants.SPACE_AND_HYPHEN + releaseDate);
                                    break;
                                case 2:
                                    chineseSubtitlesJavCodeList.add(javCode + Constants.SPACE_AND_HYPHEN + releaseDate);
                                    break;
                            }
                        }

                        //Thread.sleep(Constants.HTTP_REQUEST_SLEEP_TIME * 1000);
                        Thread.sleep((new Random().nextInt(Constants.HTTP_REQUEST_RANDOM_SLEEP_TIME) + Constants.HTTP_REQUEST_MIN_SLEEP_TIME) * 1000);
                    }
                }
            } catch (Exception e) {
                logger.debug(e.getMessage(), e);
            }
        }

        if (!downloadableJavCodeList.isEmpty() || !chineseSubtitlesJavCodeList.isEmpty()) {
            Map<String, List<String>> javsMap = new HashMap<String, List<String>>();

            javsMap.put(Constants.DUPLICATE_JAV_CODE_LIST, new Utils().collectionDeduplicateAndResort(duplicateJavCodeList));
            javsMap.put(Constants.DOWNLOADABLE_JAV_CODE_LIST, new Utils().collectionDeduplicateAndResort(downloadableJavCodeList));
            javsMap.put(Constants.CHINESE_SUBTITLES_JAV_CODE_LIST, new Utils().collectionDeduplicateAndResort(chineseSubtitlesJavCodeList));

            new TxtWriterUtils().txtWriter(new Utils().getPropValue("export_javcode_file"), javsMap);
        } else {
            logger.info("\"JavCode 检测结果.txt\" 没有任何内容写入!");
        }

        logger.info("CheckJavStatusController.taskStart() 总用时: " + new Utils().calculatingTimeDiff(System.nanoTime() - startTime));

        logger.info("CheckJavStatusController.taskStart() 执行完毕.");
    }

    /**
     * 传入待检测的 JavCode.txt, 然后依行读取里面的内容
     * 经过一些数据清理之后, 将真正的 JavCode 放入到一个 List<String> 返回
     *
     * @param file - 待检测的 JavCode.txt
     *
     * @return List<String> - 清理完毕的 JavCode
     *
     * @throws IOException
     */
    private List<String> parseJavCode(File file) throws IOException {
        Reader reader = null;
        FileReader fileReader = null;

        List<String> javCodeList = new ArrayList<String>();
        String lineString = "";

        int line = 1;

        try {
            fileReader = new FileReader(file);
            reader = new BufferedReader(fileReader);

            // 一次读入一行, 直到读入 null, 即文件结束
            while ((lineString = ((BufferedReader) reader).readLine()) != null) {
//                if (line == 72 || line == 181) {
//                    System.out.println();
//                }

                if (!lineString.isEmpty()) {
                    String[] temp = lineString.split(Constants.SPACE_SEPARATOR);
                    String javCode = temp[0];

                    if (javCode.contains(Constants.UNDERSCORE) || javCode.contains(Constants.HYPHEN)) {
                        if (javCode.contains(Constants.LEFT_PARENTHESES) || javCode.contains(Constants.RIGHT_PARENTHESES)) {
                            javCode = javCode.replace(Constants.LEFT_PARENTHESES, Constants.SPACE_SEPARATOR);
                            javCode = javCode.replace(Constants.RIGHT_PARENTHESES, Constants.SPACE_SEPARATOR);

                            String[] temp1 = javCode.split(Constants.SPACE_SEPARATOR);

                            javCodeList.add(temp1[0].toUpperCase(Locale.ENGLISH));
                            javCodeList.add(temp1[1].toUpperCase(Locale.ENGLISH));
                        } else {
                            javCodeList.add(javCode.toUpperCase(Locale.ENGLISH));
                        }
                    }
                }

                line++;
            }
        } catch (IOException e) {
            logger.debug(e.getMessage() + ", Line Number: " + line, e);
        } finally {
            if (fileReader != null) {
                fileReader.close();
            }

            if (reader != null) {
                reader.close();
            }
        }

        return javCodeList;
    }

    public static void main(String[] args) {
        CheckJavStatusController checkJavStatusController = new CheckJavStatusController();

        try {
            checkJavStatusController.taskStart();
        } catch (IOException e) {
            logger.debug(e.getMessage(), e);
        }
    }

}
