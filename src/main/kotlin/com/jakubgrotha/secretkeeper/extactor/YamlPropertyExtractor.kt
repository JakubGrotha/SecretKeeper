package com.jakubgrotha.secretkeeper.extactor

import com.jakubgrotha.secretkeeper.SecretKeeperObjectMapper
import java.io.File

class YamlPropertyExtractor : PropertyExtractor {

    private val objectMapper = SecretKeeperObjectMapper()

    @Suppress("UNCHECKED_CAST")
    override fun extract(file: File): Map<String, String> {
        val yaml = objectMapper.readValue(file, Map::class.java) as Map<String, Any>
        return flattenYaml(yaml)
    }

    @Suppress("UNCHECKED_CAST")
    private fun flattenYaml(yaml: Map<String, Any>, prefix: String = ""): Map<String, String> {
        val flatMap = HashMap<String, String>()
        yaml.forEach { (key, value) ->
            if (value is Map<*, *>) {
                flatMap.putAll(flattenYaml(value as Map<String, Any>, "$prefix$key."))
            } else {
                flatMap["$prefix$key"] = value as String
            }
        }
        return flatMap
    }
}
