package com.calvinmccormack.dualsynth

object DspBridge {
    init {
        System.loadLibrary("dualsynth")
    }

    external fun setFilterCutoff(value: Float)
    external fun triggerSample1()
}