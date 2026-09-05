FROM eclipse-temurin:25-jdk-jammy AS build
WORKDIR /opt/app
COPY . .
RUN ./gradlew clean build

FROM eclipse-temurin:25-jre-jammy
RUN mkdir /opt/app
COPY --from=build /opt/app/target/purchase-*.jar /opt/app/purchase-app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/opt/app/purchase-app.jar"]
