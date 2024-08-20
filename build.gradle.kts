plugins {
    id("org.springframework.boot") version "2.7.18"
    id("io.spring.dependency-management") version "1.1.6"
    id("com.github.davidmc24.gradle.plugin.avro") version "1.9.1"

    kotlin("jvm") version "1.9.24"
    kotlin("plugin.spring") version "1.9.24"
    kotlin("plugin.allopen") version "1.9.24"
}


repositories {
    mavenCentral()
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "java-library")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "com.github.davidmc24.gradle.plugin.avro")

    apply {
        plugin("org.jetbrains.kotlin.jvm")
        plugin("kotlin-allopen")
        plugin("kotlin")
        plugin("kotlin-spring")
        plugin("org.springframework.boot")
        plugin("io.spring.dependency-management")
    }

    repositories {
        mavenCentral()
    }

    dependencies {
        implementation("org.springframework.boot:spring-boot-starter-web")
        implementation("org.jetbrains.kotlin:kotlin-reflect")
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
        implementation("org.apache.avro:avro:1.12.0")
        implementation("org.reflections:reflections:0.10.2")
        implementation("com.github.jsqlparser:jsqlparser:4.9")

        testImplementation("io.mockk:mockk:1.13.12")
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.11.0")
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}
