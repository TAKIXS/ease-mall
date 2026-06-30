# ===== 构建阶段 =====
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY pom.xml mvnw mvnw.cmd ./
COPY .mvn .mvn
COPY src src
RUN chmod +x mvnw && ./mvnw package -DskipTests -q

# ===== 运行阶段 =====
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/ease-mall-1.0.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
