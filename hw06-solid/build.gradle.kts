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

    testImplementation(platform("org.junit:junit-bom:5.11.4"))
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
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
