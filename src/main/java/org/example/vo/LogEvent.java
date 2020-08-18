package org.example.vo;

/**
 * 业务日志的抽象接口
 */
public interface LogEvent {

    /**
     * 提取时间戳,用作事件时间
     *
     * @return 时间戳
     */
    Long extractTimestamp();
}
