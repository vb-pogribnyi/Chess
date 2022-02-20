package com.example.chess

class Fish {
    external fun fishInit(): String
    external fun fishGo(history: String): String
    external fun perft(history: String): String

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