package com.jakubgrotha.secretkeeper

import com.jakubgrotha.secretkeeper.exception.UnsupportedExtensionException
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class SecretKeeperTask : DefaultTask() {

    private val propertyAnalyzer = PropertyAnalyzer()
    private val propertiesFileFinder = PropertiesFileFinder()
    private val objectMapper = SecretKeeperObjectMapper()

    @Input
    var fields: List<String> = listOf()

    @Input
    var expectedSecretValue: String = "SECRET"

    @TaskAction
    fun keepSecrets() {
        val files = propertiesFileFinder.findFiles(project)
        files.forEach {
            when (val extension = it.extension) {
                "yaml", "yml" -> analyzeYamlFile(it)
                "properties" -> analyzePropertiesFile(it)
                else -> throw UnsupportedExtensionException(extension)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun analyzeYamlFile(file: File) {
        val yaml = objectMapper.readValue(file, Map::class.java) as Map<String, Any>
        val flattenedYaml = flattenYaml(yaml)
        propertyAnalyzer.analyze(flattenedYaml, fields, expectedSecretValue)
    }

    @Suppress("UNCHECKED_CAST")
    private fun flattenYaml(yaml: Map<String, Any>, prefix: String = ""): Map<String, String> {
        val flatMap = HashMap<String, String>()
        yaml.forEach { (key, value) ->
            if (value is Map<*, *>) {
                flatMap.putAll(flattenYaml(value as Map<String, Any>, "$prefix$key."))
            } else {
                flatMap["$prefix$key"] = value as String
            }
        }
        return flatMap
    }

    private fun analyzePropertiesFile(file: File) {
        val lines = file.readLines()
        val map = lines
            .filter { it.contains("=") }
            .associate {
                val arr = it.split("=")
                arr[0] to arr[1]
            }
        propertyAnalyzer.analyze(map, fields, expectedSecretValue)
    }
}
