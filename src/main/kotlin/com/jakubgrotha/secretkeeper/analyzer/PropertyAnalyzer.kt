package com.jakubgrotha.secretkeeper.analyzer

import com.jakubgrotha.secretkeeper.analyzer.PropertyAnalyzer.AnalysisResult.Failure
import com.jakubgrotha.secretkeeper.analyzer.PropertyAnalyzer.AnalysisResult.Success
import com.jakubgrotha.secretkeeper.exception.UnsupportedFileExtensionException
import com.jakubgrotha.secretkeeper.extactor.PropertiesPropertyExtractor
import com.jakubgrotha.secretkeeper.extactor.PropertyExtractor
import com.jakubgrotha.secretkeeper.extactor.YamlPropertyExtractor
import java.io.File

class PropertyAnalyzer(
    private val singleFilePropertyAnalyzer: SingleFilePropertyAnalyzer = SingleFilePropertyAnalyzer(),
    private val yamlPropertyExtractor: YamlPropertyExtractor = YamlPropertyExtractor(),
    private val propertiesPropertyExtractor: PropertiesPropertyExtractor = PropertiesPropertyExtractor()
) {

    fun analyze(files: List<File>, fields: List<String>, expectedSecretValue: String): AnalysisResult {
        val filesWithViolations = files.associateWith { file ->
            val propertyExtractor: PropertyExtractor = findPropertyExtractor(file.extension)
            val properties = propertyExtractor.extract(file)
            val unmaskedFields = singleFilePropertyAnalyzer.analyze(properties, fields, expectedSecretValue)
            unmaskedFields
        }
            .filter { it.value.isNotEmpty() }
        return if (filesWithViolations.isEmpty()) Success else Failure(filesWithViolations)
    }

    private fun findPropertyExtractor(fileExtension: String): PropertyExtractor {
        return when (fileExtension) {
            "yaml", "yml" -> yamlPropertyExtractor
            "properties" -> propertiesPropertyExtractor
            else -> throw UnsupportedFileExtensionException(fileExtension)
        }
    }

    sealed interface AnalysisResult {
        object Success : AnalysisResult
        data class Failure(val filesWithViolations: Map<File, List<String>>) : AnalysisResult
    }
}
