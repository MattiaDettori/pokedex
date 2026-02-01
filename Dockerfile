FROM maven:3.9.8-eclipse-temurin-21 AS build

WORKDIR /workspace
COPY . /workspace
RUN mvn -q -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/target/pokedex-0.0.1-SNAPSHOT.jar /app/pokedex.jar

EXPOSE 5000
ENTRYPOINT ["java", "-jar", "/app/pokedex.jar"]
