FROM maven:3.6.1-jdk-8 as builder

WORKDIR /usr/src/mymaven

COPY pom.xml pom.xml
COPY ./src/ ./src/

RUN mvn clean compile assembly:single

FROM openjdk:8-jre-slim

WORKDIR /app

COPY --from=builder /usr/src/mymaven/target/*.jar ./target/
COPY crawl.sh .

RUN chmod 755 crawl.sh

ENTRYPOINT ["/app/crawl.sh"]
CMD []