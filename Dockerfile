# =============================================================================
# Stage 1: Build with Maven and JDK
# =============================================================================
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copy pom.xml first — Docker caches this layer if only source code changes
COPY pom.xml ./

# Download all Maven dependencies (cached unless pom.xml changes)
RUN apk add --no-cache maven
RUN mvn dependency:go-offline -B

# Copy source and build
COPY src ./src
RUN mvn clean package -DskipTests -B

# =============================================================================
# Stage 2: Runtime with JRE (smaller than JDK)
# =============================================================================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Non-root user for container security
RUN addgroup -S spring && adduser -S spring -G spring

# Copy JAR from build stage
COPY --from=builder /app/target/*.jar app.jar
RUN chown -R spring:spring /app

USER spring:spring

# JVM tuning tuned for container cgroup memory limits
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC"

# Port: respects PORT env var (PaaS platforms like Railway, Render, etc.)
# Falls back to 8080 if not set.
ENV SERVER_PORT="${PORT:-8080}"

EXPOSE 8080

# Docker HEALTHCHECK — probes /health every 30s
HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
    CMD wget -qO- http://localhost:${SERVER_PORT}/health || exit 1

# Bind on 0.0.0.0 — required for container networking (not 127.0.0.1)
ENTRYPOINT ["sh", "-c", \
    "java $JAVA_OPTS -jar app.jar --server.address=0.0.0.0 --server.port=${SERVER_PORT}"]
