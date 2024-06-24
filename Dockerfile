FROM openjdk:21-jdk-slim
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} stock-exchange-service.jar
ENTRYPOINT ["java","-jar","/stock-exchange-service.jar"]
