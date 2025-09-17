plugins {
    id("java")
    id("io.freefair.lombok") version "8.6"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "ru.otus"

repositories {
    mavenCentral()
}

dependencies {
    implementation ("org.projectlombok:lombok")
    annotationProcessor ("org.projectlombok:lombok")

    implementation("ch.qos.logback:logback-classic")

    testImplementation ("org.junit.jupiter:junit-jupiter-api")
    testImplementation ("org.junit.jupiter:junit-jupiter-engine")
    testImplementation ("org.junit.jupiter:junit-jupiter-params")
    testImplementation ("org.assertj:assertj-core")
    testImplementation ("org.mockito:mockito-core")
    testImplementation ("org.mockito:mockito-junit-jupiter")
}

tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
    archiveVersion.set(project.version.toString())
    archiveClassifier.set("fat")

    manifest {
        attributes["Main-Class"] = "ru.otus.Main"
    }
}

tasks.build {
    dependsOn("shadowJar")
}


tasks.test {
    useJUnitPlatform()
}