package com.snake.porntools.javcode.controller;

import com.snake.utils.Constants;
import com.snake.utils.HttpClientUtils;
import com.snake.utils.TxtWriterUtils;
import com.snake.utils.Utils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

public class CheckJavStatusController {
    private static final Logger logger = LoggerFactory.getLogger(CheckJavStatusController.class);

    private static final Utils utils = new Utils();

    private static final List<String> javCodeList = new ArrayList<>();
    private static List<String> duplicateJavCodeList = new ArrayList<>();
    private static final List<String> downloadableJavCodeList = new ArrayList<>();
    private static final List<String> chineseSubtitlesJavCodeList = new ArrayList<>();

    /**
     * 任务的流程如下:
     * 1. 读取 txt 文件.
     * 2. 解析 txt 文件, 然后按行读取内容.
     * 3. 经过一些清洗之后获得真正的 JavCode.
     * 4. 依次将这些 JavCode 组装成 Http Request, 并进行请求.
     * 5. 解析返回的 Http Response, 判断当前 JavCode 是否 包含磁链 或 中文磁链
     * 6. 将所有 JavCode 都检索完毕之后, 将检索完的 JavCode 写入到一个新 txt 文件中.
     */
    public void taskStart() throws IOException {
        logger.info("开始执行 CheckJavStatusController.taskStart()...");
        long startTime = System.nanoTime();

        File file = new File(utils.getPropValue("input_javcode_file")); //设定为当前文件夹

        if (!file.exists()) {
            logger.info(file.getAbsolutePath() + " 文件不存在.");
        } else {
            parseJavCode(file);
            duplicateJavCodeList = utils.findCollectionDuplicateElements(javCodeList);

            if (!javCodeList.isEmpty()) {
                List<String> urlList = new ArrayList<>();
                String url;
                for (String javCode : javCodeList) {
                    url = Constants.JAVDB_URL.replace(Constants.JAV_CODE, javCode);

                    urlList.add(url);
                }

                Map<String, String> headers = Constants.JAVDB_HEADERS;
//                headers.put("date", getFormatDate(0));
//                headers.put("set-cookie", headers.get("set-cookie").replace(Constants.EXPIRES_DATE_FORMAT, getFormatDate(14)));

                List<Map<String, String>> htmlList = new HttpClientUtils().sendHttpRequestList(Constants.REQUEST_TYPE_GET, urlList, headers, new HashMap<>());
                String responseCode;
                String html;
                String javCode;
                for (int i = 0; i < htmlList.size(); i++) {
                    Map<String, String> htmlMap = htmlList.get(i);
                    responseCode = htmlMap.get(Constants.RESPONSE_CODE);

                    if (responseCode.equalsIgnoreCase(Constants.HTTP_RESPONSE_CODE_200)) {
                        html = htmlMap.get(Constants.HTML);
                        javCode = javCodeList.get(i);

                        // 0: 没有磁链; 1: 有磁链; 2: 有中文磁链
                        Map<String, String> javCodeMap = parseJavDBHTML(javCode, html);

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
                }
            }
        }

        if (!downloadableJavCodeList.isEmpty() || !chineseSubtitlesJavCodeList.isEmpty()) {
            Map<String, List<String>> javsMap = new HashMap<>();

            javsMap.put(Constants.DUPLICATE_JAV_CODE_LIST, utils.collectionDeduplicateAndResort(duplicateJavCodeList));
            javsMap.put(Constants.DOWNLOADABLE_JAV_CODE_LIST, utils.collectionDeduplicateAndResort(downloadableJavCodeList));
            javsMap.put(Constants.CHINESE_SUBTITLES_JAV_CODE_LIST, utils.collectionDeduplicateAndResort(chineseSubtitlesJavCodeList));

            new TxtWriterUtils().txtWriter(utils.getPropValue("export_javcode_file"), javsMap);
        } else {
            logger.info("\"JavCode 检测结果.txt\" 没有任何内容写入!");
        }

        logger.info("CheckJavStatusController.taskStart() 总用时: " + utils.calculatingTimeDiff(System.nanoTime() - startTime));

        logger.info("CheckJavStatusController.taskStart() 执行完毕.");
    }

    /**
     * 传入待检测的 JavCode.txt, 然后依行读取里面的内容
     * 经过一些数据清理之后, 将真正的 JavCode 放入到一个 List<String> 返回
     *
     * @param file - 待检测的 JavCode.txt
     */
    private void parseJavCode(File file) {
        String lineString;

        int line = 1;

        try (FileReader fileReader = new FileReader(file); BufferedReader reader = new BufferedReader(fileReader)) {
            // 一次读入一行, 直到读入 null, 即文件结束
            while ((lineString = reader.readLine()) != null) {
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
        }
    }

    /**
     * 解析 Http 返回的 html 页面, 判断当前 JavCode 的状态
     *
     * @param javCode - 需要检测的 JavCode
     * @param html    - Http 返回的 html 页面
     * @return Map    - key: resourceStatus - 影片的 <资源状态>; key: releaseDate - 影片的 <发布日期>
     */
    private Map<String, String> parseJavDBHTML(String javCode, String html) {
        Map<String, String> javCodeMap = new HashMap<>();

        String resourceStatus = Constants.NO_RESOURCES;
        String releaseDate = "";

        if (!html.isEmpty()) {
            //1.使用 parse() 将 html 解析为 document 对象
            Document document = Jsoup.parse(html);

            Elements body = document.getElementsByTag("body");
            Elements section = Objects.requireNonNull(body.first()).getElementsByClass("section");
            Elements container = Objects.requireNonNull(section.first()).getElementsByClass("container");
            Elements movieList = Objects.requireNonNull(container.first()).getElementsByClass("movie-list");

            if (!movieList.isEmpty()) {
                Elements movieListChildren = Objects.requireNonNull(movieList.first()).children();

                for (Element item : movieListChildren) {
                    Elements videoTitle = item.getElementsByClass("video-title");
                    Elements uidCode = Objects.requireNonNull(videoTitle.first()).getElementsByTag("strong");

                    // 获取影片的 uid
                    String uid = Objects.requireNonNull(uidCode.first()).text();
                    // 获取影片的 发布日期
                    Elements meta = item.getElementsByClass("meta");

                    if (!uid.isEmpty()) {
                        // 判断当前选中的 Element 的 uid 是不是正是我们要找的
                        if (javCode.equalsIgnoreCase(uid)) {
                            releaseDate = meta.text().trim();

                            // 获取影片的附加信息
                            Elements addons = item.getElementsByClass("has-addons");

                            if (!addons.isEmpty()) {
                                Elements isDownloadableSpan = Objects.requireNonNull(addons.first()).getElementsByClass(Constants.DOWNLOADABLE_SPAN_TAG);
                                if (!isDownloadableSpan.isEmpty()) {
                                    resourceStatus = Constants.HAS_DOWNLOAD_RESOURCES;
                                    break;
                                }

                                Elements isChineseSubtitlesSpan = Objects.requireNonNull(addons.first()).getElementsByClass(Constants.CHINESE_SUBTITLES_SPAN_TAG);
                                if (!isChineseSubtitlesSpan.isEmpty()) {
                                    resourceStatus = Constants.HAS_CHINESE_SUBTITLES_RESOURCES;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        javCodeMap.put(Constants.RESOURCE_STATUS, resourceStatus);
        javCodeMap.put(Constants.RELEASE_DATE, releaseDate);

        return javCodeMap;
    }

    /**
     * 将日期格式转换为 JAVDB header 里需要的特定格式
     *
     * @param offset - 偏移量, 用于计算 expires 时间, 如果只要转换当前时间的话, offset 设为 0 即可. 单位: d
     */
    private String getFormatDate(long offset) {
        Constants.JAVDB_DF.setTimeZone(TimeZone.getTimeZone("GMT"));

        return Constants.JAVDB_DF.format(new Date(new Date().getTime() + offset * 24 * 60 * 60 * 1000));
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