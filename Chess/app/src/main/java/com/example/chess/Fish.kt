package com.example.chess

class Fish {
    external fun fishInit(): String
    external fun go(depth: Int): String
    external fun setpos(history: String)
    external fun perft(history: String): String
    external fun stop()
    external fun fen(): String

    var version: String = ""

    companion object {
        // Used to load the 'chess' library on application startup.
        init {
            System.loadLibrary("chess")
        }
    }

    init {
        version = fishInit()
    }
}