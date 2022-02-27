package com.example.chess

import android.util.Log

class Game {
    var fish = Fish()

    private var history = ""
    private var moves = ArrayList<String>()
    private var endMoves = ArrayList<String>()
    private var selection = ""

    private fun getMoves(): String {
        val resultStr = fish.perft(history)
        return resultStr
    }

    fun history(): String {
        return history
    }

    fun move(dst: String) {
        Log.d("MOVE", dst)
    }

    fun select(pos: String): ArrayList<String> {
        if (endMoves.size > 0) {
            for (m in moves) {
                if (m.substring(2) == pos) {
                    move(selection + pos)
                    return ArrayList<String>()
                }
            }
            endMoves.clear()
        }
        var result = ArrayList<String>()
        selection = ""
        if (endMoves.size == 0) {
            if (pos.length != 2) {
                return result
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
        return result
    }

    fun move() {
        val bestmove = fish.fishGo(history)
        if (history.length > 0) {
            history += ' '
        }
        history += bestmove

        moves.clear()
        for (m in fish.perft(history).split(' ')) {
            if (m.length > 0) {
                moves.add(m)
            }
        }

        move(bestmove)
    }
}