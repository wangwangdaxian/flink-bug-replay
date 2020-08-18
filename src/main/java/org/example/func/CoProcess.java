package org.example.func;

import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.co.KeyedCoProcessFunction;
import org.apache.flink.util.Collector;

import org.example.vo.LogEvent;
import org.example.vo.Rule;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.StreamSupport;


public class CoProcess<T extends LogEvent> extends KeyedCoProcessFunction<String, T, Rule, Tuple2<Rule, T>> {

    private static final String EVENT_BUFFER = "EVENT_BUFFER";
    private static final String RULE_BUFFER  = "RULE_BUFFER";

    /** 日志事件缓存 */
    private transient MapState<Long, List<T>>              logBuffer;
    /** 规则事件缓存 */
    private transient MapState<String, Tuple2<Rule, Long>> ruleBuffer;


    @Override
    public void open(Configuration parameters) {
        logBuffer = getRuntimeContext().getMapState(new MapStateDescriptor<>(
                EVENT_BUFFER,
                TypeInformation.of(Long.class),
                TypeInformation.of(new TypeHint<List<T>>() {
                })));
        ruleBuffer = getRuntimeContext().getMapState(new MapStateDescriptor<>(
                RULE_BUFFER,
                TypeInformation.of(String.class),
                TypeInformation.of(new TypeHint<Tuple2<Rule, Long>>() {
                })));
    }

    @Override
    public void processElement1(T logEvent, Context ctx, Collector<Tuple2<Rule, T>> out) throws Exception {
        long time = logEvent.extractTimestamp();
        List<T> eventList = logBuffer.contains(time) ? logBuffer.get(time) : new ArrayList<>();
        eventList.add(logEvent);
        logBuffer.put(time, eventList);
        //遍历规则，输出结果
        if (ruleBuffer.isEmpty()) {
            return;
        }
        for (Tuple2<Rule, Long> ruleLongTuple2 : ruleBuffer.values()) {
            Rule rule = ruleLongTuple2.f0;
            out.collect(Tuple2.of(rule, logEvent));
        }
    }

    @Override
    public void processElement2(Rule rule, Context ctx, Collector<Tuple2<Rule, T>> out) throws Exception {
        long time = ctx.timerService().currentProcessingTime();
        ruleBuffer.put(rule.getRid(), Tuple2.of(rule, time));
        StreamSupport.stream(logBuffer.entries().spliterator(), false)
                     .flatMap(longListEntry -> longListEntry.getValue().stream())
                     .sorted(Comparator.comparingLong(T::extractTimestamp))
                     .forEach(logEvent -> out.collect(Tuple2.of(rule, logEvent)));
    }
}
