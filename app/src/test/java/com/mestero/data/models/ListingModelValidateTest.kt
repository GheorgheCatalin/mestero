package com.mestero.data.models

import org.junit.Assert.assertTrue
import org.junit.Test

class ListingModelValidateTest {

    private fun baseValidModel(): ListingModel = ListingModel(
        title = "Valid title",
        description = "Sufficiently long description for validation",
        category = "Home",
        subcategory = "Plumbing",
        pricingModel = PricingModel.createFixed(50.0, PricingUnit.PER_HOUR),
        email = "valid@example.com",
        website = "https://example.com"
    )

    @Test
    fun `invalid when short title and description`() {
        val model = baseValidModel().copy(
            title = "ab",
            description = "too short"
        )
        val errors = model.validate()
        assertTrue(errors.any { it.contains("Title must be between") })
        assertTrue(errors.any { it.contains("Description must be between") })
    }

    @Test
    fun `invalid when missing category or subcategory`() {
        val model = baseValidModel().copy(category = "", subcategory = "")
        val errors = model.validate()
        assertTrue(errors.any { it.contains("Category is required") })
        assertTrue(errors.any { it.contains("Subcategory is required") })
    }

    @Test
    fun `invalid when pricing model invalid`() {
        val model = baseValidModel().copy(
            pricingModel = PricingModel.createFixed(0.0, PricingUnit.TOTAL)
        )
        val errors = model.validate()
        assertTrue(errors.any { it.contains("Price must be greater than 0") })
    }

    @Test
    fun `invalid when email and website formats are wrong`() {
        val model = baseValidModel().copy(
            email = "badEmail",
            website = "htp://bad-url"
        )
        val errors = model.validate()
        assertTrue(errors.any { it.contains("valid email") })
        assertTrue(errors.any { it.contains("valid website") })
    }
}


