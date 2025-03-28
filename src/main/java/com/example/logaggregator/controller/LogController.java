package com.example.logaggregator.controller;

import com.example.logaggregator.model.LogEntry;
import com.example.logaggregator.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.*;

@RestController
@RequestMapping("/logs")
public class LogController {

    @Autowired
    private LogService logService;

    @PostMapping
    public ResponseEntity<String> addLog(@RequestBody Map<String, String> requestBody) {
        try {
            String serviceName = requestBody.get("service_name");
            String timestampStr = requestBody.get("timestamp");
            String message = requestBody.get("message");

            if (serviceName == null || timestampStr == null || message == null) {
                return ResponseEntity.badRequest().body("Missing fields");
            }

            LogEntry entry = new LogEntry(serviceName, Instant.parse(timestampStr), message);
            logService.addLog(entry);
            return ResponseEntity.status(HttpStatus.CREATED).body("Log added successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid request: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getLogs(@RequestParam("service") String service,
                                     @RequestParam("start") String start,
                                     @RequestParam("end") String end) {
        try {
            Instant startTime = Instant.parse(start);
            Instant endTime = Instant.parse(end);
            List<LogEntry> logs = logService.getLogs(service, startTime, endTime);

            List<Map<String, String>> response = new ArrayList<>();
            for (LogEntry entry : logs) {
                response.add(Map.of(
                        "timestamp", entry.getTimestamp().toString(),
                        "message", entry.getMessage()
                ));
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid query: " + e.getMessage());
        }
    }
}
