import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java")
    id("com.github.johnrengelman.shadow")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("ch.qos.logback:logback-classic")
}


tasks {
    named<ShadowJar>("shadowJar") {
        archiveVersion.set(project.version.toString())
        archiveClassifier.set("fat")

        manifest {
            attributes(mapOf("Main-Class" to "ru.otus.biteCodes.Main"))
        }
    }

    build {
        dependsOn(shadowJar)
    }
}