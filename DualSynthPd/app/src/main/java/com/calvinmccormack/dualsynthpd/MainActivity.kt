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