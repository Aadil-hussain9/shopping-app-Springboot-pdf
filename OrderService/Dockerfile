FROM openjdk:11

ARG JAR_FILE=build/libs/*.jar

COPY ${JAR_FILE} orderservice.jar

ENTRYPOINT ["java", "-jar","/orderservice.jar"]

EXPOSE 9090