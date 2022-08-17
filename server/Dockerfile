FROM openjdk:19-jdk-alpine
WORKDIR /server
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} /server/app.jar
ENTRYPOINT ["java","-jar","/server/app.jar"]
