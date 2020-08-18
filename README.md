# flink-bug-replay

修改Rule对象的@Data/@Value调整序列化器使用

## 当使用Kryo序列化器时能输出正确结果
(Rule(rid=r1, ua=null, timeRange=null, dimension=null, filters=null, timestamp=10003),MyLog(ua=ua1, type=null, timestamp=1000))<br>
(Rule(rid=r1, ua=null, timeRange=null, dimension=null, filters=null, timestamp=10003),MyLog(ua=ua1, type=null, timestamp=1001))<br>
(Rule(rid=r1, ua=null, timeRange=null, dimension=null, filters=null, timestamp=10003),MyLog(ua=ua1, type=null, timestamp=1002))<br>

## 当使用POJO序列化器时会抛异常

Caused by: java.lang.IllegalArgumentException: Can not set java.lang.String field org.example.vo.Rule.dimension to org.example.vo.MyLog
