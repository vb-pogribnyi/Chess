package com.example.chess

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import com.example.chess.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var game = Game()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val move = game.move()
        binding.board.movePiece(move)
        binding.board.setOnTouchListener { view, motionEvent ->
            if (motionEvent != null && motionEvent.action == MotionEvent.ACTION_DOWN) {
                val pos = binding.board.setSelection(motionEvent.getX(), motionEvent.getY())
                val move = game.select(pos)
                if (move == null) {
                    binding.board.setCandidates(game.getMoves())
                } else {
                    binding.board.movePiece(move)
                    binding.board.setCandidates(ArrayList())
                    binding.board.setSelection(-1f, -1f)
                    game.move(move)
                    val fishMove = game.move()
                    binding.board.movePiece(fishMove)
                }


                binding.sampleText.text = game.history()
            }
            true
        }
    }
}