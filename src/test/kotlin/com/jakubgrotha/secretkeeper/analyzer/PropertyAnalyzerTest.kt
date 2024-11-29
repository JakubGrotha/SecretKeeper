package com.jakubgrotha.secretkeeper.analyzer

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import com.jakubgrotha.secretkeeper.extactor.PropertiesPropertyExtractor
import com.jakubgrotha.secretkeeper.extactor.YamlPropertyExtractor
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import java.io.File
import java.io.FileNotFoundException
import kotlin.test.Test

class PropertyAnalyzerTest {

    private val singleFilePropertyAnalyzer = SingleFilePropertyAnalyzer()
    private val yamlPropertyExtractor = mock(YamlPropertyExtractor::class.java)
    private val propertiesPropertyExtractor = mock(PropertiesPropertyExtractor::class.java)
    private val propertyAnalyzer = PropertyAnalyzer(
        singleFilePropertyAnalyzer, yamlPropertyExtractor, propertiesPropertyExtractor
    )

    @Test
    fun `should correctly analyze single yaml file with no violations`() {
        // given
        val yamlFile = getYamlFile()
        val properties = mapOf(FIRST_FIELD to "SECRET", SECOND_FIELD to "SECRET")
        val analyzedFiles = listOf(yamlFile)

        // when
        `when`(yamlPropertyExtractor.extract(yamlFile)).thenReturn(properties)
        val result = propertyAnalyzer.analyze(analyzedFiles, defaultFields, DEFAULT_SECRET_VALUE)

        // then
        assertThat(result).isInstanceOf(PropertyAnalyzer.AnalysisResult.Success::class)
    }

    @Test
    fun `should correctly analyze single properties file with no violations`() {
        // given
        val propertiesFile = getPropertiesFile()
        val properties = mapOf(FIRST_FIELD to "SECRET", SECOND_FIELD to "SECRET")
        val analyzedFiles = listOf(propertiesFile)

        // when
        `when`(propertiesPropertyExtractor.extract(propertiesFile)).thenReturn(properties)
        val result = propertyAnalyzer.analyze(analyzedFiles, defaultFields, DEFAULT_SECRET_VALUE)

        // then
        assertThat(result).isInstanceOf(PropertyAnalyzer.AnalysisResult.Success::class)
    }

    @Test
    fun `should correctly analyze single yaml file with violations`() {
        // given
        val propertiesFile = getYamlFile()
        val properties = mapOf(FIRST_FIELD to "SECRET", SECOND_FIELD to "violation")
        val analyzedFiles = listOf(propertiesFile)

        // when
        `when`(yamlPropertyExtractor.extract(propertiesFile)).thenReturn(properties)
        val result = propertyAnalyzer.analyze(analyzedFiles, defaultFields, DEFAULT_SECRET_VALUE)

        // then
        assertThat(result).isInstanceOf(PropertyAnalyzer.AnalysisResult.Failure::class)
    }

    @Test
    fun `should correctly analyze single properties file with violation`() {
        // given
        val propertiesFile = getPropertiesFile()
        val properties = mapOf(FIRST_FIELD to "SECRET", SECOND_FIELD to "violation")
        val analyzedFiles = listOf(propertiesFile)

        // when
        `when`(propertiesPropertyExtractor.extract(propertiesFile)).thenReturn(properties)
        val result = propertyAnalyzer.analyze(analyzedFiles, defaultFields, DEFAULT_SECRET_VALUE)

        // then
        assertThat(result).isInstanceOf(PropertyAnalyzer.AnalysisResult.Failure::class)
    }

    @Test
    fun `should correctly analyze multiple yaml files`() {
        // given
        val yamlFile1 = getYamlFile()
        val yamlFile2 = getYamlFile("other-example.yaml")
        val properties1 = mapOf(FIRST_FIELD to "SECRET", SECOND_FIELD to "SECRET")
        val properties2 = mapOf(FIRST_FIELD to "SECRET", SECOND_FIELD to "violation")
        val analyzedFiles = listOf(yamlFile1, yamlFile2)

        // when
        `when`(yamlPropertyExtractor.extract(yamlFile1)).thenReturn(properties1)
        `when`(yamlPropertyExtractor.extract(yamlFile2)).thenReturn(properties2)
        val result = propertyAnalyzer.analyze(analyzedFiles, defaultFields, DEFAULT_SECRET_VALUE)

        // then
        assertThat(result).isInstanceOf(PropertyAnalyzer.AnalysisResult.Failure::class)
        val filesWithViolations = (result as PropertyAnalyzer.AnalysisResult.Failure).filesWithViolations
        assertThat(filesWithViolations.size).isEqualTo(1)
        assertThat(filesWithViolations.keys.first().name).isEqualTo("other-example.yaml")
        assertThat(filesWithViolations.values.first()).isEqualTo(listOf(SECOND_FIELD))
    }

    @Test
    fun `should correctly analyze multiple properties files`() {
        // given
        val propertiesFile1 = getPropertiesFile()
        val propertiesFile2 = getPropertiesFile("other-example.properties")
        val properties1 = mapOf(FIRST_FIELD to "SECRET", SECOND_FIELD to "SECRET")
        val properties2 = mapOf(FIRST_FIELD to "SECRET", SECOND_FIELD to "violation")
        val analyzedFiles = listOf(propertiesFile1, propertiesFile2)

        // when
        `when`(propertiesPropertyExtractor.extract(propertiesFile1)).thenReturn(properties1)
        `when`(propertiesPropertyExtractor.extract(propertiesFile2)).thenReturn(properties2)
        val result = propertyAnalyzer.analyze(analyzedFiles, defaultFields, DEFAULT_SECRET_VALUE)

        // then
        assertThat(result).isInstanceOf(PropertyAnalyzer.AnalysisResult.Failure::class)
        val filesWithViolations = (result as PropertyAnalyzer.AnalysisResult.Failure).filesWithViolations
        assertThat(filesWithViolations.size).isEqualTo(1)
        assertThat(filesWithViolations.keys.first().name).isEqualTo("other-example.properties")
        assertThat(filesWithViolations.values.first()).isEqualTo(listOf(SECOND_FIELD))
    }

    companion object {

        const val DEFAULT_SECRET_VALUE = "SECRET"
        const val FIRST_FIELD = "first.field"
        const val SECOND_FIELD = "second.field.field"
        val defaultFields = listOf(FIRST_FIELD, SECOND_FIELD)

        fun getYamlFile(fileName: String = "example.yaml"): File {
            return getFile("analyzer/yaml/$fileName")
        }

        fun getPropertiesFile(fileName: String = "example.properties"): File {
            return getFile("analyzer/properties/$fileName")
        }

        private fun getFile(fileName: String): File {
            val classLoader = Companion::class.java.classLoader
            val resource = classLoader.getResource(fileName) ?: throw FileNotFoundException("File not found: $fileName")
            return File(resource.path)
        }
    }
}
