FROM openjdk:11

ARG JAR_FILE=build/libs/*.jar

COPY ${JAR_FILE} serviceregistry.jar

ENTRYPOINT ["java", "-jar","/serviceregistry.jar"]

EXPOSE 8761
