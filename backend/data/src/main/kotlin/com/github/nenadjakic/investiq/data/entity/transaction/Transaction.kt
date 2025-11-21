package com.github.nenadjakic.investiq.data.entity.transaction

import com.github.nenadjakic.investiq.data.entity.asset.Asset
import com.github.nenadjakic.investiq.data.entity.core.Currency
import com.github.nenadjakic.investiq.data.entity.core.Tag
import com.github.nenadjakic.investiq.data.enum.TransactionType
import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorColumn
import jakarta.persistence.DiscriminatorType
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.Inheritance
import jakarta.persistence.InheritanceType
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.OffsetDateTime
import java.util.UUID

/**
 * Abstract base class for all transaction types.
 * Uses Single Table inheritance strategy with discriminator column `transaction_type`.
 */

@Entity
@Table(name = "transactions")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "transaction_type", discriminatorType = DiscriminatorType.STRING, length = 20)
abstract class Transaction {

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", updatable = false, insertable = false)
    lateinit var transactionType: TransactionType

    /**
     * Primary key for the transaction.
     */
    @Id
    @Column(name = "id", nullable = false)
    var id: UUID? = null

    /**
     * Asset related to this transaction.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id", nullable = false)
    lateinit var asset: Asset

    /**
     * Date and time when transaction occurred.
     */
    @Column(name = "transaction_date", nullable = false)
    lateinit var date: OffsetDateTime

    /**
     * Set of tags associated with this transaction.
     * Tags provide contextual grouping or classification such as origin (e.g. eToro),
     * purpose (e.g. long-term investment), or ownership.
     */
    @ManyToMany
    @JoinTable(
        name = "transaction_tags",
        joinColumns = [JoinColumn(name = "transaction_id")],
        inverseJoinColumns = [JoinColumn(name = "tag_id")]
    )
    var tags: MutableSet<Tag> = mutableSetOf()

    @Column(name = "description", length = 500)
    var description: String? = null
}