package com.example.logaggregator.service;

import com.example.logaggregator.model.LogEntry;

import java.time.Instant;
import java.util.List;

public interface LogService {
    void addLog(LogEntry logEntry);
    List<LogEntry> getLogs(String serviceName, Instant startTime, Instant endTime);
}
