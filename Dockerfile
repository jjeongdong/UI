# 1. OpenJDK 17, 경량 이미지 (멀티 아키 지원)
FROM --platform=linux/amd64 openjdk:17-jdk-slim

# 2. 애플리케이션 실행 경로 설정
WORKDIR /app

# 3. JAR 파일을 컨테이너에 복사
COPY target/ui-0.0.1-SNAPSHOT.jar app.jar

# 4. 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
