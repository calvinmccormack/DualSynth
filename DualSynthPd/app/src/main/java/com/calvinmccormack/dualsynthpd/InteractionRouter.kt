package com.calvinmccormack.dualsynthpd

import kotlin.math.abs

object InteractionRouter {

    // Global toggles (to be connected to UI later)
    var sendToPd = true
    var sendToOsc = true
    var oscTargetIp = "192.168.1.7"
    var oscTargetPort = 8000

    private const val FLOAT_THRESHOLD = 0.01f
    private val floatState = mutableMapOf<String, Float>()
    private val boolState = mutableMapOf<String, Boolean>()

    // For continuous controls (sticks, triggers)
    fun updateInput(label: String, value: Float) {
        val prev = floatState[label]
        if (prev == null || abs(prev - value) > FLOAT_THRESHOLD) {
            floatState[label] = value

            if (sendToPd) {
                PdMessenger.sendFloat(label, value)
            }

            if (sendToOsc) {
                OscSender.send(label, value)
            }
        }
    }

    // For binary controls (buttons)
    fun updateInput(label: String, pressed: Boolean) {
        val prev = boolState[label]
        if (prev == null || prev != pressed) {
            boolState[label] = pressed
            val value = if (pressed) 1f else 0f

            if (sendToPd) {
                PdMessenger.sendFloat(label, value)
            }

            if (sendToOsc) {
                OscSender.send(label, value)
            }
        }
    }
}