package model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public final class IdGenerator {
    private static final Map<String, AtomicInteger> COUNTERS = new ConcurrentHashMap<>();

    private IdGenerator(){}
    public static String next(String prefix){
        int n = COUNTERS.computeIfAbsent(prefix, p -> new AtomicInteger()).incrementAndGet();
        return String.format("%s%02d", prefix, n);
    }
}
