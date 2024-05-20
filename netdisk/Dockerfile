FROM openjdk:8-jdk-alpine
RUN mkdir -p /Users/monody/Desktop/upload
RUN mkdir /Users/monody/Desktop/upload/edit
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]