package com.example.chess

import android.util.Log

class Game {
    var fish = Fish()

    private var history = ""
    private var moves = ArrayList<String>()
    private var endMoves = ArrayList<String>()
    private var selection = ""

    fun getMoves(): ArrayList<String> {
        return endMoves
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
    }

    fun select(pos: String): String? {
        if (endMoves.size > 0) {
            for (m in moves) {
                if (m.substring(2) == pos) {
//                    move(selection + pos)
                    return selection + pos
                }
            }
            endMoves.clear()
        }
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

    fun move(): String {
        val bestmove = fish.fishGo(history)

        move(bestmove)
        moves.clear()
        for (m in fish.perft(history).split(' ')) {
            if (m.length > 0) {
                moves.add(m)
            }
        }

        return bestmove
    }
}