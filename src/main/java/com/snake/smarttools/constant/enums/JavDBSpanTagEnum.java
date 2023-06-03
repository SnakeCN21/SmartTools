package com.snake.smarttools.constant.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 判断 JavDB 当前影片的 <资源状态>
 * <ul>
 *     <li>含中字磁鏈 - is-warning</li>
 *     <li>含磁鏈 - is-success</li>
 *     <li>今日新種 - is-info</li>
 * </ul>
 */
@Getter
@AllArgsConstructor
public enum JavDBSpanTagEnum {
    DOWNLOADABLE_SPAN_TAG("is-success"),
    CHINESE_SUBTITLES_SPAN_TAG("is-warning"),
    ;

    private final String spanTage;
}
