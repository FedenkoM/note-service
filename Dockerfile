FROM openjdk:17-alpine

WORKDIR /app

COPY target/note-service*.jar note-service.jar

CMD ["java", "-jar", "note-service.jar"]