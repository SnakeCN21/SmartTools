package com.snake.smarttools.controller;

import com.alibaba.fastjson.JSONObject;
import com.snake.smarttools.config.ResponseData;
import com.snake.smarttools.constant.Constant;
import com.snake.smarttools.service.PorntoolsService;
import com.snake.smarttools.utils.Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/porntools")
@Slf4j
public class PorntoolsController {
    @Autowired
    private PorntoolsService porntoolsService;

    @Autowired
    private Util util;

    /**
     * 任务的流程如下:
     * <ol>
     *     <li>读取 txt 文件</li>
     *     <li>解析 txt 文件, 然后按行读取内容</li>
     *     <li>经过一些清洗之后获得真正的 JavCode</li>
     *     <li>依次将这些 JavCode 组装成 Http Request, 并进行请求</li>
     *     <li>解析返回的 Http Response, 判断当前 JavCode 是否 包含磁链 或 中文磁链</li>
     *     <li>将所有 JavCode 都检索完毕之后, 将结果写入到一个新 txt 文件中</li>
     * </ol>
     */
    @GetMapping("/taskStart")
    public ResponseData taskStart() {
        log.info("开始执行 PorntoolsController.taskStart()...");
        long startTime = System.nanoTime();

        // 读取 txt 文件
        List<String> javCodeList = porntoolsService.getJavCodeList();
        // 找出重复的 jav code
        List<String> duplicateJavCodeList = util.findCollectionDuplicateElements(javCodeList);

        // 组装待请求的 url
        List<String> javCodeUrlList = porntoolsService.assembleJavCodeUrlList(javCodeList);
        // 请求 http
        List<String> httpRespList = porntoolsService.getHttpRespList(javCodeUrlList);

        // 解析 html
        JSONObject javCodeObj = porntoolsService.parseJavDBHTML(javCodeList, httpRespList);
        javCodeObj.put(Constant.DUPLICATE_JAV_CODE_LIST, duplicateJavCodeList);
        // 将结果写入文件
        porntoolsService.setJavCodeList(javCodeObj);

        log.info("PorntoolsController.taskStart() 总用时: " + util.calculatingTimeDiff(System.nanoTime() - startTime));
        log.info("PorntoolsController.taskStart() 执行完毕");
        return ResponseData.success();
    }

}
