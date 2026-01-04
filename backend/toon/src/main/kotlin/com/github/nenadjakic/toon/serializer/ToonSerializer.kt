package com.github.nenadjakic.toon.serializer

import com.github.nenadjakic.toon.annotation.ToonProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotation

interface ToonSerializer {
    fun orderProps(props: Collection<KProperty1<out Any, *>>): List<KProperty1<out Any, *>> {
        return props.sortedWith(compareBy({ p ->
            p.findAnnotation<ToonProperty>()?.order ?: Int.MAX_VALUE
        }, { p ->
            // maintain declared order as tie-breaker by using property name as stable key
            p.name
        }))
    }

    fun serialize(obj: Any, indent: Int = 0): String
}