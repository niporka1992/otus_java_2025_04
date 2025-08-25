import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

        plugins {
            id("java")
            id("com.github.johnrengelman.shadow") version "7.1.2"
            id("io.freefair.lombok") version "8.6"
            id("org.flywaydb.flyway") version "10.16.0"

        }

group = "ru.otus"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    dependencies {
        implementation("org.reflections:reflections")
        implementation("ch.qos.logback:logback-classic")

        testImplementation("org.junit.jupiter:junit-jupiter-engine")
        testImplementation("org.junit.jupiter:junit-jupiter-params")
        testImplementation("org.assertj:assertj-core")
        testImplementation("org.mockito:mockito-junit-jupiter")
    }
}

tasks.test {
    useJUnitPlatform()
}

tasks {
    named<ShadowJar>("shadowJar") {
        archiveVersion.set(project.version.toString())
        archiveClassifier.set("fat")

        manifest {
            attributes(mapOf("Main-Class" to "ru.otus.App"))
        }
    }

    build {
        dependsOn("shadowJar")
    }
}
