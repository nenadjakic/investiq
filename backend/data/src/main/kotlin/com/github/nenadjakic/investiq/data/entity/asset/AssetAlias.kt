package com.github.nenadjakic.investiq.data.entity.asset

import com.github.nenadjakic.investiq.data.enum.Platform
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.util.UUID

/**
 * Represents an external alias or symbol for an asset on a specific platform and exchange.
 *
 * An AssetAlias allows linking an internal `Asset` entity to its representation
 * in external systems (e.g., trading platforms, stock exchanges).
 * This is useful for mapping external data sources to the internal domain model.
 *
 * Constraints:
 * - Each combination of `platform`, `externalSymbol`, and `exchange_id` must be unique.
 */
@Entity
@Table(
    name = "asset_aliases",
    uniqueConstraints = [UniqueConstraint(columnNames = ["platform", "external_symbol", "exchange_id"])]
)
data class AssetAlias(

    /**
     * Primary key identifier for the asset alias.
     */
    @Id
    @Column(name = "asset_alias_id", nullable = false)
    var id: UUID?,

    /**
     * The platform on which this alias is used (e.g., trading or data platform).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "platform", length = 20, nullable = false)
    val platform: Platform,

    /**
     * The external symbol of the asset on the given platform.
     */
    @Column(name = "external_symbol", length = 50, nullable = false)
    val externalSymbol: String,

    /**
     * The internal asset this alias refers to.
     */
    @ManyToOne
    @JoinColumn(name = "asset_id", nullable = false)
    val asset: Asset,
)