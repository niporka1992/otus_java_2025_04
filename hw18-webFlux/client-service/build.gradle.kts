plugins {
    id("java")
    id("org.springframework.boot") version "3.4.5"
    id("io.spring.dependency-management") version "1.1.7"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "ru.otus"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-websocket:3.4.5")
    implementation("org.springframework.boot:spring-boot-starter-webflux:3.4.5")

    implementation("com.google.code.findbugs:jsr305:3.0.2")

    implementation("org.webjars:webjars-locator-core:0.52")
    implementation("org.webjars:sockjs-client:1.5.1")
    implementation("org.webjars:stomp-websocket:2.3.4")
    implementation("org.webjars:bootstrap:5.3.1")
}

tasks.test {
    useJUnitPlatform()
}

tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
    archiveVersion.set(project.version.toString())
    archiveClassifier.set("fat")

    manifest {
        attributes["Main-Class"] = "ru.otus.ChatApplication"
    }
}

tasks.build {
    dependsOn("shadowJar")
}