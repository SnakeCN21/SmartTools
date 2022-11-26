package com.snake.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Constants {
    public static final String PROP_FILE_NAME = "src/main/resources/config.properties";

    public static final String FILE_SEPARATOR = "/";
    public static final String DOT = ".";
    public static final String COMMA = ",";
    public static final String SEMICOLON = ";";

    public static final String CHARSET_UTF_8 = "UTF-8";
    public static final String CHARSET_GBK = "GBK";

    public static final String SPACE_SEPARATOR = " ";

    public static final String UNDERSCORE = "_";
    public static final String HYPHEN = "-";
    public static final String SPACE_AND_HYPHEN = " - ";

    public static final String LEFT_PARENTHESES = "(";
    public static final String RIGHT_PARENTHESES = ")";

    public static final String CARRIAGE_RETURN_TO_LINE = "\r\n";

    public static final String FEATURE_ID = "id";
    public static final String FEATURE_Y = "y";
    public static final String FEATURE_X = "x";

    public static final String ROLE_GUEST = "Guest";
    public static final String ROLE_HOST = "Host";

    public static final String REQUEST_TYPE_GET = "get";
    public static final String REQUEST_TYPE_POST = "post";

    public static final int HTTP_TIMEOUT = 60 * 1000;

    public static final String HTTP_HEADER_USER_AGENT = "User-Agent";

    public static final String MESSAGE = "message";
    public static final String RESPONSE_CODE = "responseCode";
    public static final String HTML = "html";

    public static final String HTTP_RESPONSE_CODE_000 = "000";
    public static final String HTTP_RESPONSE_CODE_200 = "200";
    public static final String HTTP_RESPONSE_CODE_400 = "400";
    public static final String HTTP_RESPONSE_CODE_404 = "404";

    /******************************************************* Porntools start *******************************************************/
    public static final String JAV_CODE = "jav_code";
    public static final String EXPIRES_DATE_FORMAT = "expires_date_format";

    public static final String JAVDB_URL = "https://javdb.com/search?q=jav_code&f=all";

    public static final DateFormat JAVDB_DF = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);

//    public static final Map<String, String> JAVDB_HEADERS = new HashMap<String, String>() {{
//        put("content-type", "text/html; charset=utf-8");
//        put("date", "date_format");
//        put("set-cookie", "_jdb_session=gqVNYpjlksg86FrqeOXyypjF84FCsvYNBLyQPH5dvVSynvOrR49%2B574TxqI%2BUgHKBfS4f8zCtWtPjZzoFxjcG68oYZYqqVcDWw45hIZDZXQcfK7qQLyqt9qkpmbYW55C6%2BSij7j2vw62cm4us62%2BEsfDjNaPOV6jP1H3kKtk%2FGsmbPO5sm1A9bJEzn2XtlnWVKxIj8YZFWEkFQ042o7BL7Eky6q%2BMVmsZfJRsmlFsUTYzOmjwQ0crDC3euNAn6woiuygoPCwzz9qoEGeAcSjlKzhzRt6xjnbgTQ7P3m8bkm%2F4wOU6LJWwd5vEOqNGZQClqkky7MJsRfuuKeN8kMwOioG%2BqEKKnzTvTSKw748YwgqGJYq9sBUE0bVtEz89MnvnBo%3D--P%2BNCLNmugrBc1KYB--WtvJQ6ZWRLE4sY%2BUeufK7Q%3D%3D; path=/; expires=expires_date_format; secure; HttpOnly; SameSite=None");
//    }};

    public static final Map<String, String> JAVDB_HEADERS = new HashMap<String, String>() {{
        put("cache-control", "max-age=0");
//        put("cookie", "list_mode=h; theme=auto; locale=zh; over18=1; _ym_uid=1660399501534145376; _ym_d=1660399501; _ym_isad=1; __cf_bm=CW1BChFaQc1jYiT_2TbqRmhdkndELL9sybh38W8QHEU-1669391086-0-AY6iccnS5GXo4FdjMeVUl3ZQi+GNTur3/WUMikQqhF1BdClh0ZN0qUGfhn8VaH3Xclb+BZETjWzouUi2F9bS1Vy7QKrxwVaBKg5PBtEok1Oc1ABk322kYvwYuWIb/7lcetEVH+SzW368sESwHbQ6RXQ=; _jdb_session=hZHfKV%2FgZWUNCqSta53sUoxKCWLWgI9z3qD7geCQgrscAMVFHiZAmndIfwHktZpR22EhAxkYExYoihMzigfsOasl2UMcZVwIQ0TYH3q3K7zoEikPp1AukVB4O32F03WUlC8PVQil3mUg%2B8flv4RhvRpgA0O8TBehl5b0jaTQD3azqSJeXiKkiOwCVtS%2BIHR9okfJBoXXEAZSw57rPdY8aS8SKPhjPKkSp4qqviYJnJoLSni0L%2Byoe5Z%2F7oxDZK0pMSeWe8HYlrw8%2BDlwGm7GugTCohfM0NDBeK1B9aZ4Ltt6PE4C%2FKdYOUmRUbLINV3gQK8a6aOw8lUMLg2SSAbbpsdtJYLcslikeg3wegm5rvY1hPYeeMEaBwiT%2FAHIzjKet%2FI%3D--%2Bzyja9t1I2Z5g59y--U%2F%2BPFzG3Y%2B2ZBfpV8M0zlw%3D%3D");
        put("dnt", "1");
        put("upgrade-insecure-requests", "1");
        put("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/106.0.0.0 Safari/537.36");
    }};

    // 每轮 Http 请求间隔固定休息的时间, 单位: s
    public static final int HTTP_REQUEST_SLEEP_TIME = 5;
    // 每轮 Http 请求间隔最短休息的时间, 单位: s
    public static final int HTTP_REQUEST_MIN_SLEEP_TIME = 3;
    // 每轮 Http 请求间隔随机休息的时间, 单位: s
    public static final int HTTP_REQUEST_RANDOM_SLEEP_TIME = 3;

    public static final String DUPLICATE_JAV_CODE_LIST = "duplicateJavCodeList";
    public static final String DOWNLOADABLE_JAV_CODE_LIST = "downloadableJavCodeList";
    public static final String CHINESE_SUBTITLES_JAV_CODE_LIST = "chineseSubtitlesJavCodeList";

    // 判断当前影片的 <资源状态>
    // 含中字磁鏈: is-warning
    // 含磁鏈: is-success
    // 今日新種: is-info
    public static final String CHINESE_SUBTITLES_SPAN_TAG = "is-warning";
    public static final String DOWNLOADABLE_SPAN_TAG = "is-success";

    // 影片的 <资源状态>
    public static final String RESOURCE_STATUS = "resourceStatus";
    // 影片的 <发布日期>
    public static final String RELEASE_DATE = "releaseDate";

    // 0: 没有磁链; 1: 有磁链; 2: 有中文磁链
    public static final String NO_RESOURCES = "0";
    public static final String HAS_DOWNLOAD_RESOURCES = "1";
    public static final String HAS_CHINESE_SUBTITLES_RESOURCES = "2";
    /******************************************************* Porntools end *******************************************************/

    /******************************************************* Weather start *******************************************************/
    public static final String CITY_NAME = "city_name";
    public static final String WEATHER_KEY = "weather_key";

    public static final String WEATHER_QUERY_URL = "http://apis.juhe.cn/simpleWeather/query?city=city_name&key=weather_key";

    /******************************************************* Weather end *******************************************************/

}
