package com.jakubgrotha.secretkeeper

import com.jakubgrotha.secretkeeper.exception.SecretValueIsNotMaskedException

class PropertyAnalyzer {

    fun analyze(properties: Map<*, *>, fields: List<String>, expectedSecretValue: String) {
        val violations = properties
            .filterKeys { key -> fields.contains(key) }
            .filterValues { value -> expectedSecretValue != value }
            .toList()
        if (violations.isNotEmpty()) {
            throw SecretValueIsNotMaskedException("Not masked values: $violations")
        }
    }
}
