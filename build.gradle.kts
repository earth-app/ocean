import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.targets.js.yarn.yarn

plugins {
    kotlin("multiplatform") version "2.2.0"
    kotlin("plugin.serialization") version "2.2.0"
    id("org.jetbrains.dokka") version "2.0.0"
    id("com.vanniktech.maven.publish") version "0.33.0"
    id("dev.petuska.npm.publish") version "3.5.3"

    `maven-publish`
    jacoco
    signing
}

val v = "1.0.0"

group = "com.earth-app.ocean"
version = "${if (project.hasProperty("snapshot")) "$v-SNAPSHOT" else v}${project.findProperty("suffix")?.toString()?.run { "-${this}" } ?: ""}"
val desc = "Recommender Algorithm for The Earth App"
description = desc

repositories {
    google()
    mavenCentral()
    mavenLocal()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

kotlin {
    configureSourceSets()
    applyDefaultHierarchyTemplate()
    withSourcesJar()

    jvm() // for code coverage
    js {
        nodejs {
            testTask  {
                useMocha {
                    timeout = "10m"
                }
            }
        }

        yarn.yarnLockAutoReplace = true
        binaries.library()
        generateTypeScriptDefinitions()
    }

    sourceSets {
        commonMain.dependencies {
            implementation("com.earth-app.shovel:shovel:1.0.2")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")
            implementation("io.github.oshai:kotlin-logging:7.0.7")

            implementation("com.soywiz:korlibs-compression:6.0.0")
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
        }

        jvmMain.dependencies {
            runtimeOnly("ch.qos.logback:logback-classic:1.5.18")
        }
    }
}

fun KotlinMultiplatformExtension.configureSourceSets() {
    sourceSets
        .matching { it.name !in listOf("main", "test") }
        .all {
            val srcDir = if ("Test" in name) "test" else "main"
            val resourcesPrefix = if (name.endsWith("Test")) "test-" else ""
            val platform = when {
                (name.endsWith("Main") || name.endsWith("Test")) && "android" !in name -> name.dropLast(4)
                else -> name.substringBefore(name.first { it.isUpperCase() })
            }

            kotlin.srcDir("src/$platform/$srcDir")
            resources.srcDir("src/$platform/${resourcesPrefix}resources")

            languageSettings.apply {
                progressiveMode = true
            }
        }
}

tasks {
    clean {
        delete("kotlin-js-store")
    }

    register("jvmJacocoTestReport", JacocoReport::class) {
        dependsOn("jvmTest")

        classDirectories.setFrom(layout.buildDirectory.file("classes/kotlin/jvm/"))
        sourceDirectories.setFrom("src/common/main/", "src/jvm/main/")
        executionData.setFrom(layout.buildDirectory.files("jacoco/jvmTest.exec"))

        reports {
            xml.required.set(true)
            xml.outputLocation.set(layout.buildDirectory.file("jacoco.xml"))

            html.required.set(true)
            html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml"))
        }
    }
}

signing {
    val signingKey: String? by project
    val signingPassword: String? by project

    if (signingKey != null && signingPassword != null)
        useInMemoryPgpKeys(signingKey, signingPassword)

    sign(publishing.publications)
}

publishing {
    publications {
        filterIsInstance<MavenPublication>().forEach {
            it.apply {
                pom {
                    name = project.name

                    licenses {
                        license {
                            name = "GNU AGPL-3.0"
                            url = "https://www.gnu.org/licenses/agpl-3.0.en.html"
                        }
                    }

                    developers {
                        developer {
                            id = "gmitch215"
                            name = "Gregory Mitchell"
                            email = "me@gmitch215.xyz"
                        }
                    }

                    scm {
                        connection = "scm:git:git://github.com/earth-app/ocean.git"
                        developerConnection = "scm:git:ssh://github.com/earth-app/ocean.git"
                        url = "https://github.com/earth-app/ocean"
                    }
                }
            }
        }
    }

    repositories {
        if (!version.toString().endsWith("SNAPSHOT")) {
            maven {
                name = "GithubPackages"
                credentials {
                    username = System.getenv("GITHUB_ACTOR")
                    password = System.getenv("GITHUB_TOKEN")
                }

                url = uri("https://maven.pkg.github.com/earth-app/ocean")
            }
        }
    }
}

mavenPublishing {
    coordinates(project.group.toString(), project.name, project.version.toString())

    pom {
        name.set("ocean")
        description.set(desc)
        url.set("https://github.com/earth-app/ocean")
        inceptionYear.set("2025")

        licenses {
            license {
                name.set("GNU AGPL-3.0")
                url.set("https://www.gnu.org/licenses/agpl-3.0.en.html")
            }
        }

        developers {
            developer {
                id = "gmitch215"
                name = "Gregory Mitchell"
                email = "me@gmitch215.xyz"
            }
        }

        scm {
            connection = "scm:git:git://github.com/earth-app/ocean.git"
            developerConnection = "scm:git:ssh://github.com/earth-app/ocean.git"
            url = "https://github.com/earth-app/ocean"
        }
    }

    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL, true)
    signAllPublications()
}

npmPublish {
    readme = file("README.md")

    packages.forEach {
        it.packageJson {
            name = "@earth-app/${project.name}"
            version = project.version.toString()
            description = desc
            license = "GNU AGPL-3.0"
            homepage = "https://github.com/earth-app/ocean"

            types = "${project.name}.d.ts"

            author {
                name = "Gregory Mitchell"
                email = "me@gmitch215.xyz"
            }

            repository {
                type = "git"
                url = "git+https://github.com/earth-app/ocean.git"
            }

            keywords = listOf("earth-app", "algorithm", "kotlin", "multiplatform")
        }
    }

    registries {
        register("npmjs") {
            uri.set("https://registry.npmjs.org")
            authToken.set(System.getenv("NPM_TOKEN"))
        }

        register("GithubPackages") {
            uri.set("https://npm.pkg.github.com/earth-app")
            authToken.set(System.getenv("GITHUB_TOKEN"))
        }
    }
}