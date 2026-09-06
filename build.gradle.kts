plugins {
    // Declared here (rather than independently in each subproject) because Gradle requires
    // one shared classloader per plugin across sibling subprojects of a single multi-project
    // build - Spotless registers a build service that conflicts otherwise. Each subproject
    // applies it via `apply(plugin = ...)` and configures it via `configure<SpotlessExtension>`.
    id("com.diffplug.spotless") version "8.10.1" apply false
}
