package com.snake.smarttools.constant.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 标记 JavDB 当前影片的 <资源状态>
 * <ul>
 *     <li>0 - 没有磁链</li>
 *     <li>1 - 有磁链</li>
 *     <li>2 - 有中文磁链</li>
 * </ul>
 */
@Getter
@AllArgsConstructor
public enum JavDBResourceStatusEnum {
    NO_RESOURCES(0),
    HAS_DOWNLOAD_RESOURCES(1),
    HAS_CHINESE_SUBTITLES_RESOURCES(2),
    ;

    private final Integer resourceStatus;
}
