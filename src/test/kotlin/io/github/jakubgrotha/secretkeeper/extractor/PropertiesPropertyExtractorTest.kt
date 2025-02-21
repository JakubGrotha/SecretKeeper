package io.github.jakubgrotha.secretkeeper.extractor

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.jakubgrotha.secretkeeper.extactor.PropertiesPropertyExtractor
import kotlin.test.Test

class PropertiesPropertyExtractorTest : PropertyExtractorTest() {

    private val propertiesPropertyExtractor = PropertiesPropertyExtractor()

    @Test
    fun should_extract_properties() {
        // given
        val file = getFile("extractor/properties/example.properties")

        // when
        val result = propertiesPropertyExtractor.extract(file)

        // then
        val expected = mapOf(
            "first" to "property",
            "second.property" to "property",
            "third.secret.property" to "secret"
        )
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `should NOT extract properties from an empty file`() {
        // given
        val file = getFile("extractor/properties/empty-file.properties")

        // when
        val result = propertiesPropertyExtractor.extract(file)

        // then
        val expected = emptyMap<String, String>()
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `should extract property key when value is missing`() {
        // given
        val file = getFile("extractor/properties/missing-value.properties")

        // when
        val result = propertiesPropertyExtractor.extract(file)

        // then
        val expected = mapOf("first" to "", "second.property" to "property")
        assertThat(result).isEqualTo(expected)
    }
}
