package io.github.jakubgrotha.secretkeeper.extactor

import java.io.File

class PropertiesPropertyExtractor : PropertyExtractor {
    override fun extract(file: File): Map<String, String> {
        return file.readLines()
            .filter { it.contains("=") }
            .associate {
                val keyToValue = it.split("=")
                keyToValue[0] to keyToValue[1]
            }
    }
}
