package com.snake.weiyantemp;


import com.snake.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

public class JialianSummary {
    private static final Logger logger = LoggerFactory.getLogger(JialianSummary.class);

    private static final Utils utils = new Utils();

    /**
     * 读取 summaryPath 路径下所有的汇总表格, 统计他们的 总计 和 命中
     * @param summaryPath - 汇总文件存放的路径
     * @param cols - 需要进行特殊计算的列
     */
    private void jialianSummary(String summaryPath, String[] cols) {
        long taskStartTime = System.nanoTime();
        logger.info("开始执行 JialianSummary.jialianSummary()...");

        int total = 0;
        int hits = 0;

        File summaryFolder = new File(summaryPath);

        if (!summaryFolder.exists()) {
            logger.error("文件 " + summaryPath + " 不存在.");
        }

        Integer[] colsNum = new Integer[cols.length];
        for (int i = 0; i < cols.length; i++) {
            colsNum[i] = utils.excelNum2Digit(cols[i]);
        }

        List<List<String>> fileData;
        List<String> rowData;
        for (File file : summaryFolder.listFiles()) {
            if (file.getName().endsWith(".xlsx")) {
                fileData = utils.readFromXLSX(file.getAbsolutePath());
                rowData = fileData.get(fileData.size()-1);


                total += Double.parseDouble(rowData.get(colsNum[0]));
                hits += Double.parseDouble(rowData.get(colsNum[1]));
            }
        }

        logger.info("所有汇总数据已统计完成, 总请求 = " + total + ", 命中 = " + hits);

        logger.debug("JialianSummary.jialianSummary() 执行结束. 共计用时: " + utils.calculatingTimeDiff(System.nanoTime() - taskStartTime));
    }

    public static void main(String[] args) {
        String summaryPath = "D:/WYKJ/项目/海南嘉联科技有限公司/数据汇总/已完成/请求总结";

        new JialianSummary().jialianSummary(summaryPath, new String[]{"B", "C"});
    }
}
