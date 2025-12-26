package org.example.userprofile.entity;

import java.time.LocalDateTime;

/**
 * 用户画像实体
 */
public class UserProfile {
    private String userId;
    private String userName;
    private String department;
    private Integer activeDays;
    private Integer avgDailyPv;
    private String peakTimeSlot;
    private String topSiteCategory;
    private String topDomain;
    private Float nonWorkRatio;
    private Long totalBytes;
    private Long avgDailyBytes;
    private Integer riskLevel;
    private LocalDateTime lastUpdateTime;

    public UserProfile() {
    }

    public UserProfile(String userId, String userName, String department) {
        this.userId = userId;
        this.userName = userName;
        this.department = department;
    }

    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public Integer getActiveDays() { return activeDays; }
    public void setActiveDays(Integer activeDays) { this.activeDays = activeDays; }

    public Integer getAvgDailyPv() { return avgDailyPv; }
    public void setAvgDailyPv(Integer avgDailyPv) { this.avgDailyPv = avgDailyPv; }

    public String getPeakTimeSlot() { return peakTimeSlot; }
    public void setPeakTimeSlot(String peakTimeSlot) { this.peakTimeSlot = peakTimeSlot; }

    public String getTopSiteCategory() { return topSiteCategory; }
    public void setTopSiteCategory(String topSiteCategory) { this.topSiteCategory = topSiteCategory; }

    public String getTopDomain() { return topDomain; }
    public void setTopDomain(String topDomain) { this.topDomain = topDomain; }

    public Float getNonWorkRatio() { return nonWorkRatio; }
    public void setNonWorkRatio(Float nonWorkRatio) { this.nonWorkRatio = nonWorkRatio; }

    public Long getTotalBytes() { return totalBytes; }
    public void setTotalBytes(Long totalBytes) { this.totalBytes = totalBytes; }

    public Long getAvgDailyBytes() { return avgDailyBytes; }
    public void setAvgDailyBytes(Long avgDailyBytes) { this.avgDailyBytes = avgDailyBytes; }

    public Integer getRiskLevel() { return riskLevel; }
    public void setRiskLevel(Integer riskLevel) { this.riskLevel = riskLevel; }

    public LocalDateTime getLastUpdateTime() { return lastUpdateTime; }
    public void setLastUpdateTime(LocalDateTime lastUpdateTime) { this.lastUpdateTime = lastUpdateTime; }

    @Override
    public String toString() {
        return "UserProfile{" +
                "userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", department='" + department + '\'' +
                ", activeDays=" + activeDays +
                ", avgDailyPv=" + avgDailyPv +
                ", topSiteCategory='" + topSiteCategory + '\'' +
                ", nonWorkRatio=" + nonWorkRatio +
                ", riskLevel=" + riskLevel +
                '}';
    }
}