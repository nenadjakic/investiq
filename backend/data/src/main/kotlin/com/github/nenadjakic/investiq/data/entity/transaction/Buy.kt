package com.github.nenadjakic.investiq.data.entity.transaction

import com.github.nenadjakic.investiq.data.entity.asset.Asset
import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import java.math.BigDecimal

/**
 * Represents an "buy" transaction in the investment system.
 * This transaction type is used when a new asset position is being acquired (e.g., buying stocks, ETFs, etc.).
 *
 * In this transaction, a certain quantity of the asset is purchased at a specific price per unit.
 * It serves as the entry point for tracking asset positions, which may later be partially or fully closed.
 */

@Entity
@DiscriminatorValue("BUY")
class Buy: TradingTransaction()