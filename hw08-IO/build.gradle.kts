plugins {
    id("java")
    id("idea")
    id("com.github.johnrengelman.shadow")
}

group = "ru.otus"
version = ""

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.guava:guava:33.4.8-jre")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.19.0")
    implementation("com.fasterxml.jackson.core:jackson-core:2.19.0")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.19.0")
    testImplementation("com.fasterxml.jackson.core:jackson-databind:2.19.0")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.assertj:assertj-core")
    testImplementation("org.mockito:mockito-junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}