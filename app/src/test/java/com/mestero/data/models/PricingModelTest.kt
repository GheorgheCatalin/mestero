package com.mestero.data.models

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PricingModelTest {

    @Test
    fun `validate fixed price must be greater than 0`() {
        val model = PricingModel.createFixed(0.0, PricingUnit.TOTAL)
        val errors = model.validate()
        assertTrue(errors.any { it.contains("greater than 0") })
    }

    @Test
    fun `validate range prices`() {
        val badRange = PricingModel.createRange(0.0, -1.0, PricingUnit.PER_HOUR)
        val errors1 = badRange.validate()
        assertTrue(errors1.any { it.contains("Minimum price must be greater than 0") })
        assertTrue(errors1.any { it.contains("Maximum price must be greater than 0") })

        val inverted = PricingModel.createRange(100.0, 10.0, PricingUnit.PER_HOUR)
        val errors2 = inverted.validate()
        assertTrue(errors2.any { it.contains("Maximum price must be greater than minimum price") })
    }

    @Test
    fun `getFormattedPrice returns expected strings`() {
        val fixed = PricingModel.createFixed(50.0, PricingUnit.PER_HOUR)
        assertEquals("50.00 RON per hour", fixed.getFormattedPrice())

        val range = PricingModel.createRange(30.0, 80.0, PricingUnit.PER_SESSION)
        assertEquals("30.00 RON - 80.00 RON per session", range.getFormattedPrice())

        val tba = PricingModel.createToBeAgreed()
        assertEquals("Contact for price", tba.getFormattedPrice())
    }
}


