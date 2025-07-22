# Use an official Maven image with JDK 21 to build the application
FROM maven:3.9.10-eclipse-temurin-24-alpine AS build

# Define build-time argument
ARG SERVICE_NAME

# Set the working directory
ENV APP_HOME /app
ENV SPRING_PROFILES_ACTIVE=${SPRING_ACTIVE_PROFILE}

# Copy the pom.xml first for better layer caching
COPY pom.xml $APP_HOME/pom.xml

# Download dependencies separately to leverage Docker cache
WORKDIR $APP_HOME
RUN mvn dependency:go-offline

# Copy the source code
COPY src $APP_HOME/src

# Package the application
RUN mvn package -DskipTests

# Runtime image
FROM eclipse-temurin:23-jre-alpine

# Define runtime environment variable
ENV SERVICE_NAME=${SERVICE_NAME}
ENV SPRING_PROFILES_ACTIVE=${SPRING_ACTIVE_PROFILE}
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 \
                -XX:+UseStringDeduplication \
                -XX:-HeapDumpOnOutOfMemoryError"

# Set the working directory
WORKDIR /app

RUN mkdir /config

COPY --from=build /app/target/*.jar /app/app.jar

# Expose the port that the application will run on
EXPOSE ${SERVICE_PORT}

# Command to run the application
CMD ["sh", "-c", "java ${JAVA_OPTS} -jar /app/app.jar"]