package com.github.nenadjakic.investiq.data.repository

import com.github.nenadjakic.investiq.data.entity.Action
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate
import java.util.UUID

interface ActionRepository: JpaRepository<Action, UUID> {
    fun findAllByExecutedFalseAndDateLessThanEqual(date: LocalDate): List<Action>
}