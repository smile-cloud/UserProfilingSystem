-- 企业员工网络访问用户画像系统 - MySQL 维度表

CREATE DATABASE IF NOT EXISTS user_profile CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE user_profile;

-- 员工维表
CREATE TABLE IF NOT EXISTS employee_dim (
    user_id VARCHAR(50) PRIMARY KEY COMMENT '用户ID',
    user_name VARCHAR(100) NOT NULL COMMENT '用户姓名',
    department VARCHAR(100) NOT NULL COMMENT '部门',
    role VARCHAR(50) COMMENT '角色：admin/user/manager',
    email VARCHAR(100) COMMENT '邮箱',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用, 1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_department (department),
    INDEX idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工维表';

-- 网站分类表
CREATE TABLE IF NOT EXISTS site_category_dim (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    domain VARCHAR(255) NOT NULL COMMENT '域名',
    category VARCHAR(50) NOT NULL COMMENT '分类：技术/娱乐/社交/办公/购物/新闻/游戏等',
    sub_category VARCHAR(50) COMMENT '子分类',
    is_work_site TINYINT DEFAULT 1 COMMENT '是否工作相关：0-否, 1-是',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_domain (domain),
    INDEX idx_category (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='网站分类表';

-- 部门维表
CREATE TABLE IF NOT EXISTS department_dim (
    department_id INT PRIMARY KEY AUTO_INCREMENT,
    department_name VARCHAR(100) NOT NULL UNIQUE COMMENT '部门名称',
    manager_id VARCHAR(50) COMMENT '负责人ID',
    description TEXT COMMENT '部门描述',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门维表';

-- 权限配置表
CREATE TABLE IF NOT EXISTS permission_config (
    id INT PRIMARY KEY AUTO_INCREMENT,
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    can_view_all TINYINT DEFAULT 0 COMMENT '是否可查看全量数据',
    can_view_department TINYINT DEFAULT 0 COMMENT '是否可查看部门数据',
    can_export TINYINT DEFAULT 0 COMMENT '是否可导出数据',
    data_retention_days INT DEFAULT 90 COMMENT '数据保留天数',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_role (role_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限配置表';

-- 访问审计日志表
CREATE TABLE IF NOT EXISTS access_audit_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id VARCHAR(50) NOT NULL COMMENT '操作用户ID',
    operation VARCHAR(50) NOT NULL COMMENT '操作类型：query/export/delete等',
    resource_type VARCHAR(50) COMMENT '资源类型',
    query_params TEXT COMMENT '查询参数',
    result_count INT COMMENT '结果数量',
    ip_address VARCHAR(50) COMMENT 'IP地址',
    operation_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    INDEX idx_user_id (user_id),
    INDEX idx_operation_time (operation_time),
    INDEX idx_operation (operation)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='访问审计日志表';

-- 插入初始权限配置
INSERT INTO permission_config (role_name, can_view_all, can_view_department, can_export, data_retention_days) VALUES
('admin', 1, 1, 1, 365),
('manager', 0, 1, 1, 180),
('user', 0, 1, 0, 90);