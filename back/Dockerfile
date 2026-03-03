# -------- build stage --------
FROM docker.1ms.run/maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom first for dependency cache
COPY pom.xml ./
COPY mvnw ./
COPY .mvn .mvn
RUN chmod +x mvnw && ./mvnw -q -DskipTests dependency:go-offline

# Copy source and build
COPY src src
RUN ./mvnw -q -DskipTests package

# -------- runtime stage --------
FROM docker.1ms.run/eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar /app/app.jar
EXPOSE 8085
ENTRYPOINT ["java","-jar","/app/app.jar"]