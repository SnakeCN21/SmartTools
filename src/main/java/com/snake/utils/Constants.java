package com.snake.utils;

import java.util.HashMap;
import java.util.Map;

public class Constants {
    public static final String PROP_FILE_NAME = "src/main/resources/config.properties";

    public static final String FILE_SEPARATOR = "/";
    public static final String DOT = ".";

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

    public static final String MESSAGE = "message";
    public static final String RESPONSE_CODE = "responseCode";
    public static final String HTML = "html";

    public static final String HTTP_RESPONSE_CODE_000 = "000";
    public static final String HTTP_RESPONSE_CODE_200 = "200";
    public static final String HTTP_RESPONSE_CODE_400 = "400";
    public static final String HTTP_RESPONSE_CODE_404 = "404";

    /******************************************************* Porntools start *******************************************************/
    public static final String JAV_CODE = "jav_code";

    public static final String JAVDB_URL = "https://javdb.com/search?q=jav_code&f=all";

    public static final Map<String, String> JAVDB_HEADERS = new HashMap<String, String>() {{
        put("content-type", "text/html; charset=utf-8");
        put("accept-language", "zh-CN,zh;q=0.9");
        put("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.113 Safari/537.36");
        put("cookie", "theme=auto; locale=zh; over18=1; _jdb_session=k1TjZv98k7ehpdrbWgYsaTDq%2FFV4TDV60caoU5kqJ150hY9N%2BmKHwPZUzIKVcFyhES%2Fg2rR8%2FslXMnPiBmUwriHOGkVuZzaUK%2BJ4uL93npGPYUdqbKSx1OnccElFEZlXgMUWQ%2Bh6L%2F85QTPBFIKDheUGRoHPIKdrDOb9qyw9Qo91gASNOYA7o7Q4WSNij0xBsVTdUAdlb2UAZrUMDgKl2i%2BHuZI7ZTH6pa0i501zS%2BL4b3pecQTBCC0KUvt%2BrbrKA6lUyAcC%2BHvUlzCqJh7y0PlqbNIW1LIfszSlXvBARFqTdKHbLLuLFvglreen4TWEdkzmv0gdmDbwQDDdjJlYInMGFFoMnetgW7Q7OzetMPCqQLvW82M9ykPE7S3hVFBtSOY%3D--dz5k%2Fq1Mp2WC1Lx8--%2FsuF1iV0bxV7XUfPkivIPg%3D%3D");
    }};

    // 每轮 Http 请求间隔固定休息的时间, 单位: s
    public static final int HTTP_REQUEST_SLEEP_TIME = 5;
    // 每轮 Http 请求间隔最短休息的时间, 单位: s
    public static final int HTTP_REQUEST_MIN_SLEEP_TIME = 2;
    // 每轮 Http 请求间隔随机休息的时间, 单位: s
    public static final int HTTP_REQUEST_RANDOM_SLEEP_TIME = 5;

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
