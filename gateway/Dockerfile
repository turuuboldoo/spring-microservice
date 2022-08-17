FROM openjdk:19-jdk-alpine
WORKDIR /gateway
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} /gateway/app.jar
ENTRYPOINT ["java","-jar","/gateway/app.jar"]
