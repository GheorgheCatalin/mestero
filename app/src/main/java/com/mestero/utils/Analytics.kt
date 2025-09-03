package com.mestero.utils

import android.os.Bundle
import android.os.SystemClock
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.perf.FirebasePerformance
import com.google.firebase.perf.metrics.Trace


object Analytics {
    private val analytics: FirebaseAnalytics by lazy { Firebase.analytics }
    val performance: FirebasePerformance by lazy { FirebasePerformance.getInstance() }
    
    // Events
    object Events {
        const val APP_COLD_START = "app_cold_start"
        const val SCREEN_LOAD_TIME = "screen_load_time"
        const val FIRESTORE_QUERY = "firestore_query"

        const val CREATE_LISTING = "create_listing"
        const val SEND_MESSAGE = "send_message"
        const val BOOKING_REQUEST = "booking_request"
        const val LOAD_LISTINGS = "load_listings"
        const val CATEGORY_SELECTED = "category_selected"
        const val SEARCH_PERFORMED = "search_performed"
        const val SEARCH_RESULTS = "search_results"
    }
    
    // Parameter
    object Params {
        const val DURATION_MS = "duration_ms"
        const val SUCCESS = "success"
        const val ERROR = "error"
        const val CATEGORY = "category"
        const val SUBCATEGORY = "subcategory"
        const val USER_TYPE = "user_type"
        const val LISTING_ID = "listing_id"
        const val SCREEN_NAME = "screen_name"
        const val QUERY_TYPE = "query_type"
        const val RECORDS_COUNT = "records_count"
        const val FLOW_STEP = "flow_step"
        const val TOTAL_STEPS = "total_steps"
        const val SEARCH_QUERY = "search_query"
        const val SEARCH_TYPE = "search_type"
        const val RESULTS_COUNT = "results_count"
    }
    
    // App startup timer
    private var appStartTime: Long = 0

    fun logEvent(eventName: String, params: Map<String, Any> = emptyMap()) {
        try {
            val bundle = Bundle()
            params.forEach { (key, value) ->
                when (value) {
                    is String -> bundle.putString(key, value)
                    is Int -> bundle.putInt(key, value)
                    is Long -> bundle.putLong(key, value)
                    is Double -> bundle.putDouble(key, value)
                    is Boolean -> bundle.putBoolean(key, value)
                }
            }
            analytics.logEvent(eventName, bundle)
        } catch (e: Exception) {
        }
    }
    

    inline fun <T> measureAndLog(
        eventName: String,
        traceName: String = eventName,
        additionalParams: Map<String, Any> = emptyMap(),
        operation: () -> T
    ): T {
        val trace = performance.newTrace(traceName)
        val startTime = SystemClock.elapsedRealtime()
        
        return try {
            trace.start()
            val result = operation()
            val duration = SystemClock.elapsedRealtime() - startTime
            
            // Log successful event
            logEvent(eventName, additionalParams + mapOf(
                Params.DURATION_MS to duration,
                Params.SUCCESS to true
            ))
            
            trace.putAttribute("result", "success")
            result

        } catch (e: Exception) {
            val duration = SystemClock.elapsedRealtime() - startTime
            
            // Log error event
            logEvent(eventName, additionalParams + mapOf(
                Params.DURATION_MS to duration,
                Params.SUCCESS to false,
                Params.ERROR to (e.message ?: "Unknown error")
            ))
            
            trace.putAttribute("result", "error")

            throw e

        } finally {
            trace.stop()
        }
    }


    suspend inline fun <T> measureAndLogSuspend(
        eventName: String,
        traceName: String = eventName,
        additionalParams: Map<String, Any> = emptyMap(),
        operation: suspend () -> T
    ): T {
        val trace = performance.newTrace(traceName)
        val startTime = SystemClock.elapsedRealtime()
        
        return try {
            trace.start()
            val result = operation()
            val duration = SystemClock.elapsedRealtime() - startTime

            // Log successful event
            logEvent(eventName, additionalParams + mapOf(
                Params.DURATION_MS to duration,
                Params.SUCCESS to true
            ))
            
            trace.putAttribute("result", "success")
            result

        } catch (e: Exception) {
            val duration = SystemClock.elapsedRealtime() - startTime
            
            // Log error event
            logEvent(eventName, additionalParams + mapOf(
                Params.DURATION_MS to duration,
                Params.SUCCESS to false,
                Params.ERROR to (e.message ?: "Unknown error")
            ))
            
            trace.putAttribute("result", "error")

            throw e

        } finally {
            trace.stop()
        }
    }
    

    fun logAppStart() {
        appStartTime = SystemClock.elapsedRealtime()
    }

    // Log app cold start
    fun logAppReady() {
        if (appStartTime > 0) {
            val coldStartTime = SystemClock.elapsedRealtime() - appStartTime
            
            measureAndLog(
                eventName = Events.APP_COLD_START,
                traceName = "app_cold_start",
                additionalParams = mapOf(
                    Params.DURATION_MS to coldStartTime
                )
            ) { }
            
            appStartTime = 0
        }
    }

    // Measure the time ofa Firestore query
    suspend fun <T> measureFirestoreQuery(
        queryType: String,
        operation: suspend () -> T
    ): T {
        return measureAndLogSuspend(
            eventName = Events.FIRESTORE_QUERY,
            traceName = "firestore_$queryType",
            additionalParams = mapOf(
                Params.QUERY_TYPE to queryType
            ),
            operation = operation
        )
    }


    suspend fun <T> measureListingsLoad(
        queryType: String,
        operation: suspend () -> List<T>
    ): List<T> {
        return measureAndLogSuspend(
            eventName = Events.LOAD_LISTINGS,
            traceName = "load_listings_$queryType",
            additionalParams = mapOf(
                Params.QUERY_TYPE to queryType
            )
        ) {
            val result = operation()

            logEvent(Events.LOAD_LISTINGS, mapOf(
                Params.QUERY_TYPE to queryType,
                Params.RECORDS_COUNT to result.size
            ))
            result
        }
    }

}
