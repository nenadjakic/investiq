package com.github.nenadjakic.investiq.data.entity.asset

import com.github.nenadjakic.investiq.data.entity.core.Currency
import com.github.nenadjakic.investiq.data.enum.AssetType
import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorColumn
import jakarta.persistence.DiscriminatorType
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Inheritance
import jakarta.persistence.InheritanceType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.util.UUID

/**
 * Represents a financial asset such as stock, ETF, bond, or cryptocurrency.
 * An asset is uniquely identified by its symbol on a given exchange.
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "asset_type", discriminatorType = DiscriminatorType.STRING, length = 20)
@Table(
    name = "assets",
    uniqueConstraints = [UniqueConstraint(columnNames = ["symbol", "exchange_id"])]
)
abstract class Asset {

    @Enumerated(EnumType.STRING)
    @Column(name = "asset_type", updatable = false, insertable = false)
    lateinit var assetType: AssetType

    /**
     * Primary key for the asset.
     */
    @Id
    var id: UUID? = null

    /**
     * Ticker symbol representing the asset on the exchange.
     * Limited to 20 characters and cannot be null.
     */
    @Column(name = "symbol", nullable = false, length = 20)
    lateinit var symbol: String

    /**
     * Full name or description of the asset.
     * Limited to 150 characters and cannot be null.
     */
    @Column(name = "name", nullable = false, length = 150)
    lateinit var name: String

    /**
     * The currency in which the asset is traded.
     * Many assets may use the same currency.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "currency_code")
    lateinit var currency: Currency

    //@OneToMany(mappedBy = "asset")
    //val priceHistory: MutableSet<AssetPriceHistory> = mutableSetOf(),

    //@OneToMany(mappedBy = "asset", cascade = [CascadeType.ALL])
    //val aliases: MutableSet<AssetAlias> = mutableSetOf()
}