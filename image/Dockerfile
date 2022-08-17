FROM openjdk:19-jdk-alpine
WORKDIR /image
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} /image/app.jar
ENTRYPOINT ["java","-jar","/image/app.jar"]
