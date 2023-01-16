FROM amazoncorretto:19
COPY target/fsk-boot-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]