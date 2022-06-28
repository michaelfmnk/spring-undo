val springBootVersion = parent!!.ext["springBootVersion"]

dependencies {
    compileOnly("org.springframework.boot:spring-boot-starter-data-mongodb:2.7.0")
    compileOnly(project(":spring-undo-core"))

    testImplementation("org.springframework.boot:spring-boot-starter-data-mongodb:2.7.0")
    testImplementation("org.testcontainers:testcontainers:1.17.1")
    testImplementation(project(":spring-undo-core"))
}