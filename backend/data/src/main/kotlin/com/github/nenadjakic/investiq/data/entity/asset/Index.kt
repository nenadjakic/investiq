package com.github.nenadjakic.investiq.data.entity.asset

import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity

/**
 * Entity representing a stock market Index, which is a specific type of Asset.
 * This class uses single-table inheritance strategy with the discriminator value "INDEX".
 * 
 * Indices track the performance of a collection of stocks or other securities, 
 * such as S&P 500, NASDAQ, STOXX 50, etc.
 */
@Entity
@DiscriminatorValue("INDEX")
class Index : ListedAsset()
