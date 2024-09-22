package com.jakubgrotha.secretkeeper.analyzer

class SingleFilePropertyAnalyzer {

    fun analyze(properties: Map<*, *>, fields: List<String>, expectedSecretValue: String): List<String> {
        return properties
            .filterKeys { key -> fields.contains(key) }
            .filterValues { value -> expectedSecretValue != value }
            .map { it.key as String }
            .toList()
    }
}
