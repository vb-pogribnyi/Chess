package com.example.chess

import android.os.Handler
import android.os.Looper
import android.util.Log
//import java.util.concurrent.Executor

class Game(gameView: GameView) {
    var fish = Fish()

    private var history = ""
    private var moves = ArrayList<String>()
    private var endMoves = ArrayList<String>()
    private var selection = ""
    private val mainLooper = Looper.getMainLooper()
    private val view: GameView;

    init {
        this.view = gameView
        loadMoves()
    }

    fun stop() {
        Log.d("CHESSGAME", "Stopping game")
        fish.stop()
    }

    fun getMoves(): ArrayList<String> {
        return endMoves
    }

    fun getStartMoves(): ArrayList<String> {
        val result: ArrayList<String> = ArrayList()
        for (m in moves) {
            result.add(m.substring(0, 2))
        }
        return result
    }

    fun history(): String {
        return history
    }

    fun move(dst: String) {
        Log.d("MOVE", dst)
        if (history.length > 0) {
            history += ' '
        }
        history += dst
        fish.setpos(history)
        view.setFen(fish.fen())
    }

    // Returns Stockfish-formatted move notation if the
    // move is complete, of NULL otherwise.
    // Sets the selection variable if the move is incomplete.
    // Unsets the selection if the move is invalid
    fun select(pos: String): String? {
        if (endMoves.size > 0) {
            for (m in moves) {
                if (m.substring(2) == pos) {
                    // The move is valid and complete
                    return selection + pos
                }
            }
            // The move was invalid
            endMoves.clear()
        }
        // Calculate new set of end points given selection as the starting point
        var result = ArrayList<String>()
        selection = ""
        if (endMoves.size == 0) {
            if (pos.length != 2) {
                return null
            }
            for (m in moves) {
                if (m.substring(0, 2) == pos) {
                    result.add(m.substring(2))
                }
            }
            if (!result.isEmpty()) {
                selection = pos
            }
            endMoves = result
        }
        return null
    }

    private fun loadMoves() {
        moves.clear()
        for (m in fish.perft(history).split(' ')) {
            if (m.length > 0) {
                moves.add(m)
            }
        }
    }

    fun move(): String {
        Thread({

            val bestmove = fish.go(10)
//            val bestmove = "e2e4"
            Handler(mainLooper).post {
                move(bestmove)
                loadMoves()
            }
        }).start()
        return "bestmove"
    }
}