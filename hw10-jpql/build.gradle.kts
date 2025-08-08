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
    // === Runtime ===
    // Проверьте правильность артефакта, обычно flyway-core + драйвер базы
    implementation("org.flywaydb:flyway-core:10.14.0")
    implementation("org.postgresql:postgresql:42.7.3")
    runtimeOnly("org.flywaydb:flyway-database-postgresql")
    // === Compile time ===
    implementation("org.projectlombok:lombok:1.18.32")
    annotationProcessor("org.projectlombok:lombok:1.18.32")
    implementation("ch.qos.logback:logback-classic:1.5.18")
    implementation("org.hibernate.orm:hibernate-core:6.5.0.Final")

    // === Testing ===
    testImplementation(platform("org.junit:junit-bom:5.11.0"))
    testImplementation("com.h2database:h2:2.2.224")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core:3.26.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.12.0")
    testImplementation("org.testcontainers:junit-jupiter:1.21.3")
    testImplementation("org.testcontainers:postgresql:1.19.8")
}

tasks.test {
    useJUnitPlatform()
}

