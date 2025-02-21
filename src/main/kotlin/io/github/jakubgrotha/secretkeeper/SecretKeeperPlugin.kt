package io.github.jakubgrotha.secretkeeper

import org.gradle.api.Plugin
import org.gradle.api.Project

class SecretKeeperPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val extension = project.extensions.create("secretKeeper", SecretKeeperExtension::class.java)
        extension.secrets.convention(listOf<String>())
        extension.expectedSecretValue.convention("SECRET")
        project.tasks.register("secretKeeper", SecretKeeperTask::class.java) {
            fields = extension.secrets.get()
            expectedSecretValue = extension.expectedSecretValue.get()
        }
        project.tasks.named("check").configure {
            dependsOn("secretKeeper")
        }
    }
}
