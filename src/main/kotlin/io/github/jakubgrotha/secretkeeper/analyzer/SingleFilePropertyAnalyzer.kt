package io.github.jakubgrotha.secretkeeper.analyzer

class SingleFilePropertyAnalyzer {

    fun analyze(properties: Map<String, String>, fields: List<String>, expectedSecretValue: String): List<String> {
        return properties
            .filterKeys { key -> fields.contains(key) }
            .filterValues { value -> value.isNotEmpty() }
            .filterValues { value -> expectedSecretValue != value }
            .map { it.key }
            .toList()
    }
}
