import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.github.johnrengelman.shadow")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("ch.qos.logback:logback-classic")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks {
    named<ShadowJar>("shadowJar") {
        archiveVersion.set(project.version.toString())
        archiveClassifier.set("fat")

        manifest {
            attributes(mapOf("Main-Class" to "ru.otus.calculator.CalcDemo"))
        }
    }

    build {
        dependsOn(shadowJar)
    }
}