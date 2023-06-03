package com.snake.smarttools.constant;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

@Component
@Slf4j
public class Constant {
    /******************************************************* Porntools start *******************************************************/
    public static final String JAV_CODE = "jav_code";
    public static final String EXPIRES_DATE_FORMAT = "expires_date_format";

    public static final String JAVDB_URL = "https://javdb.com/search?q=jav_code&f=all";

    public static final DateFormat JAVDB_DF = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);

    public static JSONObject JAVDB_HEADERS = new JSONObject();

    public static SimpleClientHttpRequestFactory JAVDB_REQUEST_FACTORY = new SimpleClientHttpRequestFactory();

    // 每轮 Http 请求间隔固定休息的时间, 单位: s
    public static final int HTTP_REQUEST_SLEEP_TIME = 5;
    // 每轮 Http 请求间隔最短休息的时间, 单位: s
    public static final int HTTP_REQUEST_MIN_SLEEP_TIME = 3;
    // 每轮 Http 请求间隔随机休息的时间, 单位: s
    public static final int HTTP_REQUEST_RANDOM_SLEEP_TIME = 3;

    // 重复的 Jav Code 列表
    public static final String DUPLICATE_JAV_CODE_LIST = "duplicateJavCodeList";
    // 可下载的 Jav Code 列表
    public static final String DOWNLOADABLE_JAV_CODE_LIST = "downloadableJavCodeList";
    // 带中文字幕的 Jav Code 列表
    public static final String CHINESE_SUBTITLES_JAV_CODE_LIST = "chineseSubtitlesJavCodeList";

    // 影片的 <资源状态>
    public static final String RESOURCE_STATUS = "resourceStatus";
    // 影片的 <发布日期>
    public static final String RELEASE_DATE = "releaseDate";

    /******************************************************* Porntools end *******************************************************/

    @PostConstruct // 构造函数之后执行
    public void init() {
//        JAVDB_HEADERS.put(":authority", "javdb.com");
//        JAVDB_HEADERS.put(":method", "GET");
//        JAVDB_HEADERS.put(":scheme", "https");
//        JAVDB_HEADERS.put("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7");
//        JAVDB_HEADERS.put("accept-encoding", "gzip, deflate, br");
//        JAVDB_HEADERS.put("accept-language", "zh-CN,zh;q=0.9,zh-Hant;q=0.8,en;q=0.7,und;q=0.6");
//        JAVDB_HEADERS.put("cookie", "list_mode=h; theme=auto; locale=zh; over18=1; _jdb_session=YtU%2Brq6Fl4EoPeoLbwd4eDZDMljQL%2BxoqzfY1hrCvEtWT4l1QrprjKBnWDVaixXnpcAJlpVlj7Kkp87h8sL15MJFLeVEtrYgJ%2FI9QZjw3JKeS0%2BG9Qc03Ti0nBa%2BN%2BMAs6dZWscsy%2FUfcfJMyutw5OYbYHqbGx4uKT3qsXX47xCYrKNpEEBtV1xYn3b5q4KlrNzkJQ7qyTKMHWbawvCjR1PSTtTxjxKbkjwk%2FXbYkyy%2B2N0D5ywSSUDUxFmnFJT1KbA8MiZpJkJsU6vFFbvPdJ%2FqzYK5%2F0pE9a66YRipu4nmWpGvQx7L99va--Ve9VdWtEqwmLW5mD--5gmvR1D6ou%2FMiovLS%2F4V7A%3D%3D; __cf_bm=.m7UBIDxRvEMUL.4vtsaOKD.a0SYEmbdZm0XPTzpkgc-1682156551-0-AR38uLN0zoI1cR3/boRkzlYd+eStaW3SlCqU2Q9Vzdi7n1++zoNlVI4FI5M8Djqa6dfuGAHRwmDC/vNcwanykdviyeARJsQLOjEn0vk+0XjZ");
//        JAVDB_HEADERS.put("dnt", "1");
//        JAVDB_HEADERS.put("referer", "https://javdb.com/");
//        JAVDB_HEADERS.put("sec-ch-ua-mobile", "?0");
//        JAVDB_HEADERS.put("sec-ch-ua-platform", "windows");
//        JAVDB_HEADERS.put("sec-fetch-dest", "document");
//        JAVDB_HEADERS.put("sec-fetch-mode", "navigate");
//        JAVDB_HEADERS.put("sec-fetch-site", "none");
//        JAVDB_HEADERS.put("sec-fetch-user", "?1");
//        JAVDB_HEADERS.put("upgrade-insecure-requests", "1");
        JAVDB_HEADERS.put("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Safari/537.36");

        JAVDB_REQUEST_FACTORY.setConnectTimeout(5000);
        JAVDB_REQUEST_FACTORY.setReadTimeout(5000);
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 7890));
        JAVDB_REQUEST_FACTORY.setProxy(proxy);

        log.info("初始化 Constant 完毕...");
    }
}
