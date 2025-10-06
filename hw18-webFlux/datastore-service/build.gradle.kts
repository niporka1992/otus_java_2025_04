plugins {
    id("java")
    id("org.springframework.boot") version "3.4.5"
    id("io.spring.dependency-management") version "1.1.7"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "ru.otus"

repositories {
    mavenCentral()
    maven("https://repo.spring.io/release")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("com.google.code.findbugs:jsr305:3.0.2")

    // R2DBC + PostgreSQL
    implementation("io.r2dbc:r2dbc-postgresql:0.8.13.RELEASE")
    implementation("org.postgresql:postgresql:42.7.7")

    // Spring Data JDBC / R2DBC
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
}

tasks.test {
    useJUnitPlatform()
}

tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
    archiveVersion.set(project.version.toString())
    archiveClassifier.set("fat")

    manifest {
        attributes["Main-Class"] = "ru.otus.DatastoreApplication"
    }
}

tasks.build {
    dependsOn("shadowJar")
}