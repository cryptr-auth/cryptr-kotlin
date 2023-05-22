import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import java.net.URI


/*
 * This file was generated by the Gradle 'init' task.
 *
 * This generated file contains a sample Kotlin library project to get you started.
 * For more details take a look at the 'Building Java & JVM projects' chapter in the Gradle
 * User Manual available at https://docs.gradle.org/8.1.1/userguide/building_java_projects.html
 */
group = "co.cryptr"
version = "0.0.2"

plugins {
    `java-library`
    // Apply the org.jetbrains.kotlin.jvm Plugin to add support for Kotlin.
    id("org.jetbrains.kotlin.jvm") version "1.8.10"

    kotlin("plugin.serialization") version "1.8.10"
    id("org.jetbrains.kotlinx.kover") version "0.7.0-Beta"

    // Apply the java-library plugin for API and implementation separation.
    `maven-publish`
    signing
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
    api("org.json:json:20230227")

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
    repositories {
        maven {
            credentials {
                username = project.properties.get("NEXUS_USERNAME").toString()
                password = project.properties.get("NEXUS_PASSWORD").toString()
            }
            name = "Cryptr"
            url =
                URI.create("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
        }
    }
    publications {
        create<MavenPublication>("maven") {
            artifactId = "cryptr-kotlin"
            from(components["java"])

            pom {
                name.set("Cryptr Kotlin SDK")
                description.set("The Cryptr Kotlin SDK allows you to access Cryptr API and services in JVM languages")
                url.set("https://www.cryptr.co")
                licenses {
                    license {
                        name.set("MIT License")
                    }
                }
                developers {
                    developer {
                        id.set("tiboreno")
                        name.set("Thibaud RENAUX")
                        email.set("thibaud@cryptr.co")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/cryptr-auth/cryptr-kotlin.git")
                    developerConnection.set("scm:git:ssh://github.com/cryptr-auth/cryptr-kotlin.git")
                    url.set("https://github.com/cryptr-auth/cryptr-kotlin.git")
                }
            }
        }
    }
}

signing {
    sign(publishing.publications["maven"])
}

tasks.javadoc {
    if (JavaVersion.current().isJava9Compatible) {
        (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
}

koverReport {
    filters {
        excludes {
            classes(
                "cryptr.kotlin.objects.Constants",
                "cryptr.kotlin.enums.*",
                "cryptr.kotlin.interfaces.*",
            )
        }
    }
}