package com.jakubgrotha.secretkeeper

import java.io.File
import java.nio.file.Files

abstract class AbstractIntegrationTest {

    fun createTempProjectDir(): File {
        val tempDir = Files.createTempDirectory("secretKeeperTest").toFile()
        tempDir.deleteOnExit()
        return tempDir
    }

    fun addDirectoryAndReturn(parentDir: File, directoryName: String): File {
        File(parentDir, directoryName).mkdir()
        return File(parentDir, directoryName)
    }

    fun setupBuildFile(projectDir: File) {
        val buildFileContent = """
        plugins {
            kotlin("jvm") version "1.9.20"
            id("com.jakubgrotha.secret-keeper")
        }
        
        repositories {
            mavenCentral()
        }
        
        dependencies {
            implementation(kotlin("stdlib"))
        }
        
        secretKeeper {
            secrets = listOf("api.key")
        }
    """.trimIndent()
        File(projectDir, "build.gradle.kts").writeText(buildFileContent)
    }

    fun createPropertiesFileWithMaskedSecret(parentDir: File, fileName: String = "application.properties") {
        val propertiesContent = """
            api.key=SECRET
            db.password=SECRET
        """.trimIndent()
        File(parentDir, fileName).writeText(propertiesContent)
    }

    fun createPropertiesFileWithUnmaskedSecret(parentDir: File, fileName: String = "application.properties") {
        val propertiesContent = """
            api.key=unmasked
            db.password=unmasked
        """.trimIndent()
        File(parentDir, fileName).writeText(propertiesContent)
    }

    fun createYamlFileWithMaskedSecret(parentDir: File, fileName: String = "application.yaml") {
        val yamlContent = """
            api:
              key: SECRET
            db:
              password: SECRET
        """.trimIndent()
        File(parentDir, fileName).writeText(yamlContent)
    }

    fun createYamlFileWithUnmaskedSecret(parentDir: File, fileName: String = "application.yaml") {
        val yamlContent = """
            api:
              key: unmasked
            db:
              password: SECRET
        """.trimIndent()
        File(parentDir, fileName).writeText(yamlContent)
    }
}
