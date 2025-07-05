# Cache maven dependencies as an intermediate docker image
# (This only happens when pom.xml changes or you clear your docker image cache)
FROM maven:3-amazoncorretto-21-alpine AS dependencies
COPY pom.xml /build/
COPY rest/pom.xml /build/rest/
COPY web/pom.xml /build/web/
WORKDIR /build/
RUN mvn --batch-mode dependency:go-offline dependency:resolve-plugins

# Build the app using Maven and the cached dependencies
# (This only happens when your source code changes or you clear your docker image cache)
# Should work offline, but https://issues.apache.org/jira/browse/MDEP-82
FROM maven:3-amazoncorretto-21-alpine AS build
COPY --from=dependencies /root/.m2 /root/.m2
COPY pom.xml /build/
COPY rest/pom.xml /build/rest/
COPY web/pom.xml /build/web/
COPY rest /build/rest
COPY web /build/web
WORKDIR /build/
RUN mvn package -Dmaven.test.skip

# Run the application (using the JRE, not the JDK)
FROM amazoncorretto:21-alpine-jdk AS rest
COPY --from=build /build/rest/target/*.jar app.jar
CMD ["java", "-jar", "/app.jar"]

# Run the application (using the JRE, not the JDK)
FROM amazoncorretto:21-alpine-jdk AS web
COPY --from=build /build/web/target/*.jar app.jar
RUN mkdir /items_images
CMD ["java", "-jar", "/app.jar"]