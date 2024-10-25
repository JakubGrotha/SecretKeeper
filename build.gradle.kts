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
    plugins {
        create("secretKeeper") {
            id = "com.jakubgrotha.secret-keeper"
            implementationClass = "com.jakubgrotha.secretkeeper.SecretKeeperPlugin"
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
