package com.snake.weather.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.snake.utils.Constants;
import com.snake.utils.HttpClientUtils;
import com.snake.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class WeatherMainController {
    private static final Logger logger = LoggerFactory.getLogger(WeatherMainController.class);

    private String sendRequest(String url) {
        String html = "";

        String cityName = new Utils().getPropValue("city_name");
        String weatherKey = new Utils().getPropValue("weather_key");

        url = url.replace(Constants.CITY_NAME, cityName);
        url = url.replace(Constants.WEATHER_KEY, weatherKey);

        Map<String, String> htmlMap = new HttpClientUtils().getHTML(url, new HashMap<>(), new HashMap<>());

        String responseCode = htmlMap.get(Constants.RESPONSE_CODE);

        if (responseCode.equalsIgnoreCase(Constants.HTTP_RESPONSE_CODE_200)) {
            html = htmlMap.get(Constants.HTML);
        } else {
            logger.info("查询失败: " + url);
        }

        return html;
    }

    public void weatherCheck() {
        logger.info("开始执行 WeatherMainController.weatherCheck()...");
        long startTime = System.nanoTime();

        String html = sendRequest(Constants.WEATHER_QUERY_URL);

        JsonObject weatherObj = new JsonParser().parse(html).getAsJsonObject();

        logger.info("WeatherMainController.weatherCheck() 总用时: " + new Utils().calculatingTimeDiff(System.nanoTime() - startTime));

        logger.info("WeatherMainController.weatherCheck() 执行完毕.");
    }

    public void lifeLevelCheck() {
        logger.info("开始执行 WeatherMainController.lifeLevelCheck()...");
        long startTime = System.nanoTime();

        String html = sendRequest(Constants.WEATHER_QUERY_URL);

        JsonObject lifeLevelObj = new JsonParser().parse(html).getAsJsonObject();

        logger.info("WeatherMainController.lifeLevelCheck() 总用时: " + new Utils().calculatingTimeDiff(System.nanoTime() - startTime));

        logger.info("WeatherMainController.lifeLevelCheck() 执行完毕.");
    }

    public static void main(String[] args) {
        logger.info("开始执行 WeatherMainController...");

        WeatherMainController weatherMainController = new WeatherMainController();

        weatherMainController.weatherCheck();
    }
}
