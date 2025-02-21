package io.github.jakubgrotha.secretkeeper.extactor

import java.io.File

interface PropertyExtractor {

    fun extract(file: File): Map<String, String>
}
