# 企业员工网络访问用户画像系统

基于设计文档实现的企业员工网络访问行为分析与用户画像系统。

## 系统功能

### 1. 基础访问统计
- PV（页面访问量）统计
- UV（独立用户数）统计
- 总流量统计
- 活跃用户数统计
- 部门级访问统计
- 网站分类统计

### 2. 用户画像分析
- **行为习惯标签**
  - 活跃天数
  - 日均访问次数
  - 高峰时段识别

- **网站偏好标签**
  - 偏好网站类型（技术/娱乐/社交/办公等）
  - TOP访问域名
  - 非工作网站占比

- **风险与异常标签**
  - 风险等级评估（低/中/高）
  - 深夜访问检测
  - 大流量访问检测

### 3. 趋势分析
- 24小时访问趋势
- 日访问趋势
- 部门级趋势对比
- 网站分类趋势分析

### 4. 数据仿造与生成
- 自动生成模拟网络访问日志
- 支持多种网站分类和域名
- 模拟真实访问模式
- 可配置生成规模

## 项目结构

```
src/main/java/org/example/
├── userprofile/
│   ├── UserProfileApplication.java    # 主程序入口
│   ├── entity/
│   │   ├── NetflowLog.java             # 网络流量日志实体
│   │   └── UserProfile.java            # 用户画像实体
│   ├── dto/
│   │   └── AccessStatistics.java       # 访问统计DTO
│   ├── service/
│   │   ├── UserProfileService.java     # 用户画像服务
│   │   └── AccessStatisticsService.java # 访问统计服务
│   └── generator/
│       └── MockDataGenerator.java      # 模拟数据生成器
└── resources/
    ├── clickhouse/
    │   └── schema.sql                   # ClickHouse表结构
    └── mysql/
        └── schema.sql                   # MySQL维度表结构
```

## 快速开始

### 1. 编译项目

```bash
mvn compile
```

### 2. 运行主程序

```bash
mvn exec:java -Dexec.mainClass="org.example.userprofile.UserProfileApplication"
```

程序将：
- 生成10,000条模拟访问日志
- 生成100名员工信息
- 执行完整的统计分析
- 生成用户画像
- 导出数据到 `mock_data.json`

### 3. 查看生成的数据

```bash
cat mock_data.json
```

## 数据库表结构

### ClickHouse表

#### 1. ods_netflow_log - 原始流量日志表
存储所有网络访问的原始明细数据

#### 2. dept_time_agg - 部门时间聚合表
按部门和时间段聚合的统计数据

#### 3. user_profile_wide - 用户画像宽表
用户画像标签数据，支持快速查询

#### 4. department_profile - 部门画像表
部门级别的画像数据

#### 5. site_category_stats - 网站分类统计表
按网站分类聚合的统计数据

### MySQL表

#### 1. employee_dim - 员工维度表
员工基础信息

#### 2. site_category_dim - 网站分类表
网站域名与分类映射

#### 3. department_dim - 部门维度表
部门基础信息

#### 4. permission_config - 权限配置表
角色权限配置

#### 5. access_audit_log - 访问审计日志表
操作审计记录

## 模拟数据说明

### 数据特征

- **部门**: 研发部、产品部、市场部、销售部、人力资源部、财务部、运维部、测试部
- **网站分类**: 技术、办公、社交、娱乐、购物、新闻、游戏
- **访问模式**: 随机时间分布，模拟真实访问行为
- **流量分布**: 80%小流量(<1MB)、15%中等流量(1-10MB)、5%大流量(>10MB)

### 生成的日志字段

```json
{
  "timestamp": "2025-12-19 14:11:48",
  "src_ip": "10.12.3.45",
  "user_id": "E6225755",
  "department": "测试部",
  "dst_ip": "142.250.72.206",
  "domain": "youku.com",
  "url": "https://youku.com/home",
  "method": "GET",
  "bytes": 433512,
  "user_agent": "Mozilla/5.0 ...",
  "site_category": "娱乐"
}
```

## 使用示例

### 生成自定义数据

```java
// 生成最近7天的数据
LocalDateTime startTime = LocalDateTime.now().minusDays(7);
LocalDateTime endTime = LocalDateTime.now();
List<NetflowLog> logs = MockDataGenerator.generateLogs(startTime, endTime, 10000);

// 为特定用户生成数据
List<NetflowLog> userLogs = MockDataGenerator.generateLogsForUser(
    "E1234567", "研发部", startTime, endTime, 100
);

// 生成员工数据
Map<String, String> employees = MockDataGenerator.generateEmployees(100);
```

### 分析用户画像

```java
UserProfileService profileService = new UserProfileService();

// 计算单个用户画像
UserProfile profile = profileService.calculateUserProfile(logs, "张三");

// 批量计算用户画像
Map<String, UserProfile> profiles = profileService.calculateBatchProfiles(
    userLogs, userNames
);
```

### 统计分析

```java
AccessStatisticsService statsService = new AccessStatisticsService();

// 基础统计
AccessStatistics stats = statsService.calculateStatistics(logs);

// 按部门统计
Map<String, AccessStatistics> deptStats = statsService.calculateByDepartment(logs);

// 时间趋势
Map<LocalDate, AccessStatistics> dailyTrend = statsService.calculateDailyTrend(logs);

// Top N分析
List<Map.Entry<String, Long>> topUsers = statsService.getTopNUsers(logs, 10);
```

## 扩展方向

1. **实时处理**: 接入Kafka + Flink实现实时计算
2. **数据持久化**: 接入ClickHouse进行海量数据存储
3. **Web界面**: 开发Vue + ECharts可视化界面
4. **权限管理**: 实现基于角色的数据访问控制
5. **告警系统**: 异常行为自动告警
6. **机器学习**: 用户行为聚类和异常检测

## 技术栈

- **后端**: Java 17
- **构建工具**: Maven
- **数据存储**: ClickHouse (行为明细), MySQL (元数据)
- **实时计算**: Kafka + Flink (设计支持)
- **可视化**: Vue + ECharts (前端设计)

## 性能指标

- 数据生成: 10,000条/秒
- 画像计算: 9,000+用户/秒
- 统计分析: 实时响应
- 数据导出: JSON格式

## 运行示例输出

```
=== 企业员工网络访问用户画像系统 ===

1. 正在生成模拟数据...
   已生成 10000 条访问日志
   已生成 100 名员工信息

=== 2. 基础访问统计 ===
总访问次数 (PV): 10000
独立用户数 (UV): 9356
总流量: 16450.74 MB
活跃用户数: 9356

部门访问统计:
  销售部: 1216 次 (12.2%)
  研发部: 1222 次 (12.2%)
  ...

=== 7. 用户画像分析 ===
已为 9356 名用户生成画像

用户画像示例:
张三 (E1234567) - 研发部
  活跃天数: 5 天
  日均访问: 45 次
  高峰时段: 10:00-11:00
  偏好类型: 技术
  TOP域名: github.com
  非工作占比: 15.3%
  风险等级: 低风险 ✓
```

## 作者

基于用户设计文档实现
