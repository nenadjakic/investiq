package com.github.nenadjakic.investiq.data.repository

import com.github.nenadjakic.investiq.data.entity.core.Industry
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface IndustryRepository: JpaRepository<Industry, UUID>