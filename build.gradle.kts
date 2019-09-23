plugins {
    kotlin("jvm") version "1.3.11"
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

val kotlinVersion: String by project
dependencies {
    implementation(kotlin("stdlib", kotlinVersion))
}

allprojects {
    repositories {
        jcenter()
    }
}
