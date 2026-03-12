# Dockerfile
FROM openjdk:8-jre-slim

WORKDIR /app

# 安装排查工具
RUN apt-get update && apt-get install -y \
    procps \
    vim \
    curl \
    net-tools \
    && rm -rf /var/lib/apt/lists/*

COPY target/cpu-high-demo-1.0.0.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]