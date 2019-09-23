plugins {
    kotlin("jvm")
    `maven-publish`
}

val kotlinVersion: String by project
dependencies {
    implementation(kotlin("stdlib", kotlinVersion))
}

publishing {
    publications {
        create("default", MavenPublication::class.java) {
            groupId = "com.example"
            artifactId = "statecontainer-base"
            version = "0.1.0"

            from(components["java"])
        }
    }

    repositories {
        mavenLocal()
    }
}
