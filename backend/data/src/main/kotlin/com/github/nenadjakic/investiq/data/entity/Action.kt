package com.github.nenadjakic.investiq.data.entity

import com.github.nenadjakic.investiq.data.entity.asset.Asset
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDate
import java.util.UUID

@Entity
@Table(name = "actions")
class Action {

    @Id
    @Column(name = "id", nullable = false)
    var actionId: UUID? = null

    @Column(name = "name", length = 100, nullable = false)
    lateinit var name: String

    @ManyToOne(optional = false)
    @JoinColumn(name = "asset_id", nullable = false)
    lateinit var asset: Asset

    @Column(name = "rule", length = 500, nullable = false)
    lateinit var rule: String

    @Column(name = "description", length = 1000)
    var description: String? = null


    @Column(name = "date", nullable = false)
    lateinit var date: LocalDate

    @Column(name = "executed", nullable = false)
    var executed: Boolean = false
}