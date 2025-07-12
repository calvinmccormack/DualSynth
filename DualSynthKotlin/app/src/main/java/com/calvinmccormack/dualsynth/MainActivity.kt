package com.calvinmccormack.dualsynth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.calvinmccormack.dualsynth.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        DspBridge.setFilterCutoff(750.0f)
        DspBridge.triggerSample1()

    }

    /**
     * A native method that is implemented by the 'dualsynth' native library,
     * which is packaged with this application.
     */

    companion object {
        // Used to load the 'dualsynth' library on application startup.
        init {
            System.loadLibrary("dualsynth")
        }
    }
}