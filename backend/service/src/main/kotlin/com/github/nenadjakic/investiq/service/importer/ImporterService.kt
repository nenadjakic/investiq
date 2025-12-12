package com.github.nenadjakic.investiq.service.importer

import com.github.nenadjakic.investiq.common.dto.ImportResult
import com.github.nenadjakic.investiq.common.dto.ValidationResult
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