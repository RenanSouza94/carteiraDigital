FROM openjdk:17-jdk-slim
VOLUME /tmp
COPY target/carteiradigital-0.0.1-SNAPSHOT.jar carteiradigital-app.jar
ENTRYPOINT ["java","-jar","/carteiradigital-app.jar"]
