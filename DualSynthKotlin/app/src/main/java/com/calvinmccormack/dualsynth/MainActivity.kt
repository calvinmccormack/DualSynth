package com.calvinmccormack.dualsynth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import kotlin.random.Random
import com.calvinmccormack.dualsynth.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var nativeManager: NativeManager




    companion object {
        // Used to load the 'dualsynth' library on application startup.
        init {
            System.loadLibrary("dualsynth")
        }
    }
}