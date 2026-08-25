// spring-boot-gradle-plugin's buildpack-platform module still pulls in commons-compress:1.27.1,
// which depends on the vulnerable commons-lang3:3.16.0 (GHSA-j288-q9x7-2f5v / Dependabot alert #1).
// Force it to a patched version on the plugin classpath until Spring Boot bumps commons-compress
// to 1.28.0+ upstream. Remove once that lands.
buildscript {
    configurations.classpath {
        resolutionStrategy {
            force("org.apache.commons:commons-lang3:3.20.0")
        }
    }
}

plugins {
    java
    id("org.springframework.boot") version "4.1.1"
    id("io.spring.dependency-management") version "1.1.7"
    id("io.freefair.lombok") version "9.5.0"
    id("com.diffplug.spotless") version "8.9.0"
}

group = "com.davidconneely"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-restclient")
    implementation("org.springframework.boot:spring-boot-starter-liquibase")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.1.0")

    developmentOnly("org.springframework.boot:spring-boot-devtools")

    runtimeOnly("com.h2database:h2")
    runtimeOnly("org.postgresql:postgresql")

    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.springframework.boot:spring-boot-starter-restclient-test")
    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
}

tasks.test {
    useJUnitPlatform()
}

spotless {
    java {
        googleJavaFormat()
    }
}
