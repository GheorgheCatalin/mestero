package com.mestero.data.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.DocumentSnapshot
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class ListingModelTest {

    @Test
    fun `fromFirestoreDocument returns model with id and fields`() {
        val data = mapOf(
            "title" to "Plumbing",
            "description" to "Fix leaks",
            "category" to "Home",
            "subcategory" to "Plumbing",
            "county" to "Bucharest",
            "specificLocations" to "Sector 1",
            "phoneNumber" to "0700",
            "email" to "test@example.com",
            "website" to "https://example.com",
            "providerId" to "uid123",
            "views" to 3,
            "favoritesCount" to 1,
            "ratingSum" to 9,
            "ratingCount" to 2,
            "imageUrls" to listOf("url1"),
            "tags" to listOf("plumber"),
            "active" to true,
            "featured" to false
        )

        val snapshot = mockk<DocumentSnapshot>()
        every { snapshot.data } returns data
        every { snapshot.id } returns "doc123"

        val model = ListingModel.fromFirestoreDocument(snapshot)
        assertNotNull(model)
        model!!
        assertEquals("doc123", model.id)
        assertEquals("Plumbing", model.title)
        assertEquals("Home", model.category)
        assertEquals("uid123", model.providerId)
        assertEquals(3, model.views)
        assertEquals(1, model.favoritesCount)
        assertEquals(2, model.ratingCount)
        assertEquals(4.5, model.ratingAvg, 0.001)
    }
}



