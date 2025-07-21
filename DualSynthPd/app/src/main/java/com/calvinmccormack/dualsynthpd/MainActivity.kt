package com.calvinmccormack.dualsynthpd

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import android.view.InputDevice
import android.view.KeyEvent
import android.view.MotionEvent
import android.util.Log

import com.calvinmccormack.dualsynthpd.ui.theme.DualSynthPdTheme

import java.io.File

import org.puredata.core.PdBase
import org.puredata.android.io.PdAudio

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Copy the .pd patch from assets to internal storage
        val patchFile = File(filesDir, "all_inputs.pd")
        assets.open("pd-patches/all_inputs.pd").use { input ->
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
                    Greeting(
                        name = ", Todd Rundgren is Playing",
                        modifier = Modifier.padding(innerPadding)
                    )
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
        when (keyCode) {

            // Shape Buttons
            KeyEvent.KEYCODE_BUTTON_A -> { // X Button
                Log.d("DualSynthPd", "X button pressed")
                PdBase.sendBang("startPlayback")
                return true
            }
            KeyEvent.KEYCODE_BUTTON_B -> { // O Button
                Log.d("DualSynthPd", "O button pressed")
                PdBase.sendBang("startPlayback")
                return true
            }
            KeyEvent.KEYCODE_BUTTON_X -> { // Square Button
                Log.d("DualSynthPd", "Square button pressed")
                PdBase.sendBang("stopPlayback")
                return true
            }
            KeyEvent.KEYCODE_BUTTON_Y -> { // Triangle Button
                Log.d("DualSynthPd", "Triangle button pressed")
                PdBase.sendBang("stopPlayback")
                return true
            }

            // L1+R1
            KeyEvent.KEYCODE_BUTTON_L1 -> { // L1 Button
                Log.d("DualSynthPd", "L1 button pressed")
                PdBase.sendBang("startPlayback")
                return true
            }
            KeyEvent.KEYCODE_BUTTON_R1 -> { // R1 Button
                Log.d("DualSynthPd", "R1 button pressed")
                PdBase.sendBang("stopPlayback")
                return true
            }

            // Share + Options
            KeyEvent.KEYCODE_BUTTON_SELECT -> { // L1 Button
                Log.d("DualSynthPd", "Share button pressed")
                PdBase.sendBang("startPlayback")
                return true
            }
            KeyEvent.KEYCODE_BUTTON_START -> { // R1 Button
                Log.d("DualSynthPd", "Options button pressed")
                PdBase.sendBang("stopPlayback")
                return true
            }

            // L3 + R3
            KeyEvent.KEYCODE_BUTTON_THUMBL -> { // L3 Button
                Log.d("DualSynthPd", "L3 button pressed")
                PdBase.sendBang("startPlayback")
                return true
            }
            KeyEvent.KEYCODE_BUTTON_THUMBR -> { // R3 Button
                Log.d("DualSynthPd", "R3 button pressed")
                PdBase.sendBang("stopPlayback")
                return true
            }

        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onGenericMotionEvent(event: MotionEvent): Boolean {
        if (event.source and InputDevice.SOURCE_JOYSTICK == InputDevice.SOURCE_JOYSTICK &&
            event.action == MotionEvent.ACTION_MOVE) {

            // 🎚Analog floats — normalized and sent to Pd
            val lx = event.getAxisValue(MotionEvent.AXIS_X)           // Left Stick X (-1 to 1)
            val ly = event.getAxisValue(MotionEvent.AXIS_Y)           // Left Stick Y (-1 to 1)
            val rx = event.getAxisValue(MotionEvent.AXIS_Z)           // Right Stick X (-1 to 1)
            val ry = event.getAxisValue(MotionEvent.AXIS_RZ)          // Right Stick Y (-1 to 1)
            val l2 = event.getAxisValue(MotionEvent.AXIS_LTRIGGER)    // 0 to 1
            val r2 = event.getAxisValue(MotionEvent.AXIS_RTRIGGER)    // 0 to 1

            // Normalize stick values (-1 to 1) to (0 to 1)
            fun normalizeStick(value: Float): Float = ((value + 1f) / 2f).coerceIn(0f, 1f)

            PdMessenger.sendFloat("leftStickX", normalizeStick(lx))
            PdMessenger.sendFloat("leftStickY", normalizeStick(ly))
            PdMessenger.sendFloat("rightStickX", normalizeStick(rx))
            PdMessenger.sendFloat("rightStickY", normalizeStick(ry))
            PdMessenger.sendFloat("triggerL2", l2.coerceIn(0f, 1f))
            PdMessenger.sendFloat("triggerR2", r2.coerceIn(0f, 1f))


            // DPad Bangs
            val hatX = event.getAxisValue(MotionEvent.AXIS_HAT_X)
            val hatY = event.getAxisValue(MotionEvent.AXIS_HAT_Y)

            if (hatX < -0.5f) {
                Log.d("DualSynthPd", "Hat Left")
                PdBase.sendBang("triggerSample4")
            } else if (hatX > 0.5f) {
                Log.d("DualSynthPd", "Hat Right")
                PdBase.sendBang("triggerSample2")
            }

            if (hatY < -0.5f) { // Up is negative
                Log.d("DualSynthPd", "Hat Up")
                PdBase.sendBang("triggerSample1")
            } else if (hatY > 0.5f) {
                Log.d("DualSynthPd", "Hat Down")
                PdBase.sendBang("triggerSample3")
            }

            return true
        }
        return super.onGenericMotionEvent(event)
    }
}



@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DualSynthPdTheme {
        Greeting("Android")
    }
}