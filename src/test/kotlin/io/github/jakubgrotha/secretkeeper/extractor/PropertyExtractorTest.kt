package io.github.jakubgrotha.secretkeeper.extractor

import java.io.File
import java.io.FileNotFoundException

abstract class PropertyExtractorTest {

    protected fun getFile(fileName: String): File {
        val classLoader = javaClass.classLoader
        val resource = classLoader.getResource(fileName) ?: throw FileNotFoundException("File not found: $fileName")
        return File(resource.path)
    }
}
