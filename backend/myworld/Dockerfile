FROM maven:3.9.4-eclipse-temurin-21 AS build
WORKDIR /app

# Copy everything and build the Spring Boot jar
COPY . .
RUN chmod +x mvnw || true
RUN mvn clean install
RUN mvn spring-boot:run -B -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
