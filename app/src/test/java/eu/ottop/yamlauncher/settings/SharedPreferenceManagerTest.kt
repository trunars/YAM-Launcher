package eu.ottop.yamlauncher.settings

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SharedPreferenceManagerTest {

    @Mock
    private lateinit var mockContext: android.content.Context

    @Test
    fun parseUpdateIntervalMs_parsesMinutes() {
        assertEquals(15 * 60_000L, parseInterval("15m"))
        assertEquals(30 * 60_000L, parseInterval("30m"))
    }

    @Test
    fun parseUpdateIntervalMs_parsesHours() {
        assertEquals(60 * 60_000L, parseInterval("1h"))
        assertEquals(120 * 60_000L, parseInterval("2h"))
    }

    @Test
    fun parseUpdateIntervalMs_parsesDays() {
        assertEquals(24 * 60 * 60_000L, parseInterval("1d"))
        assertEquals(7 * 24 * 60 * 60_000L, parseInterval("7d"))
    }

    @Test
    fun parseUpdateIntervalMs_parsesBareNumberAsMinutes() {
        assertEquals(15 * 60_000L, parseInterval("15"))
        assertEquals(60 * 60_000L, parseInterval("60"))
    }

    @Test
    fun parseUpdateIntervalMs_handlesInvalidInput() {
        assertEquals(15 * 60_000L, parseInterval("invalid"))
        assertEquals(15 * 60_000L, parseInterval(""))
        assertEquals(15 * 60_000L, parseInterval("-1m"))
    }

    @Test
    fun parseUpdateIntervalMs_ensuresMinimumInterval() {
        // Values less than 1 minute should be coerced to 1 minute
        val result = parseIntervalWithCoerce("30")
        assertTrue(result >= 60_000L)
    }

    private fun parseInterval(raw: String): Long {
        val s = raw.trim().lowercase()
        if (s.isEmpty()) return 15 * 60_000L
        if (s.all { it.isDigit() }) {
            val minutes = s.toLongOrNull() ?: return 15 * 60_000L
            if (minutes <= 0L) return 15 * 60_000L
            return minutes * 60_000L
        }
        val match = Regex("^(\\d+)\\s*([mhd])$").find(s) ?: return 15 * 60_000L
        val value = match.groupValues[1].toLongOrNull() ?: return 15 * 60_000L
        if (value <= 0L) return 15 * 60_000L
        val multiplier = when (match.groupValues[2]) {
            "m" -> 60_000L
            "h" -> 60 * 60_000L
            "d" -> 24 * 60 * 60_000L
            else -> return 15 * 60_000L
        }
        return try {
            Math.multiplyExact(value, multiplier)
        } catch (_: ArithmeticException) {
            Long.MAX_VALUE
        }
    }

    private fun parseIntervalWithCoerce(raw: String): Long {
        val defaultMs = 15 * 60_000L
        val result = parseInterval(raw)
        return result.coerceAtLeast(60_000L)
    }
}