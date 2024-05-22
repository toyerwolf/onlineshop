FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY gatewayapp/build/libs/gatewayapp-0.0.1-SNAPSHOT.jar /app/gatewayapp.jar

CMD ["sh", "-c", "java -jar gatewayapp.jar"]