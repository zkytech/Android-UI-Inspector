package com.zkytech.uiinspector

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View

class InspectorOverlayView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val highlightPaint = Paint().apply {
        color = Color.parseColor("#409EFF")
        style = Paint.Style.STROKE
        strokeWidth = 5f
    }

    private val allBoundsPaint = Paint().apply {
        color = Color.parseColor("#44AAAAAA")
        style = Paint.Style.STROKE
        strokeWidth = 2f
    }

    private var highlightRect: Rect? = null
    private var allBounds: List<Rect> = emptyList()

    fun updateHighlight(rect: Rect?) {
        highlightRect = rect
        invalidate()
    }

    fun updateAllBounds(bounds: List<Rect>) {
        allBounds = bounds
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        // Draw all boundaries first (background layer)
        for (rect in allBounds) {
            canvas.drawRect(rect, allBoundsPaint)
        }

        // Draw the selected highlight on top
        highlightRect?.let {
            canvas.drawRect(it, highlightPaint)
        }
    }
}