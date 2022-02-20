package com.example.chess

class Game {
    var fish = Fish()

    private var history = ""
    private var moves = ArrayList<String>()
    private var selection = ""

    private fun getMoves(): String {
        val resultStr = fish.perft(history)
        return resultStr
    }

    fun move(dst: String) {
        //
    }

    fun select(pos: String): ArrayList<String> {
        selection = ""
        var result = ArrayList<String>()
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
        return result
    }

    fun move(): String {
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

        return bestmove
    }
}