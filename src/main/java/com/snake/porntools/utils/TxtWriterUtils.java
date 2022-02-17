package com.snake.porntools.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class TxtWriterUtils {
    private static final Logger logger = LoggerFactory.getLogger(TxtWriterUtils.class);

    /**
     * 将检索完的 JavCode 写入到 txt 文件中.
     *
     * @param fileName - 需要写入的 txt 文件
     * @param javsMap  - key: Constants.DOWNLOADABLE_JAVS - 有磁链的 Javs; Constants.CHINESE_SUBTITLES_JAVS - 有中文磁链的 Javs
     *
     * @throws IOException
     */
    public void txtWriter(String fileName, Map<String, List<String>> javsMap) throws IOException {
        logger.info("开始执行 TxtWriterUtils.txtWriter()...");
        long startTime = System.nanoTime();

        List<String> duplicateJavCodeList = javsMap.get(Constants.DUPLICATE_JAV_CODE_LIST);
        List<String> downloadableJavCodeList = javsMap.get(Constants.DOWNLOADABLE_JAV_CODE_LIST);
        List<String> chineseSubtitlesJavCodeList = javsMap.get(Constants.CHINESE_SUBTITLES_JAV_CODE_LIST);

        BufferedWriter out = null;
        File file = new File(fileName); //设定为当前文件夹

        try {
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile(); // 创建新文件

            out = new BufferedWriter(new FileWriter(file));

            if (!duplicateJavCodeList.isEmpty()) {
                out.append("以下 JavCode 重复: " + Constants.CARRIAGE_RETURN_TO_LINE);

                for (String javCode : duplicateJavCodeList) {
                    out.append(javCode).append(Constants.CARRIAGE_RETURN_TO_LINE);
                }

                out.append(Constants.CARRIAGE_RETURN_TO_LINE);
            }

            if (!chineseSubtitlesJavCodeList.isEmpty()) {
                out.append("以下 JavCode 拥有中文磁链: " + Constants.CARRIAGE_RETURN_TO_LINE);

                for (String javCodeAndReleaseDate : chineseSubtitlesJavCodeList) {
                    String[] str = javCodeAndReleaseDate.split(Constants.SPACE_AND_HYPHEN);

                    String javCode = str[0];
                    String releaseDate = str[1];

                    out.append(javCode + Constants.SPACE_AND_HYPHEN + releaseDate).append(Constants.CARRIAGE_RETURN_TO_LINE);
                }

                out.append(Constants.CARRIAGE_RETURN_TO_LINE);
            }

            if (!downloadableJavCodeList.isEmpty()) {
                out.append("以下 JavCode 拥有磁链: " + Constants.CARRIAGE_RETURN_TO_LINE);

                for (String javCodeAndReleaseDate : downloadableJavCodeList) {
                    String[] str = javCodeAndReleaseDate.split(Constants.SPACE_AND_HYPHEN);

                    String javCode = str[0];
                    String releaseDate = str[1];

                    if (new Utils().isReleaseDateOverFilter(releaseDate) < 0) {
                        out.append(javCode + Constants.SPACE_AND_HYPHEN + releaseDate).append(Constants.CARRIAGE_RETURN_TO_LINE);
                    } else {
                        out.append(javCode).append(Constants.CARRIAGE_RETURN_TO_LINE);
                    }
                }
            }
        } catch (IOException e) {
            logger.debug(e.getMessage(), e);
        } finally {
            if (out != null) {
                out.flush(); // 把缓存区内容压入文件
                out.close();
            }

            logger.info("********************************************************************\n"
                    + "文件 " + file.getAbsolutePath() + " 写入完毕.\n"
                    + "共整理了 " + chineseSubtitlesJavCodeList.size() + " 个拥有中文磁链的 JavCode, 以及 " + downloadableJavCodeList.size() + " 个拥有磁链的 JavCode.\n"
                    + "TxtWriterUtils.txtWriter() 总用时: " + new Utils().calculatingTimeDiff(System.nanoTime() - startTime) + "\n"
                    + "********************************************************************"
            );

            logger.info("TxtWriterUtils.txtWriter() 执行完毕.");
        }
    }

}
