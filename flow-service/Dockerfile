# Flow Service Dockerfile
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# 复制构建产物
COPY target/flow-core-*.jar app.jar

# 设置环境变量
ENV TZ=Asia/Shanghai
ENV JAVA_OPTS="-Xms256m -Xmx512m"

# 暴露端口
EXPOSE 8083

# 启动命令
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar"]