#FROM amazoncorretto:21-alpine-jdk

#COPY target/aaronch-0.0.1-SNAPSHOT.jar /api-v2.jar

#ENTRYPOINT ["java","-jar","/api-v2.jar"]

# =======================================================
# ETAPA 1: BUILD (Compilación)
# Utiliza una imagen con las herramientas necesarias para compilar (Maven y JDK).
# Le damos el nombre 'build' a esta etapa.
# =======================================================
FROM maven:3.9.5-eclipse-temurin-21 AS build
WORKDIR /app

# 1. Copia el archivo de configuración para descargar dependencias primero.
# Esto optimiza el caché de Docker si solo cambian los archivos .java
COPY pom.xml .
RUN mvn dependency:go-offline

# 2. Copia el código fuente completo.
COPY src /app/src

# 3. Compila el proyecto, salta las pruebas y crea el JAR.
RUN mvn clean package -DskipTests

# =======================================================
# ETAPA 2: RUNTIME (Ejecución)
# Utiliza una imagen más pequeña (solo el JRE) para ejecutar la aplicación.
# =======================================================
FROM amazoncorretto:21-alpine-jdk
WORKDIR /app

# Copia el archivo JAR compilado desde la etapa 'build'.
# Asegúrate de que el nombre del JAR (aaronch-0.0.1-SNAPSHOT.jar) sea correcto.
COPY --from=build /app/target/aaronch-0.0.1-SNAPSHOT.jar /api-v2.jar

# Define el punto de entrada para ejecutar la aplicación.
ENTRYPOINT ["java","-jar","/api-v2.jar"]