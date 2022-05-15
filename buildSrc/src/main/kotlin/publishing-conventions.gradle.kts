plugins {
    id("java-conventions")
    `maven-publish`
    signing
}

publishing {
    repositories {
        maven {
            credentials {
                username = properties["usr"] as String
                password = properties["pwd"] as String
            }

            val releasesRepoUrl =
                uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            val snapshotsRepoUrl =
                uri("https://s01.oss.sonatype.org/content/repositories/snapshots")
            url = if ((version as String).endsWith("SNAPSHOT"))
                snapshotsRepoUrl else releasesRepoUrl
        }
    }

    publications {
        create("maven", MavenPublication::class.java) {
            from(components["java"])

            pom {
                name.set(project.name)
                description.set(project.description)

                scm {
                    url.set("https://github.com/michaelfmnk/spring-undo")
                    connection.set("scm:git:git://github.com/michaelfmnk/spring-undo.git")
                    developerConnection.set("scm:git:ssh://github.com:michaelfmnk/spring-undo.git")
                }

                licenses {
                    license {
                        name.set("MIT")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }

                developers {
                    developer {
                        name.set("Mykhailo Fomenko")
                        email.set("michael@fomenko.dev")
                        url.set("https://fomenko.dev")
                        roles.addAll("Owner", "Developer")
                    }
                }
            }
        }
    }
}

signing {
    sign(publishing.publications.getByName("maven"))
}