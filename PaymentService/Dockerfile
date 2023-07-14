FROM openjdk:11

ARG JAR_FILE=build/libs/*.jar

COPY ${JAR_FILE} paymentservice.jar

ENTRYPOINT ["java", "-jar","/paymentservice.jar"]

EXPOSE 8081
