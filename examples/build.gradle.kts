plugins {
    kotlin("jvm")
}

val kotlinVersion: String by project
dependencies {
    implementation(project(":base"))
    implementation(kotlin("stdlib", kotlinVersion))
}
