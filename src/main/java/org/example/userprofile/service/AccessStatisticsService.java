package org.example.userprofile.service;

import org.example.userprofile.dto.AccessStatistics;
import org.example.userprofile.entity.NetflowLog;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 基础访问统计服务
 */
public class AccessStatisticsService {

    /**
     * 计算基础访问统计
     */
    public AccessStatistics calculateStatistics(List<NetflowLog> logs) {
        if (logs == null || logs.isEmpty()) {
            return new AccessStatistics(0L, 0L, 0L, 0L);
        }

        AccessStatistics stats = new AccessStatistics();

        // PV（访问次数）
        stats.setPv((long) logs.size());

        // UV（独立用户数）
        Set<String> uniqueUsers = logs.stream()
                .map(NetflowLog::getUserId)
                .collect(Collectors.toSet());
        stats.setUv((long) uniqueUsers.size());

        // 总流量
        long totalBytes = logs.stream()
                .mapToLong(log -> log.getBytes() != null ? log.getBytes() : 0)
                .sum();
        stats.setTotalBytes(totalBytes);

        // 活跃用户数（有访问行为的用户）
        stats.setActiveUsers(stats.getUv());

        // 部门统计
        Map<String, Long> departmentStats = logs.stream()
                .collect(Collectors.groupingBy(
                        NetflowLog::getDepartment,
                        Collectors.counting()
                ));
        stats.setDepartmentStats(departmentStats);

        // 网站分类统计
        Map<String, Long> categoryStats = logs.stream()
                .filter(log -> log.getSiteCategory() != null)
                .collect(Collectors.groupingBy(
                        NetflowLog::getSiteCategory,
                        Collectors.counting()
                ));
        stats.setCategoryStats(categoryStats);

        return stats;
    }

    /**
     * 按时间范围统计
     */
    public AccessStatistics calculateStatisticsByTimeRange(List<NetflowLog> logs,
                                                          LocalDateTime startTime,
                                                          LocalDateTime endTime) {
        List<NetflowLog> filteredLogs = logs.stream()
                .filter(log -> !log.getEventTime().isBefore(startTime))
                .filter(log -> !log.getEventTime().isAfter(endTime))
                .collect(Collectors.toList());

        return calculateStatistics(filteredLogs);
    }

    /**
     * 按部门统计
     */
    public Map<String, AccessStatistics> calculateByDepartment(List<NetflowLog> logs) {
        return logs.stream()
                .collect(Collectors.groupingBy(NetflowLog::getDepartment))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> calculateStatistics(entry.getValue())
                ));
    }

    /**
     * 按天统计趋势
     */
    public Map<LocalDate, AccessStatistics> calculateDailyTrend(List<NetflowLog> logs) {
        Map<LocalDate, List<NetflowLog>> dailyLogs = logs.stream()
                .collect(Collectors.groupingBy(log -> log.getEventTime().toLocalDate()));

        Map<LocalDate, AccessStatistics> trend = new TreeMap<>();
        for (Map.Entry<LocalDate, List<NetflowLog>> entry : dailyLogs.entrySet()) {
            trend.put(entry.getKey(), calculateStatistics(entry.getValue()));
        }

        return trend;
    }

    /**
     * 按小时统计趋势
     */
    public Map<Integer, Long> calculateHourlyTrend(List<NetflowLog> logs) {
        Map<Integer, Long> hourlyStats = new TreeMap<>();

        for (NetflowLog log : logs) {
            int hour = log.getEventTime().getHour();
            hourlyStats.put(hour, hourlyStats.getOrDefault(hour, 0L) + 1);
        }

        return hourlyStats;
    }

    /**
     * 按网站分类统计
     */
    public Map<String, AccessStatistics> calculateByCategory(List<NetflowLog> logs) {
        return logs.stream()
                .filter(log -> log.getSiteCategory() != null)
                .collect(Collectors.groupingBy(NetflowLog::getSiteCategory))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> calculateStatistics(entry.getValue())
                ));
    }

    /**
     * Top N 用户
     */
    public List<Map.Entry<String, Long>> getTopNUsers(List<NetflowLog> logs, int n) {
        Map<String, Long> userPv = logs.stream()
                .collect(Collectors.groupingBy(
                        NetflowLog::getUserId,
                        Collectors.counting()
                ));

        return userPv.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(n)
                .collect(Collectors.toList());
    }

    /**
     * Top N 域名
     */
    public List<Map.Entry<String, Long>> getTopNDomains(List<NetflowLog> logs, int n) {
        Map<String, Long> domainPv = logs.stream()
                .filter(log -> log.getDomain() != null)
                .collect(Collectors.groupingBy(
                        NetflowLog::getDomain,
                        Collectors.counting()
                ));

        return domainPv.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(n)
                .collect(Collectors.toList());
    }
}