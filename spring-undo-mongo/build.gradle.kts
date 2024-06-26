import Java_conventions_gradle.Versions

plugins {
    id("java-conventions")
    id("testing-conventions")
    id("publishing-conventions")
}

description = "Spring-Undo persistence module. Stores records in mongodb and provides access to them."

dependencies {
    compileOnly("org.springframework.boot:spring-boot-starter-data-mongodb:${Versions.springBootVersion}")
    compileOnly(project(":spring-undo-core"))

    testImplementation("org.springframework.boot:spring-boot-starter-data-mongodb:${Versions.springBootVersion}")
    testImplementation("org.testcontainers:testcontainers:1.17.1")
    testImplementation(project(":spring-undo-core"))
}