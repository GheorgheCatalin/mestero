package com.mestero.constants

import com.mestero.BuildConfig


object Constants {
    const val SHARED_PREF_INTRO = "shared_preference_intro"
    const val SHARED_INTRO_DONE_FLAG = "shared_intro_done_flag"
}

object ClientType {
    const val SHARED_PREF_INTRO = "shared_preference_intro"
    const val SHARED_INTRO_DONE_FLAG = "shared_intro_done_flag"
}


object FirestoreCollections {
    private val prefix = BuildConfig.COLLECTION_PREFIX

    val USERS = "${prefix}users"
    val LISTINGS = "${prefix}listings"
    val BOOKING_REQUESTS = "${prefix}booking_requests"
    val REVIEWS = "${prefix}reviews"
    val CONVERSATIONS = "${prefix}conversations"
    val MESSAGES = "${prefix}messages"
}