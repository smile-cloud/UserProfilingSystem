package org.example.userprofile;

import org.example.userprofile.entity.NetflowLog;
import org.example.userprofile.entity.UserProfile;
import org.example.userprofile.generator.MockDataGenerator;
import org.example.userprofile.service.AccessStatisticsService;
import org.example.userprofile.service.UserProfileService;
import org.example.userprofile.dto.AccessStatistics;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户画像系统主程序
 */
public class UserProfileApplication {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) {
        System.out.println("=== 企业员工网络访问用户画像系统 ===\n");

        // 1. 生成模拟数据
        System.out.println("1. 正在生成模拟数据...");
        LocalDateTime startTime = LocalDateTime.now().minusDays(7);
        LocalDateTime endTime = LocalDateTime.now();

        // 生成10000条日志
        List<NetflowLog> allLogs = MockDataGenerator.generateLogs(startTime, endTime, 10000);
        System.out.println("   已生成 " + allLogs.size() + " 条访问日志");

        // 生成员工数据
        Map<String, String> employees = MockDataGenerator.generateEmployees(100);
        System.out.println("   已生成 " + employees.size() + " 名员工信息\n");

        // 2. 基础访问统计
        System.out.println("=== 2. 基础访问统计 ===");
        AccessStatisticsService statsService = new AccessStatisticsService();
        AccessStatistics stats = statsService.calculateStatistics(allLogs);

        System.out.println("总访问次数 (PV): " + stats.getPv());
        System.out.println("独立用户数 (UV): " + stats.getUv());
        System.out.println("总流量: " + String.format("%.2f MB", stats.getTotalBytes() / 1024.0 / 1024.0));
        System.out.println("活跃用户数: " + stats.getActiveUsers());

        System.out.println("\n部门访问统计:");
        stats.getDepartmentStats().forEach((dept, count) ->
                System.out.printf("  %s: %d 次 (%.1f%%)\n",
                        dept, count, count * 100.0 / stats.getPv()));

        System.out.println("\n网站分类统计:");
        stats.getCategoryStats().forEach((category, count) ->
                System.out.printf("  %s: %d 次 (%.1f%%)\n",
                        category, count, count * 100.0 / stats.getPv()));

        // 3. Top N 分析
        System.out.println("\n=== 3. Top 10 用户 ===");
        List<Map.Entry<String, Long>> topUsers = statsService.getTopNUsers(allLogs, 10);
        for (int i = 1; i <= topUsers.size(); i++) {
            Map.Entry<String, Long> entry = topUsers.get(i - 1);
            String userName = employees.getOrDefault(entry.getKey(), "未知");
            System.out.printf("%d. %s (%s): %d 次访问\n", i, userName, entry.getKey(), entry.getValue());
        }

        System.out.println("\n=== 4. Top 10 域名 ===");
        List<Map.Entry<String, Long>> topDomains = statsService.getTopNDomains(allLogs, 10);
        for (int i = 1; i <= topDomains.size(); i++) {
            System.out.printf("%d. %s: %d 次访问\n", i, topDomains.get(i - 1).getKey(), topDomains.get(i - 1).getValue());
        }

        // 5. 时间趋势分析
        System.out.println("\n=== 5. 24小时访问趋势 ===");
        Map<Integer, Long> hourlyTrend = statsService.calculateHourlyTrend(allLogs);
        for (Map.Entry<Integer, Long> entry : hourlyTrend.entrySet()) {
            int hour = entry.getKey();
            long count = entry.getValue();
            int barLength = (int) (count * 50 / Collections.max(hourlyTrend.values()));
            System.out.printf("%02d:00 | %s %d\n", hour, "█".repeat(barLength), count);
        }

        // 6. 部门级统计
        System.out.println("\n=== 6. 部门级统计 ===");
        Map<String, AccessStatistics> deptStats = statsService.calculateByDepartment(allLogs);
        deptStats.forEach((dept, deptStat) -> {
            System.out.println("\n" + dept + ":");
            System.out.printf("  访问次数: %d\n", deptStat.getPv());
            System.out.printf("  活跃用户: %d\n", deptStat.getUv());
            System.out.printf("  总流量: %.2f MB\n", deptStat.getTotalBytes() / 1024.0 / 1024.0);
        });

        // 7. 用户画像分析
        System.out.println("\n=== 7. 用户画像分析 ===");
        UserProfileService profileService = new UserProfileService();

        // 按用户分组日志
        Map<String, List<NetflowLog>> userLogs = allLogs.stream()
                .collect(Collectors.groupingBy(NetflowLog::getUserId));

        // 计算用户画像
        Map<String, UserProfile> profiles = profileService.calculateBatchProfiles(userLogs, employees);

        System.out.println("已为 " + profiles.size() + " 名用户生成画像");

        // 展示部分用户画像
        System.out.println("\n用户画像示例（前5名）:");
        int count = 0;
        for (Map.Entry<String, UserProfile> entry : profiles.entrySet()) {
            if (count++ >= 5) break;
            UserProfile profile = entry.getValue();
            System.out.println("\n" + profile.getUserName() + " (" + profile.getUserId() + ") - " + profile.getDepartment());
            System.out.printf("  活跃天数: %d 天\n", profile.getActiveDays());
            System.out.printf("  日均访问: %d 次\n", profile.getAvgDailyPv());
            System.out.printf("  高峰时段: %s\n", profile.getPeakTimeSlot());
            System.out.printf("  偏好类型: %s\n", profile.getTopSiteCategory());
            System.out.printf("  TOP域名: %s\n", profile.getTopDomain());
            System.out.printf("  非工作占比: %.1f%%\n", profile.getNonWorkRatio() * 100);
            System.out.printf("  总流量: %.2f MB\n", profile.getTotalBytes() / 1024.0 / 1024.0);
            System.out.printf("  风险等级: %s\n", getRiskLevelText(profile.getRiskLevel()));
        }

        // 8. 导出数据
        System.out.println("\n=== 8. 导出数据 ===");
        exportDataToFile(allLogs, profiles, "mock_data.json");
        System.out.println("数据已导出到 mock_data.json");

        System.out.println("\n=== 分析完成 ===");
    }

    /**
     * 获取风险等级文本
     */
    private static String getRiskLevelText(Integer riskLevel) {
        if (riskLevel == null) return "未知";
        switch (riskLevel) {
            case 0: return "低风险 ✓";
            case 1: return "中风险 ⚠";
            case 2: return "高风险 ⚠⚠";
            default: return "未知";
        }
    }

    /**
     * 导出数据到文件
     */
    private static void exportDataToFile(List<NetflowLog> logs, Map<String, UserProfile> profiles, String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("{\n");
            writer.write("  \"logs\": [\n");

            for (int i = 0; i < Math.min(100, logs.size()); i++) {
                NetflowLog log = logs.get(i);
                writer.write("    {\n");
                writer.write("      \"timestamp\": \"" + log.getEventTime().format(FORMATTER) + "\",\n");
                writer.write("      \"user_id\": \"" + log.getUserId() + "\",\n");
                writer.write("      \"department\": \"" + log.getDepartment() + "\",\n");
                writer.write("      \"domain\": \"" + log.getDomain() + "\",\n");
                writer.write("      \"category\": \"" + log.getSiteCategory() + "\",\n");
                writer.write("      \"bytes\": " + log.getBytes() + "\n");
                writer.write("    }");
                if (i < Math.min(100, logs.size()) - 1) {
                    writer.write(",");
                }
                writer.write("\n");
            }

            writer.write("  ],\n");
            writer.write("  \"profiles\": {\n");

            int profileCount = 0;
            for (Map.Entry<String, UserProfile> entry : profiles.entrySet()) {
                UserProfile profile = entry.getValue();
                writer.write("    \"" + profile.getUserId() + "\": {\n");
                writer.write("      \"user_name\": \"" + profile.getUserName() + "\",\n");
                writer.write("      \"department\": \"" + profile.getDepartment() + "\",\n");
                writer.write("      \"active_days\": " + profile.getActiveDays() + ",\n");
                writer.write("      \"avg_daily_pv\": " + profile.getAvgDailyPv() + ",\n");
                writer.write("      \"peak_time_slot\": \"" + profile.getPeakTimeSlot() + "\",\n");
                writer.write("      \"top_site_category\": \"" + profile.getTopSiteCategory() + "\",\n");
                writer.write("      \"non_work_ratio\": " + profile.getNonWorkRatio() + ",\n");
                writer.write("      \"risk_level\": " + profile.getRiskLevel() + "\n");
                writer.write("    }");
                if (profileCount++ < profiles.size() - 1) {
                    writer.write(",");
                }
                writer.write("\n");
            }

            writer.write("  }\n");
            writer.write("}\n");

            System.out.println("导出完成，共 " + logs.size() + " 条日志，" + profiles.size() + " 个用户画像");
        } catch (IOException e) {
            System.err.println("导出失败: " + e.getMessage());
        }
    }
}
