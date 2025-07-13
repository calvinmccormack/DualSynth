package com.calvinmccormack.dualsynth

import android.app.Application
import com.rmsl.juce.Java

class DualSynthApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Java.initialiseJUCE(this)
    }
}