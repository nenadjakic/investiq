package com.github.nenadjakic.investiq.importer.service

import com.github.nenadjakic.investiq.importer.model.ImportResult
import com.github.nenadjakic.investiq.importer.model.ValidationResult
import java.io.InputStream

interface ImporterService<T> {

    /**
     * Validate required CSV headers.
     * InputStream is not owned by the service and must be closed by the caller.
     */
    fun validateHeaders(input: InputStream): ValidationResult

    /**
     * Import CSV content from InputStream.
     * InputStream is not owned by the service and must be closed by the caller.
     */
    fun import(input: InputStream): ImportResult<T>
}