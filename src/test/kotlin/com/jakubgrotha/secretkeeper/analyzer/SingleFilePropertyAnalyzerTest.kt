package com.jakubgrotha.secretkeeper.analyzer

import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlin.test.Test

class SingleFilePropertyAnalyzerTest {

    private val singleFilePropertyAnalyzer = SingleFilePropertyAnalyzer()

    @Test
    fun `should analyze single file`() {
        // given
        val properties = mapOf(
            FIRST_FIELD to "SECRET",
            SECOND_FIELD to "violation"
        )

        // when
        val result = singleFilePropertyAnalyzer.analyze(properties, defaultFields, DEFAULT_SECRET_VALUE)

        // then
        assertThat(result).isEqualTo(listOf(SECOND_FIELD))
    }

    @Test
    fun `should return empty list if no violations are found`() {
        val properties = mapOf(
            FIRST_FIELD to "SECRET",
            SECOND_FIELD to "SECRET"
        )

        // when
        val result = singleFilePropertyAnalyzer.analyze(properties, defaultFields, DEFAULT_SECRET_VALUE)

        // then
        assertThat(result).isEqualTo(emptyList())
    }

    @Test
    fun `should return empty list if no fields are specified`() {
        val properties = mapOf(
            FIRST_FIELD to "SECRET",
            SECOND_FIELD to "SECRET"
        )

        // when
        val result = singleFilePropertyAnalyzer.analyze(properties, emptyList(), DEFAULT_SECRET_VALUE)

        // then
        assertThat(result).isEqualTo(emptyList())
    }

    companion object {

        const val DEFAULT_SECRET_VALUE = "SECRET"
        const val FIRST_FIELD = "first.field"
        const val SECOND_FIELD = "second.field.field"
        val defaultFields = listOf(FIRST_FIELD, SECOND_FIELD)
    }
}
