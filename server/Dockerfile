# ---- Build Stage ----
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Cache dependencies
COPY pom.xml .
COPY skillhub-domain/pom.xml skillhub-domain/
COPY skillhub-auth/pom.xml skillhub-auth/
COPY skillhub-search/pom.xml skillhub-search/
COPY skillhub-infra/pom.xml skillhub-infra/
COPY skillhub-storage/pom.xml skillhub-storage/
COPY skillhub-notification/pom.xml skillhub-notification/
COPY skillhub-app/pom.xml skillhub-app/
COPY mvnw .
COPY .mvn .mvn

COPY . .
RUN dos2unix mvnw mvnw.cmd .mvn/wrapper/wrapper.jar 2>/dev/null || true
RUN chmod +x mvnw mvnw.cmd

# Configure Maven to use Aliyun mirror for better network access in China
RUN mkdir -p /app/.m2 && \
    echo '<?xml version="1.0" encoding="UTF-8"?>' > /app/.m2/settings.xml && \
    echo '<settings xmlns="http://maven.apache.org/SETTINGS/1.2.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.2.0 https://maven.apache.org/xsd/settings-1.2.0.xsd">' >> /app/.m2/settings.xml && \
    echo '  <mirrors>' >> /app/.m2/settings.xml && \
    echo '    <mirror>' >> /app/.m2/settings.xml && \
    echo '      <id>aliyun</id>' >> /app/.m2/settings.xml && \
    echo '      <mirrorOf>central</mirrorOf>' >> /app/.m2/settings.xml && \
    echo '      <name>Aliyun Maven Mirror</name>' >> /app/.m2/settings.xml && \
    echo '      <url>https://maven.aliyun.com/repository/public</url>' >> /app/.m2/settings.xml && \
    echo '    </mirror>' >> /app/.m2/settings.xml && \
    echo '  </mirrors>' >> /app/.m2/settings.xml && \
    echo '</settings>' >> /app/.m2/settings.xml

RUN ./mvnw package -DskipTests -B -s /app/.m2/settings.xml

# ---- Runtime Stage ----
FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S app && adduser -S app -G app
WORKDIR /app

COPY --from=build /app/skillhub-app/target/*.jar app.jar

# Create storage directory and set permissions
RUN mkdir -p /var/lib/skillhub/storage && \
    chown -R app:app /app /var/lib/skillhub/storage

USER app

EXPOSE 8080
HEALTHCHECK --interval=10s --timeout=3s --start-period=60s --retries=12 \
  CMD wget -qO- http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]