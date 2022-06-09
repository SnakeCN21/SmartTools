package com.snake.porntools.javcode;

import com.snake.porntools.javcode.controller.CheckJavStatusController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class PorntoolsMainController {
    private static final Logger logger = LoggerFactory.getLogger(PorntoolsMainController.class);

    public static void main(String[] args) {
        logger.info("开始执行 PorntoolsMainController...");

        CheckJavStatusController checkJavStatusController = new CheckJavStatusController();

        try {
            checkJavStatusController.taskStart();
        } catch (IOException e) {
            logger.debug(e.getMessage(), e);
        }
    }

}
