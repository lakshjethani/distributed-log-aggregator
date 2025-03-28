package com.example.logaggregator.model;

import java.time.Instant;

public class LogEntry {
    private String serviceName;
    private Instant timestamp;
    private String message;

    public LogEntry() {}

    public LogEntry(String serviceName, Instant timestamp, String message) {
        this.serviceName = serviceName;
        this.timestamp = timestamp;
        this.message = message;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
