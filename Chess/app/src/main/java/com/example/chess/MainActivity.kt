package com.example.chess

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import com.example.chess.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var game = Game()

    fun selectCallback(pos: String) {
        Log.d("SEL", pos)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        game.move()
        game.select("a7")
        game.select("a6")
        binding.sampleText.text = game.history()
        binding.board.setOnTouchListener { view, motionEvent ->
            if (motionEvent != null) {
                binding.board.setSelection(motionEvent.getX(), motionEvent.getY())
            }
            true
        }

        // Example of a call to a native method
//        binding.sampleText.text = fishInit()
//        var history = "e2e4"
//        val firstMove = fishGo(history)
//        history += " " + firstMove
//        val moves1 = perft(history)
//        history += " " + moves1.split(' ')[3]
//        val secondMove = fishGo(history)
//        history += " " + secondMove
//        val moves2 = perft(history)
//        history += " " + moves2.split(' ')[6]
//        binding.sampleText.text = fishInit()
    }

    /**
     * A native method that is implemented by the 'chess' native library,
     * which is packaged with this application.
     */
}