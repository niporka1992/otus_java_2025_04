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
    implementation("ch.qos.logback:logback-classic")
    implementation("com.google.code.gson:gson")

    implementation("org.eclipse.jetty.ee10:jetty-ee10-servlet")
    implementation("org.eclipse.jetty:jetty-server")
    implementation("org.eclipse.jetty.ee10:jetty-ee10-webapp")
    implementation("org.eclipse.jetty:jetty-security")
    implementation("org.eclipse.jetty:jetty-http")
    implementation("org.eclipse.jetty:jetty-io")
    implementation("org.eclipse.jetty:jetty-util")
    implementation("org.freemarker:freemarker")

    testImplementation("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testImplementation("org.assertj:assertj-core")
    testImplementation("org.mockito:mockito-junit-jupiter")

    implementation("org.flywaydb:flyway-core:10.14.0")
    implementation("org.postgresql:postgresql:42.7.3")
    runtimeOnly("org.flywaydb:flyway-database-postgresql")

    implementation("org.projectlombok:lombok:1.18.32")
    annotationProcessor("org.projectlombok:lombok:1.18.32")
    implementation("ch.qos.logback:logback-classic:1.5.18")
    implementation("org.hibernate.orm:hibernate-core:6.5.0.Final")
}

tasks.test {
    useJUnitPlatform()
}

tasks {
    named<ShadowJar>("shadowJar") {
        archiveVersion.set(project.version.toString())
        archiveClassifier.set("fat")

        manifest {
            attributes(mapOf("Main-Class" to "ru.otus.Main"))
        }
    }

    build {
        dependsOn("shadowJar")
    }
}
