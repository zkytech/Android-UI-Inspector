package com.zkytech.uiinspector

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import kotlin.math.min

class MaxWidthLinearLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val maxWidthPx = (300 * context.resources.displayMetrics.density).toInt()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val measuredWidth = MeasureSpec.getSize(widthMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)

        var newWidthMeasureSpec = widthMeasureSpec

        if (widthMode == MeasureSpec.AT_MOST || widthMode == MeasureSpec.EXACTLY) {
            if (measuredWidth > maxWidthPx) {
                newWidthMeasureSpec = MeasureSpec.makeMeasureSpec(maxWidthPx, MeasureSpec.EXACTLY)
            }
        } else {
             // UNSPECIFIED, let's limit it if it grows too big, but usually we want AT_MOST behavior for max width
             // Actually for UNSPECIFIED we can't really enforce unless we measure first.
             // But typically we are in a container that constraints us.
             // Let's just enforce AT_MOST maxWidth
             newWidthMeasureSpec = MeasureSpec.makeMeasureSpec(maxWidthPx, MeasureSpec.AT_MOST)
        }

        super.onMeasure(newWidthMeasureSpec, heightMeasureSpec)
    }
}
