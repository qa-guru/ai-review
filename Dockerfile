FROM openjdk:17-jdk-slim

WORKDIR /app

COPY gradlew .
COPY gradle/ gradle/

COPY . .

RUN ./gradlew bootJar

EXPOSE 8080

CMD java -jar build/libs/*.jar