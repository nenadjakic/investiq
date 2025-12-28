package com.github.nenadjakic.toon.serializer

import com.github.nenadjakic.toon.annotation.ToonIgnore
import com.github.nenadjakic.toon.annotation.ToonProperty
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ReflectionToonSerializerTest {

    data class Context(
        val task: String,
        val location: String,
        val season: String
    )

    data class Hike(
        val id: Int,
        val name: String,
        val distanceKm: Double,
        val elevationGain: Int,
        val companion: String,
        val wasSunny: Boolean
    )

    data class Trip(
        val context: Context,
        val friends: List<String>,
        val hikes: List<Hike>
    )

    data class SimpleDTO(
        val id: Int,
        @param:ToonIgnore
        val secret: String,
        val name: String
    )

    data class TransientDTO(
        val id: Int,
        @Transient
        val runtimeOnly: String,
        val name: String
    )

    data class RenamedDTO(
        @property:ToonProperty("user_id")
        val id: Int,
        @property:ToonProperty("full_name")
        val name: String
    )

    data class Elem(
        @property:ToonProperty("person_id", order = 2)
        val id: Int,
        @property:ToonProperty("full_name", order = 1)
        val name: String
    )

    data class Wrapper(
        val elems: List<Elem>
    )

    private val serializer = ReflectionToonSerializer()

    @Test
    fun `serialize complex object`() {
        val trip = Trip(
            context = Context(
                task = "Our favorite hikes together",
                location = "Boulder",
                season = "spring_2025"
            ),
            friends = listOf("ana", "luis", "sam"),
            hikes = listOf(
                Hike(1, "Blue Lake Trail", 7.5, 320, "ana", true),
                Hike(2, "Ridge Overlook", 9.2, 540, "luis", false),
                Hike(3, "Wildflower Loop", 5.1, 180, "sam", true)
            )
        )

        val actual = serializer.serialize(trip).trim()
        println("[ACTUAL serialize complex object]\n$actual")
        assertTrue(actual.contains("task: Our favorite hikes together"))
        assertTrue(actual.contains("location: Boulder"))
        assertTrue(actual.contains("season: spring_2025"))
        assertTrue(actual.contains("friends[3]: ana,luis,sam"))

        val headerRegex = Regex("hikes\\[3\\]\\{([^}]*)\\}:")
        val match = headerRegex.find(actual)
        assertTrue(match != null, "Expected hikes header not found in serialized output")
        val cols = match!!.groupValues[1].split(",").map { it.trim() }.filter { it.isNotEmpty() }.toSet()
        val expectedCols = setOf("id", "name", "distanceKm", "elevationGain", "companion", "wasSunny")
        assertEquals(expectedCols, cols)

        val headerList = match.groupValues[1].split(",").map { it.trim() }
        val lines = actual.lines()
        val headerLineIndex = lines.indexOfFirst { headerRegex.containsMatchIn(it) }
        val rowLines = if (headerLineIndex >= 0) lines.drop(headerLineIndex + 1).takeWhile { it.isNotBlank() } else emptyList()
        assertTrue(rowLines.size == 3, "Expected 3 rows for hikes but found ${'$'}{rowLines.size}")

        val expectedById = mapOf(
            "1" to mapOf("id" to "1", "name" to "Blue Lake Trail", "distanceKm" to "7.5", "elevationGain" to "320", "companion" to "ana", "wasSunny" to "true"),
            "2" to mapOf("id" to "2", "name" to "Ridge Overlook", "distanceKm" to "9.2", "elevationGain" to "540", "companion" to "luis", "wasSunny" to "false"),
            "3" to mapOf("id" to "3", "name" to "Wildflower Loop", "distanceKm" to "5.1", "elevationGain" to "180", "companion" to "sam", "wasSunny" to "true")
        )

        for (row in rowLines) {
            val vals = row.trim().split(",")
            val rowMap = headerList.zip(vals).associate { it.first to it.second }
            val id = rowMap["id"] ?: error("Missing id in row: ${'$'}row")
            val expectedRow = expectedById[id] ?: error("Unexpected id in row: ${'$'}id")
            assertEquals(expectedRow, rowMap)
        }

    }

    @Test
    fun `ignores fields annotated with ToonIgnore`() {
        val dto = SimpleDTO(1, "topsecret", "Alice")
        val out = serializer.serialize(dto)
        println("[ACTUAL ignores fields annotated with ToonIgnore]\n$out")
        assertTrue(out.contains("id: 1"))
        assertTrue(out.contains("name: Alice"))
        assertTrue(!out.contains("secret"), "Field annotated with @ToonIgnore should be omitted")
    }

    @Test
    fun `ignores transient fields`() {
        val dto = TransientDTO(2, "temp", "Bob")
        val out = serializer.serialize(dto)
        println("[ACTUAL ignores transient fields]\n$out")
        assertTrue(out.contains("id: 2"))
        assertTrue(out.contains("name: Bob"))
        assertTrue(!out.contains("runtimeOnly"), "Transient field should be omitted")
    }

    @Test
    fun `respects ToonProperty name on property`() {
        val dto = RenamedDTO(10, "Charlie")
        val out = serializer.serialize(dto)
        println("[ACTUAL respects ToonProperty name on property]\n$out")
        assertTrue(out.contains("user_id: 10"), "Expected renamed property key 'user_id' to be present")
        assertTrue(out.contains("full_name: Charlie"), "Expected renamed property key 'full_name' to be present")
    }

    @Test
    fun `respects ToonProperty names in list headers`() {
        val wrapper = Wrapper(listOf(Elem(1, "A"), Elem(2, "B")))
        val out = serializer.serialize(wrapper)
        println("[ACTUAL respects ToonProperty names in list headers]\n$out")
        // header can be in any order, extract columns using regex
        val headerRegex = Regex("elems\\[2\\]\\{([^}]*)\\}:")
        val match = headerRegex.find(out)
        assertTrue(match != null, "Expected elems header not found")
        val headerStr = match!!.groupValues[1]
        val headerList = headerStr.split(",").map { it.trim() }
        val cols = headerList.toSet()
        assertEquals(setOf("person_id", "full_name"), cols)
        // order should be respected: full_name (order=1) first, then person_id (order=2)
        assertEquals(listOf("full_name", "person_id"), headerList)
        // also verify row values map correctly regardless of header order
        val lines = out.lines()
        val headerLineIndex = lines.indexOfFirst { headerRegex.containsMatchIn(it) }
        val rowLines = if (headerLineIndex >= 0) lines.drop(headerLineIndex + 1).takeWhile { it.isNotBlank() } else emptyList()
        assertTrue(rowLines.size == 2)
        val expectedById = mapOf(
            "1" to mapOf("person_id" to "1", "full_name" to "A"),
            "2" to mapOf("person_id" to "2", "full_name" to "B")
        )
        for (row in rowLines) {
            val vals = row.trim().split(",")
            val rowMap = headerList.zip(vals).associate { it.first to it.second }
            val id = rowMap["person_id"] ?: error("Missing person_id in row: ${'$'}row")
            assertEquals(expectedById[id], rowMap)
        }
    }

    @Test
    fun `uses configured pipe delimiter`() {
        val pipeSerializer = ReflectionToonSerializer("|")
        val wrapper = Wrapper(listOf(Elem(1, "A"), Elem(2, "B")))
        val out = pipeSerializer.serialize(wrapper)
        println("[ACTUAL]\n$out")

        val headerRegex = Regex("elems\\[2\\]\\{([^}]*)\\}:")
        val match = headerRegex.find(out)
        assertTrue(match != null, "Expected elems header not found")
        val headerStr = match!!.groupValues[1]
        val headerList = headerStr.split('|').map { it.trim() }
        // header order should respect ToonProperty.order (full_name then person_id)
        assertEquals(listOf("full_name", "person_id"), headerList)

        val lines = out.lines()
        val headerLineIndex = lines.indexOfFirst { headerRegex.containsMatchIn(it) }
        val rowLines = if (headerLineIndex >= 0) lines.drop(headerLineIndex + 1).takeWhile { it.isNotBlank() } else emptyList()
        assertTrue(rowLines.size == 2, "Expected 2 rows for elems but found ${'$'}{rowLines.size}")

        val expectedById = mapOf(
            "1" to mapOf("person_id" to "1", "full_name" to "A"),
            "2" to mapOf("person_id" to "2", "full_name" to "B")
        )

        for (row in rowLines) {
            val vals = row.trim().split('|')
            val rowMap = headerList.zip(vals).associate { it.first to it.second }
            val id = rowMap["person_id"] ?: error("Missing person_id in row: ${'$'}row")
            assertEquals(expectedById[id], rowMap)
        }
    }
}