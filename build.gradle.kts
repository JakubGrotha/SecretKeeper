plugins {
    `kotlin-dsl`
    `maven-publish`
    `java-gradle-plugin`
    kotlin("jvm") version "1.9.20"
}

group = "com.jakubgrotha"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.2")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.17.2")
    testImplementation(kotlin("test"))
}

gradlePlugin {
    plugins {
        create("secretKeeper") {
            id = "com.jakubgrotha.secret-keeper"
            implementationClass = "com.jakubgrotha.secretkeeper.SecretKeeperPlugin"
        }
    }
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}
