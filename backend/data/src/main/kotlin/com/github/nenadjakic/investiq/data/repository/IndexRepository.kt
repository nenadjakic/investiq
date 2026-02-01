package com.github.nenadjakic.investiq.data.repository

import com.github.nenadjakic.investiq.data.entity.asset.Index
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface IndexRepository: JpaRepository<Index, UUID>