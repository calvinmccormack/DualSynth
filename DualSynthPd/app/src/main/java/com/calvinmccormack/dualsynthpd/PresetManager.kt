package com.calvinmccormack.dualsynthpd

import android.content.Context
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

object PresetManager {
    private const val DIR_NAME = "presets"

    fun savePreset(context: Context, preset: InputPreset) {
        val dir = File(context.filesDir, DIR_NAME)
        dir.mkdirs()
        val file = File(dir, "${preset.name}.json")
        file.writeText(Json.encodeToString(preset))
    }

    fun loadPreset(context: Context, name: String): InputPreset? {
        val file = File(File(context.filesDir, DIR_NAME), "$name.json")
        return if (file.exists()) {
            Json.decodeFromString(file.readText())
        } else null
    }

    fun listPresets(context: Context): List<String> {
        val dir = File(context.filesDir, DIR_NAME)
        return dir.listFiles()?.mapNotNull {
            it.nameWithoutExtension.takeIf { name -> it.extension == "json" }
        } ?: emptyList()
    }
}