package com.example.chess

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.chess.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Example of a call to a native method
        binding.sampleText.text = fishInit()
        fishGo("")
    }

    /**
     * A native method that is implemented by the 'chess' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String
    external fun fishInit(): String
    external fun fishGo(history:String): String

    companion object {
        // Used to load the 'chess' library on application startup.
        init {
            System.loadLibrary("chess")
        }
    }
}