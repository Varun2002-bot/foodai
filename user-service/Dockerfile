FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml pom.xml
COPY common/pom.xml common/pom.xml
COPY common/src common/src
COPY user-service/pom.xml user-service/pom.xml
COPY user-service/src user-service/src

RUN mvn -pl user-service -am clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/user-service/target/user-service-1.0.0-SNAPSHOT.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
