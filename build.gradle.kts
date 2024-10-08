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

tasks.register<Jar>("sourceJar") {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

artifacts {
    archives(tasks.named("sourceJar"))
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifact(tasks["sourceJar"])
        }
    }

    repositories {
        maven {
            url = uri("https://repo.rtast.cn/api/v4/projects/19/packages/maven")
            credentials {
                username = "RTAkland"
                password = System.getenv("TOKEN")
            }
        }
    }
}
