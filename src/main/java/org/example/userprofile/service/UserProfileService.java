package org.example.userprofile.service;

import org.example.userprofile.entity.UserProfile;
import org.example.userprofile.entity.NetflowLog;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 用户画像分析服务
 */
public class UserProfileService {

    /**
     * 计算用户画像
     */
    public UserProfile calculateUserProfile(List<NetflowLog> logs, String userName) {
        if (logs == null || logs.isEmpty()) {
            return null;
        }

        UserProfile profile = new UserProfile();
        profile.setUserId(logs.get(0).getUserId());
        profile.setUserName(userName);
        profile.setDepartment(logs.get(0).getDepartment());
        profile.setLastUpdateTime(LocalDateTime.now());

        // 计算活跃天数
        Set<Integer> activeDays = new HashSet<>();
        for (NetflowLog log : logs) {
            activeDays.add(log.getEventTime().getDayOfYear());
        }
        profile.setActiveDays(activeDays.size());

        // 计算日均访问次数
        int totalPv = logs.size();
        profile.setAvgDailyPv(totalPv / Math.max(activeDays.size(), 1));

        // 计算高峰时段
        profile.setPeakTimeSlot(calculatePeakTimeSlot(logs));

        // 计算偏好网站类型
        profile.setTopSiteCategory(calculateTopSiteCategory(logs));

        // 计算top域名
        profile.setTopDomain(calculateTopDomain(logs));

        // 计算非工作网站占比
        profile.setNonWorkRatio(calculateNonWorkRatio(logs));

        // 计算总流量和日均流量
        long totalBytes = logs.stream().mapToLong(log -> log.getBytes() != null ? log.getBytes() : 0).sum();
        profile.setTotalBytes(totalBytes);
        profile.setAvgDailyBytes(totalBytes / Math.max(activeDays.size(), 1));

        // 计算风险等级
        profile.setRiskLevel(calculateRiskLevel(logs, profile));

        return profile;
    }

    /**
     * 计算高峰时段
     */
    private String calculatePeakTimeSlot(List<NetflowLog> logs) {
        Map<Integer, Integer> hourCount = new HashMap<>();

        for (NetflowLog log : logs) {
            int hour = log.getEventTime().getHour();
            hourCount.put(hour, hourCount.getOrDefault(hour, 0) + 1);
        }

        int maxHour = hourCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(9);

        return String.format("%02d:00-%02d:00", maxHour, maxHour + 1);
    }

    /**
     * 计算偏好网站类型
     */
    private String calculateTopSiteCategory(List<NetflowLog> logs) {
        Map<String, Integer> categoryCount = new HashMap<>();

        for (NetflowLog log : logs) {
            String category = log.getSiteCategory() != null ? log.getSiteCategory() : "未知";
            categoryCount.put(category, categoryCount.getOrDefault(category, 0) + 1);
        }

        return categoryCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("未知");
    }

    /**
     * 计算top域名
     */
    private String calculateTopDomain(List<NetflowLog> logs) {
        Map<String, Integer> domainCount = new HashMap<>();

        for (NetflowLog log : logs) {
            String domain = log.getDomain() != null ? log.getDomain() : "未知";
            domainCount.put(domain, domainCount.getOrDefault(domain, 0) + 1);
        }

        return domainCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("未知");
    }

    /**
     * 计算非工作网站占比
     */
    private Float calculateNonWorkRatio(List<NetflowLog> logs) {
        long totalPv = logs.size();
        if (totalPv == 0) {
            return 0.0f;
        }

        long nonWorkPv = logs.stream()
                .filter(log -> "娱乐".equals(log.getSiteCategory()) ||
                        "社交".equals(log.getSiteCategory()) ||
                        "购物".equals(log.getSiteCategory()) ||
                        "游戏".equals(log.getSiteCategory()))
                .count();

        return (float) nonWorkPv / totalPv;
    }

    /**
     * 计算风险等级
     * 0-低风险, 1-中风险, 2-高风险
     */
    private Integer calculateRiskLevel(List<NetflowLog> logs, UserProfile profile) {
        int riskScore = 0;

        // 深夜高频访问 (22:00-06:00)
        long nightAccessCount = logs.stream()
                .filter(log -> {
                    int hour = log.getEventTime().getHour();
                    return hour >= 22 || hour < 6;
                })
                .count();

        if (nightAccessCount > logs.size() * 0.3) {
            riskScore += 1;
        }

        // 非工作网站占比过高
        if (profile.getNonWorkRatio() > 0.5) {
            riskScore += 1;
        }

        // 大流量外传
        long avgBytes = logs.stream().mapToLong(log -> log.getBytes() != null ? log.getBytes() : 0).sum()
                / Math.max(logs.size(), 1);
        if (avgBytes > 10 * 1024 * 1024) {  // 平均单次访问超过10MB
            riskScore += 1;
        }

        return Math.min(riskScore, 2);
    }

    /**
     * 批量计算用户画像
     */
    public Map<String, UserProfile> calculateBatchProfiles(Map<String, List<NetflowLog>> userLogs,
                                                           Map<String, String> userNames) {
        Map<String, UserProfile> profiles = new HashMap<>();

        for (Map.Entry<String, List<NetflowLog>> entry : userLogs.entrySet()) {
            String userId = entry.getKey();
            List<NetflowLog> logs = entry.getValue();
            String userName = userNames.getOrDefault(userId, "未知用户");

            UserProfile profile = calculateUserProfile(logs, userName);
            if (profile != null) {
                profiles.put(userId, profile);
            }
        }

        return profiles;
    }
}