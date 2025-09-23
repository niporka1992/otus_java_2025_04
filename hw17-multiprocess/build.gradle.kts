import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("io.freefair.lombok") version "8.6"
    id("org.flywaydb.flyway") version "10.16.0"
    id("com.google.protobuf") version "0.9.4" // для генерации gRPC из .proto
}

group = "ru.otus"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // SLF4J + Logback (актуальная версия, без janino)
    implementation("org.slf4j:slf4j-api:2.0.16")
    implementation("ch.qos.logback:logback-classic:1.5.18")

    // YAML-конфиг
    implementation("org.yaml:snakeyaml:2.3")

    // gRPC
    implementation("io.grpc:grpc-netty-shaded:1.66.0")
    implementation("io.grpc:grpc-protobuf:1.66.0")
    implementation("io.grpc:grpc-stub:1.66.0")

    // Аннотации для сгенерированного кода
    compileOnly("javax.annotation:javax.annotation-api:1.3.2")

    // Фикс конфликта
    constraints {
        implementation("com.google.errorprone:error_prone_annotations:2.36.0")
    }
}

// Конфигурация protobuf
protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.25.3"
    }
    plugins {
        create("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.66.0"
        }
    }
    generateProtoTasks {
        all().forEach { task ->
            task.plugins {
                create("grpc")
            }
        }
    }
}

// Подключаем директории со сгенерированными классами
sourceSets {
    main {
        java {
            srcDir("build/generated/source/proto/main/java")
            srcDir("build/generated/source/proto/main/grpc")
        }
    }
}

tasks.test {
    useJUnitPlatform()
}

tasks {
    named<ShadowJar>("shadowJar") {
        archiveVersion.set(project.version.toString())
        archiveClassifier.set("fat")

        manifest {
            attributes(
                mapOf("Main-Class" to "ru.otus.App")
            )
        }
    }

    build {
        dependsOn("shadowJar")
    }
}

// Глобальная стратегия разрешения конфликтов
configurations.all {
    resolutionStrategy {
        force("com.google.errorprone:error_prone_annotations:2.36.0")
    }
}
