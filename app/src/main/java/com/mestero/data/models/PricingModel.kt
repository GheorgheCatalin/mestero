package com.mestero.data.models

import android.content.Context
import com.mestero.R
import com.mestero.utils.FormatUtils

 data class PricingModel(
    val type: PricingType = PricingType.FIXED,
    val unit: PricingUnit = PricingUnit.TOTAL,
    val fixedPrice: Double = 0.0,
    val minPrice: Double = 0.0,
    val maxPrice: Double = 0.0
) {
    
    companion object {
        // Factory methods to create the different types of PricingModel instances
        fun createFixed(price: Double, unit: PricingUnit) = PricingModel(
            type = PricingType.FIXED,
            unit = unit,
            fixedPrice = price
        )
        
        fun createRange(minPrice: Double, maxPrice: Double, unit: PricingUnit) = PricingModel(
            type = PricingType.RANGE,
            unit = unit,
            minPrice = minPrice,
            maxPrice = maxPrice
        )
        
        fun createToBeAgreed() = PricingModel(
            type = PricingType.TO_BE_AGREED
        )
    }
    
    fun getFormattedPrice(context: Context): String {
        return when (type) {
            PricingType.FIXED -> "${FormatUtils.formatCurrency(fixedPrice)} ${unit.getDisplayText(context)}"
            PricingType.RANGE -> "${FormatUtils.formatCurrency(minPrice)} - ${FormatUtils.formatCurrency(maxPrice)} ${unit.getDisplayText(context)}"
            PricingType.TO_BE_AGREED -> context.getString(R.string.contact_for_price)
        }
    }
    
    fun validate(): List<String> {
        val errorsList = mutableListOf<String>()
        
        when (type) {
            PricingType.FIXED -> {
                if (fixedPrice <= 0) {
                    errorsList.add("Price must be greater than 0")
                }
            }
            PricingType.RANGE -> {
                if (minPrice <= 0) {
                    errorsList.add("Minimum price must be greater than 0")
                }
                if (maxPrice <= 0) {
                    errorsList.add("Maximum price must be greater than 0")
                }
                if (minPrice >= maxPrice) {
                    errorsList.add("Maximum price must be greater than minimum price")
                }
            }
            PricingType.TO_BE_AGREED -> {
                // No validation needed for TO_BE_AGREED
            }
        }
        
        return errorsList
    }
}

enum class PricingType {
    FIXED,
    RANGE,
    TO_BE_AGREED
}

enum class PricingUnit(private val displayTextResId: Int) {
    TOTAL(R.string.pricing_fixed),
    PER_HOUR(R.string.pricing_hourly),
    PER_SESSION(R.string.pricing_session),
    PER_SQUARE_METER(R.string.pricing_square_meter);
    
    fun getDisplayText(context: Context): String = context.getString(displayTextResId)
}

 