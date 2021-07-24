FROM alpine/git
WORKDIR /app
RUN git clone https://github.com/superuserz/file-downloader-poc.git

FROM maven:3.5-jdk-8-alpine as builder
WORKDIR /app
COPY --from=0 /app/file-downloader-poc /app
RUN mvn -q clean package -Dmaven.test.skip=true

FROM openjdk:8-jdk-alpine
COPY --from=builder /app/target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]