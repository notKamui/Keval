/*
 * @notKamui
 *
 * Gradle build file for Keval
 */

import java.net.URL

group = "com.notkamui.libs"
version = "0.7.1"

plugins {
    kotlin("jvm") version "1.4.30"
    id("org.jetbrains.dokka") version "1.4.20"
    `java-library`
    `maven-publish`
    signing
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.google.guava:guava:29.0-jre")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
    api("org.apache.commons:commons-math3:3.6.1")
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
    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
}

java {
    withJavadocJar()
    withSourcesJar()
}

tasks.dokkaHtml.configure {
    outputDirectory.set(rootDir.resolve("docs"))
    moduleName.set("Keval")
    dokkaSourceSets {
        configureEach {
            sourceLink {
                localDirectory.set(file("src/main/kotlin"))
                remoteUrl.set(
                    URL(
                        "https://github.com/notKamui/Keval/tree/main/keval/src/main/kotlin"
                    )
                )
                remoteLineSuffix.set("#L")
            }
            jdkVersion.set(8)
        }
    }
}


publishing {
    repositories {
        maven {
            setUrl("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = project.properties[System.getenv("OSSRH_USRNAME")] as String? ?: "Unknown user"
                password = project.properties[System.getenv("OSSRH_PWD")] as String? ?: "Unknown user"
            }
        }
    }
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = project.group.toString()
            artifactId = project.name.toLowerCase()
            version = project.version.toString()

            from(components["java"])

            pom {
                name.set("Keval")
                description.set("A Kotlin mini library for mathematical expression string evaluation")
                url.set("https://github.com/notKamui/Keval")
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://mit-license.org/")
                    }
                }
                developers {
                    developer {
                        id.set("notKamui")
                        name.set("Jimmy Teillard")
                        email.set("jimmy.teillard@notkamui.com")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/notKamui/Keval.git")
                    developerConnection.set("scm:git:ssh://github.com/notKamui/Keval.git")
                    url.set("https://github.com/notKamui/Keval.git")
                }
            }
        }
    }
}

signing {
    sign(publishing.publications["mavenJava"])
}