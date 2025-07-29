package com.calvinmccormack.dualsynthpd

import kotlinx.serialization.Serializable

@Serializable
data class InputPreset(
    val name: String,
    val buttonMappings: Map<String, String>,
    val floatMappings: Map<String, String>
)