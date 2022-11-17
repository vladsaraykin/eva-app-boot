FROM openjdk:18.0.2.1-oracle

COPY ./target/*.jar evaapp.jar
EXPOSE 8080
ENTRYPOINT java -jar evaapp.jar