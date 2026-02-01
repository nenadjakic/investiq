package com.github.nenadjakic.investiq.common.validator

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [AssetAdditionalValidator::class])
annotation class AssetAdditionalValidation (
    val message: String = "Invalid asset data",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)