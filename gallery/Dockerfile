FROM openjdk:19-jdk-alpine
WORKDIR /gallery
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} /gallery/app.jar
ENTRYPOINT ["java","-jar","/gallery/app.jar"]
