package com.snake.smarttools.constant.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CharsetEnum {
    CHARSET_UTF_8("UTF-8"),
    CHARSET_GBK("GBK"),
    ;

    private final String charset;
}
