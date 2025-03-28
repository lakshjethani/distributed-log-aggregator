package com.example.logaggregator.service;

import com.example.logaggregator.model.LogEntry;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;

@Service
public class InMemoryLogService implements LogService {
    private final Map<String, ConcurrentSkipListMap<Instant, List<LogEntry>>> logsMap = new ConcurrentHashMap<>();

    @Override
    public void addLog(LogEntry logEntry) {
        logsMap.putIfAbsent(logEntry.getServiceName(), new ConcurrentSkipListMap<>());
        logsMap.get(logEntry.getServiceName())
               .computeIfAbsent(logEntry.getTimestamp(), k -> Collections.synchronizedList(new ArrayList<>()))
               .add(logEntry);
    }

    @Override
    public List<LogEntry> getLogs(String serviceName, Instant startTime, Instant endTime) {
        if (!logsMap.containsKey(serviceName)) return Collections.emptyList();

        return logsMap.get(serviceName).subMap(startTime, true, endTime, true).values().stream()
                .flatMap(List::stream)
                .sorted(Comparator.comparing(LogEntry::getTimestamp))
                .toList();
    }
}
