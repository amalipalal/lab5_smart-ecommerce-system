package com.example.ecommerce_system.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PerformanceReportGenerator {

    private static final Pattern LOG_PATTERN = Pattern.compile(
        "\\[(REST|GraphQL)]\\s+([^-]+)\\s+-\\s+Duration:\\s+(\\d+)ms,\\s+Payload Size:\\s+(\\d+)\\s+bytes"
    );

    public static void main(String[] args) {
        if (args.length == 0) {
            printUsageAndExit();
        }

        Map<String, List<MetricData>> restMetrics = new HashMap<>();
        Map<String, List<MetricData>> graphqlMetrics = new HashMap<>();

        parseLogFile(args[0], restMetrics, graphqlMetrics);
        generateReport(restMetrics, graphqlMetrics);
    }

    private static void printUsageAndExit() {
        System.err.println("Usage: java PerformanceReportGenerator <log-file-path>");
        System.err.println("Example: java PerformanceReportGenerator logs/application.log");
        System.exit(1);
    }

    private static void parseLogFile(String logFile, Map<String, List<MetricData>> restMetrics,
                                     Map<String, List<MetricData>> graphqlMetrics) {
        try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
            reader.lines()
                .map(LOG_PATTERN::matcher)
                .filter(Matcher::find)
                .forEach(matcher -> {
                    String type = matcher.group(1);
                    String method = matcher.group(2).trim();
                    MetricData data = new MetricData(
                        Long.parseLong(matcher.group(3)),
                        Long.parseLong(matcher.group(4))
                    );

                    Map<String, List<MetricData>> targetMap = "REST".equals(type) ? restMetrics : graphqlMetrics;
                    targetMap.computeIfAbsent(method, k -> new ArrayList<>()).add(data);
                });
        } catch (Exception e) {
            System.err.println("Error reading log file: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void generateReport(Map<String, List<MetricData>> rest, Map<String, List<MetricData>> graphql) {
        printHeader();
        printEndpointMetrics("REST ENDPOINTS", rest);
        printEndpointMetrics("GRAPHQL ENDPOINTS", graphql);
        printOverallComparison(rest, graphql);
    }

    private static void printHeader() {
        System.out.println("==========================================================");
        System.out.println("           PERFORMANCE COMPARISON REPORT");
        System.out.println("==========================================================\n");
    }

    private static void printEndpointMetrics(String title, Map<String, List<MetricData>> metrics) {
        System.out.println(title);
        System.out.println("----------------------------------------------------------");

        if (metrics.isEmpty()) {
            System.out.println("  No data found\n");
            return;
        }

        metrics.forEach(PerformanceReportGenerator::printMethodMetrics);
    }

    private static void printMethodMetrics(String method, List<MetricData> dataList) {
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
        System.out.printf("    Avg Payload:     %.2f bytes\n\n", payloadStats.getAverage());
    }

    private static void printOverallComparison(Map<String, List<MetricData>> rest, Map<String, List<MetricData>> graphql) {
        System.out.println("OVERALL COMPARISON");
        System.out.println("----------------------------------------------------------");

        ApiStats restStats = calculateStats(rest);
        ApiStats graphqlStats = calculateStats(graphql);

        printApiSummary("REST API", restStats);
        printApiSummary("GraphQL API", graphqlStats);

        if (restStats.avgDuration > 0 && graphqlStats.avgDuration > 0) {
            printComparisonAnalysis(restStats, graphqlStats);
        }

        System.out.println("==========================================================");
    }

    private static ApiStats calculateStats(Map<String, List<MetricData>> metrics) {
        List<MetricData> allData = metrics.values().stream()
            .flatMap(Collection::stream)
            .toList();

        return new ApiStats(
            allData.size(),
            allData.stream().mapToDouble(MetricData::getDuration).average().orElse(0),
            allData.stream().mapToDouble(MetricData::getPayloadSize).average().orElse(0)
        );
    }

    private static void printApiSummary(String name, ApiStats stats) {
        System.out.printf("  %s\n", name);
        System.out.printf("    Total Calls:       %d\n", stats.callCount);
        System.out.printf("    Avg Duration:      %.2fms\n", stats.avgDuration);
        System.out.printf("    Avg Payload Size:  %.2f bytes\n\n", stats.avgPayload);
    }

    private static void printComparisonAnalysis(ApiStats rest, ApiStats graphql) {
        System.out.println("  COMPARISON ANALYSIS");
        System.out.println("  --------------------------------------------------");

        printComparison("Speed", rest.avgDuration, graphql.avgDuration, "ms", "faster");
        printComparison("Payload", rest.avgPayload, graphql.avgPayload, "bytes", "smaller payload");
    }

    private static void printComparison(String metric, double restValue, double graphqlValue,
                                       String unit, String qualifier) {
        double diff = Math.abs(restValue - graphqlValue);
        String winner = restValue < graphqlValue ? "REST" : "GraphQL";
        double percentage = (diff / Math.max(restValue, graphqlValue)) * 100;

        System.out.printf("    %s:     %s is %.2f%s (%.1f%%) %s\n",
            metric, winner, diff, unit, percentage, qualifier);
    }

    private record MetricData(long duration, long payloadSize) {
        public long getDuration() {
            return duration;
        }

        public long getPayloadSize() {
            return payloadSize;
        }
    }

    private record ApiStats(long callCount, double avgDuration, double avgPayload) {}
}
