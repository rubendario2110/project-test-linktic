FROM eclipse-temurin:17-jre
WORKDIR /app
COPY target/products-service-1.0.0.jar app.jar
ENTRYPOINT ["java","-Dmanagement.metrics.binders.processor.enabled=false","-jar","/app/app.jar"]