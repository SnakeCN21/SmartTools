package com.snake.smarttools.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.io.file.FileWriter;
import com.alibaba.fastjson.JSONObject;
import com.snake.smarttools.constant.Constant;
import com.snake.smarttools.constant.enums.JavDBResourceStatusEnum;
import com.snake.smarttools.constant.enums.JavDBSpanTagEnum;
import com.snake.smarttools.constant.enums.SpecialCharacterEnum;
import com.snake.smarttools.utils.Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
@Slf4j
public class PorntoolsService {
    @Autowired
    private Util util;

    @Value("${constants.jav_code_read_file}")
    private String javCodeReadFile;
    @Value("${constants.jav_code_write_file}")
    private String javCodeWriteFile;
    @Value("${constants.release_date_filter}")
    private String releaseDateFilter;

    /**
     * 读取并解析 javCodeReadFile 中的内容
     *
     * @return List<String> - 返回一个只包含 JavCode 的 List
     */
    public List<String> getJavCodeList() {
        List<String> javCodeList = new ArrayList<>();
        FileReader fileReader = new FileReader(javCodeReadFile);

        List<String> lines = fileReader.readLines();
        String lineStr = null;

        String[] temp = null;
        String javCode = null;
        String[] temp1 = null;

        for (int i = 0; i < lines.size(); i++) {
            lineStr = lines.get(i);
            if (StringUtils.isNotBlank(lineStr)) {
                try {
                    temp = util.split(lineStr, SpecialCharacterEnum.SPACE_SEPARATOR);
                    javCode = temp[0];

                    if (util.contains(javCode, SpecialCharacterEnum.UNDERSCORE) || util.contains(javCode, SpecialCharacterEnum.HYPHEN)) {
                        if (util.contains(javCode, SpecialCharacterEnum.LEFT_PARENTHESES) || util.contains(javCode, SpecialCharacterEnum.RIGHT_PARENTHESES)) {
                            javCode = util.replace(javCode, SpecialCharacterEnum.LEFT_PARENTHESES, SpecialCharacterEnum.SPACE_SEPARATOR);
                            javCode = util.replace(javCode, SpecialCharacterEnum.RIGHT_PARENTHESES, SpecialCharacterEnum.SPACE_SEPARATOR);

                            temp1 = util.split(javCode, SpecialCharacterEnum.SPACE_SEPARATOR);

                            javCodeList.add(temp1[0].toUpperCase(Locale.ENGLISH));
                            javCodeList.add(temp1[1].toUpperCase(Locale.ENGLISH));
                        } else {
                            javCodeList.add(javCode.toUpperCase(Locale.ENGLISH));
                        }
                    }
                } catch (Exception e) {
                    log.debug(e.getMessage() + ", Line Number: " + i, e);
                }
            }
        }

        return javCodeList;
    }

    /**
     * 根据 javCodeList 替换预设好的 JAVDB_URL
     *
     * @param javCodeList - 包含 JavCode 的 List
     * @return List - 返回组装完成待请求的 urlList
     */
    public List<String> assembleJavCodeUrlList(List<String> javCodeList) {
        List<String> javCodeUrlList = new ArrayList<>();

        if (CollectionUtil.isNotEmpty(javCodeList)) {
            String url;
            for (String javCode : javCodeList) {
                url = Constant.JAVDB_URL.replace(Constant.JAV_CODE, javCode);
                javCodeUrlList.add(url);
            }
        }

        return javCodeUrlList;
    }

    /**
     * 根据 javCodeUrlList 依次进行 Http 请求
     *
     * @param javCodeUrlList - 待请求的 urlList
     * @return List - 返回结果 List
     */
    public List<String> getHttpRespList(List<String> javCodeUrlList) {
        List<String> httpRespList = new ArrayList<>();

        String retBody = null;
        if (CollectionUtil.isNotEmpty(javCodeUrlList)) {
            for (String url : javCodeUrlList) {
                retBody = util.httpGet(MediaType.APPLICATION_XHTML_XML, MediaType.TEXT_HTML, url, Constant.JAVDB_HEADERS, null, Constant.JAVDB_REQUEST_FACTORY);
                if (retBody != null && !retBody.isEmpty()) {
                    httpRespList.add(retBody);
                }

                // 加入延迟设定, 避免请求过于频繁
                try {
                    Thread.sleep((new Random().nextInt(Constant.HTTP_REQUEST_RANDOM_SLEEP_TIME) + Constant.HTTP_REQUEST_MIN_SLEEP_TIME) * 1000);
                } catch (InterruptedException e) {
                    log.debug(e.getMessage(), e);
                }
            }
        }

        return httpRespList;
    }

    /**
     * 根据 javCodeList 依次解析里面的每一个 httpResp, 获取其真实的状态
     *
     * @param javCodeList  - JavCodeList
     * @param httpRespList - http 返回 List
     * @return JSONObject  - key: resourceStatus-影片的 <资源状态>; key: releaseDate-影片的 <发布日期>
     */
    public JSONObject parseJavDBHTML(List<String> javCodeList, List<String> httpRespList) {
        JSONObject javCodeObj = new JSONObject();
        List<String> downloadableJavCodeList = new ArrayList<>();
        List<String> chineseSubtitlesJavCodeList = new ArrayList<>();

        JSONObject retObj = new JSONObject();
        for (int i = 0; i < javCodeList.size(); i++) {
            retObj = parseJavDBHTML(javCodeList.get(i), httpRespList.get(i));

            JavDBResourceStatusEnum resourceStatus = retObj.getObject(Constant.RESOURCE_STATUS, JavDBResourceStatusEnum.class);
            String releaseDate = retObj.getString(Constant.RELEASE_DATE);

            switch (resourceStatus) {
                case HAS_DOWNLOAD_RESOURCES:
                    downloadableJavCodeList.add(javCodeList.get(i) + SpecialCharacterEnum.SPACE_AND_HYPHEN.getCharacter() + releaseDate);
                    break;
                case HAS_CHINESE_SUBTITLES_RESOURCES:
                    chineseSubtitlesJavCodeList.add(javCodeList.get(i) + SpecialCharacterEnum.SPACE_AND_HYPHEN.getCharacter() + releaseDate);
                    break;
            }
        }

        javCodeObj.put(Constant.DOWNLOADABLE_JAV_CODE_LIST, downloadableJavCodeList);
        javCodeObj.put(Constant.CHINESE_SUBTITLES_JAV_CODE_LIST, chineseSubtitlesJavCodeList);
        return javCodeObj;
    }

    /**
     * 根据 javCode 解析 httpResp, 获取其真实的状态
     *
     * @param javCode  - JavCodeList
     * @param httpResp - http 返回 List
     * @return JSONObject   - resourceStatus-影片的 <资源状态>; releaseDate-影片的 <发布日期>
     */
    public JSONObject parseJavDBHTML(String javCode, String httpResp) {
        JSONObject retObj = new JSONObject();

        JavDBResourceStatusEnum resourceStatus = JavDBResourceStatusEnum.NO_RESOURCES;
        String releaseDate = "";

        if (!httpResp.isEmpty()) {
            //1.使用 parse() 将 httpResp 解析为 document 对象
            Document document = Jsoup.parse(httpResp);

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
                                Elements isChineseSubtitlesSpan = Objects.requireNonNull(addons.first()).getElementsByClass(JavDBSpanTagEnum.CHINESE_SUBTITLES_SPAN_TAG.getSpanTage());
                                if (!isChineseSubtitlesSpan.isEmpty()) {
                                    resourceStatus = JavDBResourceStatusEnum.HAS_CHINESE_SUBTITLES_RESOURCES;
                                    break;
                                }
                                Elements isDownloadableSpan = Objects.requireNonNull(addons.first()).getElementsByClass(JavDBSpanTagEnum.DOWNLOADABLE_SPAN_TAG.getSpanTage());
                                if (!isDownloadableSpan.isEmpty()) {
                                    resourceStatus = JavDBResourceStatusEnum.HAS_DOWNLOAD_RESOURCES;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        retObj.put(Constant.RESOURCE_STATUS, resourceStatus);
        retObj.put(Constant.RELEASE_DATE, releaseDate);
        return retObj;
    }

    /**
     * 将 javCodeObj 写入到结果文件中
     *
     * @param javCodeObj - 包含 3 个 List. duplicateJavCodeList-重复的 Jav Code 列表; downloadableJavCodeList-可下载的 Jav Code 列表; chineseSubtitlesJavCodeList-带中文字幕的 Jav Code 列表
     */
    public void setJavCodeList(JSONObject javCodeObj) {
        log.info("开始执行 PorntoolsService.setJavCodeList()...");
        long startTime = System.nanoTime();

        List<String> duplicateJavCodeList = util.collectionDeduplicateAndResort(javCodeObj.getObject(Constant.DUPLICATE_JAV_CODE_LIST, List.class));
        List<String> downloadableJavCodeList = util.collectionDeduplicateAndResort(javCodeObj.getObject(Constant.DOWNLOADABLE_JAV_CODE_LIST, List.class));
        List<String> chineseSubtitlesJavCodeList = util.collectionDeduplicateAndResort(javCodeObj.getObject(Constant.CHINESE_SUBTITLES_JAV_CODE_LIST, List.class));

        if (FileUtil.exist(javCodeWriteFile)) {
            FileUtil.del(javCodeWriteFile);
        }

        FileWriter writer = new FileWriter(javCodeWriteFile);
        if (CollectionUtil.isNotEmpty(duplicateJavCodeList)) {
            writer.write("以下 JavCode 重复:" + SpecialCharacterEnum.CARRIAGE_RETURN_TO_LINE.getCharacter());
            for (String javCode : duplicateJavCodeList) {
                writer.append(javCode + SpecialCharacterEnum.CARRIAGE_RETURN_TO_LINE.getCharacter());
            }
            writer.append(SpecialCharacterEnum.CARRIAGE_RETURN_TO_LINE.getCharacter());
        }
        if (CollectionUtil.isNotEmpty(chineseSubtitlesJavCodeList)) {
            writer.append("以下 JavCode 拥有中文磁链:" + SpecialCharacterEnum.CARRIAGE_RETURN_TO_LINE.getCharacter());
            for (String javCodeAndReleaseDate : chineseSubtitlesJavCodeList) {
                String[] str = util.split(javCodeAndReleaseDate, SpecialCharacterEnum.SPACE_AND_HYPHEN);
                String javCode = str[0];
                String releaseDate = str[1];

                writer.append(javCode + SpecialCharacterEnum.SPACE_AND_HYPHEN.getCharacter() + releaseDate + SpecialCharacterEnum.CARRIAGE_RETURN_TO_LINE.getCharacter());
            }
            writer.append(SpecialCharacterEnum.CARRIAGE_RETURN_TO_LINE.getCharacter());
        }
        if (CollectionUtil.isNotEmpty(downloadableJavCodeList)) {
            writer.append("以下 JavCode 拥有磁链:" + SpecialCharacterEnum.CARRIAGE_RETURN_TO_LINE.getCharacter());
            for (String javCodeAndReleaseDate : downloadableJavCodeList) {
                String[] str = util.split(javCodeAndReleaseDate, SpecialCharacterEnum.SPACE_AND_HYPHEN);
                String javCode = str[0];
                String releaseDate = str[1];

                if (isReleaseDateOverFilter(releaseDate) < 0) {
                    writer.append(javCode + SpecialCharacterEnum.SPACE_AND_HYPHEN.getCharacter() + releaseDate + SpecialCharacterEnum.CARRIAGE_RETURN_TO_LINE.getCharacter());
                } else {
                    writer.append(javCode + SpecialCharacterEnum.CARRIAGE_RETURN_TO_LINE.getCharacter());
                }
            }
        }

        log.info("\n********************************************************************\n" + "文件 " + javCodeWriteFile + " 写入完毕.\n" + "共整理了 " + chineseSubtitlesJavCodeList.size() + " 个拥有中文磁链的 JavCode, 以及 " + downloadableJavCodeList.size() + " 个拥有磁链的 JavCode.\n" + "PorntoolsService.setJavCodeList() 总用时: " + util.calculatingTimeDiff(System.nanoTime() - startTime) + "\n" + "********************************************************************");

        log.info("PorntoolsService.setJavCodeList() 执行完毕.");
    }

    /**
     * 判断 JavCode 的 Release Date 是否已经超出了预设的 filter
     *
     * @param date - JavCode 的 Release Date
     * @return int: -1 - releaseDate + filter < now; 1 - releaseDate + filter > now
     */
    public int isReleaseDateOverFilter(String date) {
        LocalDate releaseDate = LocalDate.parse(date);
        String[] str = util.split(releaseDateFilter, SpecialCharacterEnum.HYPHEN);

        for (String filter : str) {
            filter = filter.toUpperCase();
            if (filter.contains("Y")) {
                long num = Long.parseLong(filter.substring(0, filter.indexOf("Y")));
                releaseDate = releaseDate.plusYears(num);
            } else if (filter.contains("M")) {
                long num = Long.parseLong(filter.substring(0, filter.indexOf("M")));
                releaseDate = releaseDate.plusMonths(num);
            } else if (filter.contains("D")) {
                long num = Long.parseLong(filter.substring(0, filter.indexOf("D")));
                releaseDate = releaseDate.plusDays(num);
            }
        }

        return releaseDate.compareTo(LocalDate.now());
    }

}
