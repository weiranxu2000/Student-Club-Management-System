# build stage
FROM maven:3.9-amazoncorretto-17 AS build
WORKDIR /app

COPY . .

RUN mvn clean package -DwarName=api

# run stage
FROM tomcat:10.1.28-jre17

COPY --from=build /app/target/api-1.0-SNAPSHOT.war $CATALINA_HOME/webapps/unimelbsc.war