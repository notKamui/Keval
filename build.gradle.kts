/*
 * @notKamui
 *
 * Gradle build file for Keval
 */

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "com.notkamui.libs"
version = "1.0.0"

plugins {
    kotlin("multiplatform") version "1.9.22"
    java
    `maven-publish`
    signing
}

repositories {
    mavenCentral()
}

java {
    withJavadocJar()
    withSourcesJar()
}

kotlin {
    jvm()
    js(IR) {
        nodejs()
        browser()
    }
    linuxX64()
    mingwX64()

    sourceSets {
        val commonMain by getting {
        }
        val commonTest by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-test")
                implementation("org.jetbrains.kotlin:kotlin-test-junit")
            }
        }
    }
}

tasks {
    jar {
        manifest {
            attributes(
                mapOf(
                    "Implementation-Title" to project.name,
                    "Implementation-Version" to project.version
                )
            )
        }
    }

    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }

    withType<JavaCompile> {
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"
    }

    withType<Wrapper> {
        distributionType = Wrapper.DistributionType.ALL
    }
}

val isSnapshot = version.toString().endsWith("-SNAPSHOT")

val repositoryUrl = if (isSnapshot)
    "https://oss.sonatype.org/content/repositories/snapshots/"
else
    "https://oss.sonatype.org/service/local/staging/deploy/maven2/"

publishing {
    publications {
        withType<MavenPublication> {
            artifactId = artifactId.toLowerCase()

            artifact(tasks.getByName("javadocJar"))

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
        if (!isSnapshot) {
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
}

signing {
    sign(publishing.publications)
}
