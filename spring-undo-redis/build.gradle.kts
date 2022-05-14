val springBootVersion = parent!!.ext["springBootVersion"]

dependencies {
    compileOnly("org.springframework.boot:spring-boot-starter-data-redis:${springBootVersion}")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.9.8")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.9.3")

    compileOnly(project(":spring-undo-core"))

    testImplementation("org.testcontainers:testcontainers:1.17.1")
    testImplementation("org.springframework.boot:spring-boot-starter-data-redis:${springBootVersion}")
    testImplementation(project(":spring-undo-core"))
}