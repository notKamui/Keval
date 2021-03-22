/*
 * @notKamui
 *
 * Gradle build file for Keval
 */

import java.net.URL

group = "com.notkamui.libs"
version = "0.7.4"

plugins {
    kotlin("jvm") version "1.4.31"
    id("org.jetbrains.dokka") version "1.4.20"
    java
    `maven-publish`
    signing
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}

val compileKotlin by tasks.getting(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class) {
    kotlinOptions.jvmTarget = "1.8"
}

val compileTestKotlin by tasks.getting(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class) {
    kotlinOptions.jvmTarget = "1.8"
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

java {
    withJavadocJar()
    withSourcesJar()
}

tasks {
    withType<Wrapper> {
        distributionType = Wrapper.DistributionType.ALL
    }
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

val repositoryUrl = if (version.toString().endsWith("SNAPSHOT"))
    "https://oss.sonatype.org/content/repositories/snapshots/"
else
    "https://oss.sonatype.org/service/local/staging/deploy/maven2/"

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            groupId = project.group.toString()
            artifactId = project.name.toLowerCase()
            version = project.version.toString()

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
    repositories {
        maven {
            setUrl(repositoryUrl)
            credentials {
                username = project.properties["ossrhUsername"] as String? ?: "Unknown user"
                password = project.properties["ossrhPassword"] as String? ?: "Unknown user"
            }
        }
        maven {
            name = "GitHubPackages"
            setUrl("https://maven.pkg.github.com/notKamui/${project.name}")
            credentials {
                username = project.properties["githubUsername"] as String? ?: "Unknown user"
                password = project.properties["githubPassword"] as String? ?: "Unknown user"
            }
        }
    }
}

signing {
    useGpgCmd()
    sign(publishing.publications["mavenJava"])
}