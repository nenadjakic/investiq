package com.github.nenadjakic.toon.serializer

import com.github.nenadjakic.toon.annotation.ToonIgnore
import com.github.nenadjakic.toon.annotation.ToonProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.javaField

class ReflectionToonSerializer(private val defaultDelimiter: String = ",") {

    private fun orderProps(props: Collection<KProperty1<out Any, *>>): List<KProperty1<out Any, *>> {
        return props.sortedWith(compareBy({ p ->
            p.findAnnotation<ToonProperty>()?.order ?: Int.MAX_VALUE
        }, { p ->
            // maintain declared order as tie-breaker by using property name as stable key
            p.name
        }))
    }

    fun serialize(obj: Any, indent: Int = 0): String {
        val sb = StringBuilder()
        val space = " ".repeat(indent)

        val kClass = obj::class
        // Collect constructor parameters annotated with ToonIgnore (common when annotating data-class params)
        val ctorIgnored = kClass.primaryConstructor?.parameters
            ?.filter { it.findAnnotation<ToonIgnore>() != null }
            ?.mapNotNull { it.name }
            ?.toSet() ?: emptySet()

        val props = kClass.declaredMemberProperties.filterNot { prop ->
            // ignore ToonIgnore annotation on the property
            if (prop.findAnnotation<ToonIgnore>() != null) return@filterNot true
            // ignore if constructor parameter is annotated (e.g. @ToonIgnore on primary constructor param)
            if (prop.name in ctorIgnored) return@filterNot true
            // ignore kotlin's @Transient on property
            if (prop.findAnnotation<kotlin.jvm.Transient>() != null) return@filterNot true
            // ignore java field level transient/annotations if present on the backing field
            val f = prop.javaField
            if (f != null) {
                if (f.getAnnotation(kotlin.jvm.Transient::class.java) != null) return@filterNot true
                if (f.getAnnotation(java.beans.Transient::class.java) != null) return@filterNot true
                if (f.getAnnotation(ToonIgnore::class.java) != null) return@filterNot true
                if (java.lang.reflect.Modifier.isTransient(f.modifiers)) return@filterNot true
            }
            false
        }

        val orderedProps = orderProps(props)

        for (prop in orderedProps) {
            val name = prop.findAnnotation<ToonProperty>()?.name ?: prop.name
            val value = prop.call(obj)

            when (value) {
                null -> sb.appendLine("$space$name: null")
                is String, is Number, is Boolean -> sb.appendLine("$space$name: $value")
                is List<*> -> {
                    if (value.isEmpty()) {
                        sb.appendLine("$space$name[0]:")
                    } else {
                        val first = value.firstOrNull()
                        when (first) {
                            null -> {
                                val listValues = value.joinToString(defaultDelimiter) { it.toString() }
                                sb.appendLine("$space$name[${value.size}]: $listValues")
                            }
                            is String, is Number, is Boolean -> {
                                val listValues = value.joinToString(defaultDelimiter) { it.toString() }
                                sb.appendLine("$space$name[${value.size}]: $listValues")
                            }
                            else -> {
                                // complex objects in the list -> print header with property names and CSV rows
                                val elemClass = first::class
                                val elemProps = elemClass.declaredMemberProperties.filterNot { it.findAnnotation<ToonIgnore>() != null }
                                val orderedElemProps = orderProps(elemProps)
                                val headerNames = orderedElemProps.joinToString(defaultDelimiter) { it.findAnnotation<ToonProperty>()?.name ?: it.name }
                                sb.appendLine("$space$name[${value.size}]{$headerNames}:")
                                val elemIndent = " ".repeat(indent + 2)
                                for (elem in value) {
                                    val row = orderedElemProps.joinToString(defaultDelimiter) { p ->
                                        val v = if (elem == null) null else p.call(elem)
                                        v?.toString() ?: ""
                                    }
                                    sb.appendLine("$elemIndent$row")
                                }
                            }
                        }
                    }
                }
                else -> {
                    sb.appendLine("$space$name:")
                    sb.append(serialize(value, indent + 2))
                }
            }
        }
        return sb.toString()
    }
}
