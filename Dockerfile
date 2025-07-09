# ---------- STAGE 1: Build ----------
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder

WORKDIR /build

COPY pom.xml .
COPY domain/pom.xml domain/pom.xml
COPY infrastructure/pom.xml infrastructure/pom.xml

# Preload dependencies for all modules
RUN mvn dependency:go-offline validate

# Copy source
COPY . .

# Build all modules
RUN mvn clean package -DskipTests

# ---------- STAGE 2: Run ----------
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copy the JAR from the builder stage
COPY --from=builder /build/infrastructure/target/infrastructure-1.0.0-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT sh -c 'java -jar app.jar --spring.profiles.active=${SPRING_PROFILES_ACTIVE:-dev}'
