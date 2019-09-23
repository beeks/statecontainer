plugins {
    kotlin("jvm")
}

val kotlinVersion: String by project
dependencies {
    implementation(kotlin("stdlib", kotlinVersion))
    implementation("org.pcollections:pcollections:2.1.2")
}
