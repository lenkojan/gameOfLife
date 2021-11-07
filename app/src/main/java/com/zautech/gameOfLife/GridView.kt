package com.zautech.gameOfLife

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.ceil

class GridView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var cellSize = 0
    private val gridSize
        get() = grid.size
    private val inactivePaint: Paint = Paint()
    private var m = 0
    private var grid: List<List<Boolean>> = listOf(listOf())
    var onClickListener: GridViewClicked? = null

    fun setGridData(data: List<List<Boolean>>) {
        if (data.size == data.firstOrNull()?.size) {
            grid = data
        }
    }

    init {
        inactivePaint.style = Paint.Style.FILL_AND_STROKE
        inactivePaint.color = context.resources.getColor(R.color.theme_primary_light)
        inactivePaint.strokeWidth = 0f
        calculateSize()
        inactivePaint.isAntiAlias = true
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.also {
            val x = ceil(((it.x - m) / cellSize).toDouble()).toInt() - 1
            val y = ceil(((it.y - m) / cellSize).toDouble()).toInt() - 1
            onClickListener?.onTouched(x, y)
        }
        return super.onTouchEvent(event)
    }

    private fun calculateSize() {
        cellSize = if (width > height) {
            height / gridSize
        } else {
            width / gridSize
        }
        m = if (width > height) {
            height - cellSize * gridSize
        } else {
            width - cellSize * gridSize
        } / 2
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        calculateSize()
        invalidate()
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.BLACK)
        for (i in 0 until gridSize) {
            for (j in 0 until gridSize) {
                if (grid.getOrNull(i)?.getOrNull(j) == true)
                    canvas.drawRect(
                        m + (i * cellSize.toFloat()) + 1,
                        m + (j * cellSize.toFloat()) + 1,
                        m + ((i + 1) * cellSize.toFloat()) - 1,
                        m + ((j + 1) * cellSize.toFloat()) - 1,
                        inactivePaint
                    );
            }
        }
    }

    interface GridViewClicked {
        fun onTouched(x: Int, y: Int)
    }
}