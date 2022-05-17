plugins {
    idea
    `maven-publish`
}

idea {
    module.isDownloadJavadoc = true
    module.isDownloadSources = true
}

subprojects {
    group = "dev.fomenko"
    version = "0.0.1-SNAPSHOT"
}