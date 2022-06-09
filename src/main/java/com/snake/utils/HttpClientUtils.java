package com.snake.utils;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HttpClientUtils {
    private static final Logger logger = LoggerFactory.getLogger(HttpClientUtils.class);

    //采用建造者模式，通过 Builder 可以设置连接超时时间，读写时间. 通过延长时间，可避免java.net.SocketTimeoutException: timeout
    private OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
            .connectTimeout(300, TimeUnit.SECONDS)
            .readTimeout(300, TimeUnit.SECONDS)
            .writeTimeout(300, TimeUnit.SECONDS)
            .build();
    private String resourceStatus;

    /**
     * 请求 Http Request
     *
     * @param url     - 需要搜索的网址
     * @param headers - request headers
     *
     * @return Map<String, String> - key: responseCode - HTTP状态码; key: html - Http 返回的 html 页面
     */
    public Map<String, String> getHTML(String url, Map<String, String> headers) {
        Map<String, String> map = new HashMap<String, String>();

        String responseCode = Constants.HTTP_RESPONSE_CODE_200;
        String html = "";

        Response response = null;

        try {
            Request.Builder builder = new Request.Builder().url(url);

            if (!headers.isEmpty()) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    //System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());

                    builder.addHeader(entry.getKey(), entry.getValue());
                }
            }

            Request request = builder.build();

            Call call = okHttpClient.newCall(request);

            response = call.execute();

            if (!response.isSuccessful()) {
                logger.info("HTTP 请求失败. URL = " + url);

                responseCode = Integer.toString(response.code());
            } else {
                html = response.body().string();
            }
        } catch (IOException e) {
            logger.debug("HTTP 请求失败. URL = " + url, e);
        } finally {
            if (response != null) {
                response.close();
            }
        }

        map.put(Constants.RESPONSE_CODE, responseCode);
        map.put(Constants.HTML, html);

        return map;
    }

    /**
     * 解析 Http 返回的 html 页面, 判断当前 JavCode 的状态
     *
     * @param javCode - 需要检测的 JavCode
     * @param html    - Http 返回的 html 页面
     *
     * @return Map    - key: resourceStatus - 影片的 <资源状态>; key: releaseDate - 影片的 <发布日期>
     */
    public Map<String, String> parseJavDBHTML(String javCode, String html) {
        Map<String, String> javCodeMap = new HashMap<String, String>();

        String resourceStatus = Constants.NO_RESOURCES;
        String releaseDate = "";

        if (!html.isEmpty()) {
            //1.使用 parse() 将 html 解析为 document 对象
            Document document = Jsoup.parse(html);

            Elements body = document.getElementsByTag("body");
            Elements section = body.first().getElementsByClass("section");
            Elements container = section.first().getElementsByClass("container");
            Elements movieList = container.first().getElementsByClass("movie-list");

            if (!movieList.isEmpty()) {
                Elements movieListChildren = movieList.first().children();

                for (Element item : movieListChildren) {
                    Elements videoTitle = item.getElementsByClass("video-title");
                    Elements uidCode = videoTitle.first().getElementsByTag("strong");

                    // 获取影片的 uid
                    String uid = uidCode.first().text();
                    // 获取影片的 发布日期
                    Elements meta = item.getElementsByClass("meta");

                    if (!uid.isEmpty()) {
                        // 判断当前选中的 Element 的 uid 是不是正是我们要找的
                        if (javCode.equalsIgnoreCase(uid)) {
                            releaseDate = meta.text().trim();

                            // 获取影片的附加信息
                            Elements addons = item.getElementsByClass("has-addons");

                            if (!addons.isEmpty()) {
                                Elements isDownloadableSpan = addons.first().getElementsByClass(Constants.DOWNLOADABLE_SPAN_TAG);
                                if (!isDownloadableSpan.isEmpty()) {
                                    resourceStatus = Constants.HAS_DOWNLOAD_RESOURCES;
                                    break;
                                }

                                Elements isChineseSubtitlesSpan = addons.first().getElementsByClass(Constants.CHINESE_SUBTITLES_SPAN_TAG);
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

}
