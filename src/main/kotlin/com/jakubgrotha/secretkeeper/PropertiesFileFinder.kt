package com.jakubgrotha.secretkeeper

import com.jakubgrotha.secretkeeper.exception.NoSrcDirectoryFoundException
import org.gradle.api.Project
import java.io.File

class PropertiesFileFinder {

    private val supportedFileExtensions = listOf("properties", "yaml", "yml")

    fun findFiles(project: Project): List<File> {
        val src = findSrcDirectory(project)
        return src.walk()
            .filter { it.isFile }
            .filter { supportedFileExtensions.contains(it.extension) }
            .toList()
    }

    private fun findSrcDirectory(project: Project): File =
        project.rootDir.walk()
            .filter { it.name == "src" }
            .firstOrThrow(NoSrcDirectoryFoundException())
}
