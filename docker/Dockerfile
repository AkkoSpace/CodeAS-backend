# 使用OpenJDK 21作为基础镜像
FROM openjdk:25-jdk-slim

# 设置维护者信息
LABEL maintainer="akko <akko@akko.space>"
LABEL description="CodeAS Backend - akko.space 网站后端服务"
LABEL version="0.0.1"

# 设置环境变量
ENV APP_HOME=/app
ENV APP_NAME=backend
ENV JAVA_OPTS="-server -Xms512m -Xmx2g -XX:+UseG1GC -XX:MaxGCPauseMillis=200"
ENV SPRING_PROFILES_ACTIVE=prod
ENV TZ=Asia/Shanghai

# 安装必要的工具
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
        curl \
        netcat-openbsd \
        tzdata && \
    ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && \
    echo $TZ > /etc/timezone && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# 创建应用目录
RUN mkdir -p $APP_HOME/logs $APP_HOME/conf

# 创建非root用户
RUN groupadd -r appuser && useradd -r -g appuser appuser

# 复制应用文件
COPY target/${APP_NAME}-*.jar $APP_HOME/lib/${APP_NAME}.jar
COPY src/main/resources/application*.yml $APP_HOME/conf/
COPY src/main/resources/logback-spring.xml $APP_HOME/conf/
COPY docker/entrypoint.sh $APP_HOME/

# 设置文件权限
RUN chmod +x $APP_HOME/entrypoint.sh && \
    chown -R appuser:appuser $APP_HOME

# 切换到非root用户
USER appuser

# 设置工作目录
WORKDIR $APP_HOME

# 健康检查
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# 暴露端口
EXPOSE 8080

# 启动应用
ENTRYPOINT ["./entrypoint.sh"]
