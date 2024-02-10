FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /opt/app
COPY . .
RUN ./mvnw clean package

FROM eclipse-temurin:21-jre-jammy
RUN mkdir /opt/app
COPY --from=build /opt/app/target/purchase-*.jar /opt/app/purchase-app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/opt/app/purchase-app.jar"]
