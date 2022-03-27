package com.example.chess

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.min

public data class MuPair<A, B>(
    public var first: A,
    public var second: B
)

class GameView : View {
    constructor(context: Context): super(context)
    constructor(context: Context, attrs: AttributeSet): super(context, attrs)

    var selection = Pair(-1, -1)
    var side = 0
    val candidates = ArrayList<Pair<Int, Int>>()
    private val colorWhite = Color.rgb(255, 188, 0)
    private val colorBlack = Color.rgb(173, 129, 3)
    private val colorSelection = Color.BLUE
    private val colorCandidate = Color.GREEN
    val squares = 8
    var squareWidth: Float = 0f
    var squareHeight: Float = 0f
    val vstep = 110
    val hstep = 220
    val tmarg = 15
    val lmarg = 0
    val pieceRects = mapOf(
        'q' to Rect(lmarg, tmarg, lmarg + vstep, tmarg + hstep),
        'k' to Rect(lmarg + vstep, tmarg, lmarg + 2 * vstep, tmarg + hstep),
        'r' to Rect(lmarg + 2 * vstep, tmarg, lmarg + 3 * vstep, tmarg + hstep),
        'b' to Rect(lmarg + 3 * vstep, tmarg, lmarg + 4 * vstep, tmarg + hstep),
        'n' to Rect(lmarg + 4 * vstep, tmarg, lmarg + 5 * vstep, tmarg + hstep),
        'p' to Rect(lmarg + 5 * vstep, tmarg, lmarg + 6 * vstep, tmarg + hstep),

        'Q' to Rect(lmarg, tmarg + hstep, lmarg + vstep, tmarg + 2 * hstep),
        'K' to Rect(lmarg + vstep, tmarg + hstep, lmarg + 2 * vstep, tmarg + 2 * hstep),
        'R' to Rect(lmarg + 2 * vstep, tmarg + hstep, lmarg + 3 * vstep, tmarg + 2 * hstep),
        'B' to Rect(lmarg + 3 * vstep, tmarg + hstep, lmarg + 4 * vstep, tmarg + 2 * hstep),
        'N' to Rect(lmarg + 4 * vstep, tmarg + hstep, lmarg + 5 * vstep, tmarg + 2 * hstep),
        'P' to Rect(lmarg + 5 * vstep, tmarg + hstep, lmarg + 6 * vstep, tmarg + 2 * hstep)
    )
    var gameState = mutableListOf(
        Pair('R', MuPair(0, 0)),
        Pair('N', MuPair(0, 1)),
        Pair('B', MuPair(0, 2)),
        Pair('Q', MuPair(0, 3)),
        Pair('K', MuPair(0, 4)),
        Pair('B', MuPair(0, 5)),
        Pair('N', MuPair(0, 6)),
        Pair('R', MuPair(0, 7)),
        Pair('P', MuPair(1, 0)),
        Pair('P', MuPair(1, 1)),
        Pair('P', MuPair(1, 2)),
        Pair('P', MuPair(1, 3)),
        Pair('P', MuPair(1, 4)),
        Pair('P', MuPair(1, 5)),
        Pair('P', MuPair(1, 6)),
        Pair('P', MuPair(1, 7)),

        Pair('r', MuPair(7, 0)),
        Pair('n', MuPair(7, 1)),
        Pair('b', MuPair(7, 2)),
        Pair('q', MuPair(7, 3)),
        Pair('k', MuPair(7, 4)),
        Pair('b', MuPair(7, 5)),
        Pair('n', MuPair(7, 6)),
        Pair('r', MuPair(7, 7)),
        Pair('p', MuPair(6, 0)),
        Pair('p', MuPair(6, 1)),
        Pair('p', MuPair(6, 2)),
        Pair('p', MuPair(6, 3)),
        Pair('p', MuPair(6, 4)),
        Pair('p', MuPair(6, 5)),
        Pair('p', MuPair(6, 6)),
        Pair('p', MuPair(6, 7))
    )
    var pieces: Bitmap
    init {
        pieces = BitmapFactory.decodeResource(getResources(), R.drawable.pieces);

    }

    fun setFen(fen: String) {
        val pos = fen.split(' ')[0]
        gameState.clear()
        var row = 7
        var col = 0
        for (char in pos) {
            if (char == '/') {
                row--
                col = 0
            }
            else if (char.isDigit()) {
                col += char.digitToInt()
            }
            else {
                gameState.add(Pair(char, MuPair(row, col++)))
            }
        }
        invalidate()
    }

    fun updSide(side: Int) {
        if (side == 0 || side == 1) {
            this.side = side
        }
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var size = min(
            MeasureSpec.getSize(heightMeasureSpec),
            MeasureSpec.getSize(widthMeasureSpec)
        )
        squareWidth = (measuredWidth / squares).toFloat()
        squareHeight = (measuredHeight / squares).toFloat()

        setMeasuredDimension(size, size)
//        setMeasuredDimension(measuredHeight, measuredWidth)
    }

    override fun onDraw(canvas: Canvas?) {
        val paint = Paint()
        paint.color = colorBlack
        canvas?.drawRect(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat(), paint)
        paint.color = colorWhite
        for (row in 0 .. squares) {
            for (col in 0 .. squares) {
                if ((col + row) % 2 == 0) {
                    canvas?.drawRect(
                        (col * squareWidth).toFloat(),
                        (row * squareHeight).toFloat(),
                        ((col + 1) * squareWidth).toFloat(),
                        ((row + 1) * squareHeight).toFloat(),
                        paint
                    )
                }
            }
        }

        if (selection.first >= 0 && selection.second >= 0) {
            paint.color = colorSelection
            canvas?.drawRect(
                (selection.first * squareWidth).toFloat(),
                (selection.second * squareHeight).toFloat(),
                ((selection.first + 1) * squareWidth).toFloat(),
                ((selection.second + 1) * squareHeight).toFloat(),
                paint
            )
        }

        paint.color = colorCandidate
        for (c in candidates) {
            canvas?.drawCircle(
                ((c.first + 0.5) * squareWidth).toFloat(),
                ((c.second + 0.5) * squareHeight).toFloat(),
                (min(squareWidth, squareHeight) / 4).toFloat(),
                paint
            )
        }

        for (piece in gameState) {
            drawPiece(piece.first, piece.second.first, piece.second.second, canvas, paint)
        }

    }

    private fun drawPiece(piece: Char, row: Int, col: Int, canvas: Canvas?, paint: Paint) {
        val factor = 5
        val dstRect = Rect(
            (col * squareWidth + squareWidth / factor).toInt(),
            (row * squareHeight).toInt(),
            ((col + 1) * squareWidth - squareWidth / factor).toInt(),
            ((row + 1) * squareHeight).toInt()
        )
        canvas?.drawBitmap(pieces, pieceRects[piece], dstRect, paint)
    }

    fun setSelection(x: Float, y: Float): String {
        var pos = Pair(-1, -1)
        if (x > 0 && y > 0) {
            pos = Pair((x / squareWidth).toInt(), (y / squareHeight).toInt())
        }
        selection = Pair(
            pos.first,
            pos.second
        )
        invalidate()

        return (pos.first + 97).toChar() + (pos.second + 1).toString()
    }

    fun movePiece(move: String): Boolean {
        if (move.length != 4) {
            return false
        }
        val moveStart = Pair(move[1].digitToInt() - 1, move[0].toInt() - 97)
        val moveEnd = Pair(move[3].digitToInt() - 1, move[2].toInt() - 97)
        var result = false
        var pieceRm: Pair<Char, MuPair<Int, Int>>? = null

        for (piece in gameState) {
            if (piece.second.first == moveEnd.first && piece.second.second == moveEnd.second) {
                pieceRm = piece
                break
            }
        }
        for (piece in gameState) {
            if (piece.second.first == moveStart.first && piece.second.second == moveStart.second) {
                piece.second.first = moveEnd.first
                piece.second.second = moveEnd.second
                result = true
                if (pieceRm != null) {
                    gameState.remove(pieceRm)
                }
                break
            }
        }
        invalidate()

        // Check for castling
        if (move == "e8g8") movePiece("h8f8")
        if (move == "e8c8") movePiece("a8d8")
        if (move == "e1g1") movePiece("h1f1")
        if (move == "e1c1") movePiece("a1d1")

        return result
    }

    fun setCandidates(newCandidates: ArrayList<String>) {
        candidates.clear()
        for (c in newCandidates) {
            val row = c[1].digitToInt() - 1
            val col = c[0].toInt() - 97
            candidates.add(Pair(col, row))
        }
        invalidate()
    }
}