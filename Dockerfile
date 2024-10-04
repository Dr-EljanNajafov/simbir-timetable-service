# Этап сборки
FROM openjdk:17-jdk-alpine AS build
WORKDIR /app

# Установка dos2unix
RUN apk add --no-cache dos2unix

# Копируем gradlew и другие необходимые файлы
COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle build.gradle
COPY settings.gradle settings.gradle
COPY src src

# Конвертируем line endings и делаем gradlew исполняемым
RUN dos2unix gradlew && chmod +x gradlew

# запускаем сборку
RUN ./gradlew build -x test

FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/build/libs/service.jar /app/timetableService.jar
ENTRYPOINT ["java", "-jar", "timetableService.jar"]