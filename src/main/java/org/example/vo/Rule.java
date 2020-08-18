package org.example.vo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;


//@Value使用Kryo序列化器,@Data使用POJO序列化器
@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
public class Rule {
    /** 规则ID */
    String  rid;
    /** 项目ID */
    String  ua;
    /** 查询的时间范围 */
    Integer timeRange;
    /** 聚合维度 */
    String  dimension;
    /** 过滤条件 */
    String  filters;
    /** 时间戳 */
    Long    timestamp;
}