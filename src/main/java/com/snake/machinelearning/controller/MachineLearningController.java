package com.snake.machinelearning.controller;

import com.snake.machinelearning.utils.CSVUtils;
import com.snake.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MachineLearningController {
    private static final Logger logger = LoggerFactory.getLogger(MachineLearningController.class);

    public void splitDataSet() {
        logger.info("开始执行 MachineLearningController.splitDataSet()...");
        long startTime = System.nanoTime();

        new CSVUtils().taskStart();

        logger.info("MachineLearningController.splitDataSet() 总用时: " + new Utils().calculatingTimeDiff(System.nanoTime() - startTime));
        logger.info("MachineLearningController.splitDataSet() 执行完毕.");
    }

    public static void main(String[] args) {
        new MachineLearningController().splitDataSet();
    }
}
