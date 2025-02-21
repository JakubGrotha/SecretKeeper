package io.github.jakubgrotha.secretkeeper.exception

class UnsupportedFileExtensionException(extension: String) : RuntimeException("Unsupported file extension: $extension")
