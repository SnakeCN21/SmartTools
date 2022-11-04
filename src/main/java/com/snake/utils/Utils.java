package com.snake.utils;


import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.Collator;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Utils {
    private static final Logger logger = LoggerFactory.getLogger(Utils.class);

    /**
     * 解析 Constants.PROP_FILE_NAME 文件
     *
     * @param key - Constants.PROP_FILE_NAME 文件中的 key
     * @return value
     */
    public String getPropValue(String key) {
        String value = "";
        Properties props = new Properties();

        try {
            FileInputStream input = new FileInputStream(Constants.PROP_FILE_NAME);
            props.load(new InputStreamReader(input, StandardCharsets.UTF_8));

            value = props.getProperty(key);
        } catch (IOException e) {
            logger.debug("解析 " + Constants.PROP_FILE_NAME + " 文件错误: " + e.getMessage());
        }

        return value;
    }

    /**
     * 解析 Constants.PROP_FILE_NAME 文件, 将多个 value 转换成一个 list 返回
     *
     * @param key - Constants.PROP_FILE_NAME 文件中的 key
     * @return List
     */
    public List<String> getPropLists(String key) {
        List<String> list = new ArrayList<>();

        String value;
        Properties props = new Properties();

        try {
            FileInputStream input = new FileInputStream(Constants.PROP_FILE_NAME);
            props.load(new InputStreamReader(input, StandardCharsets.UTF_8));

            value = props.getProperty(key);
            String[] valueLists = value.split(";");

            for (String str : valueLists) {
                list.add(str.trim());
            }
        } catch (IOException e) {
            logger.debug("解析 " + Constants.PROP_FILE_NAME + " 文件错误: " + e.getMessage());
        }

        return list;
    }

    /**
     * 解析 Constants.PROP_FILE_NAME 文件
     *
     * @param propertiesPath - 指定一个特定的 properties 文件位置
     * @param key            - properties 文件中的 key
     */
    public String getPropValue(String propertiesPath, String key) {
        String value = "";
        Properties props = new Properties();

        try {
            FileInputStream input = new FileInputStream(propertiesPath);
            props.load(new InputStreamReader(input, StandardCharsets.UTF_8));

            value = props.getProperty(key);
        } catch (IOException e) {
            logger.debug("解析 " + propertiesPath + " 文件错误: " + e.getMessage());
        }

        return value;
    }

    /**
     * 解析 propertiesFilePath 文件, 将多个 value 转换成一个 list 返回
     *
     * @param propertiesPath - 指定一个特定的 properties 文件位置
     * @param key            - properties 文件中的 key
     */
    public List<String> getPropLists(String propertiesPath, String key) {
        List<String> list = new ArrayList<>();

        String value;
        Properties props = new Properties();

        try {
            FileInputStream input = new FileInputStream(propertiesPath);
            props.load(new InputStreamReader(input, StandardCharsets.UTF_8));

            value = props.getProperty(key);
            String[] valueLists = value.split(";");

            for (String str : valueLists) {
                list.add(str.trim());
            }
        } catch (IOException e) {
            logger.debug("解析 " + propertiesPath + " 文件错误: " + e.getMessage());
        }

        return list;
    }

    /**
     * 在文件名最后添加一个后缀
     *
     * @param fullName - 完整的文件名(不包含文件路径, 但可能包含文件扩展名)
     * @param suffix   - 需要添加的后缀
     * @param index    - index
     */
    public String appendNameSuffix(String fullName, String suffix, String index) {
        String newName;

        if (fullName.contains(Constants.DOT)) {
            String name = fullName.substring(0, fullName.lastIndexOf(Constants.DOT));
            String extension = fullName.substring(fullName.lastIndexOf(Constants.DOT) + 1);

            if (StringUtils.isNotBlank(index)) {
                newName = name + suffix + Constants.UNDERSCORE + index + Constants.DOT + extension;
            } else {
                newName = name + suffix + Constants.DOT + extension;
            }
        } else {
            if (StringUtils.isNotBlank(index)) {
                newName = fullName + suffix + Constants.UNDERSCORE + index;
            } else {
                newName = fullName + suffix;
            }
        }

        return newName;
    }

    /**
     * 将 long 类型的时间格式转换成可读性更高的时间格式
     * timeDiff 是基于 System.nanoTime() 计算出来的
     *
     * @param timeDiff - 需要转换的long类型时间
     */
    public String calculatingTimeDiff(long timeDiff) {
        final long day = TimeUnit.NANOSECONDS.toDays(timeDiff);

        final long hours = TimeUnit.NANOSECONDS.toHours(timeDiff)
                - TimeUnit.DAYS.toHours(TimeUnit.NANOSECONDS.toDays(timeDiff));

        final long minutes = TimeUnit.NANOSECONDS.toMinutes(timeDiff)
                - TimeUnit.HOURS.toMinutes(TimeUnit.NANOSECONDS.toHours(timeDiff));

        final long seconds = TimeUnit.NANOSECONDS.toSeconds(timeDiff)
                - TimeUnit.MINUTES.toSeconds(TimeUnit.NANOSECONDS.toMinutes(timeDiff));

        final long ms = TimeUnit.NANOSECONDS.toMillis(timeDiff)
                - TimeUnit.SECONDS.toMillis(TimeUnit.NANOSECONDS.toSeconds(timeDiff));

        StringBuilder sb = new StringBuilder(64);

        if (day != 0) {
            sb.append(day).append(" 天 ");
        }
        if (hours != 0) {
            sb.append(hours).append(" 小时 ");
        }
        if (minutes != 0) {
            sb.append(minutes).append(" 分钟 ");
        }
        if (seconds != 0) {
            sb.append(seconds).append(" 秒 ");
        }
        if (ms != 0) {
            sb.append(ms).append(" 毫秒");
        }

        return sb.toString();
    }

    /**
     * 找出 list 中重复的数据, 并将之放入到一个新的 List 中返回
     *
     * @param list - 需要进行查重的 List
     * @return <T> List - List 中重复的元素
     */
    public <T> List<T> findCollectionDuplicateElements(Collection<T> list) {
        if (list instanceof Set) {
            return new ArrayList<>();
        }

        HashSet<T> set = new HashSet<>();
        List<T> duplicateElements = new ArrayList<>();

        for (T t : list) {
            if (set.contains(t)) {
                duplicateElements.add(t);
            } else {
                set.add(t);
            }
        }

        return duplicateElements;
    }

    /**
     * 将传入的 list 去重并排序
     *
     * @param list - 需要被去重并排序的 list
     * @return <T> List - 去重并排序之后的 ArrayList
     */
    public <T> List<T> collectionDeduplicateAndResort(Collection<T> list) {
        if (list == null) {
            return new ArrayList<>();
        }

        List<T> sortedList;

        Set<T> set = new HashSet<>(list);
        sortedList = new ArrayList<>(set);

        sortedList.sort(Collator.getInstance());

        return sortedList;
    }

    /**
     * 将传入的 list 去重并排序
     *
     * @param list - 需要被去重并排序的 list
     * @return 去重并排序之后的 list
     */
    public List<String> resortList(List<String> list) {
        List<String> sortedList = new ArrayList<>();

        if (!list.isEmpty()) {
            LinkedHashSet<String> set = new LinkedHashSet<>(list.size());
            set.addAll(list);
            sortedList.addAll(set);

            sortedList.sort(Collator.getInstance());
        }

        return sortedList;
    }

    /**
     * 判断 JavCode 的 Release Date 是否已经超出了预设的 filter
     *
     * @param date - JavCode 的 Release Date
     * @return int: -1 - releaseDate + filter < now; 1 - releaseDate + filter > now
     */
    public int isReleaseDateOverFilter(String date) {
        LocalDate releaseDate = LocalDate.parse(date);
        String releaseDateFilter = this.getPropValue("release_date_filter").toUpperCase(Locale.ROOT);
        String[] str = releaseDateFilter.split(Constants.HYPHEN);

        for (String filter : str) {
            if (filter.contains("Y")) {
                long num = Long.parseLong(filter.substring(0, filter.indexOf("Y")));
                releaseDate = releaseDate.plusYears(num);
            } else if (filter.contains("M")) {
                long num = Long.parseLong(filter.substring(0, filter.indexOf("M")));
                releaseDate = releaseDate.plusMonths(num);
            } else if (filter.contains("D")) {
                long num = Long.parseLong(filter.substring(0, filter.indexOf("D")));
                releaseDate = releaseDate.plusDays(num);
            }
        }

        return releaseDate.compareTo(LocalDate.now());
    }

    /**
     * 传入两个List<String>, 通过 flag 求它们的 旧数据/重复的数据/新增的数据
     *
     * @param oldList 旧集合
     * @param newList 新集合
     * @param flag    1 - 旧数据(差集); 2 - 重复的数据(交集); 3 - 新增的数据(差集)
     */
    public static List<String> getCompareList(List<String> oldList, List<String> newList, Integer flag) {
        Map<String, Integer> map = mapCompare(oldList, newList);
        List<String> result;

        List<String> oldData = Lists.newArrayList();
        List<String> addData = Lists.newArrayList();
        List<String> repeatData = Lists.newArrayList();

        map.forEach((key, value) -> {
            if (value == 1) {
                oldData.add(key);
            } else if (value == 2) {
                repeatData.add(key);
            } else {
                addData.add(key);
            }
        });

        if (flag.equals(1)) {
            result = oldData;
        } else if (flag.equals(2)) {
            result = repeatData;
        } else {
            result = addData;
        }

        return result;
    }

    /**
     * 对比两个 List<String>, 返回 List 并集
     *
     * @param oldList - 旧集合
     * @param newList - 新集合
     * @return value: 1 - 旧数据(差集); 2 - 重复的数据(交集); 3 - 新增的数据(差集)
     */
    public static Map<String, Integer> mapCompare(List<String> oldList, List<String> newList) {
        //若知道两个list大小区别较大, 以大的list优先处理
        Map<String, Integer> map = new HashMap<>(oldList.size());

        //lambda for循环数据量越大, 效率越高, 小数据建议用普通 for 循环
        oldList.forEach(s -> map.put(s, 1));

        newList.forEach(s -> {
            if (map.get(s) != null) {
                //相同的数据
                map.put(s, 2);
            }

//            if (map.get(s) != null) {
//                //相同的数据
//                map.put(s, 2);
//            } else {
//                //若只是比较不同数据，不需要此步骤，浪费资源
//                map.put(s, 3);
//            }
        });

        return map;
    }

    /**
     * 读取 xlsx 文件内容, 返回一个 List<List<String>>
     *
     * @param filePath - xlsx 文件路径
     */
    public List<List<String>> readFromXLSX(String filePath) {
        File file = new File(filePath);

        if (!file.exists()) {
            logger.error("文件 " + file.getAbsolutePath() + " 不存在.");
        } else {
            FileInputStream fis;
            XSSFWorkbook wb;
            XSSFSheet sheet;

            try {
                fis = new FileInputStream(file);

                wb = new XSSFWorkbook(fis);
                sheet = wb.getSheetAt(0);

                int lastRowNum = sheet.getPhysicalNumberOfRows();

                List<List<String>> list = new ArrayList<>(lastRowNum);
                List<String> rowData;

                XSSFRow row;
                int lastCellNum;

                XSSFCell cell;
                CellType cellType;
                FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();
                CellValue formulaCellValue;
                CellType formulaCellType;

                for (int x = 0; x < lastRowNum; x++) {
                    rowData = new ArrayList<>();

                    row = sheet.getRow(x);
                    lastCellNum = row.getLastCellNum();

                    for (int y = 0; y < lastCellNum; y++) {
                        cell = row.getCell(y);

                        if (cell == null) {
                            rowData.add("");
                        } else {
                            cellType = cell.getCellType();

                            if (cellType.equals(CellType._NONE) || cellType.equals(CellType.BLANK) || cellType.equals(CellType.ERROR)) {
                                rowData.add("");
                            } else if (cellType.equals(CellType.STRING)) {
                                rowData.add(cell.getStringCellValue());
                            } else if (cellType.equals(CellType.NUMERIC)) {
                                rowData.add(Double.toString(cell.getNumericCellValue()));
                            } else if (cellType.equals(CellType.BOOLEAN)) {
                                rowData.add(String.valueOf(cell.getBooleanCellValue()));
                            } else if (cellType.equals(CellType.FORMULA)) {
                                formulaCellValue = evaluator.evaluate(cell);
                                formulaCellType = formulaCellValue.getCellType();

                                if (formulaCellType.equals(CellType._NONE) || formulaCellType.equals(CellType.BLANK) || formulaCellType.equals(CellType.ERROR)) {
                                    rowData.add("");
                                } else if (formulaCellType.equals(CellType.STRING)) {
                                    rowData.add(formulaCellValue.getStringValue());
                                } else if (formulaCellType.equals(CellType.NUMERIC)) {
                                    rowData.add(Double.toString(formulaCellValue.getNumberValue()));
                                } else if (formulaCellType.equals(CellType.BOOLEAN)) {
                                    rowData.add(String.valueOf(formulaCellValue.getBooleanValue()));
                                }
                            }
                        }
                    }

                    list.add(rowData);
                }

                return list;
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }

        return new ArrayList<>();
    }

    /**
     * 将 String headLabel 和 List<List<String>> dataList 写入到 String filePath
     *
     * @param headLabel - 头部标签
     * @param dataList  - 数据列表
     * @param filePath  - 文件路径
     */
    public void writeToXLSX(String filePath, List<String> headLabel, List<List<String>> dataList) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet();

        XSSFRow row;
        XSSFCell cell;

        int firstDataRow = 0;

        if (headLabel != null && !headLabel.isEmpty()) {
            firstDataRow = 1;

            row = sheet.createRow(0);

            //在第一行插入单元格设置值
            for (int i = 0; i < headLabel.size(); i++) {
                cell = row.createCell(i);
                cell.setCellValue(headLabel.get(i));
            }
        }

        for (int x = firstDataRow; x < dataList.size(); x++) {
            row = sheet.createRow(x);

            List<String> data = dataList.get(x);
            for (int y = 0; y < data.size(); y++) {
                cell = row.createCell(y);
                cell.setCellValue(data.get(y));
            }
        }

        File file = new File(filePath);

        OutputStream stream;
        try {
            stream = Files.newOutputStream(file.toPath());
            workbook.write(stream);

            stream.close();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 根据文件路径读取 CSV 文件的内容
     *
     * @param filePath - CSV 文件的绝对地址
     */
    public List<String> readFromCSV(String filePath) {
        ArrayList<String> dataList = new ArrayList<>();
        BufferedReader buffReader = null;
        try {
            //构建文件对象
            File csvFile = new File(filePath);
            //判断文件是否存在
            if (!csvFile.exists()) {
                logger.error("文件 " + filePath + " 不存在.");
                return dataList;
            }
            //构建字符输入流
            FileReader fileReader = new FileReader(csvFile);
            //构建缓存字符输入流
            buffReader = new BufferedReader(fileReader);
            String line;
            //根据合适的换行符来读取一行数据,赋值给line
            while ((line = buffReader.readLine()) != null) {
                if (StringUtils.isNotBlank(line)) {
                    //数据不为空则加入列表
                    dataList.add(line);
                }
            }
        } catch (Exception e) {
            logger.error("读取 CSV 文件发生异常: " + filePath);
            logger.error(e.getMessage(), e);
        } finally {
            try {
                //关闭流
                if (buffReader != null) {
                    buffReader.close();
                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }

        return dataList;
    }

    /**
     * 将 String headLabel 和 String data 写入到 String filePath
     *
     * @param headLabel - 头部标签
     * @param data      - 待写入数据
     * @param filePath  - 文件路径
     * @param addFlag   - 是否追加
     */
    public void writeToCSVFromSingleLine(String filePath, String headLabel, String data, String charsetName, boolean addFlag) {
        BufferedWriter buffWriter = null;
        try {
            //构建缓存字符输出流 (不推荐使用 OutputStreamWriter)
            //buffWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(filePath)), StandardCharsets.UTF_8), 1024);
            buffWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath, addFlag), charsetName), 1024);

            //头部不为空则写入头部, 并且换行
            if (StringUtils.isNotBlank(headLabel)) {
                buffWriter.write(headLabel);
                buffWriter.newLine();
            }

            if (StringUtils.isNotBlank(data)) {
                buffWriter.write(data);
                buffWriter.newLine(); //文件写完最后一个换行不用处理
            }

            //刷新流, 也就是把缓存中剩余的内容输出到文件
            buffWriter.flush();
        } catch (Exception e) {
            logger.error("CSV 写入出现异常: " + filePath);
            logger.error(e.getMessage(), e);
        } finally {
            try {
                //关闭流
                if (buffWriter != null) {
                    buffWriter.close();
                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * 将 String headLabel 和 List<String> dataList 写入到 String filePath
     *
     * @param headLabel - 头部标签
     * @param dataList  - 待写入数据列表
     * @param filePath  - 文件路径
     * @param addFlag   - 是否追加
     */
    public void writeToCSVFromList(String filePath, String headLabel, List<String> dataList, String charsetName, boolean addFlag) {
        BufferedWriter buffWriter = null;
        try {
            //构建缓存字符输出流 (不推荐使用 OutputStreamWriter)
            //buffWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(filePath)), StandardCharsets.UTF_8), 1024);
            buffWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath, addFlag), charsetName), 1024);

            //头部不为空则写入头部, 并且换行
            if (StringUtils.isNotBlank(headLabel)) {
                buffWriter.write(headLabel);
                buffWriter.newLine();
            }
            //遍历list
            for (String rowStr : dataList) {
                //如果数据不为空, 则写入文件内容, 并且换行
                if (StringUtils.isNotBlank(rowStr)) {
                    buffWriter.write(rowStr);
                    buffWriter.newLine(); //文件写完最后一个换行不用处理
                }
            }
            //刷新流, 也就是把缓存中剩余的内容输出到文件
            buffWriter.flush();
        } catch (Exception e) {
            logger.error("CSV 写入出现异常: " + filePath);
            logger.error(e.getMessage(), e);
        } finally {
            try {
                //关闭流
                if (buffWriter != null) {
                    buffWriter.close();
                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Excel, CSV 列号转换成数字, 方便 poi 后续操作
     *
     * @param excelNum - excel, csv 文件中对应的列号
     */
    public int excelNum2Digit(String excelNum) {
        char[] chs = excelNum.toCharArray();
        int digit = 0;

        /**
         * B*26^2 + C*26^1 + F*26^0 = ((0*26 + B)*26 + C)*26 + F
         */
        for (char ch : chs) {
            digit = digit * 26 + (ch - 'A');
        }

        return digit;
    }

    /**
     * 在准备 Excel, CSV 数据的时候, 提前对 逗号, 引号 和 换行符 进行数据清理
     * 避免写入之后的数据格式不正确
     *
     * @param data - 需要进行数据清理的内容
     */
    public String escapeSpecialCharacters(String data) {
        String escapedData = data.replaceAll("\\R", " ");

        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }

        return escapedData;
    }

    /**
     * 在读取某些文本文件内容的时候(例如 csv文件)
     * 可能文本内本身的内容就带有多余的 双引号, 需要进行一些数据清理
     * 避免读取之后的数据格式不正确
     *
     * @param data - 需要进行数据清理的内容
     */
    public String clearStartAndEndQuote(String data) {
        if (data != null && data.length() >= 2) {
            if (data.indexOf("\"") == 0) data = data.substring(1); //去掉第一个 "
            if (data.lastIndexOf("\"") == (data.length() - 1)) data = data.substring(0, data.length() - 1); //去掉最后一个 "

            data = data.replaceAll("\"\"", "\""); //把两个双引号换成一个双引号
        }

        return data;
    }

    /**
     * 生成 times 个随机 2 位小数
     * @param times - 生成小数的个数
     */
    public void generateRandomDecimal(int times) {
        Random random = new Random();
        Double decimal;
        for (int i=0; i<times; i++) {
            decimal = random.nextDouble();
            System.out.println((double) Math.round(decimal * 100) / 100);
        }
    }

    /**
     * 生成 times 个随机整数
     * @param times - 生成整数的个数
     * @param minimum - 整数的最小边界
     * @param maximization - 整数的最大边界
     */
    public void generateRandomInt(int times, int minimum, int maximization) {
        Random random = new Random();
        for (int i=0; i<times; i++) {
            System.out.println(random.nextInt(maximization) + minimum);
        }
    }

    public static void main(String[] args) {
        new Utils().generateRandomInt(50, 200, 300);
    }

}
