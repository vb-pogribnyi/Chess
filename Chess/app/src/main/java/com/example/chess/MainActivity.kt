package com.example.chess

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.chess.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var game = Game()

    private fun endgame(msg: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Game over")
        builder.setMessage(msg)

        builder.setPositiveButton("OK") { dialog, which ->
            Toast.makeText(applicationContext,
                "Game over", Toast.LENGTH_SHORT).show()
        }
        builder.show()
    }

    private fun startgame() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose your side")
        builder.setMessage("White or black?")

        builder.setPositiveButton("White") { dialog, which ->
            binding.board.updSide(1)
            Toast.makeText(applicationContext,
                "You're white", Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton("Black") { dialog, which ->
            binding.board.updSide(0)
            Toast.makeText(applicationContext,
                "You're black", Toast.LENGTH_SHORT).show()
            val move = game.move()
            binding.board.movePiece(move)
        }
        builder.show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startgame()

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
                    if (game.getStartMoves().size == 0) {
                        endgame("You win")
                    }
                    val fishMove = game.move()
                    if (game.getStartMoves().size == 0) {
                        endgame("You lose")
                    }
                    binding.board.movePiece(fishMove)
                }


                binding.sampleText.text = game.history()
            }
            true
        }
    }
}