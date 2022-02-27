package com.example.chess

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.min

class GameView : View {
    constructor(context: Context): super(context)
    constructor(context: Context, attrs: AttributeSet): super(context, attrs)

    var selection = Pair(-1, -1)
    val candidates = ArrayList<Pair<Int, Int>>()
    private val colorWhite = Color.WHITE
    private val colorBlack = Color.BLACK
    private val colorSelection = Color.BLUE
    private val colorCandidate = Color.GREEN
    val squares = 8
    var squareWidth: Float = 0f
    var squareHeight: Float = 0f

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

    }

    fun setSelection(x: Float, y: Float) {
        selection = Pair(
            (x / squareWidth).toInt(),
            (y / squareHeight).toInt()
        )
        invalidate()
    }
}