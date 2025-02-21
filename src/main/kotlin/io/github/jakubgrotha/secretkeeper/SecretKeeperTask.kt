package io.github.jakubgrotha.secretkeeper

import io.github.jakubgrotha.secretkeeper.analyzer.PropertyAnalyzer
import io.github.jakubgrotha.secretkeeper.analyzer.PropertyAnalyzer.AnalysisResult.Failure
import io.github.jakubgrotha.secretkeeper.exception.SecretValueIsNotMaskedException
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.internal.sharedruntime.support.appendReproducibleNewLine
import java.io.File

abstract class SecretKeeperTask : DefaultTask() {

    private val fileFinder = FileFinder()
    private val propertyAnalyzer = PropertyAnalyzer()

    @Input
    var fields: List<String> = listOf()

    @Input
    var expectedSecretValue: String = "SECRET"

    @TaskAction
    fun keepSecrets() {
        val files = fileFinder.findFiles(project)
        val result = propertyAnalyzer.analyze(files, fields, expectedSecretValue)
        if (result is Failure) {
            val message = composeMessage(result.filesWithViolations)
            throw SecretValueIsNotMaskedException(message)
        }
    }

    private fun composeMessage(filesWithViolations: Map<File, List<String>>): String {
        val rootDirectory = project.rootDir
        return buildString {
            append("Secret values that are not masked:")
            filesWithViolations.forEach { (file, violations) ->
                appendReproducibleNewLine()
                append("File: ${file.relativeTo(rootDirectory)}, fields: $violations")
            }
        }
    }
}
