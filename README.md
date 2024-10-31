# Secret Keeper

Sharing your API Keys and other secrets in a public or even a private GitHub repository is unsafe.
One of the conventions some people use is changing the secret value to a masked value (like `SECRET`) before pushing the code to GitHub.
However, it's easy to forget about masking the secret value each time, and you can accidentally push the code with the unmasked secret value.
This plugin lets its users specify the secret value and has a task that checks if the secret value has been masked.

Secret keeper currently supports file extensions commonly used in Spring Boot projects:
* `.properties`
* `.yaml`
* `.yml`

**Please note: This plugin is not yet published**

### How to use

Add the plugin to your `build.gradle.kts` file:

```kotlin
plugins {
    id("com.jakubgrotha.secret-keeper") version "1.0"
}
```

Specify which field should be masked:
```kotlin
secretKeeper {
    secrets = listOf("my.secret.property")
}
```

You can also specify what the expected masked secret value should be (if this field is not passed, the default value is `SECRET`)
```kotlin
secretKeeper {
    secrets = listOf("my.secret.property")
    expectedSecretValue = "OTHER_VALUE"
}
```

### Examples:

Given this plugin configuration:
```kotlin
secretKeeper {
    secrets = listOf("my.secret.property", "empty.secret.property")
}
```

And this application.properties file:
```yaml
my.secret.property=123
other.secret.property=SECRET
empty.secret.property=
```

The validation will fail, because the `my.secret.property` does not equal to `SECRET`.

However, for this application.properties file:
```yaml
my.secret.property=SECRET
other.secret.property=123
empty.secret.property=
```

The validation will pass, because:
* `my.secret.property` is masked
* `other.secret.property` is not specified in the configuration
* `empty.secret.property` is empty so it does not leak any secrets
