# Form Service Dockerfile
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

COPY target/form-core-*.jar app.jar

ENV TZ=Asia/Shanghai
ENV JAVA_OPTS="-Xms256m -Xmx512m"

EXPOSE 8082

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar"]