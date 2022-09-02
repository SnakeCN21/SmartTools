package com.snake.utils;


import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
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

    public static void main(String[] args) {

    }

}
