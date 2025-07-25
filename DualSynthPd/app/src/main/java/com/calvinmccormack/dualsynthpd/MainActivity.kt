package com.calvinmccormack.dualsynthpd

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import android.view.InputDevice
import android.view.KeyEvent
import android.view.MotionEvent
import android.util.Log
import androidx.compose.ui.unit.sp


import com.calvinmccormack.dualsynthpd.ui.theme.DualSynthPdTheme

import java.io.File

import org.puredata.core.PdBase
import org.puredata.android.io.PdAudio

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Copy the .pd patch from assets to internal storage
        val patchFile = File(filesDir, "test_logger.pd")
        assets.open("pd-patches/test_logger.pd").use { input ->
            patchFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        // Extract the audio file into a relative path
        val soundDir = File(filesDir, "sound")
        soundDir.mkdirs()

        val assetFiles = assets.list("pd-patches/sound") ?: emptyArray()

        for (filename in assetFiles) {
            val destFile = File(soundDir, filename)
            assets.open("pd-patches/sound/$filename").use { input ->
                destFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        }

        // Initialize audio
        PdAudio.initAudio(44100, 0, 2, 8, true)

        // Open patch
        PdBase.openPatch(patchFile)

        // Open Pd Logger
        PdBase.setReceiver(PdLogReceiver())

        // Start audio
        PdAudio.startAudio(this)

        enableEdgeToEdge()
        setContent {
            DualSynthPdTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    InputMappingUI(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        PdAudio.stopAudio()
        PdBase.release()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        val label = when (keyCode) {
            KeyEvent.KEYCODE_BUTTON_A -> "startPlayback"
            KeyEvent.KEYCODE_BUTTON_B -> "startPlayback"
            KeyEvent.KEYCODE_BUTTON_X -> "stopPlayback"
            KeyEvent.KEYCODE_BUTTON_Y -> "stopPlayback"
            KeyEvent.KEYCODE_BUTTON_L1 -> "startPlayback"
            KeyEvent.KEYCODE_BUTTON_R1 -> "stopPlayback"
            KeyEvent.KEYCODE_BUTTON_SELECT -> "startPlayback"
            KeyEvent.KEYCODE_BUTTON_START -> "stopPlayback"
            KeyEvent.KEYCODE_BUTTON_THUMBL -> "startPlayback"
            KeyEvent.KEYCODE_BUTTON_THUMBR -> "stopPlayback"
            else -> null
        }

        if (label != null) {
            Log.d("DualSynthPd", "$label pressed")
            InteractionRouter.updateInput(label, true)
            return true
        }

        return super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        val label = when (keyCode) {
            KeyEvent.KEYCODE_BUTTON_A -> "startPlayback"
            KeyEvent.KEYCODE_BUTTON_B -> "startPlayback"
            KeyEvent.KEYCODE_BUTTON_X -> "stopPlayback"
            KeyEvent.KEYCODE_BUTTON_Y -> "stopPlayback"
            KeyEvent.KEYCODE_BUTTON_L1 -> "startPlayback"
            KeyEvent.KEYCODE_BUTTON_R1 -> "stopPlayback"
            KeyEvent.KEYCODE_BUTTON_SELECT -> "startPlayback"
            KeyEvent.KEYCODE_BUTTON_START -> "stopPlayback"
            KeyEvent.KEYCODE_BUTTON_THUMBL -> "startPlayback"
            KeyEvent.KEYCODE_BUTTON_THUMBR -> "stopPlayback"
            else -> null
        } // same switch as onKeyDown
        if (label != null) {
            InteractionRouter.updateInput(label, false)
            return true
        }
        return super.onKeyUp(keyCode, event)
    }

    override fun onGenericMotionEvent(event: MotionEvent): Boolean {
        if ((event.source and InputDevice.SOURCE_JOYSTICK) == InputDevice.SOURCE_JOYSTICK &&
            event.action == MotionEvent.ACTION_MOVE
        ) {

            // 🎚 Analog floats
            fun normalize(value: Float): Float = ((value + 1f) / 2f).coerceIn(0f, 1f)

            InteractionRouter.updateInput(
                "leftStickX",
                normalize(event.getAxisValue(MotionEvent.AXIS_X))
            )
            InteractionRouter.updateInput(
                "leftStickY",
                normalize(event.getAxisValue(MotionEvent.AXIS_Y))
            )
            InteractionRouter.updateInput(
                "rightStickX",
                normalize(event.getAxisValue(MotionEvent.AXIS_Z))
            )
            InteractionRouter.updateInput(
                "rightStickY",
                normalize(event.getAxisValue(MotionEvent.AXIS_RZ))
            )
            InteractionRouter.updateInput(
                "triggerL2",
                event.getAxisValue(MotionEvent.AXIS_LTRIGGER).coerceIn(0f, 1f)
            )
            InteractionRouter.updateInput(
                "triggerR2",
                event.getAxisValue(MotionEvent.AXIS_RTRIGGER).coerceIn(0f, 1f)
            )

            // 🎯 DPad hat switches
            val hatX = event.getAxisValue(MotionEvent.AXIS_HAT_X)
            val hatY = event.getAxisValue(MotionEvent.AXIS_HAT_Y)

            InteractionRouter.updateInput("triggerSample4", hatX < -0.5f)
            InteractionRouter.updateInput("triggerSample2", hatX > 0.5f)
            InteractionRouter.updateInput("triggerSample1", hatY < -0.5f)
            InteractionRouter.updateInput("triggerSample3", hatY > 0.5f)

            return true
        }

        return super.onGenericMotionEvent(event)
    }
}

@Composable
fun InputMappingUI(modifier: Modifier = Modifier) {
    val buttonOptions = listOf(
        "startPlayback", "stopPlayback",
        "triggerSample1", "triggerSample2",
        "triggerSample3", "triggerSample4",
        "increaseSpeed", "decreaseSpeed",
        "tapTempo", "swapPreset",
        "swapFxLStick", "swapFxRStick",
    )

    val buttonLabels = mapOf(
        "startPlayback" to "Cross (X)",
        "stopPlayback" to "Circle (O)",
        "tapTempo" to "Square",
        "swapPreset" to "Triangle",
        "decreaseSpeed" to "L1",
        "increaseSpeed" to "R1",
        "swapFxLStick" to "L3",
        "swapFxRStick" to "R3",
        "triggerSample1" to "DPadUp",
        "triggerSample2" to "DPadRight",
        "triggerSample3" to "DPadDown",
        "triggerSample4" to "DPadLeft"
    )

    val floatOptions = listOf(
        "Reverb", "Delay",
        "Bitcrush", "Distortion",
        "GranShiftUp", "Flange",
        "GranShiftDown", "Chorus",
        "stutter1", "stutter2"
    )

    val floatLabels = mapOf(
        "Reverb" to "Left Stick ↑",
        "Delay" to "Left Stick →",
        "Bitcrush" to "Left Stick ↓",
        "Distortion" to "Left Stick ←",
        "GranShiftUp" to "Right Stick ↑",
        "Flange" to "Right Stick →",
        "GranShiftDown" to "Right Stick ↓",
        "Chorus" to "Right Stick ←",
        "stutter1" to "L2 Trigger",
        "stutter2" to "R2 Trigger"
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text("Map Buttons to Actions", fontSize = 12.sp)
        }

        items(buttonOptions) { label ->
            val displayLabel = buttonLabels[label] ?: label
            var expanded by remember { mutableStateOf(false) }
            var selectedOption by remember { mutableStateOf(buttonOptions.first()) }

            Column {
                Text(text = displayLabel, fontSize = 10.sp)
                Box {
                    Button(
                        onClick = { expanded = true },
                        modifier = Modifier
                            .height(32.dp)
                            .padding(2.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(selectedOption, fontSize = 10.sp)
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        buttonOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    selectedOption = option
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(6.dp))
            Text("Map Joysticks/Triggers to Effects", fontSize = 12.sp)
        }

        items(floatOptions) { label ->
            val displayLabel = floatLabels[label] ?: label
            var expanded by remember { mutableStateOf(false) }
            var selectedOption by remember { mutableStateOf(floatOptions.first()) }

            Column {
                Text(text = displayLabel, fontSize = 10.sp)
                Box {
                    Button(
                        onClick = { expanded = true },
                        modifier = Modifier
                            .height(32.dp)
                            .padding(2.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(selectedOption, fontSize = 10.sp)
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        floatOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    selectedOption = option
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
