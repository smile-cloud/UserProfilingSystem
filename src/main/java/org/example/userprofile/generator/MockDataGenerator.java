package org.example.userprofile.generator;

import org.example.userprofile.entity.NetflowLog;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 模拟数据生成器
 */
public class MockDataGenerator {

    private static final Random RANDOM = new Random();

    // 部门列表
    private static final String[] DEPARTMENTS = {
            "研发部", "产品部", "市场部", "销售部", "人力资源部", "财务部", "运维部", "测试部"
    };

    // 网站分类及域名映射
    private static final Map<String, String[]> SITE_CATEGORIES = new LinkedHashMap<String, String[]>() {{
        put("技术", new String[]{"github.com", "stackoverflow.com", "cnblogs.com", "juejin.cn", "csdn.net"});
        put("办公", new String[]{"docs.qq.com", "kdocs.cn", "dingtalk.com", "feishu.cn", "work.weixin.qq.com"});
        put("社交", new String[]{"weibo.com", "twitter.com", "facebook.com", "linkedin.com", "zhihu.com"});
        put("娱乐", new String[]{"youtube.com", "bilibili.com", "iqiyi.com", "youku.com", "douyin.com"});
        put("购物", new String[]{"taobao.com", "jd.com", "pinduoduo.com", "amazon.com", "tmall.com"});
        put("新闻", new String[]{"sina.com.cn", "163.com", "qq.com", "people.com.cn", "xinhuanet.com"});
        put("游戏", new String[]{"steam.com", "taptap.cn", "4399.com", "nga.cn", "dmm.com"});
    }};

    // User Agent 列表
    private static final String[] USER_AGENTS = {
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 Chrome/120.0.0.0",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 Chrome/120.0.0.0",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:121.0) Gecko/20100101 Firefox/121.0",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) Safari/537.36",
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 Chrome/120.0.0.0"
    };

    // HTTP 方法
    private static final String[] METHODS = {"GET", "POST", "PUT", "DELETE"};

    /**
     * 生成单个用户ID
     */
    public static String generateUserId(String department) {
        int deptCode = department.hashCode() % 1000;
        int userNum = 1000 + RANDOM.nextInt(9000);
        return "E" + String.format("%03d%04d", Math.abs(deptCode), userNum);
    }

    /**
     * 生成IP地址
     */
    public static String generateIp() {
        return String.format("%d.%d.%d.%d",
                10 + RANDOM.nextInt(20),
                RANDOM.nextInt(256),
                RANDOM.nextInt(256),
                RANDOM.nextInt(256));
    }

    /**
     * 生成网站分类和域名
     */
    public static String[] generateSite() {
        List<String> categories = new ArrayList<>(SITE_CATEGORIES.keySet());
        String category = categories.get(RANDOM.nextInt(categories.size()));
        String[] domains = SITE_CATEGORIES.get(category);
        String domain = domains[RANDOM.nextInt(domains.length)];

        return new String[]{category, domain};
    }

    /**
     * 生成流量大小（字节）
     */
    public static long generateBytes() {
        // 80% 的访问是小流量 (< 1MB)
        if (RANDOM.nextDouble() < 0.8) {
            return 1000 + RANDOM.nextInt(999000);
        }
        // 15% 是中等流量 (1-10MB)
        else if (RANDOM.nextDouble() < 0.95) {
            return 1024000 + RANDOM.nextInt(9000000);
        }
        // 5% 是大流量 (> 10MB)
        else {
            return 10485760 + RANDOM.nextInt(50000000);
        }
    }

    /**
     * 生成单条网络流量日志
     */
    public static NetflowLog generateLog(LocalDateTime baseTime) {
        String department = DEPARTMENTS[RANDOM.nextInt(DEPARTMENTS.length)];
        String userId = generateUserId(department);
        String[] site = generateSite();
        String category = site[0];
        String domain = site[1];

        // 生成随机时间（在基准时间前后1小时内）
        int minutesOffset = RANDOM.nextInt(120) - 60;
        LocalDateTime eventTime = baseTime.plusMinutes(minutesOffset);

        // 构造URL
        String url = String.format("https://%s/%s", domain, generatePath());

        NetflowLog log = new NetflowLog();
        log.setEventTime(eventTime);
        log.setUserId(userId);
        log.setDepartment(department);
        log.setSrcIp(generateIp());
        log.setDstIp(generateIp());
        log.setDomain(domain);
        log.setUrl(url);
        log.setMethod(METHODS[RANDOM.nextInt(METHODS.length)]);
        log.setBytes(generateBytes());
        log.setUserAgent(USER_AGENTS[RANDOM.nextInt(USER_AGENTS.length)]);
        log.setSiteCategory(category);

        return log;
    }

    /**
     * 生成URL路径
     */
    private static String generatePath() {
        String[] paths = {"api/v1/data", "home", "detail/123", "search?q=test",
                "user/profile", "images/2024/12/file.jpg", "docs/guide", "post/45678"};
        return paths[RANDOM.nextInt(paths.length)];
    }

    /**
     * 批量生成日志
     */
    public static List<NetflowLog> generateLogs(LocalDateTime startTime,
                                                 LocalDateTime endTime,
                                                 int totalLogs) {
        List<NetflowLog> logs = new ArrayList<>();
        long totalMinutes = java.time.Duration.between(startTime, endTime).toMinutes();
        int logsPerMinute = totalLogs / Math.max((int) totalMinutes, 1);

        for (int i = 0; i < totalLogs; i++) {
            // 随机生成时间
            long randomMinutes = RANDOM.nextInt((int) totalMinutes);
            LocalDateTime eventTime = startTime.plusMinutes(randomMinutes);

            logs.add(generateLog(eventTime));
        }

        // 按时间排序
        logs.sort(Comparator.comparing(NetflowLog::getEventTime));

        return logs;
    }

    /**
     * 为特定用户生成日志
     */
    public static List<NetflowLog> generateLogsForUser(String userId,
                                                        String department,
                                                        LocalDateTime startTime,
                                                        LocalDateTime endTime,
                                                        int logCount) {
        List<NetflowLog> logs = new ArrayList<>();
        long totalMinutes = java.time.Duration.between(startTime, endTime).toMinutes();

        for (int i = 0; i < logCount; i++) {
            String[] site = generateSite();
            String category = site[0];
            String domain = site[1];

            // 随机生成时间
            long randomMinutes = RANDOM.nextInt((int) totalMinutes);
            LocalDateTime eventTime = startTime.plusMinutes(randomMinutes);

            // 构造URL
            String url = String.format("https://%s/%s", domain, generatePath());

            NetflowLog log = new NetflowLog();
            log.setEventTime(eventTime);
            log.setUserId(userId);
            log.setDepartment(department);
            log.setSrcIp(generateIp());
            log.setDstIp(generateIp());
            log.setDomain(domain);
            log.setUrl(url);
            log.setMethod(METHODS[RANDOM.nextInt(METHODS.length)]);
            log.setBytes(generateBytes());
            log.setUserAgent(USER_AGENTS[RANDOM.nextInt(USER_AGENTS.length)]);
            log.setSiteCategory(category);

            logs.add(log);
        }

        logs.sort(Comparator.comparing(NetflowLog::getEventTime));

        return logs;
    }

    /**
     * 生成模拟员工数据
     */
    public static Map<String, String> generateEmployees(int count) {
        Map<String, String> employees = new HashMap<>();
        String[] firstNames = {"张", "李", "王", "刘", "陈", "杨", "赵", "黄", "周", "吴"};
        String[] lastNames = {"伟", "芳", "娜", "秀英", "敏", "静", "丽", "强", "磊", "军"};

        for (int i = 0; i < count; i++) {
            String department = DEPARTMENTS[RANDOM.nextInt(DEPARTMENTS.length)];
            String userId = generateUserId(department);
            String userName = firstNames[RANDOM.nextInt(firstNames.length)] +
                    lastNames[RANDOM.nextInt(lastNames.length)];
            employees.put(userId, userName);
        }

        return employees;
    }

    /**
     * 导出日志为JSON格式
     */
    public static String exportToJson(List<NetflowLog> logs) {
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");

        for (int i = 0; i < logs.size(); i++) {
            NetflowLog log = logs.get(i);
            sb.append("  {\n");
            sb.append("    \"timestamp\": \"").append(log.getEventTime()).append("\",\n");
            sb.append("    \"src_ip\": \"").append(log.getSrcIp()).append("\",\n");
            sb.append("    \"user_id\": \"").append(log.getUserId()).append("\",\n");
            sb.append("    \"department\": \"").append(log.getDepartment()).append("\",\n");
            sb.append("    \"dst_ip\": \"").append(log.getDstIp()).append("\",\n");
            sb.append("    \"domain\": \"").append(log.getDomain()).append("\",\n");
            sb.append("    \"url\": \"").append(log.getUrl()).append("\",\n");
            sb.append("    \"method\": \"").append(log.getMethod()).append("\",\n");
            sb.append("    \"bytes\": ").append(log.getBytes()).append(",\n");
            sb.append("    \"user_agent\": \"").append(log.getUserAgent()).append("\",\n");
            sb.append("    \"site_category\": \"").append(log.getSiteCategory()).append("\"\n");
            sb.append("  }");
            if (i < logs.size() - 1) {
                sb.append(",");
            }
            sb.append("\n");
        }

        sb.append("]");
        return sb.toString();
    }
}
