package com.github.nenadjakic.investiq.data.enum

/**
 * Represents the classification of an asset by its primary investment type.
 *
 * This enum is used to categorize assets into broad classes to aid in
 * portfolio analysis, risk assessment, and diversification strategies.
 */
enum class AssetClass {
    /**
     * Represents equity investments such as stocks or equity ETFs.
     */
    EQUITY,

    /**
     * Represents fixed-income investments such as bonds or bond ETFs.
     */
    BOND,

    /**
     * Represents commodity investments such as gold, oil, or commodity ETFs.
     */
    COMMODITY,

    /**
     * Represents mixed or multi-asset investments that combine multiple asset classes.
     */
    MIXED
}
