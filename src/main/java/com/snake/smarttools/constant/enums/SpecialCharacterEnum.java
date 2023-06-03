package com.snake.smarttools.constant.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SpecialCharacterEnum {
    FILE_SEPARATOR("/"),
    DOT("."),
    COMMA(","),
    SEMICOLON(";"),
    SPACE_SEPARATOR(" "),
    UNDERSCORE("_"),
    HYPHEN("-"),
    SPACE_AND_HYPHEN(" - "),
    LEFT_PARENTHESES("("),
    RIGHT_PARENTHESES(")"),
    CARRIAGE_RETURN_TO_LINE("\r\n"),
    ;

    private final String character;
}
