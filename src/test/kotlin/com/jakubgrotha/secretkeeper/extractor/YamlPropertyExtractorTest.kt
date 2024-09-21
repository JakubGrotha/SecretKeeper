package com.jakubgrotha.secretkeeper.extractor

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.jakubgrotha.secretkeeper.extactor.YamlPropertyExtractor
import org.junit.jupiter.api.Test

class YamlPropertyExtractorTest : PropertyExtractorTest() {

    private val yamlPropertyExtractor = YamlPropertyExtractor()

    @Test
    fun `should extract properties`() {
        // given
        val file = getFile("extractor/yaml/example.yaml")

        // when
        val result = yamlPropertyExtractor.extract(file)

        // then
        val expected = mapOf("first" to "property", "second.property" to "property")
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `should extract nested properties`() {
        // given
        val file = getFile("extractor/yaml/nested-example.yaml")

        // when
        val result = yamlPropertyExtractor.extract(file)

        // then
        val expected = mapOf(
            "first" to "test",
            "second.property" to "test",
            "third.secret" to "test",
            "third.nested.property" to "test"
        )
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `should NOT extract properties from an empty file`() {
        // given
        val file = getFile("extractor/yaml/empty-file.yaml")

        // when
        val result = yamlPropertyExtractor.extract(file)

        // then
        val expected = emptyMap<String, String>()
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `should extract property key when value is missing`() {
        // given
        val file = getFile("extractor/yaml/missing-value.yaml")

        // when
        val result = yamlPropertyExtractor.extract(file)

        // then
        val expected = mapOf(
            "first" to "test",
            "second.property" to "",
            "third.secret" to "",
            "third.nested.property" to "test"
        )
        assertThat(result).isEqualTo(expected)
    }
}
