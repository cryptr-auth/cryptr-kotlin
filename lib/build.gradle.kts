import org.gradle.api.tasks.testing.logging.TestExceptionFormat

/*
 * This file was generated by the Gradle 'init' task.
 *
 * This generated file contains a sample Kotlin library project to get you started.
 * For more details take a look at the 'Building Java & JVM projects' chapter in the Gradle
 * User Manual available at https://docs.gradle.org/8.1.1/userguide/building_java_projects.html
 */
group = "org.cryptr"
version = "0.0.1"

plugins {
    // Apply the org.jetbrains.kotlin.jvm Plugin to add support for Kotlin.
    id("org.jetbrains.kotlin.jvm") version "1.8.10"

    kotlin("plugin.serialization") version "1.8.10"
    id("org.jetbrains.kotlinx.kover") version "0.7.0-Beta"

    // Apply the java-library plugin for API and implementation separation.
    `java-library`
    `maven-publish`
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

dependencies {
    // Use the Kotlin JUnit 5 integration.
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")

    // Use the JUnit 5 integration.
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.1")

    testImplementation("com.github.tomakehurst:wiremock-jre8:2.35.0")

    // This dependency is exported to consumers, that is to say found on their compile classpath.
    api("org.apache.commons:commons-math3:3.6.1")

    // This dependency is used internally, and not exposed to consumers on their own compile classpath.
    implementation("com.google.guava:guava:31.1-jre")

    //JSON
    api("org.json:json:20220320")

    // Logging
    implementation("io.github.oshai:kotlin-logging-jvm:4.0.0-beta-22")
    implementation(group = "ch.qos.logback", name = "logback-classic", version = "1.2.9")

    // Serialization
    api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
}


// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
    withSourcesJar()
    withJavadocJar()
}

tasks.jar {
    manifest {
        attributes(
            mapOf(
                "Implementation-Title" to project.name,
                "Implementation-Version" to project.version
            )
        )
    }
}

tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
    testLogging {
        showStandardStreams = true
        exceptionFormat = TestExceptionFormat.FULL
    }
}

publishing {
    publications {
        create<MavenPublication>("Cryptr") {
            from(components["java"])
        }
    }
}

koverReport {
    filters {
        excludes {
            classes("cryptr.kotlin.objects.Constants", "cryptr.kotlin.enums.*")
        }
    }
}