package com.jakubgrotha.secretkeeper

import com.jakubgrotha.secretkeeper.analyzer.PropertyAnalyzer
import com.jakubgrotha.secretkeeper.analyzer.PropertyAnalyzer.AnalysisResult.Failure
import com.jakubgrotha.secretkeeper.exception.SecretValueIsNotMaskedException
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
            val message = composeMessage(result.violations)
            throw SecretValueIsNotMaskedException(message)
        }
    }

    private fun composeMessage(violations: Map<File, List<String>>): String {
        val rootDirectory = project.rootDir
        val stringBuilder = StringBuilder("Secret values that are not masked:")
        violations.map { (file, violations) ->
            stringBuilder.appendReproducibleNewLine()
            stringBuilder.append("File: ${file.relativeTo(rootDirectory)}, fields: $violations")
        }
        return stringBuilder.toString()
    }
}
