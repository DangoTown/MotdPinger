plugins {
    kotlin("jvm") version "2.0.20"
    id("maven-publish")
}

val libVersion: String by project

group = "cn.rtast"
version = libVersion

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.code.gson:gson:2.11.0")
}

val sourceJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

artifacts {
    archives(sourceJar)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifact(sourceJar)
            artifactId = "motd-pinger"
            version = libVersion
        }
    }

    repositories {
        maven {
            url = uri("https://maven.rtast.cn/repository/maven-releases/")
            credentials {
                username = "admin"
                password = System.getenv("PUBLISH_TOKEN")
            }
        }
    }
}
