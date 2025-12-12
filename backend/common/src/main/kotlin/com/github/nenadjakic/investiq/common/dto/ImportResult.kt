package com.github.nenadjakic.investiq.common.dto

data class ImportResult<T>(
    val summary: ImportSummary,
    val rowResults: List<RowResult<T>> = emptyList(),
    val errors: List<ImportError> = emptyList()
)

data class ImportSummary(
    val totalRows: Int = 0,
    val successfulRows: Int = 0,
    val failedRows: Int = 0
)

data class RowResult<T>(
    val index: Int,
    val status: RowStatus,
    val mappedObjectInfo: T? = null
)

enum class RowStatus { SUCCESS, FAILED }

data class ImportError(
    val rowIndex: Int? = null,
    val field: String? = null,
    val message: String
)

sealed class ValidationResult {
    object Valid : ValidationResult()
    data class Invalid(
        val missingHeaders: List<String>,
        val otherIssues: List<String> = emptyList()
    ) : ValidationResult()
}