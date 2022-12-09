package com.snake.machinelearning.utils;

import com.snake.utils.Constants;
import com.snake.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.*;

public class CSVUtils {
    private static final Logger logger = LoggerFactory.getLogger(CSVUtils.class);

    private static final Utils utils = new Utils();

    /**
     * 一键式完成对 源数据集 的切分, 切分完成后一共会生成 6 份数据集, 分别是:
     * <ol>
     *     <li>总训练集</li>
     *     <li>总验证集</li>
     *     <li>训练集 - Guest</li>
     *     <li>训练集 - Host</li>
     *     <li>验证集 - Guest</li>
     *     <li>验证集 - Host</li>
     * </ol>
     */
    public void taskStart() {
        logger.info("开始执行 CSVUtils.taskStart()...");
        long startTime = System.nanoTime();

        String dataFolder = utils.getPropValue("data_folder");
        String dataFileName = utils.getPropValue("data_file_name");
        String dataPath = dataFolder + dataFileName;

        String suffixOfTrainingSet = utils.getPropValue("suffix_of_training_set");
        String suffixOfValidationSet = utils.getPropValue("suffix_of_validation_set");

        String trainingSetFileName = utils.appendNameSuffix(dataFileName, suffixOfTrainingSet, "");
        String validationSetFileName = utils.appendNameSuffix(dataFileName, suffixOfValidationSet, "");

        String trainingSetPath = dataFolder + trainingSetFileName;
        String validationSetPath = dataFolder + validationSetFileName;

        this.splitTrainingAndValidationSet(dataPath, trainingSetPath, validationSetPath);
        this.splitDataSetForHostAndGuest(dataFolder, dataFileName, trainingSetPath, validationSetPath);

        logger.info("CSVUtils.taskStart() 总用时: " + utils.calculatingTimeDiff(System.nanoTime() - startTime));
        logger.info("CSVUtils.taskStart() 执行完毕.");
    }

    /**
     * 对总的数据集进行 训练集 和 验证集 的切分
     *
     * @param dataPath          - 源数据集的路径
     * @param trainingSetPath   - 切分后 训练集 保存的路径
     * @param validationSetPath - 切分后 验证集集 保存的路径
     */
    public void splitTrainingAndValidationSet(String dataPath, String trainingSetPath, String validationSetPath) {
        logger.info("开始执行 CSVUtils.splitTrainingAndValidationSet()...");
        long startTime = System.nanoTime();

        List<String> records = utils.readFromCSV(dataPath);
        int recordSize = records.size();
        String[] headers = records.get(0).split(Constants.COMMA);

        String header = Constants.FEATURE_ID + Constants.COMMA + Constants.FEATURE_Y;

        String featureIDName = utils.getPropValue("feature_id_name").trim();
        String featureYName = utils.getPropValue("feature_y_name").trim();

        int indexOfFeatureID = -1;
        int indexOfFeatureY = -1;

        // 找出 特征 id 和 y 在 record 数组中的 index
        for (int col = 0; col < headers.length; col++) {
            if (featureIDName.equals(utils.clearStartAndEndQuote(headers[col]).trim())) {
                indexOfFeatureID = col;
                continue;
            } else if (featureYName.equals(utils.clearStartAndEndQuote(headers[col]).trim())) {
                indexOfFeatureY = col;
                continue;
            }

            header += Constants.COMMA + utils.clearStartAndEndQuote(headers[col]).trim();
        }

        if (indexOfFeatureID < 0) {
            logger.debug(dataPath + " 未找到 Feature ID.");
        }
        if (indexOfFeatureY < 0) {
            logger.debug(dataPath + " 未找到 Feature Y.");
        }

        String percentageOfValidationSetOfTotalSet = utils.getPropValue("percentage_of_validation_set_of_total_set");

        int validationSetRows = new BigDecimal(recordSize).multiply(new BigDecimal(percentageOfValidationSetOfTotalSet)).intValue();
        int trainingSetEndToRows = recordSize - validationSetRows; // 计算出 训练集 的总记录数

        // 准备对 总数据集 进行切分
        writeTrainingAndValidationSet(trainingSetPath, header, records, 1, trainingSetEndToRows, indexOfFeatureID, indexOfFeatureY);
        writeTrainingAndValidationSet(validationSetPath, header, records, trainingSetEndToRows, recordSize, indexOfFeatureID, indexOfFeatureY);

        logger.info("CSVUtils.splitTrainingAndValidationSet() 总用时: " + utils.calculatingTimeDiff(System.nanoTime() - startTime));
        logger.info("CSVUtils.splitTrainingAndValidationSet() 执行完毕.");
    }

    /**
     * 切分 总的 训练集 和 验证集
     *
     * @param filePath         - 文件的写入路径
     * @param header           - 头部标签
     * @param records          - 数据集
     * @param startFromRows    - 从数据集的第 n 行开始写入
     * @param endToRows        - 从数据集的第 n 行终止写入
     * @param indexOfFeatureID - 特征 id 在 record 数组中的 index
     * @param indexOfFeatureY  - 特征 y 在 record 数组中的 index
     */
    private void writeTrainingAndValidationSet(String filePath, String header, List<String> records, int startFromRows, int endToRows, int indexOfFeatureID, int indexOfFeatureY) {
        utils.writeToCSVBySingleLine(filePath, "", header, Constants.CHARSET_UTF_8, Boolean.TRUE);
        int headerLength = header.split(Constants.COMMA).length;

        String[] record;
        String lineData;

        // 写入记录
        for (int row = startFromRows; row < endToRows; row++) {
            record = records.get(row).split(Constants.COMMA);

            lineData = "";
            lineData += utils.escapeSpecialCharacters(utils.clearStartAndEndQuote(record[indexOfFeatureID])).trim() + Constants.COMMA + utils.escapeSpecialCharacters(utils.clearStartAndEndQuote(record[indexOfFeatureY])).trim();

            for (int col = 0; col < headerLength; col++) {
                if (col == indexOfFeatureID || col == indexOfFeatureY) {
                    continue;
                }

                lineData += Constants.COMMA + utils.escapeSpecialCharacters(utils.clearStartAndEndQuote(record[col])).trim();
            }

            utils.writeToCSVBySingleLine(filePath, "", lineData, Constants.CHARSET_UTF_8, Boolean.TRUE);
        }
    }

    /**
     * 将已经切分好的 训练集 和 验证集
     * * 再次按照 角色 进行切分
     *
     * @param dataFolder        - 存放数据集的文件夹
     * @param dataFileName      - 源数据集 的文件名
     * @param trainingSetPath   - 切分后 训练集 保存的路径
     * @param validationSetPath - 切分后 验证集集 保存的路径
     */
    public void splitDataSetForHostAndGuest(String dataFolder, String dataFileName, String trainingSetPath, String validationSetPath) {
        logger.info("开始执行 CSVUtils.splitTrainingAndValidationSet()...");
        long startTime = System.nanoTime();

        List<String> trainingSet = utils.readFromCSV(trainingSetPath);
        List<String> validationSet = utils.readFromCSV(validationSetPath);

        String suffixOfTrainingSetForGuest = utils.getPropValue("suffix_of_training_set_for_guest");
        String suffixOfValidationSetForGuest = utils.getPropValue("suffix_of_validation_set_for_guest");

        String trainingSetFileNameForGuest = utils.appendNameSuffix(dataFileName, suffixOfTrainingSetForGuest, "");
        String validationSetFileNameForGuest = utils.appendNameSuffix(dataFileName, suffixOfValidationSetForGuest, "");

        // 写入 训练集 - Guest
        writeDataSetForHostAndGuest(trainingSet, dataFolder + trainingSetFileNameForGuest, Constants.ROLE_GUEST);
        // 写入 验证集 - Guest
        writeDataSetForHostAndGuest(validationSet, dataFolder + validationSetFileNameForGuest, Constants.ROLE_GUEST);

        int trainingSetSize = trainingSet.size();
        int validationSetSize = validationSet.size();

        List<String> tempTrainingSet;
        List<String> tempValidationSet;

        String suffixOfTrainingSetForHost = utils.getPropValue("suffix_of_training_set_for_host");
        String suffixOfValidationSetForHost = utils.getPropValue("suffix_of_validation_set_for_host");

        int numberOfHost = Integer.parseInt(utils.getPropValue("number_of_host"));

        for (int i = 0; i < numberOfHost; i++) {
            String trainingSetFileNameForHost = utils.appendNameSuffix(dataFileName, suffixOfTrainingSetForHost, String.valueOf(i));
            String validationSetFileNameForHost = utils.appendNameSuffix(dataFileName, suffixOfValidationSetForHost, String.valueOf(i));

            int tempTrainingSetFromIndex = (new BigDecimal(trainingSetSize).multiply(new BigDecimal(i).divide(new BigDecimal(numberOfHost)))).intValue();
            int tempTrainingSetToIndex = (new BigDecimal(trainingSetSize).multiply((new BigDecimal(i).add(new BigDecimal(1))).divide(new BigDecimal(numberOfHost)))).intValue();

            int tempValidationSetFromIndex = new BigDecimal(validationSetSize).multiply(new BigDecimal(i).divide(new BigDecimal(numberOfHost))).intValue();
            int tempValidationSetToIndex = new BigDecimal(validationSetSize).multiply((new BigDecimal(i).add(new BigDecimal(1))).divide(new BigDecimal(numberOfHost))).intValue();

            tempTrainingSet = trainingSet.subList(tempTrainingSetFromIndex, tempTrainingSetToIndex);
            tempValidationSet = validationSet.subList(tempValidationSetFromIndex, tempValidationSetToIndex);

            if (i != 0) { // 补全缺失的 header
                tempTrainingSet.add(0, trainingSet.get(0));
                tempValidationSet.add(0, validationSet.get(0));
            }

            // 写入 训练集 - Host
            writeDataSetForHostAndGuest(tempTrainingSet, dataFolder + trainingSetFileNameForHost, Constants.ROLE_HOST);
            // 写入 验证集 - Host
            writeDataSetForHostAndGuest(tempValidationSet, dataFolder + validationSetFileNameForHost, Constants.ROLE_HOST);
        }

        logger.info("CSVUtils.splitDataSetForHostAndGuest() 总用时: " + utils.calculatingTimeDiff(System.nanoTime() - startTime));
        logger.info("CSVUtils.splitDataSetForHostAndGuest() 执行完毕.");
    }

    /**
     * 从 inputFilePath 里面读取数据集, 然后按照 role 对应的规则, 写入到 outputFilePath 中
     *
     * @param dataSet        - 输入的数据集
     * @param outputFilePath - 输出的数据集文件
     * @param role           - 角色, Guest or Host
     */
    private void writeDataSetForHostAndGuest(List<String> dataSet, String outputFilePath, String role) {
        int dataSetSize = dataSet.size();

        String[] headers = dataSet.get(0).split(Constants.COMMA);
        int headerLength = headers.length;

        int splitFeaturesIndex;

        String percentageOfFeaturesForGuest = utils.getPropValue("percentage_of_features_for_guest");
        int needFeaturesForGuest = new BigDecimal(headerLength).multiply(new BigDecimal(percentageOfFeaturesForGuest)).intValue(); // 计算出 Guest 数据集的特征数量

        // 根据 角色 来设定需要摘取的 特征数
        if (role.equals(Constants.ROLE_GUEST)) {
            splitFeaturesIndex = needFeaturesForGuest > 2 ? needFeaturesForGuest : -1;
        } else {
            splitFeaturesIndex = needFeaturesForGuest > 2 && (headerLength - needFeaturesForGuest) <= (headerLength - 3) ? needFeaturesForGuest : -1;
        }

        // 开始正式写入 - Guest 数据集
        if (role.equals(Constants.ROLE_GUEST)) {
            String header = "";
            for (int col = 0; col < splitFeaturesIndex; col++) {
                if (col == 0) {
                    header += headers[col];
                } else {
                    header += Constants.COMMA + headers[col];
                }
            }

            utils.writeToCSVBySingleLine(outputFilePath, "", header, Constants.CHARSET_UTF_8, Boolean.TRUE);

            String[] record;
            String lineData;

            for (int row = 1; row < dataSetSize; row++) {
                record = dataSet.get(row).split(Constants.COMMA);

                lineData = "";

                for (int col = 0; col < splitFeaturesIndex; col++) {
                    if (col == 0) {
                        lineData += record[col];
                    } else {
                        lineData += Constants.COMMA + record[col];
                    }
                }

                utils.writeToCSVBySingleLine(outputFilePath, "", lineData, Constants.CHARSET_UTF_8, Boolean.TRUE);
            }
        } else { // 开始正式写入 - Host 数据集
            String header = headers[0];
            for (int col = splitFeaturesIndex; col < headerLength; col++) {
                header += Constants.COMMA + headers[col];
            }

            utils.writeToCSVBySingleLine(outputFilePath, "", header, Constants.CHARSET_UTF_8, Boolean.TRUE);

            String[] record;
            String lineData;

            for (int row = 1; row < dataSetSize; row++) {
                record = dataSet.get(row).split(Constants.COMMA);

                lineData = record[0];

                for (int col = splitFeaturesIndex; col < headerLength; col++) {
                    lineData += Constants.COMMA + record[col];
                }

                utils.writeToCSVBySingleLine(outputFilePath, "", lineData, Constants.CHARSET_UTF_8, Boolean.TRUE);
            }
        }
    }

    public static void main(String[] args) {
        new CSVUtils().taskStart();
    }

}
