/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.example;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import org.example.func.CoProcess;
import org.example.vo.MyLog;
import org.example.vo.Rule;

/**
 * 修改Rule对象的@Data/@Value调整序列化器使用，
 * 当使用Kryo序列化器时能输出正确结果
 * (Rule(rid=r1, ua=null, timeRange=null, dimension=null, filters=null, timestamp=10003),MyLog(ua=ua1, type=null, timestamp=1000))
 * (Rule(rid=r1, ua=null, timeRange=null, dimension=null, filters=null, timestamp=10003),MyLog(ua=ua1, type=null, timestamp=1001))
 * (Rule(rid=r1, ua=null, timeRange=null, dimension=null, filters=null, timestamp=10003),MyLog(ua=ua1, type=null, timestamp=1002))
 * <p>
 * 当使用POJO序列化器时会抛异常
 * java.lang.IllegalArgumentException: Can not set java.lang.String field org.example.vo.Rule.dimension to org.example.vo.MyLog
 */
public class StreamingJob {

    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        SingleOutputStreamOperator<MyLog> source1 = env.fromElements(logs);
        SingleOutputStreamOperator<Rule> source2 = env.fromElements(rules);
        SingleOutputStreamOperator<Tuple2<Rule, MyLog>> processedStream = source1
                .connect(source2)
                .keyBy(value -> "1", value -> "1")
                .process(new CoProcess<>());

        processedStream.keyBy(value -> value.f0.getRid())
                       .print();

        // execute program
        env.execute("Flink Streaming Java API Skeleton");
    }


    static MyLog[] logs = {
            MyLog.builder().ua("ua1").timestamp(1000L).build(),
            MyLog.builder().ua("ua1").timestamp(1001L).build(),
            MyLog.builder().ua("ua1").timestamp(1002L).build(),
            };

    static Rule[] rules = {
            Rule.builder().rid("r1").timestamp(10003L).build()
    };
}
