package org.example.userprofile.dto;

import java.util.Map;

/**
 * 访问统计数据传输对象
 */
public class AccessStatistics {
    private Long pv;  // 访问次数
    private Long uv;  // 独立用户数
    private Long totalBytes;  // 总流量
    private Long activeUsers;  // 活跃用户数
    private Map<String, Long> departmentStats;  // 部门统计
    private Map<String, Long> categoryStats;  // 网站分类统计

    public AccessStatistics() {
    }

    public AccessStatistics(Long pv, Long uv, Long totalBytes, Long activeUsers) {
        this.pv = pv;
        this.uv = uv;
        this.totalBytes = totalBytes;
        this.activeUsers = activeUsers;
    }

    // Getters and Setters
    public Long getPv() { return pv; }
    public void setPv(Long pv) { this.pv = pv; }

    public Long getUv() { return uv; }
    public void setUv(Long uv) { this.uv = uv; }

    public Long getTotalBytes() { return totalBytes; }
    public void setTotalBytes(Long totalBytes) { this.totalBytes = totalBytes; }

    public Long getActiveUsers() { return activeUsers; }
    public void setActiveUsers(Long activeUsers) { this.activeUsers = activeUsers; }

    public Map<String, Long> getDepartmentStats() { return departmentStats; }
    public void setDepartmentStats(Map<String, Long> departmentStats) { this.departmentStats = departmentStats; }

    public Map<String, Long> getCategoryStats() { return categoryStats; }
    public void setCategoryStats(Map<String, Long> categoryStats) { this.categoryStats = categoryStats; }

    @Override
    public String toString() {
        return "AccessStatistics{" +
                "pv=" + pv +
                ", uv=" + uv +
                ", totalBytes=" + totalBytes +
                ", activeUsers=" + activeUsers +
                '}';
    }
}