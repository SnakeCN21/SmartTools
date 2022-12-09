package com.snake.weiyantemp;

import com.snake.utils.Constants;
import com.snake.utils.Utils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ProfessorWangUtils {
    private static final Logger logger = LoggerFactory.getLogger(ProfessorWangUtils.class);

    private static final Utils utils = new Utils();

    /**
     * 将传入的源文件的 cols 列转换为 md5 加密后的数据, 最后输出到 outputPath 路径
     *
     * @param inputPath  - 源文件路径
     * @param exportPath - 输出文件路径
     * @param isHasHead  - 源文件是否包含标题
     * @param cols       - 需要进行 md5 加密的列名
     */
    public void convertCSVtoMD5(String inputPath, String exportPath, boolean isHasHead, String separator, String[] cols) {
        long taskStartTime = System.nanoTime();
        logger.info("开始执行 Test.convertCSVtoMD5()...");

        File inputFile = new File(inputPath);

        if (!inputFile.exists()) {
            logger.error("文件 " + inputPath + " 不存在.");
        }

        Integer[] colsNum = new Integer[cols.length];
        for (int i = 0; i < cols.length; i++) {
            colsNum[i] = utils.excelNum2Digit(cols[i]);
        }

        List<String> exportList = new ArrayList<>();

        List<String> list = utils.readFromCSV(inputFile.getAbsolutePath());
        String rowData;
        String[] tempRowData;
        StringBuilder convertedRowData;

        for (int i = 0; i < list.size(); i++) {
            convertedRowData = new StringBuilder();

            rowData = list.get(i);

            if (i == 0 && isHasHead) {
                convertedRowData = new StringBuilder(rowData.replace(separator, Constants.COMMA));
            } else {
                tempRowData = rowData.split(separator);

                for (int x = 0; x < tempRowData.length; x++) {
                    String tempStr = tempRowData[x];

                    for (int y : colsNum) {
                        if (x == y) {
                            if (StringUtils.isNotBlank(tempStr)) {
                                tempStr = DigestUtils.md5Hex(tempStr.getBytes());
                                break;
                            }
                        }
                    }

                    tempStr = utils.escapeSpecialCharacters(tempStr);

                    if (x != 0) {
                        tempStr = Constants.COMMA + tempStr;
                    }

                    convertedRowData.append(tempStr);
                }
            }

            exportList.add(convertedRowData.toString());
        }

        utils.writeToCSVByList(exportPath, null, exportList, Constants.CHARSET_GBK, Boolean.FALSE);

        logger.debug("Test.convertCSVtoMD5() 执行结束. 共计用时: " + utils.calculatingTimeDiff(System.nanoTime() - taskStartTime));
    }

    public static void main(String[] args) {
        String file1 = "C:\\Users\\Fei Huang\\Downloads\\无老板20220926\\茶室棋牌室手机号20220923.csv";
        String file2 = "C:\\Users\\Fei Huang\\Downloads\\无老板20220926\\回溯数据，手机号+时间20220923.csv";

        String file1_1 = "C:\\Users\\Fei Huang\\Downloads\\无老板20220926\\茶室棋牌室手机号20220923-md5.csv";
        String file2_1 = "C:\\Users\\Fei Huang\\Downloads\\无老板20220926\\回溯数据，手机号+时间20220923-md5.csv";

        new ProfessorWangUtils().convertCSVtoMD5(file1, file1_1, Boolean.TRUE, Constants.COMMA, new String[]{"F"});
        new ProfessorWangUtils().convertCSVtoMD5(file2, file2_1, Boolean.TRUE, Constants.COMMA, new String[]{"A"});
    }
}
