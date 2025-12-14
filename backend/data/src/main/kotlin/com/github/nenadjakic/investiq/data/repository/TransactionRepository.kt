package com.github.nenadjakic.investiq.data.repository

import com.github.nenadjakic.investiq.data.entity.transaction.Transaction
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import java.util.UUID

interface TransactionRepository: JpaRepository<Transaction, UUID>, JpaSpecificationExecutor<Transaction>


