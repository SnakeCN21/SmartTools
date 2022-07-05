package com.snake.machinelearning.utils;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.snake.utils.Constants;
import com.snake.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

public class CSVUtils {
    private static final Logger logger = LoggerFactory.getLogger(CSVUtils.class);

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

        try {
            new CSVUtils().splitTrainingAndValidationSet();
            new CSVUtils().splitDataSetForHostAndGuest();
        } catch (IOException e) {
            logger.debug(e.getMessage(), e);
        }

        logger.info("CSVUtils.taskStart() 总用时: " + new Utils().calculatingTimeDiff(System.nanoTime() - startTime));
        logger.info("CSVUtils.taskStart() 执行完毕.");
    }

    /**
     * 对总的数据集进行 训练集 和 验证集 的切分
     *
     * @throws IOException
     */
    public void splitTrainingAndValidationSet() throws IOException {
        logger.info("开始执行 CSVUtils.splitTrainingAndValidationSet()...");
        long startTime = System.nanoTime();

        String dataFolder = new Utils().getPropValue("data_folder");
        String dataFileName = new Utils().getPropValue("data_file_name");

        FileReader fileReader = null;
        CSVReader csvReader = null;

        try {
            fileReader = new FileReader(dataFolder + dataFileName);
            csvReader = new CSVReader(fileReader);

            List<String[]> records = csvReader.readAll();
            int recordSize = records.size();
            String[] headers = records.get(0);

            String featureIDName = new Utils().getPropValue("feature_id_name").trim();
            String featureYName = new Utils().getPropValue("feature_y_name").trim();

            int indexOfFeatureID = -1;
            int indexOfFeatureY = -1;

            // 找出 特征 id 和 y 在 record 数组中的 index
            for (int col=0; col<headers.length; col++) {
                if (featureIDName.equals(headers[col].trim())) {
                    indexOfFeatureID = col;
                } else if (featureYName.equals(headers[col].trim())) {
                    indexOfFeatureY = col;
                }

                if (indexOfFeatureID != -1 && indexOfFeatureY != -1) {
                    break;
                }
            }

            if (indexOfFeatureID < 0) {
                logger.debug(dataFileName + " 未找到 Feature ID Header");
            }
            if (indexOfFeatureY < 0) {
                logger.debug(dataFileName + " 未找到 Feature Y Header");
            }

            String suffixOfTrainingSet = new Utils().getPropValue("suffix_of_training_set");
            String suffixOfValidationSet = new Utils().getPropValue("suffix_of_validation_set");

            String trainingSetFileName =new Utils().appendNameSuffix(dataFileName, suffixOfTrainingSet);
            String validationSetFileName = new Utils().appendNameSuffix(dataFileName, suffixOfValidationSet);

            String percentageOfValidationSetOfTotalSet = new Utils().getPropValue("percentage_of_validation_set_of_total_set");

            int validationSetRows = new BigDecimal(recordSize).multiply(new BigDecimal(percentageOfValidationSetOfTotalSet)).intValue();
            int trainingSetEndToRows = recordSize - validationSetRows;

            // 准备对 总数据集 进行切分
            if (!writeTrainingAndValidationSet(dataFolder + trainingSetFileName, records, 1, trainingSetEndToRows, indexOfFeatureID, indexOfFeatureY)) {
                logger.debug(trainingSetFileName + " 写入失败!");
                return;
            }
            if (!writeTrainingAndValidationSet(dataFolder + validationSetFileName, records, trainingSetEndToRows, recordSize, indexOfFeatureID, indexOfFeatureY)) {
                logger.debug(validationSetFileName + " 写入失败!");
            }
        } catch (Exception e) {
            logger.debug(e.getMessage(), e);
        } finally {
            if (csvReader != null) {
                csvReader.close();
            }
            if (fileReader != null) {
                fileReader.close();
            }

            logger.info("CSVUtils.splitTrainingAndValidationSet() 总用时: " + new Utils().calculatingTimeDiff(System.nanoTime() - startTime));
            logger.info("CSVUtils.splitTrainingAndValidationSet() 执行完毕.");
        }
    }

    /**
     * 切分 总的 训练集 和 验证集
     *
     * @param fileName - 文件名
     * @param records - 数据集
     * @param startFromRows - 从数据集的第 n 行开始写入
     * @param endToRows - 从数据集的第 n 行终止写入
     * @param indexOfFeatureID - 特征 id 在 record 数组中的 index
     * @param indexOfFeatureY - 特征 y 在 record 数组中的 index
     *
     * @return 是否成功写入对应的数据集
     * @throws IOException
     */
    private boolean writeTrainingAndValidationSet(String fileName, List<String[]> records, int startFromRows, int endToRows, int indexOfFeatureID, int indexOfFeatureY) throws IOException {
        boolean isSucceed = false;

        FileWriter fileWriter = null;
        CSVWriter csvWriter = null;

        try {
            File file = new File(fileName);

            if (file.exists()) {
                file.delete();
            }

            fileWriter = new FileWriter(fileName);
            csvWriter = new CSVWriter(fileWriter, CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.RFC4180_LINE_END);

            int headerLength = records.get(0).length;

            // 写入 header
            String[] header = new String[headerLength];
            header[0] = Constants.FEATURE_ID;
            header[1] = Constants.FEATURE_Y;

            for (int i=2; i<headerLength; i++) {
                header[i] = Constants.FEATURE_X + (i-2);
            }

            csvWriter.writeNext(header);

            // 写入记录
            for (int row=startFromRows; row<endToRows; row++) {
                String[] record = records.get(row);

                String[] temp = new String[headerLength];

                temp[0] = record[indexOfFeatureID];
                temp[1] = record[indexOfFeatureY];

                for (int col=0; col<headerLength; col++) {
                    int index = col + 2;

                    if (col == indexOfFeatureID || col == indexOfFeatureY) {
                        continue;
                    }

                    if (col > indexOfFeatureID) {
                        index --;
                    }
                    if (col > indexOfFeatureY) {
                        index --;
                    }

                    temp[index] = record[col];
                }

                csvWriter.writeNext(temp);
            }

            csvWriter.flush();

            isSucceed = true;
        } catch (IOException e) {
            logger.debug(e.getMessage(), e);
        } finally {
            if (csvWriter != null) {
                csvWriter.close();
            }
            if (fileWriter != null) {
                fileWriter.close();
            }
        }

        return isSucceed;
    }

    /**
     * 将已经切分好的 训练集 和 验证集
     * 再次按照 角色 进行切分
     */
    public void splitDataSetForHostAndGuest() {
        logger.info("开始执行 CSVUtils.splitTrainingAndValidationSet()...");
        long startTime = System.nanoTime();

        String dataFolder = new Utils().getPropValue("data_folder");
        String dataFileName = new Utils().getPropValue("data_file_name");

        String suffixOfTrainingSet = new Utils().getPropValue("suffix_of_training_set");
        String suffixOfValidationSet = new Utils().getPropValue("suffix_of_validation_set");

        String trainingSetFileName =new Utils().appendNameSuffix(dataFileName, suffixOfTrainingSet);
        String validationSetFileName = new Utils().appendNameSuffix(dataFileName, suffixOfValidationSet);

        String suffixOfTrainingSetForGuest = new Utils().getPropValue("suffix_of_training_set_for_guest");
        String suffixOfTrainingSetForHost = new Utils().getPropValue("suffix_of_training_set_for_host");
        String suffixOfValidationSetForGuest = new Utils().getPropValue("suffix_of_validation_set_for_guest");
        String suffixOfValidationSetForHost = new Utils().getPropValue("suffix_of_validation_set_for_host");

        String trainingSetFileNameForGuest =new Utils().appendNameSuffix(dataFileName, suffixOfTrainingSetForGuest);
        String trainingSetFileNameForHost = new Utils().appendNameSuffix(dataFileName, suffixOfTrainingSetForHost);
        String validationSetFileNameForGuest =new Utils().appendNameSuffix(dataFileName, suffixOfValidationSetForGuest);
        String validationSetFileNameForHost = new Utils().appendNameSuffix(dataFileName, suffixOfValidationSetForHost);

        try {
            // 写入 训练集 - Guest
            if (!writeDataSetForHostAndGuest(dataFolder + trainingSetFileName, dataFolder + trainingSetFileNameForGuest, Constants.ROLE_GUEST)) {
                logger.debug(trainingSetFileNameForGuest + " 写入失败!");
                return;
            }
            // 写入 训练集 - Host
            if (!writeDataSetForHostAndGuest(dataFolder + trainingSetFileName, dataFolder + trainingSetFileNameForHost, Constants.ROLE_HOST)) {
                logger.debug(trainingSetFileNameForHost + " 写入失败!");
                return;
            }
            // 写入 验证集 - Guest
            if (!writeDataSetForHostAndGuest(dataFolder + validationSetFileName, dataFolder + validationSetFileNameForGuest, Constants.ROLE_GUEST)) {
                logger.debug(validationSetFileNameForGuest + " 写入失败!");
                return;
            }
            // 写入 验证集 - Host
            if (!writeDataSetForHostAndGuest(dataFolder + validationSetFileName, dataFolder + validationSetFileNameForHost, Constants.ROLE_HOST)) {
                logger.debug(validationSetFileNameForHost + " 写入失败!");
            }
        } catch (IOException e) {
            logger.debug(e.getMessage(), e);
        } finally {
            logger.info("CSVUtils.splitDataSetForHostAndGuest() 总用时: " + new Utils().calculatingTimeDiff(System.nanoTime() - startTime));
            logger.info("CSVUtils.splitDataSetForHostAndGuest() 执行完毕.");
        }
    }

    /**
     * 从 inputFile 里面读取数据集, 然后按照 role 对应的规则, 写入到 outputFile 中
     *
     * @param inputFileName - 输入的数据集文件
     * @param outputFileName - 输出的数据集文件
     * @param role - 角色, Guest or Host
     * @return 是否成功写入对应的数据集
     * @throws IOException
     */
    private boolean writeDataSetForHostAndGuest(String inputFileName, String outputFileName, String role) throws IOException {
        boolean isSucceed = false;

        FileReader fileReader = null;
        CSVReader csvReader = null;

        FileWriter fileWriter = null;
        CSVWriter csvWriter = null;

        int splitFeaturesIndex = -1;
        int numberOfRecordsRequired = -1;

        String percentageOfFeaturesForGuest = new Utils().getPropValue("percentage_of_features_for_guest");

        boolean isNeedRandomSplit = Boolean.parseBoolean(new Utils().getPropValue("is_need_random_split"));

        String percentageOfDataSetForGuest = new Utils().getPropValue("percentage_of_data_set_for_guest");
        String percentageOfDataSetForHost = new Utils().getPropValue("percentage_of_data_set_for_host");

        try {
            fileReader = new FileReader(inputFileName);
            csvReader = new CSVReader(fileReader);

            File file = new File(outputFileName);

            if (file.exists()) {
                file.delete();
            }

            fileWriter = new FileWriter(outputFileName);
            csvWriter = new CSVWriter(fileWriter, CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.RFC4180_LINE_END);

            List<String[]> records = csvReader.readAll();
            int recordSize = records.size();

            String[] headers = records.get(0);
            int headerLength = headers.length;

            int needFeaturesForGuest = new BigDecimal(headerLength).multiply(new BigDecimal(percentageOfFeaturesForGuest)).intValue();

            // 根据 角色 来设定需要摘取的 Features 以及 记录数
            if (role.equals(Constants.ROLE_GUEST)) {
                splitFeaturesIndex = needFeaturesForGuest > 2 ? needFeaturesForGuest : -1;

                if (isNeedRandomSplit) {
                    numberOfRecordsRequired = new BigDecimal(recordSize).multiply(new BigDecimal(percentageOfDataSetForGuest)).intValue();
                } else {
                    numberOfRecordsRequired = recordSize;
                }
            } else {
                splitFeaturesIndex = needFeaturesForGuest > 2 && (headerLength-needFeaturesForGuest) <= (headerLength-3) ? needFeaturesForGuest : -1;

                if (isNeedRandomSplit) {
                    numberOfRecordsRequired = new BigDecimal(recordSize).multiply(new BigDecimal(percentageOfDataSetForHost)).intValue();
                } else {
                    numberOfRecordsRequired = recordSize;
                }
            }

            if (splitFeaturesIndex < 0) {
                logger.debug(inputFileName + " 的 splitFeaturesIndex = " + splitFeaturesIndex + ", 请重新确认 Features 数量");
            }
            if (numberOfRecordsRequired < 0) {
                logger.debug(inputFileName + " 的 numberOfRecordsRequired = " + numberOfRecordsRequired + ", 请重新确认 数据集 数量");
            }

            // 开始写入
            if (role.equals(Constants.ROLE_GUEST)) {
                // 写入 header
                String[] header = new String[splitFeaturesIndex];

                for (int col=0; col<splitFeaturesIndex; col++) {
                    header[col] = headers[col];
                }

                csvWriter.writeNext(header);

                // 写入 record
                if (numberOfRecordsRequired == recordSize) {
                    for (int row=1; row<recordSize; row++) {
                        String[] temp = new String[splitFeaturesIndex];

                        for (int col=0; col<splitFeaturesIndex; col++) {
                            temp[col] = records.get(row)[col];
                        }

                        csvWriter.writeNext(temp);
                    }
                } else {
                    HashSet<Integer> savedRows = new HashSet<Integer>();

                    while (savedRows.size() < numberOfRecordsRequired) {
                        int rowIndex = new Random().nextInt(numberOfRecordsRequired) + 1;

                        if (!savedRows.contains(rowIndex)) {
                            String[] temp = new String[splitFeaturesIndex];

                            for (int col=0; col<splitFeaturesIndex; col++) {
                                temp[col] = records.get(rowIndex)[col];
                            }

                            csvWriter.writeNext(temp);

                            savedRows.add(rowIndex);
                        }
                    }
                }
            } else {
                // 写入 header
                String[] header = new String[headerLength-needFeaturesForGuest+1];

                header[0] = records.get(0)[0];
                int i = 1;
                for (int col=splitFeaturesIndex; col<headerLength; col++) {
                    header[i] = headers[col];
                    i ++;
                }

                csvWriter.writeNext(header);

                // 写入 record
                if (numberOfRecordsRequired == recordSize) {
                    for (int row=1; row<recordSize; row++) {
                        String[] temp = new String[headerLength-needFeaturesForGuest+1];

                        temp[0] = records.get(row)[0];
                        i = 1;
                        for (int col=splitFeaturesIndex; col<headerLength; col++) {
                            temp[i] = records.get(row)[col];
                            i ++;
                        }

                        csvWriter.writeNext(temp);
                    }
                } else {
                    HashSet<Integer> savedRows = new HashSet<Integer>();

                    while (savedRows.size() < numberOfRecordsRequired) {
                        int rowIndex = new Random().nextInt(numberOfRecordsRequired) + 1;

                        if (!savedRows.contains(rowIndex)) {
                            String[] temp = new String[headerLength-needFeaturesForGuest+1];

                            temp[0] = records.get(rowIndex)[0];
                            i = 1;
                            for (int col=splitFeaturesIndex; col<headerLength; col++) {
                                temp[i] = records.get(rowIndex)[col];
                                i ++;
                            }

                            csvWriter.writeNext(temp);

                            savedRows.add(rowIndex);
                        }
                    }
                }
            }

            csvWriter.flush();

            isSucceed = true;
        } catch (Exception e) {
            logger.debug("写入" + outputFileName + " 出错.");
            logger.debug(e.getMessage(), e);
        } finally {
            if (csvWriter != null) {
                csvWriter.close();
            }
            if (fileWriter != null) {
                fileWriter.close();
            }

            if (csvReader != null) {
                csvReader.close();
            }
            if (fileReader != null) {
                fileReader.close();
            }
        }

        return isSucceed;
    }

    public static void main(String[] args) {
        new CSVUtils().taskStart();
    }

}
