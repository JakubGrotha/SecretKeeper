package com.jakubgrotha.secretkeeper.extactor

import java.io.File

class PropertiesPropertyExtractor : PropertyExtractor {
    override fun extract(file: File): Map<String, String> {
        return file.readLines()
            .filter { it.contains("=") }
            .associate {
                val arr = it.split("=")
                arr[0] to arr[1]
            }
    }

}
