package com.github.nenadjakic.investiq.data.repository

import com.github.nenadjakic.investiq.data.entity.core.Country
import org.springframework.data.jpa.repository.JpaRepository

interface CountryRepository: JpaRepository<Country, String>