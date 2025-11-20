# ========================================
# Java Web安全教学系统 - 完整应用Dockerfile
# 包含前后端的单体镜像（用于快速部署）
# ========================================

# ========================================
# 阶段1: 前端构建
# ========================================
FROM node:20-alpine AS frontend-builder

WORKDIR /app/frontend

# 只安装git，现代npm包通常不需要原生编译工具
# 如果构建失败，可以添加: python3 make g++
RUN apk add --no-cache git

COPY src/frontend/package*.json ./
RUN npm ci --silent

COPY src/frontend/ ./
RUN npm run build

# ========================================
# 阶段2: 后端构建
# ========================================
FROM maven:3.9-eclipse-temurin-17 AS backend-builder

WORKDIR /app

COPY src/backend/pom.xml .
COPY src/backend/.mvn .mvn
RUN mvn dependency:go-offline -B || true

COPY src/backend/src ./src
RUN mvn clean package -DskipTests -B \
    && mv target/security-teaching-system-*.jar target/app.jar

# ========================================
# 阶段3: 运行时镜像
# ========================================
FROM eclipse-temurin:17-jre-alpine

RUN apk add --no-cache curl tzdata \
    && cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
    && echo "Asia/Shanghai" > /etc/timezone

RUN addgroup -S appuser && adduser -S appuser -G appuser

WORKDIR /app

RUN mkdir -p /app/logs /app/uploads /app/static \
    && chown -R appuser:appuser /app

# 复制前端静态文件
COPY --from=frontend-builder /app/frontend/dist /app/static

# 复制后端JAR文件
COPY --from=backend-builder /app/target/app.jar app.jar

# 复制配置文件
COPY docker/application-docker.yml application.yml

RUN chown -R appuser:appuser /app

USER appuser

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

ENV SPRING_PROFILES_ACTIVE=docker
ENV JAVA_OPTS="-Xms512m -Xmx1024m -Djava.security.egd=file:/dev/./urandom"
ENV SERVER_PORT=8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
