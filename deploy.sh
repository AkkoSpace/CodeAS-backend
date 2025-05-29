#!/bin/bash

# 后端平台部署脚本
# Author: akko
# Version: 1.0.0

set -e

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 配置变量
APP_NAME="backend"
BUILD_DIR="target"
DEPLOY_DIR="/opt/backend"
BACKUP_DIR="/opt/backend/backup"
SERVICE_NAME="backend"

# 日志函数
log_info() {
    echo -e "${GREEN}[INFO]${NC} $(date '+%Y-%m-%d %H:%M:%S') $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $(date '+%Y-%m-%d %H:%M:%S') $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $(date '+%Y-%m-%d %H:%M:%S') $1"
}

log_header() {
    echo -e "${BLUE}=== $1 ===${NC}"
}

# 检查环境
check_environment() {
    log_header "环境检查"

    # 检查Java
    if ! command -v java &> /dev/null; then
        log_error "Java未安装"
        exit 1
    fi

    JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
    if [ "$JAVA_VERSION" -lt 21 ]; then
        log_error "Java版本过低，需要21或更高版本"
        exit 1
    fi
    log_info "Java版本检查通过: $JAVA_VERSION"

    # 检查Maven
    if ! command -v mvn &> /dev/null; then
        log_error "Maven未安装"
        exit 1
    fi
    log_info "Maven检查通过"

    # 检查部署目录
    if [ ! -d "$DEPLOY_DIR" ]; then
        log_info "创建部署目录: $DEPLOY_DIR"
        sudo mkdir -p "$DEPLOY_DIR"
        sudo chown $(whoami):$(whoami) "$DEPLOY_DIR"
    fi

    # 检查备份目录
    if [ ! -d "$BACKUP_DIR" ]; then
        log_info "创建备份目录: $BACKUP_DIR"
        mkdir -p "$BACKUP_DIR"
    fi
}

# 构建应用
build_application() {
    log_header "构建应用"

    log_info "清理旧的构建文件..."
    mvn clean

    log_info "编译和打包应用..."
    mvn package -DskipTests

    log_info "创建分发包..."
    mvn assembly:single

    if [ ! -f "$BUILD_DIR/${APP_NAME}-*.jar" ]; then
        log_error "构建失败，JAR文件不存在"
        exit 1
    fi

    log_info "构建完成"
}

# 备份当前版本
backup_current_version() {
    log_header "备份当前版本"

    if [ -f "$DEPLOY_DIR/lib/${APP_NAME}.jar" ]; then
        BACKUP_FILE="$BACKUP_DIR/${APP_NAME}-$(date +%Y%m%d_%H%M%S).jar"
        cp "$DEPLOY_DIR/lib/${APP_NAME}.jar" "$BACKUP_FILE"
        log_info "备份完成: $BACKUP_FILE"
    else
        log_warn "没有找到当前版本，跳过备份"
    fi
}

# 停止服务
stop_service() {
    log_header "停止服务"

    if [ -f "$DEPLOY_DIR/bin/stop.sh" ]; then
        log_info "停止应用服务..."
        cd "$DEPLOY_DIR"
        ./bin/stop.sh
    else
        log_warn "停止脚本不存在，尝试查找进程..."
        PID=$(pgrep -f "${APP_NAME}.jar" || true)
        if [ -n "$PID" ]; then
            log_info "发现进程 $PID，正在停止..."
            kill -TERM "$PID"
            sleep 5
            if ps -p "$PID" > /dev/null; then
                log_warn "进程未正常停止，强制终止..."
                kill -KILL "$PID"
            fi
        else
            log_info "没有发现运行中的进程"
        fi
    fi
}

# 部署新版本
deploy_new_version() {
    log_header "部署新版本"

    # 解压分发包
    DIST_FILE=$(ls $BUILD_DIR/${APP_NAME}-*-distribution.tar.gz | head -1)
    if [ -z "$DIST_FILE" ]; then
        log_error "分发包不存在"
        exit 1
    fi

    log_info "解压分发包: $DIST_FILE"
    tar -xzf "$DIST_FILE" -C /tmp/

    EXTRACTED_DIR=$(ls -d /tmp/${APP_NAME}-* | head -1)

    # 复制文件到部署目录
    log_info "复制文件到部署目录..."
    cp -r "$EXTRACTED_DIR"/* "$DEPLOY_DIR"/

    # 设置执行权限
    chmod +x "$DEPLOY_DIR"/bin/*.sh
    chmod +x "$DEPLOY_DIR"/sbin/*.sh

    # 清理临时文件
    rm -rf "$EXTRACTED_DIR"

    log_info "部署完成"
}

# 启动服务
start_service() {
    log_header "启动服务"

    cd "$DEPLOY_DIR"

    # 环境检查
    if [ -f "./sbin/env-check.sh" ]; then
        log_info "执行环境检查..."
        ./sbin/env-check.sh
    fi

    # 启动应用
    log_info "启动应用服务..."
    ./bin/start.sh

    # 等待启动
    log_info "等待服务启动..."
    sleep 10

    # 检查状态
    if [ -f "./bin/status.sh" ]; then
        ./bin/status.sh
    fi
}

# 健康检查
health_check() {
    log_header "健康检查"

    local max_attempts=30
    local attempt=1

    while [ $attempt -le $max_attempts ]; do
        log_info "健康检查尝试 $attempt/$max_attempts"

        if curl -f -s http://localhost:8080/actuator/health > /dev/null; then
            log_info "健康检查通过"
            return 0
        fi

        sleep 2
        ((attempt++))
    done

    log_error "健康检查失败"
    return 1
}

# 回滚
rollback() {
    log_header "回滚到上一版本"

    LATEST_BACKUP=$(ls -t "$BACKUP_DIR"/${APP_NAME}-*.jar | head -1)
    if [ -z "$LATEST_BACKUP" ]; then
        log_error "没有找到备份文件"
        exit 1
    fi

    log_info "回滚到: $LATEST_BACKUP"

    # 停止服务
    stop_service

    # 恢复备份
    cp "$LATEST_BACKUP" "$DEPLOY_DIR/lib/${APP_NAME}.jar"

    # 启动服务
    start_service

    log_info "回滚完成"
}

# 清理旧备份
cleanup_old_backups() {
    log_header "清理旧备份"

    # 保留最近10个备份
    BACKUP_COUNT=$(ls -1 "$BACKUP_DIR"/${APP_NAME}-*.jar 2>/dev/null | wc -l)
    if [ "$BACKUP_COUNT" -gt 10 ]; then
        log_info "清理旧备份文件..."
        ls -t "$BACKUP_DIR"/${APP_NAME}-*.jar | tail -n +11 | xargs rm -f
        log_info "清理完成"
    else
        log_info "备份文件数量: $BACKUP_COUNT，无需清理"
    fi
}

# 显示帮助
show_help() {
    echo "用法: $0 [选项]"
    echo ""
    echo "选项:"
    echo "  deploy    完整部署（默认）"
    echo "  build     仅构建"
    echo "  rollback  回滚到上一版本"
    echo "  start     启动服务"
    echo "  stop      停止服务"
    echo "  restart   重启服务"
    echo "  status    查看状态"
    echo "  health    健康检查"
    echo "  help      显示帮助"
    echo ""
}

# 主函数
main() {
    local action=${1:-deploy}

    case $action in
        deploy)
            check_environment
            build_application
            backup_current_version
            stop_service
            deploy_new_version
            start_service
            if health_check; then
                cleanup_old_backups
                log_info "部署成功完成！"
            else
                log_error "健康检查失败，考虑回滚"
                exit 1
            fi
            ;;
        build)
            check_environment
            build_application
            ;;
        rollback)
            rollback
            ;;
        start)
            start_service
            ;;
        stop)
            stop_service
            ;;
        restart)
            stop_service
            start_service
            ;;
        status)
            if [ -f "$DEPLOY_DIR/bin/status.sh" ]; then
                cd "$DEPLOY_DIR"
                ./bin/status.sh
            else
                log_error "状态脚本不存在"
            fi
            ;;
        health)
            health_check
            ;;
        help)
            show_help
            ;;
        *)
            log_error "未知选项: $action"
            show_help
            exit 1
            ;;
    esac
}

# 执行主函数
main "$@"
