package io.github.jakubgrotha.secretkeeper

import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property

interface SecretKeeperExtension {

    val secrets: ListProperty<String>
    val expectedSecretValue: Property<String>
}
