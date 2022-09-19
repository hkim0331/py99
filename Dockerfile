FROM openjdk:8-alpine

COPY target/uberjar/py99.jar /app/py99.jar

EXPOSE 3000

CMD ["java", "-jar", "/app/py99.jar"]
