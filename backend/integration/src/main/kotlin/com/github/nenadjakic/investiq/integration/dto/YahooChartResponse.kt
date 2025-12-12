package com.github.nenadjakic.investiq.integration.dto

class YahooChartResponse() {
    var chart: ChartContainer? = null

    class ChartContainer() {
        var result: MutableList<Result>? = null
        var error: Any? = null
    }

    class Result() {
        var meta: Meta? = null
        var timestamp: MutableList<Long>? = null
        var indicators: Indicators? = null
    }

    class Meta() {
        var symbol: String? = null
        var currency: String? = null
        var exchangeName: String? = null
        var instrumentType: String? = null
        var timezone: String? = null
    }

    class Indicators() {
        var quote: MutableList<Quote>? = null
        var adjclose: MutableList<AdjClose>? = null
    }

    class Quote() {
        var open: MutableList<Double?>? = null
        var close: MutableList<Double?>? = null
        var low: MutableList<Double?>? = null
        var high: MutableList<Double?>? = null
        var volume: MutableList<Long?>? = null
    }

    class AdjClose() {
        var adjclose: MutableList<Double?>? = null
    }
}
