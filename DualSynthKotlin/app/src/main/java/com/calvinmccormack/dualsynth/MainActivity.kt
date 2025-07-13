package com.calvinmccormack.dualsynth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.calvinmccormack.dualsynth.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    /**
     * A native method that is implemented by the 'dualsynth' native library,
     * which is packaged with this application.
     */

    companion object {
        // Used to load the 'dualsynth' library on application startup.
        init {
            System.loadLibrary("native-lib")
        }
    }
}