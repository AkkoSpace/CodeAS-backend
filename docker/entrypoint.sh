#!/bin/bash

# Docker容器启动脚本
# Author: akko
# Version: 1.0.0

set -e

# 应用配置
APP_HOME=${APP_HOME:-/app}
APP_NAME=${APP_NAME:-backend}
JAR_FILE="$APP_HOME/lib/$APP_NAME.jar"

# 默认JVM参数
DEFAULT_JAVA_OPTS="-server -Xms512m -Xmx2g -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+UnlockExperimentalVMOptions -XX:+UseContainerSupport -Djava.awt.headless=true -Dfile.encoding=UTF-8 -Duser.timezone=Asia/Shanghai"

# 合并JVM参数
JAVA_OPTS="${JAVA_OPTS:-$DEFAULT_JAVA_OPTS}"

# Spring配置
SPRING_OPTS="--spring.config.location=classpath:/application.yml,file:$APP_HOME/conf/"
SPRING_OPTS="$SPRING_OPTS --logging.config=file:$APP_HOME/conf/logback-spring.xml"
SPRING_OPTS="$SPRING_OPTS --logging.file.path=$APP_HOME/logs"

# 等待数据库就绪
wait_for_database() {
    local host=${DB_HOST:-localhost}
    local port=${DB_PORT:-5432}
    local timeout=${DB_WAIT_TIMEOUT:-60}
    
    echo "Waiting for database at $host:$port..."
    
    for i in $(seq 1 $timeout); do
        if nc -z "$host" "$port"; then
            echo "Database is ready!"
            return 0
        fi
        echo "Waiting for database... ($i/$timeout)"
        sleep 1
    done
    
    echo "Database is not ready after $timeout seconds"
    return 1
}

# 等待Redis就绪
wait_for_redis() {
    local host=${REDIS_HOST:-localhost}
    local port=${REDIS_PORT:-6379}
    local timeout=${REDIS_WAIT_TIMEOUT:-30}
    
    echo "Waiting for Redis at $host:$port..."
    
    for i in $(seq 1 $timeout); do
        if nc -z "$host" "$port"; then
            echo "Redis is ready!"
            return 0
        fi
        echo "Waiting for Redis... ($i/$timeout)"
        sleep 1
    done
    
    echo "Redis is not ready after $timeout seconds"
    return 1
}

# 主函数
main() {
    echo "Starting $APP_NAME..."
    echo "Java Options: $JAVA_OPTS"
    echo "Spring Options: $SPRING_OPTS"
    echo "Active Profile: ${SPRING_PROFILES_ACTIVE:-default}"
    
    # 等待依赖服务
    if [ "${WAIT_FOR_DB:-true}" = "true" ]; then
        wait_for_database
    fi
    
    if [ "${WAIT_FOR_REDIS:-true}" = "true" ]; then
        wait_for_redis
    fi
    
    # 启动应用
    exec java $JAVA_OPTS -jar "$JAR_FILE" $SPRING_OPTS "$@"
}

# 执行主函数
main "$@"
