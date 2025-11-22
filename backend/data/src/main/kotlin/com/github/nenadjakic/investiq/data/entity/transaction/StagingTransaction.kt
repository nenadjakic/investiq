package com.github.nenadjakic.investiq.data.entity.transaction

import com.github.nenadjakic.investiq.data.entity.asset.Asset
import com.github.nenadjakic.investiq.data.entity.core.Currency
import com.github.nenadjakic.investiq.data.entity.core.Tag
import com.github.nenadjakic.investiq.data.enum.TransactionType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.ManyToOne
import jakarta.persistence.PrePersist
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(name = "staging_transactions")
data class StagingTransaction(
    @Id
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

    @Column(name = "amount")
    val amount: Double? = null,

    @Column(name = "gross_amount")
    val grossAmount: Double? = null,

    @Column(name = "tax_percentage")
    val taxPercentage: Double? = null,

    @Column(name = "tax_amount")
    val taxAmount: Double? = null,

    @Column(name = "notes")
    val notes: String? = null,

    @Column(name = "external_symbol")
    val externalSymbol: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resolved_asset_id")
    val resolvedAsset: Asset? = null,

    @ManyToOne
    @JoinColumn(name = "currency_code")
    val currency: Currency? = null,

    @Column(name = "external_id", length = 100)
    val externalId: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_staging_transaction_id")
    var relatedStagingTransaction: StagingTransaction? = null,

    @Column(name = "resolution_note")
    val resolutionNote: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "import_status", nullable = false)
    var importStatus: ImportStatus = ImportStatus.PENDING,

    @ManyToMany
    @JoinTable(
        name = "staging_transaction_tags",
        joinColumns = [JoinColumn(name = "staging_transaction_id")],
        inverseJoinColumns = [JoinColumn(name = "tag_id")]
    )
    var tags: MutableSet<Tag> = mutableSetOf()
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