package org.example.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Hma日志数据VO，JsonProperty用于字段名映射
 */
@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class MyLog implements LogEvent {
    /** 项目ID */
    String ua;
    /** 日志类型 */
    String type;
    /** 事件时间戳 */
    Long   timestamp;

    /**
     * 提取时间戳,用作事件时间
     *
     * @return 时间戳
     */
    @Override
    public Long extractTimestamp() {
        return getTimestamp();
    }
}
