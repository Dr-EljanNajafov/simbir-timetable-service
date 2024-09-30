FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY /build/libs/service.jar /app/timetableService.jar
ENTRYPOINT ["java", "-jar", "timetableService.jar"]