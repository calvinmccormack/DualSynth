package com.calvinmccormack.dualsynthpd

import kotlin.math.abs

object InteractionRouter {

    // Global toggles (to be connected to UI later)
    var sendToPd = true
    var sendToOsc = true
    var oscTargetIp = "192.168.1.102"
    var oscTargetPort = 8000

    private const val FLOAT_THRESHOLD = 0.01f
    private val floatState = mutableMapOf<String, Float>()
    private val boolState = mutableMapOf<String, Boolean>()

    // For continuous controls (sticks, triggers)
    fun updateInput(inputName: String, value: Float) {
        val mappedName = MappingConfig.floatMappings[inputName] ?: return
        val prev = floatState[inputName]
        if (prev == null || abs(prev - value) > FLOAT_THRESHOLD) {
            floatState[inputName] = value

            if (sendToPd) {
                PdMessenger.sendFloat(mappedName, value)
            }

            if (sendToOsc) {
                OscSender.send(mappedName, value)
            }
        }
    }

    // For binary controls (buttons)
    fun updateInput(inputName: String, pressed: Boolean) {
        val mappedName = MappingConfig.buttonMappings[inputName] ?: return
        val prev = boolState[inputName]
        if (prev == null || prev != pressed) {
            boolState[inputName] = pressed
            val value = if (pressed) 1f else 0f

            if (sendToPd) {
                PdMessenger.sendFloat(mappedName, value)
            }

            if (sendToOsc) {
                OscSender.send(mappedName, value)
            }
        }
    }
}