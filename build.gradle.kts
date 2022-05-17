plugins {
    idea
    `maven-publish`
}

idea {
    module.isDownloadJavadoc = true
    module.isDownloadSources = true
}

subprojects {
    version = "0.0.1-SNAPSHOT"
}