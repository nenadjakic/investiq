package com.github.nenadjakic.investiq.data.repository

import com.github.nenadjakic.investiq.data.entity.core.Currency
import org.springframework.data.jpa.repository.JpaRepository

interface CurrencyRepository: JpaRepository<Currency, String>