package com.snake.utils;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class HttpClientUtils {
    private static final Logger logger = LoggerFactory.getLogger(HttpClientUtils.class);

    /**
     * 请求 Http Request
     *
     * @param requestType - Http 请求类型: get or post
     * @param url         - 需要搜索的网址
     * @param headers     - request headers
     * @param formBody    - request formBody
     *
     * @return 请求相应的内容
     */
    public Map<String, String> sendHttpRequest(String requestType, String url, Map<String, String> headers, Map<String, Object> formBody) {
        Map<String, String> map = new HashMap<String, String>();

        String responseCode = Constants.HTTP_RESPONSE_CODE_200;
        String html = "";

        HttpRequest request = null;

        if (Constants.REQUEST_TYPE_GET.equalsIgnoreCase(requestType)) {
            request = HttpRequest.get(url);
        } else {
            request = HttpRequest.post(url);
        }
        request.timeout(300 * 1000);

        if (headers != null && !headers.isEmpty()) {
            request.removeHeader(Constants.HTTP_HEADER_USER_AGENT);

            request.addHeaders(headers);
        }
        if (formBody != null && !formBody.isEmpty()) {
            request.form(formBody);
        }

        HttpResponse response = request.execute();

        if (!response.isOk()) {
            logger.info("HTTP 请求失败. URL = " + url);

            responseCode = Integer.toString(response.getStatus());
        } else {
            html = response.body();
        }

        response.close();

        map.put(Constants.RESPONSE_CODE, responseCode);
        map.put(Constants.HTML, html);

        return map;
    }

}
