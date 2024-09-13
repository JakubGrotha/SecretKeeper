package com.jakubgrotha.secretkeeper

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator

class SecretKeeperObjectMapper : ObjectMapper(YAMLFactory().enable(YAMLGenerator.Feature.CANONICAL_OUTPUT))
