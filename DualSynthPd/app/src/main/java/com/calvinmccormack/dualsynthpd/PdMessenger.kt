package com.calvinmccormack.dualsynthpd

import org.puredata.core.PdBase

object PdMessenger {
    fun sendBang(receiver: String) {
        PdBase.sendBang(receiver)
    }

    fun sendFloat(receiver: String, value: Float) {
        PdBase.sendFloat(receiver, value)
    }

    fun sendSymbol(receiver: String, symbol: String) {
        PdBase.sendSymbol(receiver, symbol)
    }
}