package com.github.nenadjakic.investiq.data.entity.transaction

import com.github.nenadjakic.investiq.data.entity.asset.Asset
import com.github.nenadjakic.investiq.data.enum.TransactionType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.PrePersist
import jakarta.persistence.Table
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(name = "staging_transactions")
data class StagingTransaction(
    @Id
    @Column(name = "staging_id", nullable = false)
    var id: UUID? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    val transactionType: TransactionType,

    @Column(name = "transaction_date", nullable = false)
    val transactionDate: OffsetDateTime,

    @Column(name = "description")
    val description: String? = null,

    @Column(name = "price")
    val price: Double? = null,

    @Column(name = "quantity")
    val quantity: Double? = null,

    @Column(name = "notes")
    val notes: String? = null,

    @Column(name = "external_symbol")
    val externalSymbol: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resolved_asset_id")
    val resolvedAsset: Asset? = null,

    @Column(name = "resolution_note")
    val resolutionNote: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "import_status", nullable = false)
    var importStatus: ImportStatus = ImportStatus.PENDING,
) {
    @PrePersist
    fun prePersist() {
        if (id == null) {
            id = UUID.randomUUID()
        }
    }
}

enum class ImportStatus {
    PENDING,
    VALIDATED,
    FAILED,
    IMPORTED
}