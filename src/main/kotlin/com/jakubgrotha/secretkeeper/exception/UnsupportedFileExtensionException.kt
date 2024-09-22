package com.jakubgrotha.secretkeeper.exception

class UnsupportedFileExtensionException(extension: String) : RuntimeException("Unsupported file extension: $extension")
