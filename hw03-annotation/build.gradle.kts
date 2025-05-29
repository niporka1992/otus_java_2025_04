import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.github.johnrengelman.shadow")
}

dependencies {
    implementation("ch.qos.logback:logback-classic")
    testImplementation(platform("org.junit:junit-bom:5.11.4"))
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks {
    val shadowJar by existing(ShadowJar::class) {
        archiveClassifier.set("fat")
        manifest {
            attributes("Main-Class" to "ru.otus.Main")
        }
    }

    val run by registering(Test::class) {
        group = "custom_test"
        description = "Runs custom test engine"
        testClassesDirs = sourceSets["test"].output.classesDirs
        classpath = sourceSets["test"].runtimeClasspath
        useJUnitPlatform()
    }

    build {
        dependsOn(shadowJar)
    }
    test {
        ignoreFailures = true
    }
}
