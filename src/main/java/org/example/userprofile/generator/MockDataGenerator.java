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

    // 部门列表及权重（模拟实际公司部门规模差异）
    private static final Map<String, Integer> DEPARTMENT_WEIGHTS = new LinkedHashMap<String, Integer>() {{
        put("研发部", 35);    // 最大的部门
        put("产品部", 12);
        put("销售部", 18);
        put("市场部", 10);
        put("运维部", 8);
        put("测试部", 10);
        put("财务部", 4);
        put("人力资源部", 3);  // 最小的部门
    }};

    // 网站分类及域名映射（含权重：工作相关 vs 非工作相关）
    private static final Map<String, Object[]> SITE_CATEGORIES = new LinkedHashMap<String, Object[]>() {{
        put("技术", new Object[]{new String[]{"github.com", "stackoverflow.com", "cnblogs.com", "juejin.cn", "csdn.net"}, 30, true});
        put("办公", new Object[]{new String[]{"docs.qq.com", "kdocs.cn", "dingtalk.com", "feishu.cn", "work.weixin.qq.com"}, 25, true});
        put("社交", new Object[]{new String[]{"weibo.com", "twitter.com", "facebook.com", "linkedin.com", "zhihu.com"}, 12, false});
        put("娱乐", new Object[]{new String[]{"youtube.com", "bilibili.com", "iqiyi.com", "youku.com", "douyin.com"}, 8, false});
        put("购物", new Object[]{new String[]{"taobao.com", "jd.com", "pinduoduo.com", "amazon.com", "tmall.com"}, 10, false});
        put("新闻", new Object[]{new String[]{"sina.com.cn", "163.com", "qq.com", "people.com.cn", "xinhuanet.com"}, 10, false});
        put("游戏", new Object[]{new String[]{"steam.com", "taptap.cn", "4399.com", "nga.cn", "dmm.com"}, 5, false});
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
     * 加权随机选择部门
     */
    public static String generateDepartment() {
        int totalWeight = DEPARTMENT_WEIGHTS.values().stream().mapToInt(Integer::intValue).sum();
        int random = RANDOM.nextInt(totalWeight);
        int currentWeight = 0;

        for (Map.Entry<String, Integer> entry : DEPARTMENT_WEIGHTS.entrySet()) {
            currentWeight += entry.getValue();
            if (random < currentWeight) {
                return entry.getKey();
            }
        }
        return DEPARTMENT_WEIGHTS.keySet().iterator().next();
    }

    /**
     * 加权随机选择网站（工作相关网站概率更高）
     */
    public static String[] generateSite() {
        int totalWeight = 0;
        for (Object[] categoryData : SITE_CATEGORIES.values()) {
            totalWeight += (Integer) categoryData[1]; // 权重
        }

        int random = RANDOM.nextInt(totalWeight);
        int currentWeight = 0;
        String selectedCategory = null;
        Object[] selectedData = null;

        for (Map.Entry<String, Object[]> entry : SITE_CATEGORIES.entrySet()) {
            Object[] data = entry.getValue();
            currentWeight += (Integer) data[1];
            if (random < currentWeight) {
                selectedCategory = entry.getKey();
                selectedData = data;
                break;
            }
        }

        if (selectedData == null) {
            selectedCategory = SITE_CATEGORIES.keySet().iterator().next();
            selectedData = SITE_CATEGORIES.get(selectedCategory);
        }

        String[] domains = (String[]) selectedData[0];
        String domain = domains[RANDOM.nextInt(domains.length)];
        boolean isWorkRelated = (Boolean) selectedData[2];

        return new String[]{selectedCategory, domain, String.valueOf(isWorkRelated)};
    }

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
     * 生成带时间偏移的时间（工作时间概率更高）
     * 工作时间：8:00-18:00，非工作时间访问量低
     */
    public static LocalDateTime generateWeightedTime(LocalDateTime startTime, LocalDateTime endTime) {
        long totalSeconds = java.time.Duration.between(startTime, endTime).toSeconds();
        long randomSeconds;

        // 85% 概率生成在工作时间内
        if (RANDOM.nextDouble() < 0.85) {
            // 尝试多次找到合适的工作时间
            for (int attempt = 0; attempt < 10; attempt++) {
                randomSeconds = RANDOM.nextInt((int) totalSeconds);
                LocalDateTime candidate = startTime.plusSeconds(randomSeconds);
                int hour = candidate.getHour();

                // 工作时间 8:00-18:00
                if (hour >= 8 && hour < 18) {
                    return candidate;
                }
            }
        }

        // 如果没找到，或者落在15%的非工作时间
        randomSeconds = RANDOM.nextInt((int) totalSeconds);
        return startTime.plusSeconds(randomSeconds);
    }

    /**
     * 生成单条网络流量日志（为指定用户）
     */
    public static NetflowLog generateLogForUser(String userId, String department,
                                                 LocalDateTime startTime, LocalDateTime endTime) {
        LocalDateTime eventTime = generateWeightedTime(startTime, endTime);
        String[] site = generateSite();
        String category = site[0];
        String domain = site[1];

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
     * 生成单条网络流量日志（随机用户）
     */
    public static NetflowLog generateLog(LocalDateTime baseTime) {
        String department = generateDepartment();
        String userId = generateUserId(department);
        return generateLogForUser(userId, department, baseTime, baseTime.plusDays(1));
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
     * 批量生成日志（基于员工生成）
     */
    public static List<NetflowLog> generateLogs(LocalDateTime startTime,
                                                 LocalDateTime endTime,
                                                 int totalLogs) {
        List<NetflowLog> logs = new ArrayList<>();

        // 生成员工数据（人数少一些）
        int employeeCount = Math.max(20, totalLogs / 100); // 每100条日志大约1个人
        Map<String, String> employees = generateEmployees(employeeCount);
        List<String> userIds = new ArrayList<>(employees.keySet());
        List<String> departments = new ArrayList<>();
        for (String userId : userIds) {
            // 从userId推断部门（通过重新生成）
            departments.add(generateDepartmentFromUserId(userId));
        }

        // 为每个用户生成不同数量的日志（更丰富）
        for (int i = 0; i < totalLogs; i++) {
            // 随机选择用户
            int userIndex = RANDOM.nextInt(userIds.size());
            String userId = userIds.get(userIndex);
            String department = departments.get(userIndex);

            // 为该用户生成一条日志
            NetflowLog log = generateLogForUser(userId, department, startTime, endTime);
            logs.add(log);
        }

        // 按时间排序
        logs.sort(Comparator.comparing(NetflowLog::getEventTime));

        return logs;
    }

    /**
     * 从userId推断部门
     */
    private static String generateDepartmentFromUserId(String userId) {
        // 使用userId的hash值选择部门，确保一致性
        int hash = Math.abs(userId.hashCode());
        int totalWeight = DEPARTMENT_WEIGHTS.values().stream().mapToInt(Integer::intValue).sum();
        int random = hash % totalWeight;
        int currentWeight = 0;

        for (Map.Entry<String, Integer> entry : DEPARTMENT_WEIGHTS.entrySet()) {
            currentWeight += entry.getValue();
            if (random < currentWeight) {
                return entry.getKey();
            }
        }
        return DEPARTMENT_WEIGHTS.keySet().iterator().next();
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
     * 生成模拟员工数据（各部门人数不均匀）
     */
    public static Map<String, String> generateEmployees(int count) {
        Map<String, String> employees = new HashMap<>();
        Map<String, Integer> departmentCounts = new HashMap<>();
        String[] firstNames = {"张", "李", "王", "刘", "陈", "杨", "赵", "黄", "周", "吴"};
        String[] lastNames = {"伟", "芳", "娜", "秀英", "敏", "静", "丽", "强", "磊", "军"};

        // 先生成各部门人数
        for (String dept : DEPARTMENT_WEIGHTS.keySet()) {
            departmentCounts.put(dept, 0);
        }

        // 按权重分配员工到各部门
        for (int i = 0; i < count; i++) {
            String department = generateDepartment();
            departmentCounts.put(department, departmentCounts.get(department) + 1);
        }

        // 为每个部门生成员工
        for (Map.Entry<String, Integer> entry : departmentCounts.entrySet()) {
            String department = entry.getKey();
            int deptCount = entry.getValue();

            for (int i = 0; i < deptCount; i++) {
                String userId = generateUserId(department);
                String userName = firstNames[RANDOM.nextInt(firstNames.length)] +
                        lastNames[RANDOM.nextInt(lastNames.length)];
                employees.put(userId, userName);
            }
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
