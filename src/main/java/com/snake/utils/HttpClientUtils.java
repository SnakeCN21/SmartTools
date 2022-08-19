package com.snake.utils;


import jodd.http.HttpRequest;
import jodd.http.HttpResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class HttpClientUtils {
    private static final Logger logger = LoggerFactory.getLogger(HttpClientUtils.class);

    /**
     * 请求 Http Request
     *
     * @param requestType - Http 请求类型: get or post
     * @param url         - 需要搜索的网址
     * @param headers     - request headers
     * @param formBody    - request formBody
     * @return 请求相应的内容
     */
    public Map<String, String> sendHttpRequest(String requestType, String url, Map<String, String> headers, Map<String, Object> formBody) {
        Map<String, String> map = new HashMap<String, String>();

        HttpRequest request = null;
        HttpResponse response = null;

        if (Constants.REQUEST_TYPE_GET.equalsIgnoreCase(requestType)) {
            request = HttpRequest.get(url);
        } else {
            request = HttpRequest.post(url);
        }

        request.connectionTimeout(Constants.HTTP_TIMEOUT);
        request.timeout(Constants.HTTP_TIMEOUT);

        if (!headers.isEmpty()) {
            request.header(headers);
        }

        if (formBody.isEmpty()) {
            response = request.send();
        } else {
            response = request.form(formBody).send();
        }

        String responseCode = Integer.toString(response.statusCode());
        String html = response.bodyText();

        response.close();

        map.put(Constants.RESPONSE_CODE, responseCode);
        map.put(Constants.HTML, html);

        return map;
    }

    /**
     * 通过长链接的方式依次请求 urlList
     *
     * @param requestType - Http 请求类型: get or post
     * @param urlList     - 需要搜索的网址 List
     * @param headers     - request headers
     * @param formBody    - request formBody
     * @return
     */
    public List<Map<String, String>> sendHttpRequestList(String requestType, List<String> urlList, Map<String, String> headers, Map<String, Object> formBody) {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        Map<String, String> map = new HashMap<String, String>();

        HttpRequest request = null;
        HttpResponse response = null;

        String url = "";
        boolean isFirstConnection = true;

        String responseCode = "";
        String html = "";

        for (int i = 0; i < urlList.size(); i++) {
            url = urlList.get(i);

            html = "";
            responseCode = "";

            if (Constants.REQUEST_TYPE_GET.equalsIgnoreCase(requestType)) {
                request = HttpRequest.get(url);
            } else {
                request = HttpRequest.post(url);
            }

            request.connectionTimeout(Constants.HTTP_TIMEOUT);
            request.timeout(Constants.HTTP_TIMEOUT);

            if (!headers.isEmpty()) {
                request.header(headers);
            }

            if (urlList.size() != 1 && i == urlList.size() - 1) {
                if (formBody.isEmpty()) {
                    response = request.keepAlive(response, false).send();
                } else {
                    response = request.keepAlive(response, false).form(formBody).send();
                }
            } else {
                if (isFirstConnection) {
                    if (formBody.isEmpty()) {
                        response = request.connectionKeepAlive(true).send();
                    } else {
                        response = request.connectionKeepAlive(true).form(formBody).send();
                    }

                    isFirstConnection = false;
                } else {
                    if (formBody.isEmpty()) {
                        response = request.keepAlive(response, true).send();
                    } else {
                        response = request.keepAlive(response, true).form(formBody).send();
                    }
                }
            }

            responseCode = Integer.toString(response.statusCode());
            html = response.bodyText();

            map = new HashMap<String, String>();
            map.put(Constants.RESPONSE_CODE, responseCode);
            map.put(Constants.HTML, html);

            list.add(map);

            try {
                Thread.sleep((new Random().nextInt(Constants.HTTP_REQUEST_RANDOM_SLEEP_TIME) + Constants.HTTP_REQUEST_MIN_SLEEP_TIME) * 1000);
            } catch (InterruptedException e) {
                logger.debug(e.getMessage(), e);
            }
        }

        if (response != null) {
            response.close();
        }

        return list;
    }

}
