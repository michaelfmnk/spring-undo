val springBootVersion = "2.6.7"
val lombokVersion = "1.18.24"
val springUndoVersion = "0.0.1-SNAPSHOT"

ext {
    set("springBootVersion", springBootVersion)
}

subprojects {
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")

    plugins.withType<MavenPublishPlugin> {
        extensions.configure<PublishingExtension> {
            publications {
                create("maven", MavenPublication::class.java) {
                    from(components["java"])

                    artifactId = project.name
                    groupId = "dev.fomenko"
                    version = springUndoVersion
                }
            }
        }
    }

    tasks.withType<JavaCompile> {
        val javaToolchains  = project.extensions.getByType<JavaToolchainService>()
        javaCompiler.set(
            javaToolchains.compilerFor {
                languageVersion.set(JavaLanguageVersion.of(8))
            }
        )
    }

    plugins.withType<JavaPlugin> {
        extensions.configure<JavaPluginExtension> {

            repositories {
                mavenCentral()
            }

            dependencies {
                "compileOnly"("org.springframework.boot:spring-boot-starter-data-redis:${springBootVersion}")
                "testImplementation"("org.springframework.boot:spring-boot-starter-test:${springBootVersion}")

                "compileOnly"("org.projectlombok:lombok:$lombokVersion")
                "annotationProcessor"("org.projectlombok:lombok:$lombokVersion")
                "testCompileOnly"("org.projectlombok:lombok:$lombokVersion")
                "testAnnotationProcessor"("org.projectlombok:lombok:$lombokVersion")
            }

            tasks.withType<Test> {
                useJUnitPlatform()
            }
        }
    }
}

