package com.github.nenadjakic.investiq.data.repository

import com.github.nenadjakic.investiq.data.entity.asset.Asset
import com.github.nenadjakic.investiq.data.entity.transaction.Transaction
import com.github.nenadjakic.investiq.data.enum.Platform
import com.github.nenadjakic.investiq.data.enum.TransactionType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.UUID

interface TransactionRepository: JpaRepository<Transaction, UUID> {

}