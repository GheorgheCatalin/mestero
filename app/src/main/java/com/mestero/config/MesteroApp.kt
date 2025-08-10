package com.mestero.config

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

// Required for Hilt DI setup
@HiltAndroidApp
class MesteroApp : Application()