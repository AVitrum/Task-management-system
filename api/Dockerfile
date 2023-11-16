FROM maven:latest AS dependencies
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:resolve

FROM dependencies AS build
COPY . .
RUN mvn package -DskipTests

FROM openjdk:17 AS final
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]