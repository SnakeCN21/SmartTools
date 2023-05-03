package com.snake.smarttools.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

@Configuration
public class LocalDateTimeConfig {
    @Bean
    public Converter<String, LocalDateTime> localDateTimeConverter() {
        return new Converter<String, LocalDateTime>() {
            @Override
            public LocalDateTime convert(String s) {
                if (StringUtils.isBlank(s)) {
                    return null;
                }
                // 时间格式化
                // 创建格式化/解析模板
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                // 解析
                LocalDateTime parse = LocalDateTime.parse(s, dateTimeFormatter);
                return parse;
            }
        };
    }

    @Bean
    public Converter<String, LocalDate> localDateConverter() {
        return new Converter<String, LocalDate>() {
            @Override
            public LocalDate convert(String s) {
                if (StringUtils.isBlank(s)) {
                    return null;
                }
                if (Pattern.matches("^\\w{4}-\\w{2}$", s)) {
                    s = s + "-01";
                }
                // 时间格式化
                // 创建格式化/解析模板
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                // 解析
                LocalDate parse = LocalDate.parse(s, dateTimeFormatter);
                return parse;
            }
        };
    }

    @Bean
    public Converter<String, LocalTime> localTimeConverter() {
        return new Converter<String, LocalTime>() {
            @Override
            public LocalTime convert(String s) {
                if (StringUtils.isBlank(s)) {
                    return null;
                }
                // 时间格式化
                // 创建格式化/解析模板
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                // 解析
                LocalTime parse = LocalTime.parse(s, dateTimeFormatter);
                return parse;
            }
        };
    }
}