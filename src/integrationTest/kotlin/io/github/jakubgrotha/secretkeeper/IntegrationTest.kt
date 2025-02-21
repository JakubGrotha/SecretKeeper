package io.github.jakubgrotha.secretkeeper

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Test

class IntegrationTest : AbstractIntegrationTest() {

    @Test
    fun `should validate files in src directory with masked secret property`() {
        // given
        val projectDir = createTempProjectDir()
        val srcDir = addDirectoryAndReturn(projectDir, "src")
        setupBuildFile(projectDir)

        createPropertiesFileWithMaskedSecret(srcDir, "application.properties")
        createYamlFileWithMaskedSecret(srcDir, "application.yaml")

        // when
        val result = GradleRunner.create()
            .withProjectDir(projectDir)
            .withArguments("secretKeeper")
            .withPluginClasspath()
            .build()

        // then
        val outcome = result.task(":secretKeeper")?.outcome
        assertThat(outcome).isEqualTo(TaskOutcome.SUCCESS)
    }

    @Test
    fun `should fail when one of the files contains a unmasked secret property`() {
        // given
        val projectDir = createTempProjectDir()
        val srcDir = addDirectoryAndReturn(projectDir, "src")
        setupBuildFile(projectDir)

        createPropertiesFileWithMaskedSecret(srcDir)
        createYamlFileWithUnmaskedSecret(srcDir)

        // when
        val result = GradleRunner.create()
            .withProjectDir(projectDir)
            .withArguments("secretKeeper")
            .withPluginClasspath()
            .buildAndFail()  // Expecting failure

        // then
        val outcome = result.task(":secretKeeper")?.outcome
        assertThat(outcome).isEqualTo(TaskOutcome.FAILED)
        assertThat(result.output.contains("api.key")).isTrue()
    }

    @Test
    fun `should only validate files in the src directory`() {
        // given
        val projectDir = createTempProjectDir()
        val srcDir = addDirectoryAndReturn(projectDir, "src")
        val otherDirectory = addDirectoryAndReturn(projectDir, "other")
        setupBuildFile(projectDir)

        createPropertiesFileWithMaskedSecret(srcDir)
        createYamlFileWithMaskedSecret(srcDir)
        createPropertiesFileWithUnmaskedSecret(otherDirectory)

        // when
        val result = GradleRunner.create()
            .withProjectDir(projectDir)
            .withArguments("secretKeeper")
            .withPluginClasspath()
            .build()

        // then
        val outcome = result.task(":secretKeeper")?.outcome
        assertThat(outcome).isEqualTo(TaskOutcome.SUCCESS)
    }

    @Test
    fun `should fail if there is no src directory`() {
        // given
        val projectDir = createTempProjectDir()
        val otherDirectory = addDirectoryAndReturn(projectDir, "otherDir")
        setupBuildFile(projectDir)

        createPropertiesFileWithMaskedSecret(otherDirectory)

        // when
        val result = GradleRunner.create()
            .withProjectDir(projectDir)
            .withArguments("secretKeeper")
            .withPluginClasspath()
            .buildAndFail()

        // then
        val outcome = result.task(":secretKeeper")?.outcome
        assertThat(outcome).isEqualTo(TaskOutcome.FAILED)
        assertThat(result.output).contains("No src directory was found")
    }
}
