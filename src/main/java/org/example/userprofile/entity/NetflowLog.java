package org.example.userprofile.entity;

import java.time.LocalDateTime;

/**
 * 网络流量日志实体
 */
public class NetflowLog {
    private LocalDateTime eventTime;
    private String userId;
    private String department;
    private String srcIp;
    private String dstIp;
    private String domain;
    private String url;
    private String method;
    private Long bytes;
    private String userAgent;
    private String siteCategory;

    public NetflowLog() {
    }

    public NetflowLog(LocalDateTime eventTime, String userId, String department,
                     String srcIp, String dstIp, String domain, String url,
                     String method, Long bytes, String userAgent, String siteCategory) {
        this.eventTime = eventTime;
        this.userId = userId;
        this.department = department;
        this.srcIp = srcIp;
        this.dstIp = dstIp;
        this.domain = domain;
        this.url = url;
        this.method = method;
        this.bytes = bytes;
        this.userAgent = userAgent;
        this.siteCategory = siteCategory;
    }

    // Getters and Setters
    public LocalDateTime getEventTime() { return eventTime; }
    public void setEventTime(LocalDateTime eventTime) { this.eventTime = eventTime; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getSrcIp() { return srcIp; }
    public void setSrcIp(String srcIp) { this.srcIp = srcIp; }

    public String getDstIp() { return dstIp; }
    public void setDstIp(String dstIp) { this.dstIp = dstIp; }

    public String getDomain() { return domain; }
    public void setDomain(String domain) { this.domain = domain; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public Long getBytes() { return bytes; }
    public void setBytes(Long bytes) { this.bytes = bytes; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public String getSiteCategory() { return siteCategory; }
    public void setSiteCategory(String siteCategory) { this.siteCategory = siteCategory; }

    @Override
    public String toString() {
        return "NetflowLog{" +
                "eventTime=" + eventTime +
                ", userId='" + userId + '\'' +
                ", department='" + department + '\'' +
                ", domain='" + domain + '\'' +
                ", bytes=" + bytes +
                '}';
    }
}