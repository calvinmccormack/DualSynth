package com.calvinmccormack.dualsynth
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import kotlin.random.Random
import android.util.Log
import android.os.Handler
import android.os.Looper
import com.calvinmccormack.dualsynth.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var nativeManager: NativeManager

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        // player setup
        nativeManager = NativeManager()


        Handler(Looper.getMainLooper()).post({
            nativeManager.setup()


            Log.d("NativePlay", "Calling nativeManager.play()")
            runOnUiThread {
                // Play A440
                nativeManager.play(440.0)
                Log.d("NativePlay", "Returned from nativeManager.play()")
            }
        })
    }


}