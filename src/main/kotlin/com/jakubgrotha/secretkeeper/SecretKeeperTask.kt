package com.jakubgrotha.secretkeeper

import com.jakubgrotha.secretkeeper.exception.UnsupportedExtensionException
import com.jakubgrotha.secretkeeper.extactor.PropertiesPropertyExtractor
import com.jakubgrotha.secretkeeper.extactor.PropertyExtractor
import com.jakubgrotha.secretkeeper.extactor.YamlPropertyExtractor
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

abstract class SecretKeeperTask : DefaultTask() {

    private val propertyAnalyzer = PropertyAnalyzer()
    private val fileFinder = FileFinder()
    private val yamlPropertyExtractor = YamlPropertyExtractor()
    private val propertiesPropertyExtractor = PropertiesPropertyExtractor()

    @Input
    var fields: List<String> = listOf()

    @Input
    var expectedSecretValue: String = "SECRET"

    @TaskAction
    fun keepSecrets() {
        val files = fileFinder.findFiles(project)
        files.forEach { file ->
            val propertyExtractor: PropertyExtractor = findPropertyExtractor(file.extension)
            val properties = propertyExtractor.extract(file)
            propertyAnalyzer.analyze(properties, fields, expectedSecretValue)
        }
    }

    private fun findPropertyExtractor(fileExtension: String): PropertyExtractor {
        return when (fileExtension) {
            "yaml", "yml" -> yamlPropertyExtractor
            "properties" -> propertiesPropertyExtractor
            else -> throw UnsupportedExtensionException(fileExtension)
        }
    }
}
