package com.jakubgrotha.secretkeeper.exception

class UnsupportedExtensionException(extension: String) : RuntimeException("Unsupported file extension: $extension")
