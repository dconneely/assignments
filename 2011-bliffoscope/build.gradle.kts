import com.diffplug.gradle.spotless.SpotlessExtension

plugins {
    java
    application
}

// Version is managed by the root build.gradle.kts (apply false there).
apply(plugin = "com.diffplug.spotless")

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

application {
    mainClass = "com.davidconneely.scope.Scope"
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:6.1.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    archiveBaseName = "scope"
    manifest {
        attributes("Main-Class" to "com.davidconneely.scope.Scope")
    }
}

configure<SpotlessExtension> {
    java {
        googleJavaFormat()
    }
}
