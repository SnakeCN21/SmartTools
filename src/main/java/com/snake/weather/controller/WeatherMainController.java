package com.snake.weather.controller;

import com.google.gson.*;
import com.snake.utils.Constants;
import com.snake.utils.HttpClientUtils;
import com.snake.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class WeatherMainController {
    private static final Logger logger = LoggerFactory.getLogger(WeatherMainController.class);

    private static final Utils utils = new Utils();

    private String sendHttpRequest(String url) {
        String html = "";

        String cityName = utils.getPropValue("city_name");
        String weatherKey = utils.getPropValue("weather_key");

        url = url.replace(Constants.CITY_NAME, cityName);
        url = url.replace(Constants.WEATHER_KEY, weatherKey);

        Map<String, String> htmlMap = new HttpClientUtils().sendHttpRequest(Constants.REQUEST_TYPE_GET, url, new HashMap<>(), new HashMap<>());

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

        String html = sendHttpRequest(Constants.WEATHER_QUERY_URL);

        Gson gson = new Gson();

        JsonElement element = gson.fromJson(html, JsonElement.class);
        JsonObject weatherObj = element.getAsJsonObject();

        logger.info("WeatherMainController.weatherCheck() 总用时: " + utils.calculatingTimeDiff(System.nanoTime() - startTime));

        logger.info("WeatherMainController.weatherCheck() 执行完毕.");
    }

    public void lifeLevelCheck() {
        logger.info("开始执行 WeatherMainController.lifeLevelCheck()...");
        long startTime = System.nanoTime();

        String html = sendHttpRequest(Constants.WEATHER_QUERY_URL);

        Gson gson = new Gson();

        JsonElement element = gson.fromJson(html, JsonElement.class);
        JsonObject lifeLevelObj = element.getAsJsonObject();

        logger.info("WeatherMainController.lifeLevelCheck() 总用时: " + utils.calculatingTimeDiff(System.nanoTime() - startTime));

        logger.info("WeatherMainController.lifeLevelCheck() 执行完毕.");
    }

    public static void main(String[] args) {
        logger.info("开始执行 WeatherMainController...");

        WeatherMainController weatherMainController = new WeatherMainController();

        weatherMainController.weatherCheck();
    }

}
