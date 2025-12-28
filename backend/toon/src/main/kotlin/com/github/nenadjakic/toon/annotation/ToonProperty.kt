package com.github.nenadjakic.toon.annotation

@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class ToonProperty(
    val name: String,
    val order: Int = Int.MAX_VALUE
)
