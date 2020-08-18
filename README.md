# flink-bug-replay

修改Rule对象的@Data/@Value调整序列化器使用

当使用Kryo序列化器时能输出正确结果
(Rule(rid=r1, ua=null, timeRange=null, dimension=null, filters=null, timestamp=10003),MyLog(ua=ua1, type=null, timestamp=1000))
(Rule(rid=r1, ua=null, timeRange=null, dimension=null, filters=null, timestamp=10003),MyLog(ua=ua1, type=null, timestamp=1001))
(Rule(rid=r1, ua=null, timeRange=null, dimension=null, filters=null, timestamp=10003),MyLog(ua=ua1, type=null, timestamp=1002))

当使用POJO序列化器时会抛异常
Caused by: java.lang.IllegalArgumentException: Can not set java.lang.String field org.example.vo.Rule.dimension to org.example.vo.MyLog
at sun.reflect.UnsafeFieldAccessorImpl.throwSetIllegalArgumentException(UnsafeFieldAccessorImpl.java:167)
at sun.reflect.UnsafeFieldAccessorImpl.throwSetIllegalArgumentException(UnsafeFieldAccessorImpl.java:171)
at sun.reflect.UnsafeFieldAccessorImpl.ensureObj(UnsafeFieldAccessorImpl.java:58)
at sun.reflect.UnsafeObjectFieldAccessorImpl.set(UnsafeObjectFieldAccessorImpl.java:75)
at java.lang.reflect.Field.set(Field.java:764)
at org.apache.flink.api.java.typeutils.runtime.PojoSerializer.initializeFields(PojoSerializer.java:209)
at org.apache.flink.api.java.typeutils.runtime.PojoSerializer.deserialize(PojoSerializer.java:390)
at org.apache.flink.api.java.typeutils.runtime.TupleSerializer.deserialize(TupleSerializer.java:151)
at org.apache.flink.api.java.typeutils.runtime.TupleSerializer.deserialize(TupleSerializer.java:37)
at org.apache.flink.streaming.runtime.streamrecord.StreamElementSerializer.deserialize(StreamElementSerializer.java:202)
at org.apache.flink.streaming.runtime.streamrecord.StreamElementSerializer.deserialize(StreamElementSerializer.java:46)
at org.apache.flink.runtime.plugable.NonReusingDeserializationDelegate.read(NonReusingDeserializationDelegate.java:55)
at org.apache.flink.runtime.io.network.api.serialization.SpillingAdaptiveSpanningRecordDeserializer.getNextRecord(SpillingAdaptiveSpanningRecordDeserializer.java:106)
at org.apache.flink.streaming.runtime.io.StreamTaskNetworkInput.emitNext(StreamTaskNetworkInput.java:121)
