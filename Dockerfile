# --- run stage only ---
FROM eclipse-temurin:17-jre

WORKDIR /app

# GitHub Actions에서 만든 jar 파일을 COPY
COPY app.jar app.jar

ENV JAVA_OPTS="-XX:MaxRAMPercentage=75"
EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
  CMD wget -qO- http://127.0.0.1:8080/check/health || exit 1

USER 1000
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar app.jar"]