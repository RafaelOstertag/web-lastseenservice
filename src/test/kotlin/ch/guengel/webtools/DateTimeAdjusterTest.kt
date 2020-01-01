package ch.guengel.webtools

import org.joda.time.DateTime
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class DateTimeAdjusterTest {
    private val baseTime = DateTime(2006, 7, 8, 12, 0, 0)
    private val subject = DateTimeAdjuster(baseTime)

    @Test
    fun `full specification`() {
        val actual = subject.by("6d5h4m3s")
        val expected = DateTime(2006, 7, 2, 6, 55, 57)

        assertEquals(expected, actual)
    }

    @Test
    fun `hour minute second specfication`() {
        val actual = subject.by("5h4m3s")
        val expected = DateTime(2006, 7, 8, 6, 55, 57)

        assertEquals(expected, actual)
    }

    @Test
    fun `minute second specfication`() {
        val actual = subject.by("4m3s")
        val expected = DateTime(2006, 7, 8, 11, 55, 57)

        assertEquals(expected, actual)
    }

    @Test
    fun `second specfication`() {
        val actual = subject.by("3s")
        val expected = DateTime(2006, 7, 8, 11, 59, 57)

        assertEquals(expected, actual)
    }

    @Test
    fun `minute specification`() {
        val actual = subject.by("4m")
        val expected = DateTime(2006, 7, 8, 11, 56, 0)

        assertEquals(expected, actual)
    }

    @Test
    fun `hour specification`() {
        val actual = subject.by("5h")
        val expected = DateTime(2006, 7, 8, 7, 0, 0)

        assertEquals(expected, actual)
    }

    @Test
    fun `day specification`() {
        val actual = subject.by("6d")
        val expected = DateTime(2006, 7, 2, 12, 0, 0)

        assertEquals(expected, actual)
    }

    @Test
    fun `mixed specification`() {
        val actual = subject.by("6d1s")
        val expected = DateTime(2006, 7, 2, 11, 59, 59)

        assertEquals(expected, actual)
    }
}