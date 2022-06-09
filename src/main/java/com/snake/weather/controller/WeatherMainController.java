package com.snake.weather.controller;

import com.snake.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class WeatherMainController {
    private static final Logger logger = LoggerFactory.getLogger(WeatherMainController.class);

    public void taskStart() {
        logger.info("开始执行 WeatherMainController.taskStart()...");
        long startTime = System.nanoTime();

        String cityName = new Utils().getPropValue("city_name");
        String key = new Utils().getPropValue("key");

        Map<String, String> map = new HashMap<String, String>(){{
            put("city", cityName);
            put("key", key);
        }};


    }

    public static void main(String[] args) {
        logger.info("开始执行 WeatherMainController...");

        WeatherMainController weatherMainController = new WeatherMainController();

        weatherMainController.taskStart();
    }
}
