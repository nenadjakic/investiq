package com.github.nenadjakic.investiq.data.repository

import com.github.nenadjakic.investiq.data.entity.transaction.StagingTransaction
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface StagingTransactionRepository: JpaRepository<StagingTransaction, UUID>