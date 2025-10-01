FROM amazoncorretto:21-alpha-jdk

COPY target/aaronch-0.0.1-SNAPSHOT.jar /api-v1.jar

ENTRYPOINT ["java","-jar","/api-v1.jar"]