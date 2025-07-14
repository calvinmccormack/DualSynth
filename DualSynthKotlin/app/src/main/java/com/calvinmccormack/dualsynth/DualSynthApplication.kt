package com.calvinmccormack.dualsynth

import android.app.Application
import com.rmsl.juce.Java
import android.util.Log


class DualSynthApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Log.d("JUCEInit", "initialising JUCE...")
        Java.initialiseJUCE(this)
        Log.d("JUCEInit", "initialised JUCE!")
    }
}