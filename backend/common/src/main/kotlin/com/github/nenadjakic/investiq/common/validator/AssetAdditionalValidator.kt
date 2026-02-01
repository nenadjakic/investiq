package com.github.nenadjakic.investiq.common.validator

import com.github.nenadjakic.investiq.common.dto.asset.AssetAddRequest
import com.github.nenadjakic.investiq.data.enum.AssetClass
import com.github.nenadjakic.investiq.data.enum.AssetType
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class AssetAdditionalValidator: ConstraintValidator<AssetAdditionalValidation, AssetAddRequest> {
    override fun isValid(
        request: AssetAddRequest?,
        context: ConstraintValidatorContext?
    ): Boolean {
        var valid = true
        request!!
        context!!
        when (request.assetType) {
            AssetType.STOCK -> {
                if (request.companyId == null) {
                    context.disableDefaultConstraintViolation()
                    context.buildConstraintViolationWithTemplate("Company ID must not be null for stocks")
                        .addPropertyNode("companyId")
                        .addConstraintViolation()
                    valid = false
                }

                if (request.exchangeId == null) {
                    context.disableDefaultConstraintViolation()
                    context.buildConstraintViolationWithTemplate("Exchange ID must not be null for stocks")
                        .addPropertyNode("exchangeId")
                        .addConstraintViolation()
                    valid = false
                }
            }
            AssetType.ETF -> {
                if (request.fundManager.isNullOrBlank()) {
                    context.disableDefaultConstraintViolation()
                    context.buildConstraintViolationWithTemplate("Fund manager must not be null for ETFs")
                        .addPropertyNode("fundManager")
                        .addConstraintViolation()
                    valid = false
                }

                if (request.assetClass == null) {
                    context.disableDefaultConstraintViolation()
                    context.buildConstraintViolationWithTemplate("Asset class must not be blank for ETFs")
                        .addPropertyNode("assetClass")
                        .addConstraintViolation()
                    valid = false
                }
            }
            else -> {}
        }
        return valid
    }
}