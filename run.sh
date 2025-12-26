#!/bin/bash

# 企业员工网络访问用户画像系统 - 运行脚本

echo "=== 企业员工网络访问用户画像系统 ==="
echo ""

# 检查Java环境
if ! command -v java &> /dev/null; then
    echo "错误: 未找到Java环境，请先安装Java 8或更高版本"
    exit 1
fi

# 检查Maven
if ! command -v mvn &> /dev/null; then
    echo "错误: 未找到Maven，请先安装Maven"
    exit 1
fi

# 显示菜单
echo "请选择操作："
echo "1. 编译项目"
echo "2. 运行主程序（生成测试数据）"
echo "3. 仅编译和运行"
echo "4. 清理并重新编译"
echo "5. 查看生成的数据"
echo "6. 初始化数据库"
echo "0. 退出"
echo ""
read -p "请输入选项 (0-6): " choice

case $choice in
    1)
        echo ""
        echo "正在编译项目..."
        mvn compile
        ;;
    2)
        echo ""
        echo "正在编译并运行主程序..."
        mvn clean compile exec:java -Dexec.mainClass="org.example.userprofile.UserProfileApplication"
        echo ""
        echo "运行完成！"
        echo "数据已导出到: mock_data.json"
        ;;
    3)
        echo ""
        echo "正在运行程序（跳过编译）..."
        mvn exec:java -Dexec.mainClass="org.example.userprofile.UserProfileApplication"
        ;;
    4)
        echo ""
        echo "正在清理并重新编译..."
        mvn clean compile
        ;;
    5)
        echo ""
        if [ -f "mock_data.json" ]; then
            echo "=== 生成的数据文件 ==="
            head -100 mock_data.json
            echo ""
            echo "..."
            echo ""
            echo "完整数据请查看: mock_data.json"
        else
            echo "未找到数据文件，请先运行主程序生成数据"
        fi
        ;;
    6)
        echo ""
        echo "=== 数据库初始化脚本 ==="
        echo ""
        echo "ClickHouse 表结构:"
        echo "文件: src/main/resources/clickhouse/schema.sql"
        echo ""
        echo "MySQL 表结构:"
        echo "文件: src/main/resources/mysql/schema.sql"
        echo ""
        echo "请手动在相应的数据库中执行这些SQL文件"
        ;;
    0)
        echo "退出"
        exit 0
        ;;
    *)
        echo "无效的选项"
        exit 1
        ;;
esac

echo ""
echo "完成！"
