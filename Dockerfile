FROM maven:3.9.9-eclipse-temurin-23 AS build
WORKDIR /app

COPY pom.xml ./
RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B -DskipTests clean package && ls -lh target

FROM FROM eclipse-temurin:23-jre
WORKDIR /app
COPY --from=build /app/target/*.jar /app/app.jar
COPY /etc/certs/irn /etc/certs/irn

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
