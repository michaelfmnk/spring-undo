import Java_conventions_gradle.Versions

plugins {
    id("java-conventions")
    id("testing-conventions")
    id("publishing-conventions")
}

description = "Spring-Undo persistence module. Stores records in redis and provides access to them."

dependencies {
    compileOnly("org.springframework.boot:spring-boot-starter-data-redis:${Versions.springBootVersion}")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.13.2")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.2.2")

    compileOnly(project(":spring-undo-core"))

    testImplementation("org.testcontainers:testcontainers:1.17.1")
    testImplementation("org.springframework.boot:spring-boot-starter-data-redis:${Versions.springBootVersion}")
    testImplementation(project(":spring-undo-core"))
}