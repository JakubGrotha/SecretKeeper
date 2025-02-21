package io.github.jakubgrotha.secretkeeper

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory

class SecretKeeperObjectMapper : ObjectMapper(YAMLFactory())
