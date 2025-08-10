package com.mestero.utils

import com.google.firebase.Timestamp
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*

class FormatUtilsTest {

    @Test
    fun `formatCurrency formats RON`() {
        assertEquals("10.00 RON", FormatUtils.formatCurrency(10.0))
    }

    @Test
    fun `formatRating formats single decimal`() {
        assertEquals("4.5", FormatUtils.formatRating(4.49))
        assertEquals("4.5", FormatUtils.formatRating(4.5f))
    }

    @Test
    fun `formatBookingDate returns fallback on null`() {
        assertEquals("Unknown date", FormatUtils.formatBookingDate(null))
    }

    @Test
    fun `formatRelativeDate basic ranges`() {
        val now = System.currentTimeMillis()
        val today = Timestamp(Date(now))
        val threeDaysAgo = Timestamp(Date(now - 3L * 24 * 60 * 60 * 1000))

        // We check that outputs are one of the expected buckets
        assertEquals("Today", FormatUtils.formatRelativeDate(today))
        val threeDays = FormatUtils.formatRelativeDate(threeDaysAgo)
        // e.g., "3d ago"
        assertEquals(true, threeDays.endsWith("d ago") || threeDays == "Today")
    }
}


