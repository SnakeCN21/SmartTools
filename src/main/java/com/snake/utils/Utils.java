package com.snake.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
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
     *
     * @return value
     */
    public String getPropValue(String key) {
        String value = "";
        Properties props = new Properties();

        try {
            FileInputStream input = new FileInputStream(new File(Constants.PROP_FILE_NAME));
            props.load(new InputStreamReader(input, Charset.forName("UTF-8")));

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
     *
     * @return List
     */
    public List<String> getPropLists(String key) {
        List<String> list = new ArrayList<String>();

        String value = "";
        Properties props = new Properties();

        try {
            FileInputStream input = new FileInputStream(new File(Constants.PROP_FILE_NAME));
            props.load(new InputStreamReader(input, Charset.forName("UTF-8")));

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
     * 在文件名最后添加一个后缀
     *
     * @param fullName - 完整的文件名(不包含文件路径, 但可能包含文件扩展名)
     * @param suffix - 需要添加的后缀
     * @return
     */
    public String appendNameSuffix(String fullName, String suffix) {
        String newName = "";

        if (fullName.contains(Constants.DOT)) {
            String name = fullName.substring(0, fullName.lastIndexOf(Constants.DOT));
            String extension = fullName.substring(fullName.lastIndexOf(Constants.DOT) + 1);

            newName = name + suffix + Constants.DOT + extension;
        } else {
            newName = fullName + suffix;
        }

        return newName;
    }

    /**
     * 将 long 类型的时间格式转换成可读性更高的时间格式
     * timeDiff 是基于 System.nanoTime() 计算出来的
     *
     * @param timeDiff - 需要转换的long类型时间
     *
     * @return
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
     *
     * @return <T> List - List 中重复的元素
     */
    public <T> List<T> findCollectionDuplicateElements(Collection<T> list) {
        if (list instanceof Set) {
            return new ArrayList<>();
        }

        HashSet<T> set = new HashSet<T>();
        List<T> duplicateElements = new ArrayList<T>();

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
     *
     * @return <T> List - 去重并排序之后的 ArrayList
     */
    public <T> List<T> collectionDeduplicateAndResort(Collection<T> list) {
        if (list == null) {
            return new ArrayList<>();
        }

        List<T> sortedList = new ArrayList<T>();

        Set<T> set = new HashSet<>(list);
        sortedList = new ArrayList<>(set);

        java.util.Collections.sort(sortedList, Collator.getInstance());

        return sortedList;
    }

    /**
     * 将传入的 list 去重并排序
     *
     * @param list - 需要被去重并排序的 list
     *
     * @return 去重并排序之后的 list
     */
    public List<String> resortList(List<String> list) {
        List<String> sortedList = new ArrayList<String>();

        if (!list.isEmpty()) {
            LinkedHashSet<String> set = new LinkedHashSet<String>(list.size());
            set.addAll(list);
            sortedList.addAll(set);

            java.util.Collections.sort(sortedList, Collator.getInstance());
        }

        return sortedList;
    }

    /**
     * 判断 JavCode 的 Release Date 是否已经超出了预设的 filter
     *
     * @param date - JavCdoe 的 Release Date
     *
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

    public static void main(String[] args) {

    }

}
