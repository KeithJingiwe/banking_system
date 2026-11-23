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

    public static void syncFrom(String existingId) {
        java.util.regex.Matcher m =
                java.util.regex.Pattern.compile("^([A-Za-z]+)-?(\\d+)$").matcher(existingId);
        if (!m.matches()) return;
        String prefix = m.group(1);
        int n = Integer.parseInt(m.group(2));
        COUNTERS.computeIfAbsent(prefix, p -> new AtomicInteger()).accumulateAndGet(n, Math::max);
    }
}
