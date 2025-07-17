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
import android.view.KeyEvent
import android.util.Log

import com.calvinmccormack.dualsynthpd.ui.theme.DualSynthPdTheme

import java.io.File

import org.puredata.core.PdBase
import org.puredata.android.io.PdAudio

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Copy the .pd patch from assets to internal storage
        val patchFile = File(filesDir, "bang_to_play_wav.pd")
        assets.open("pd-patches/bang_to_play_wav.pd").use { input ->
            patchFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        // Extract the audio file into a relative path
        val soundDir = File(filesDir, "sound")
        soundDir.mkdirs()
        val wavFile = File(soundDir, "saving_grace.wav")
        assets.open("pd-patches/sound/saving_grace.wav").use { input ->
            wavFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        // Initialize audio
        PdAudio.initAudio(44100, 0, 2, 8, true)

        // Open patch
        PdBase.openPatch(patchFile)

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
        if (keyCode == KeyEvent.KEYCODE_BUTTON_A) {
            Log.d("DualSynthPd", "X button pressed")
            // Send a bang to Pd
            PdBase.sendBang("playSample")
            return true
        }
        return super.onKeyDown(keyCode, event)
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