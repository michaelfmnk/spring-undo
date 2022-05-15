plugins {
    id("java")
    `java-library`
}

java {
    withSourcesJar()
    withJavadocJar()
    toolchain {
        version = "11"
    }
}

repositories {
    mavenCentral()
    mavenLocal()
}

object Versions {
    const val lombokVersion = "1.18.24"
    const val springBootVersion = "2.6.7"
}

dependencies {
    "compileOnly"("org.springframework.boot:spring-boot-starter-data-redis:${Versions.springBootVersion}")
    "testImplementation"("org.springframework.boot:spring-boot-starter-test:${Versions.springBootVersion}")

    "compileOnly"("org.projectlombok:lombok:${Versions.lombokVersion}")
    "annotationProcessor"("org.projectlombok:lombok:${Versions.lombokVersion}")
    "testCompileOnly"("org.projectlombok:lombok:${Versions.lombokVersion}")
    "testAnnotationProcessor"("org.projectlombok:lombok:${Versions.lombokVersion}")
}