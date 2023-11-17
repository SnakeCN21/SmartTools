package com.snake.smarttools.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.snake.smarttools.constant.enums.SpecialCharacterEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.Collator;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class Util {
    @Autowired
    @Qualifier("restTemplate")
    private RestTemplate restTemplate;

    /**
     * 向外部提交 Http GET 请求, Content-Type: application/json;charset=UTF-8; Accept: application/json, all
     *
     * @param url        - 请求的 url 地址
     * @param headersObj - Headers(选填)
     * @param bodyObj    - Body 体(选填)
     */
    public String httpGet(String url, JSONObject headersObj, JSONObject bodyObj) {
        return http(MediaType.APPLICATION_JSON_UTF8, null, HttpMethod.GET, url, headersObj, bodyObj, null);
    }

    /**
     * 向外部提交 Http GET 请求
     *
     * @param contentType    - Header Content-Type
     * @param acceptType     - Header Accept-Type(选填)
     * @param url            - 请求的 url 地址
     * @param headersObj     - Headers(选填)
     * @param bodyObj        - Body 体(选填)
     * @param requestFactory - SimpleClientHttpRequestFactory(选填)
     */
    @Retryable(value = RestClientException.class, maxAttempts = 3, // 最大重试次数
            backoff = @Backoff(delay = 5000L, multiplier = 2))
    public String httpGet(MediaType contentType, MediaType acceptType, String url, JSONObject headersObj, JSONObject bodyObj, SimpleClientHttpRequestFactory requestFactory) {
        return http(contentType, acceptType, HttpMethod.GET, url, headersObj, bodyObj, requestFactory);
    }

    /**
     * 向外部提交 Http POST 请求, Content-Type: application/json;charset=UTF-8; Accept: application/json, all
     *
     * @param url        - 请求的 url 地址
     * @param headersObj - Headers(选填)
     * @param bodyObj    - Body 体(选填)
     */
    public String httpPost(String url, JSONObject headersObj, JSONObject bodyObj) {
        return http(MediaType.APPLICATION_JSON_UTF8, null, HttpMethod.POST, url, headersObj, bodyObj, null);
    }

    /**
     * 向外部提交 Http POST 请求
     *
     * @param contentType    - Header Content-Type
     * @param acceptType     - Header Accept-Type(选填)
     * @param url            - 请求的 url 地址
     * @param headersObj     - Headers(选填)
     * @param bodyObj        - Body 体(选填)
     * @param requestFactory - SimpleClientHttpRequestFactory(选填)
     */
    @Retryable(value = RestClientException.class, maxAttempts = 3, // 最大重试次数
            backoff = @Backoff(delay = 5000L, multiplier = 2))
    public String httpPost(MediaType contentType, MediaType acceptType, String url, JSONObject headersObj, JSONObject bodyObj, SimpleClientHttpRequestFactory requestFactory) {
        return http(contentType, acceptType, HttpMethod.POST, url, headersObj, bodyObj, requestFactory);
    }

    /**
     * 向外部提交 Http 请求
     *
     * @param contentType    - Header Content-Type
     * @param acceptType     - Header Accept-Type(选填)
     * @param httpMethod     - Http 请求方式
     * @param url            - 请求的 url 地址
     * @param headersObj     - Headers(选填)
     * @param bodyObj        - Body 体(选填)
     * @param requestFactory - SimpleClientHttpRequestFactory
     */
    public String http(MediaType contentType, MediaType acceptType, HttpMethod httpMethod, String url, JSONObject headersObj, JSONObject bodyObj, SimpleClientHttpRequestFactory requestFactory) {
        ResponseEntity<String> retEntity = null;

        try {
            HttpHeaders headers = setHttpHeaders(contentType, acceptType, headersObj);
            HttpEntity httpEntity = setHttpEntity(httpMethod, url, headers, bodyObj);

            if (Objects.nonNull(requestFactory)) {
                restTemplate.setRequestFactory(requestFactory);
            }

            retEntity = restTemplate.exchange(url, httpMethod, httpEntity, String.class);
        } catch (RestClientException | UnsupportedEncodingException e) {
            log.error("请求接口失败! url = " + url, e);
            return null;
        }
        return retEntity.getBody();
    }

    /**
     * 组装 HttpHeaders
     *
     * @param contentType - Header Content-Type
     * @param acceptType  - Header Accept-Type(选填)
     * @param headersObj  - Headers(选填)
     */
    private HttpHeaders setHttpHeaders(MediaType contentType, MediaType acceptType, JSONObject headersObj) throws UnsupportedEncodingException {
        HttpHeaders headers = new HttpHeaders();
        if (Objects.nonNull(headersObj) && !headersObj.isEmpty()) { //组装 header
            for (Map.Entry<String, Object> entry : headersObj.entrySet()) {
                String key = URLEncoder.encode(entry.getKey(), "UTF-8");
                if (Objects.isNull(entry.getValue())) {
                    continue;
                }
                String value = URLEncoder.encode(entry.getValue().toString(), "UTF-8");
                headers.set(key, value);
            }
        }
        headers.setContentType(contentType);
        List acceptTypeList = new ArrayList<>();
        if (Objects.nonNull(acceptType)) {
            acceptTypeList.add(acceptType);
        }
        acceptTypeList.add(MediaType.APPLICATION_JSON);
        acceptTypeList.add(MediaType.ALL);
        headers.setAccept(acceptTypeList);

        return headers;
    }

    /**
     * 组装 HttpEntity
     *
     * @param httpMethod - Http 请求方式
     * @param url        - 请求的 url 地址
     * @param headers    - HttpHeaders
     * @param bodyObj    - Body 体(选填)
     */
    private HttpEntity setHttpEntity(HttpMethod httpMethod, String url, HttpHeaders headers, JSONObject bodyObj) throws UnsupportedEncodingException {
        HttpEntity httpEntity = null;
        if (httpMethod.equals(HttpMethod.GET)) { //GET 请求
            if (Objects.nonNull(bodyObj) && !bodyObj.isEmpty()) {
                url += "?";
                StringBuilder urlBuilder = new StringBuilder(url);
                for (Map.Entry<String, Object> entry : bodyObj.entrySet()) {
                    String key = URLEncoder.encode(entry.getKey(), "UTF-8");
                    if (Objects.isNull(entry.getValue())) {
                        continue;
                    }
                    String value = URLEncoder.encode(entry.getValue().toString(), "UTF-8");
                    //判断对象是否为数组
                    if (value.startsWith("[")) {
                        JSONArray jsonArray = JSON.parseArray(value);
                        StringBuilder valueBuild = new StringBuilder();
                        for (Object o : jsonArray) {
                            valueBuild.append(o).append("&");
                        }
                        value = URLEncoder.encode(valueBuild.substring(0, valueBuild.length() - 1), "UTF-8");
                    }
                    urlBuilder.append(key).append("=").append(value).append("&");
                }
            }
            httpEntity = new HttpEntity(null, headers);
        } else if (httpMethod.equals(HttpMethod.POST)) { //POST 请求
            if (Objects.nonNull(bodyObj)) {
                httpEntity = new HttpEntity(bodyObj, headers);
            } else {
                httpEntity = new HttpEntity(null, headers);
            }
        }
        return httpEntity;
    }

    /**
     * 将 str 按照 character 进行 split
     *
     * @param str       - 原字符串
     * @param character - SpecialCharacterEnum
     */
    public String[] split(String str, SpecialCharacterEnum character) {
        return str.split(character.getCharacter());
    }

    /**
     * 判断 str 是否包含 character
     *
     * @param str       - 原字符串
     * @param character - SpecialCharacterEnum
     */
    public Boolean contains(String str, SpecialCharacterEnum character) {
        return str.contains(character.getCharacter());
    }

    /**
     * 将 str 中 oldChar 替换为 newChar
     *
     * @param str     - 原字符串
     * @param oldChar - SpecialCharacterEnum
     * @param newChar - SpecialCharacterEnum
     */
    public String replace(String str, SpecialCharacterEnum oldChar, SpecialCharacterEnum newChar) {
        return str.replace(oldChar.getCharacter(), newChar.getCharacter());
    }

    /**
     * 将 long 类型的时间格式转换成可读性更高的时间格式
     * timeDiff 是基于 System.nanoTime() 计算出来的
     *
     * @param timeDiff - 需要转换的long类型时间
     */
    public String calculatingTimeDiff(long timeDiff) {
        final long day = TimeUnit.NANOSECONDS.toDays(timeDiff);
        final long hours = TimeUnit.NANOSECONDS.toHours(timeDiff) - TimeUnit.DAYS.toHours(TimeUnit.NANOSECONDS.toDays(timeDiff));
        final long minutes = TimeUnit.NANOSECONDS.toMinutes(timeDiff) - TimeUnit.HOURS.toMinutes(TimeUnit.NANOSECONDS.toHours(timeDiff));
        final long seconds = TimeUnit.NANOSECONDS.toSeconds(timeDiff) - TimeUnit.MINUTES.toSeconds(TimeUnit.NANOSECONDS.toMinutes(timeDiff));
        final long ms = TimeUnit.NANOSECONDS.toMillis(timeDiff) - TimeUnit.SECONDS.toMillis(TimeUnit.NANOSECONDS.toSeconds(timeDiff));

        StringBuilder sb = new StringBuilder(64);

        if (day != 0) {
            sb.append(day).append(" 天 ");
        }
        if (hours != 0) {
            sb.append(hours).append(" 小时 ");
        }
        if (minutes != 0) {
            sb.append(minutes).append(" 分钟 ");
        }
        if (seconds != 0) {
            sb.append(seconds).append(" 秒 ");
        }
        if (ms != 0) {
            sb.append(ms).append(" 毫秒");
        }

        return sb.toString();
    }

    /**
     * 找出 list 中重复的数据, 并将之放入到一个新的 List 中返回
     *
     * @param list - 需要进行查重的 List
     * @return <T> List - List 中重复的元素
     */
    public <T> List<T> findCollectionDuplicateElements(Collection<T> list) {
        if (list instanceof Set) {
            return new ArrayList<>();
        }

        HashSet<T> set = new HashSet<>();
        List<T> duplicateElements = new ArrayList<>();

        for (T t : list) {
            if (set.contains(t)) {
                duplicateElements.add(t);
            } else {
                set.add(t);
            }
        }

        return duplicateElements;
    }

    /**
     * 将传入的 list 去重并排序
     *
     * @param list - 需要被去重并排序的 list
     * @return <T> List - 去重并排序之后的 ArrayList
     */
    public <T> List<T> collectionDeduplicateAndResort(Collection<T> list) {
        if (list == null) {
            return new ArrayList<>();
        }

        List<T> sortedList;
        Set<T> set = new HashSet<>(list);
        sortedList = new ArrayList<>(set);

        sortedList.sort(Collator.getInstance());

        return sortedList;
    }

    /**
     * 将传入的 list 去重并排序
     *
     * @param list - 需要被去重并排序的 list
     * @return 去重并排序之后的 list
     */
    public List<String> resortList(List<String> list) {
        List<String> sortedList = new ArrayList<>();

        if (!list.isEmpty()) {
            LinkedHashSet<String> set = new LinkedHashSet<>(list.size());
            set.addAll(list);
            sortedList.addAll(set);

            sortedList.sort(Collator.getInstance());
        }

        return sortedList;
    }
}
