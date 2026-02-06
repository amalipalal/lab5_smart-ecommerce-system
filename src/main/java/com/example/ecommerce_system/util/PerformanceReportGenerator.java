package com.example.ecommerce_system.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PerformanceReportGenerator {

    private static final Pattern LOG_PATTERN = Pattern.compile(
        "\\[(REST|GraphQL)\\]\\s+([^-]+)\\s+-\\s+Duration:\\s+(\\d+)ms,\\s+Payload Size:\\s+(\\d+)\\s+bytes"
    );

    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Usage: java PerformanceReportGenerator <log-file-path>");
            System.err.println("Example: java PerformanceReportGenerator logs/application.log");
            System.exit(1);
        }

        String logFile = args[0];
        Map<String, List<MetricData>> restMetrics = new HashMap<>();
        Map<String, List<MetricData>> graphqlMetrics = new HashMap<>();

        // Parse log file
        try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher matcher = LOG_PATTERN.matcher(line);
                if (matcher.find()) {
                    String type = matcher.group(1);
                    String method = matcher.group(2).trim();
                    long duration = Long.parseLong(matcher.group(3));
                    long payloadSize = Long.parseLong(matcher.group(4));

                    MetricData data = new MetricData(duration, payloadSize);

                    if ("REST".equals(type)) {
                        restMetrics.computeIfAbsent(method, k -> new ArrayList<>()).add(data);
                    } else {
                        graphqlMetrics.computeIfAbsent(method, k -> new ArrayList<>()).add(data);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error reading log file: " + e.getMessage());
            System.exit(1);
        }

        // Generate report
        generateReport(restMetrics, graphqlMetrics);
    }

    private static void generateReport(Map<String, List<MetricData>> rest, Map<String, List<MetricData>> graphql) {
        System.out.println("==========================================================");
        System.out.println("           PERFORMANCE COMPARISON REPORT");
        System.out.println("==========================================================\n");

        printEndpointMetrics("REST ENDPOINTS", rest);
        printEndpointMetrics("GRAPHQL ENDPOINTS", graphql);
        printOverallComparison(rest, graphql);
    }

    private static void printEndpointMetrics(String title, Map<String, List<MetricData>> metrics) {
        System.out.println(title);
        System.out.println("----------------------------------------------------------");

        if (metrics.isEmpty()) {
            System.out.println("  No data found\n");
            return;
        }

        metrics.forEach((method, dataList) -> {
            DoubleSummaryStatistics durationStats = dataList.stream()
                .mapToDouble(MetricData::getDuration)
                .summaryStatistics();

            DoubleSummaryStatistics payloadStats = dataList.stream()
                .mapToDouble(MetricData::getPayloadSize)
                .summaryStatistics();

            System.out.printf("  %s\n", method);
            System.out.printf("    Calls:           %d\n", durationStats.getCount());
            System.out.printf("    Avg Duration:    %.2fms\n", durationStats.getAverage());
            System.out.printf("    Min Duration:    %.0fms\n", durationStats.getMin());
            System.out.printf("    Max Duration:    %.0fms\n", durationStats.getMax());
            System.out.printf("    Avg Payload:     %.2f bytes\n", payloadStats.getAverage());
            System.out.println();
        });
    }

    private static void printOverallComparison(Map<String, List<MetricData>> rest, Map<String, List<MetricData>> graphql) {
        System.out.println("OVERALL COMPARISON");
        System.out.println("----------------------------------------------------------");

        // REST statistics
        List<MetricData> allRestData = rest.values().stream()
            .flatMap(Collection::stream)
            .toList();

        long restCallCount = allRestData.size();
        double restAvgDuration = allRestData.stream()
            .mapToDouble(MetricData::getDuration)
            .average()
            .orElse(0);
        double restAvgPayload = allRestData.stream()
            .mapToDouble(MetricData::getPayloadSize)
            .average()
            .orElse(0);

        // GraphQL statistics
        List<MetricData> allGraphQLData = graphql.values().stream()
            .flatMap(Collection::stream)
            .toList();

        long graphqlCallCount = allGraphQLData.size();
        double graphqlAvgDuration = allGraphQLData.stream()
            .mapToDouble(MetricData::getDuration)
            .average()
            .orElse(0);
        double graphqlAvgPayload = allGraphQLData.stream()
            .mapToDouble(MetricData::getPayloadSize)
            .average()
            .orElse(0);

        // Print REST summary
        System.out.printf("  REST API\n");
        System.out.printf("    Total Calls:       %d\n", restCallCount);
        System.out.printf("    Avg Duration:      %.2fms\n", restAvgDuration);
        System.out.printf("    Avg Payload Size:  %.2f bytes\n\n", restAvgPayload);

        // Print GraphQL summary
        System.out.printf("  GraphQL API\n");
        System.out.printf("    Total Calls:       %d\n", graphqlCallCount);
        System.out.printf("    Avg Duration:      %.2fms\n", graphqlAvgDuration);
        System.out.printf("    Avg Payload Size:  %.2f bytes\n\n", graphqlAvgPayload);

        // Compare and show winner
        if (restAvgDuration > 0 && graphqlAvgDuration > 0) {
            System.out.println("  COMPARISON ANALYSIS");
            System.out.println("  --------------------------------------------------");

            // Duration comparison
            double durationDiff = Math.abs(restAvgDuration - graphqlAvgDuration);
            String fasterApi = restAvgDuration < graphqlAvgDuration ? "REST" : "GraphQL";
            double durationPercentage = (durationDiff / Math.max(restAvgDuration, graphqlAvgDuration)) * 100;

            System.out.printf("    Speed:     %s is %.2fms (%.1f%%) faster\n",
                fasterApi, durationDiff, durationPercentage);

            // Payload comparison
            double payloadDiff = Math.abs(restAvgPayload - graphqlAvgPayload);
            String smallerPayload = restAvgPayload < graphqlAvgPayload ? "REST" : "GraphQL";
            double payloadPercentage = (payloadDiff / Math.max(restAvgPayload, graphqlAvgPayload)) * 100;

            System.out.printf("    Payload:   %s has %.2f bytes (%.1f%%) smaller payload\n",
                smallerPayload, payloadDiff, payloadPercentage);
        }

        System.out.println("==========================================================");
    }

    private static class MetricData {
        private final long duration;
        private final long payloadSize;

        public MetricData(long duration, long payloadSize) {
            this.duration = duration;
            this.payloadSize = payloadSize;
        }

        public long getDuration() {
            return duration;
        }

        public long getPayloadSize() {
            return payloadSize;
        }
    }
}
