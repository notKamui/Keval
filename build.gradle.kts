/*
 * @notKamui
 *
 * Gradle build file for Keval
 */

import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jetbrains.kotlin.multiplatform") version "2.3.21"
    id("maven-publish")
    id("com.vanniktech.maven.publish") version "0.36.0"
}

group = "com.notkamui.libs"
version = "1.1.1"

repositories {
    mavenCentral()
}

kotlin {
    jvm()
    js(IR) {
        nodejs()
        browser()
    }
    linuxX64()
    mingwX64()

    macosArm64()
    iosSimulatorArm64()
    iosX64()
    iosArm64()
    watchosSimulatorArm64()
    watchosArm32()
    watchosArm64()
    tvosSimulatorArm64()
    tvosArm64()

    sourceSets {
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

tasks {
    withType<KotlinCompile> {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_1_8)
        }
    }

    withType<Wrapper> {
        distributionType = Wrapper.DistributionType.ALL
    }
}

mavenPublishing {
    coordinates(group.toString(), "keval", version.toString())

    publishToMavenCentral()

    if (project.hasProperty("signingInMemoryKey")) {
        signAllPublications()
    }

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

publishing {
    repositories {
        maven {
            name = "GitHubPackages"

            url = uri("https://maven.pkg.github.com/notKamui/${rootProject.name}")

            credentials {
                username = project.findProperty("githubPackagesUser") as String? ?: System.getenv("GH_PACKAGES_USER")
                password = project.findProperty("githubPackagesToken") as String? ?: System.getenv("GH_PACKAGES_TOKEN")
            }
        }
    }
}
