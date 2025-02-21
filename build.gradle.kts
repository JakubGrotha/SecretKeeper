plugins {
    `kotlin-dsl`
    `maven-publish`
    `java-gradle-plugin`
    kotlin("jvm") version "1.9.20"
    id("com.gradle.plugin-publish") version "1.2.1"
}

group = "io.github.jakubgrotha"
version = "0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.2")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.17.2")
    testImplementation("org.mockito:mockito-core:5.13.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0")
    testImplementation("com.willowtreeapps.assertk:assertk:0.28.1")
    testImplementation(kotlin("test"))
    testImplementation(gradleTestKit())
}

sourceSets {
    create("integrationTest") {
        java.srcDir("src/integrationTest/kotlin")
        resources.srcDir("src/integrationTest/resources")
        runtimeClasspath += sourceSets["main"].runtimeClasspath + sourceSets["test"].runtimeClasspath
        compileClasspath += sourceSets["main"].compileClasspath + sourceSets["test"].compileClasspath
    }
}

gradlePlugin {
    testSourceSets(sourceSets["integrationTest"])
    website = "https://github.com/JakubGrotha/SecretKeeper"
    vcsUrl = "https://github.com/JakubGrotha/SecretKeeper"
    plugins {
        create("secretKeeper") {
            id = "io.github.JakubGrotha.secret-keeper"
            displayName = "secret-keeper-plugin"
            description = "A plugin that checks if your sensitive fields (secrets) are masked"
            tags = listOf("secret", "secrets", "masking")
            implementationClass = "io.github.jakubgrotha.secretkeeper.SecretKeeperPlugin"
        }
    }
}

tasks {
    register<Test>("integrationTest") {
        testClassesDirs = sourceSets["integrationTest"].output.classesDirs
        classpath = sourceSets["integrationTest"].runtimeClasspath
        mustRunAfter(named("test"))
    }

    withType<Test>().configureEach {
        useJUnitPlatform()
    }

    named("check") {
        dependsOn(named("integrationTest"))
    }
}

kotlin {
    jvmToolchain(21)
}
