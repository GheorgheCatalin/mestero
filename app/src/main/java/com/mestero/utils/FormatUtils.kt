package com.mestero.utils

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*


object FormatUtils {
    
    // Date Formatters (thread-safe via lazy )
    private val bookingDateFormatter by lazy {
        SimpleDateFormat("MMM dd, yyyy 'at' HH:mm", Locale.getDefault())
    }

    fun formatBookingDate(timestamp: Timestamp?): String {
        return timestamp?.toDate()?.let { date ->
            bookingDateFormatter.format(date)
        } ?: "Unknown date"
    }
    

    // Format timestamp as relative time (ex: "Today", "xd ago", "xw ago")
    fun formatRelativeDate(timestamp: Timestamp?): String {
        return timestamp?.toDate()?.let { date ->
            val now = System.currentTimeMillis()
            val reviewTime = date.time
            val diffInDays = (now - reviewTime) / (1000 * 60 * 60 * 24)
            
            when {
                diffInDays < 1 -> "Today"
                diffInDays < 7 -> "${diffInDays}d ago"
                diffInDays < 30 -> "${diffInDays / 7}w ago"
                diffInDays < 365 -> "${diffInDays / 30}mo ago"
                else -> "${diffInDays / 365}y ago"
            }
        } ?: "Unknown date"
    }
    

    fun formatCurrency(amount: Double): String {
        return "%.2f RON".format(amount)
    }
    

    fun formatRating(rating: Float): String {
        // Format rating with 1 decimal
        return "%.1f".format(rating)
    }
    

    fun formatRating(rating: Double): String {
        return "%.1f".format(rating)
    }
    

    fun formatRatingWithCount(rating: Float, count: Int): String {
        return "${formatRating(rating)} ($count ${if (count == 1) "review" else "reviews"})"
    }
}

// Extension functions for cleaner usage
fun Double.formatAsCurrency(): String = FormatUtils.formatCurrency(this)
fun Float.formatAsRating(): String = FormatUtils.formatRating(this)
fun Double.formatAsRating(): String = FormatUtils.formatRating(this)
fun Timestamp?.formatAsBookingDate(): String = FormatUtils.formatBookingDate(this)
fun Timestamp?.formatAsRelativeDate(): String = FormatUtils.formatRelativeDate(this) 