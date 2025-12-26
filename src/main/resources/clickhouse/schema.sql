-- 企业员工网络访问用户画像系统 - ClickHouse 数据模型

-- 5.1 ODS原始明细表
CREATE DATABASE IF NOT EXISTS user_profile;

USE user_profile;

-- 原始网络流量日志表
CREATE TABLE IF NOT EXISTS ods_netflow_log (
    event_time DateTime,
    user_id String,
    department String,
    src_ip String,
    dst_ip String,
    domain String,
    url String,
    method String,
    bytes UInt64,
    user_agent String,
    site_category String  -- 网站分类：技术/娱乐/社交/办公等
) ENGINE = MergeTree
PARTITION BY toDate(event_time)
ORDER BY (event_time, user_id)
SETTINGS index_granularity = 8192;

-- 7.3 部门时间聚合表
CREATE TABLE IF NOT EXISTS dept_time_agg (
    department String,
    time_bucket DateTime,
    pv UInt32,
    uv UInt32,
    bytes UInt64,
    avg_bytes_per_user UInt32
) ENGINE = AggregatingMergeTree()
PARTITION BY toYYYYMM(time_bucket)
ORDER BY (department, time_bucket)
SETTINGS index_granularity = 8192;

-- 8.3 用户画像宽表
CREATE TABLE IF NOT EXISTS user_profile_wide (
    user_id String,
    user_name String,
    department String,
    active_days UInt16,
    avg_daily_pv UInt32,
    peak_time_slot String,  -- 高峰时段，如"09:00-10:00"
    top_site_category String,
    top_domain String,
    non_work_ratio Float32,  -- 非工作网站占比
    total_bytes UInt64,
    avg_daily_bytes UInt64,
    risk_level UInt8,  -- 风险等级：0-低, 1-中, 2-高
    last_update_time DateTime
) ENGINE = ReplacingMergeTree(last_update_time)
ORDER BY user_id
SETTINGS index_granularity = 8192;

-- 部门画像表
CREATE TABLE IF NOT EXISTS department_profile (
    department String,
    total_users UInt32,
    active_users UInt32,
    total_pv UInt64,
    total_bytes UInt64,
    avg_daily_pv_per_user UInt32,
    top_site_category String,
    non_work_ratio Float32,
    last_update_time DateTime
) ENGINE = ReplacingMergeTree(last_update_time)
ORDER BY department
SETTINGS index_granularity = 8192;

-- 网站分类统计表
CREATE TABLE IF NOT EXISTS site_category_stats (
    site_category String,
    pv UInt64,
    uv UInt32,
    bytes UInt64,
    unique_domains UInt32,
    stats_date Date,
    last_update_time DateTime
) ENGINE = AggregatingMergeTree()
PARTITION BY toYYYYMM(stats_date)
ORDER BY (site_category, stats_date)
SETTINGS index_granularity = 8192;

-- 用户访问明细表（用于快速查询）
CREATE TABLE IF NOT EXISTS user_access_detail (
    user_id String,
    event_time DateTime,
    domain String,
    url String,
    site_category String,
    bytes UInt64,
    department String
) ENGINE = MergeTree()
PARTITION BY toYYYYMM(event_time)
ORDER BY (user_id, event_time)
SETTINGS index_granularity = 8192;