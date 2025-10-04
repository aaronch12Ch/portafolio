FROM amazoncorretto:21-alpine-jdk

COPY target/aaronch-0.0.1-SNAPSHOT.jar /api-v2.jar

ENTRYPOINT ["java","-jar","/api-v2.jar"]