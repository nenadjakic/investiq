package com.github.nenadjakic.investiq.data.entity

import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.MappedSuperclass

/**
 * Abstract base class for assets that are always listed on an exchange,
 * such as stocks, ETFs, and bonds.
 *
 * Inherits from [Asset] and adds mandatory references to [Exchange] and [Currency].
 * Used to separate listed assets from those that are not necessarily tied to an exchange,
 * such as cryptocurrencies.
 */
@MappedSuperclass
abstract class ListedAsset : Asset() {

    /**
     * The exchange where this asset is traded.
     * Many assets may belong to one exchange.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exchange_id", nullable = false)
    lateinit var exchange: Exchange
}