package com.github.nenadjakic.investiq.data.repository

import com.github.nenadjakic.investiq.data.entity.core.Sector
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface SectorRepository: JpaRepository<Sector, UUID>