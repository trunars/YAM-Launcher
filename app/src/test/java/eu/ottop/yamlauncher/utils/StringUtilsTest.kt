package eu.ottop.yamlauncher.utils

import org.junit.Assert.*
import org.junit.Test

class StringUtilsTest {

    private val stringUtils = StringUtils()

    @Test
    fun cleanString_removesSpecialCharacters() {
        val result = stringUtils.cleanString("App@#\$Name!")
        assertEquals("AppName", result)
    }

    @Test
    fun cleanString_handlesNull() {
        val result = stringUtils.cleanString(null)
        assertNull(result)
    }

    @Test
    fun cleanString_handlesEmptyString() {
        val result = stringUtils.cleanString("")
        assertEquals("", result)
    }

    @Test
    fun cleanString_preservesAlphanumeric() {
        val result = stringUtils.cleanString("App123Name")
        assertEquals("App123Name", result)
    }

    @Test
    fun cleanString_handlesWhitespace() {
        val result = stringUtils.cleanString("  App  Name  ")
        assertEquals("AppName", result)
    }

    @Test
    fun addStartTextIfNotEmpty_withNonEmptyText() {
        val result = stringUtils.addStartTextIfNotEmpty("Temperature", "* ")
        assertEquals("* Temperature", result)
    }

    @Test
    fun addStartTextIfNotEmpty_withEmptyText() {
        val result = stringUtils.addStartTextIfNotEmpty("", "* ")
        assertEquals("", result)
    }

    @Test
    fun addEndTextIfNotEmpty_withNonEmptyText() {
        val result = stringUtils.addEndTextIfNotEmpty("Temperature", " C")
        assertEquals("Temperature C", result)
    }

    @Test
    fun addEndTextIfNotEmpty_withEmptyText() {
        val result = stringUtils.addEndTextIfNotEmpty("", " C")
        assertEquals("", result)
    }

    @Test
    fun getFuzzyPattern_returnsValidRegex() {
        val pattern = stringUtils.getFuzzyPattern("abc")
        assertTrue(pattern.matches("abc"))
        assertTrue(pattern.matches("aXXbXXc"))
        assertFalse(pattern.matches("acb"))
    }

    @Test
    fun getFuzzyPattern_cachesPattern() {
        val pattern1 = stringUtils.getFuzzyPattern("test")
        val pattern2 = stringUtils.getFuzzyPattern("test")
        assertSame(pattern1, pattern2)
    }
}