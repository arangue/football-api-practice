FROM maven:3.9.9-eclipse-temurin-8 AS build

WORKDIR /app

COPY pom.xml .
RUN mvn -B dependency:resolve dependency:resolve-plugins

COPY src ./src
COPY run.sh .

RUN mvn clean package
RUN mvn dependency:copy-dependencies

FROM eclipse-temurin:8-jre

WORKDIR /app

COPY --from=build /app/target/classes /app/target/classes
COPY --from=build /app/target/dependency /app/target/dependency
COPY --from=build /app/src/main/resources /app/src/main/resources
COPY --from=build /app/run.sh /app/run.sh

RUN chmod +x /app/run.sh

EXPOSE 4567

CMD ["/app/run.sh"]