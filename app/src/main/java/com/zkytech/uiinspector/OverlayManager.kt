package com.zkytech.uiinspector

import android.content.Context
import android.graphics.PixelFormat
import android.graphics.Rect
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class OverlayManager(private val context: Context) {

    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val mainHandler = Handler(Looper.getMainLooper())
    private var floatingControlView: View? = null
    private var inspectorOverlayView: View? = null
    private var inspectorView: InspectorOverlayView? = null
    private var copyFeedbackView: TextView? = null

    var onInspectClicked: (() -> Unit)? = null
    var onCloseInspectorClicked: (() -> Unit)? = null
    var onParentClicked: (() -> Unit)? = null
    var onOverlayTouch: ((Int, Int) -> Unit)? = null

    fun showFloatingControl() {
        if (floatingControlView != null) return

        floatingControlView = LayoutInflater.from(context).inflate(R.layout.layout_floating_control, null)
        
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 0
            y = 200
        }

        floatingControlView?.findViewById<Button>(R.id.btn_inspect)?.setOnClickListener {
            onInspectClicked?.invoke()
        }

        windowManager.addView(floatingControlView, params)
    }

    fun showInspectorOverlay() {
        if (inspectorOverlayView != null) return

        inspectorOverlayView = LayoutInflater.from(context).inflate(R.layout.layout_inspector_overlay, null)
        inspectorView = inspectorOverlayView?.findViewById(R.id.inspector_view)
        copyFeedbackView = inspectorOverlayView?.findViewById(R.id.tv_copy_feedback)
        
        // Find the included layout view (it's the LinearLayout inside the include)
        // Since <include> merges or replaces depending on tag, but here layout_node_info is root LinearLayout.
        // In layout_inspector_overlay.xml, <include> is used. If <include> has ID, it might be the view itself if it's not merge.
        // Let's assume the LinearLayout inside layout_node_info is what we want to drag.
        // Actually, in layout_inspector_overlay.xml, the include doesn't have an ID that we referenced in code before?
        // Wait, previous code didn't reference the container.
        // Let's find the LinearLayout by traversing or ID if possible.
        // In layout_node_info.xml, the root is LinearLayout.
        // In layout_inspector_overlay.xml:
        // <include layout="@layout/layout_node_info" ... />
        // We can find the children of the FrameLayout.
        
        // Better way: Let's assume we can find the views by ID since they are inflated into inspectorOverlayView.
        val propertiesContainer = inspectorOverlayView?.findViewById<View>(R.id.tl_node_properties)
        // The parent of the properties container (ScrollView) is inside the LinearLayout from layout_node_info.
        // We want to drag the whole info panel, which is the root of layout_node_info.
        // In layout_inspector_overlay, it is included.
        // Let's find the ScrollView's parent, which is the LinearLayout.
        val infoContainer = propertiesContainer?.parent?.parent as? View

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        )

        inspectorOverlayView?.findViewById<Button>(R.id.btn_close_inspector)?.setOnClickListener {
            onCloseInspectorClicked?.invoke()
        }

        inspectorOverlayView?.findViewById<Button>(R.id.btn_parent)?.setOnClickListener {
            onParentClicked?.invoke()
        }

        val dragHandle = inspectorOverlayView?.findViewById<View>(R.id.iv_drag_handle)
        
        // Drag logic for infoContainer via dragHandle
        dragHandle?.setOnTouchListener(object : View.OnTouchListener {
            var lastX = 0f
            var lastY = 0f
            var dX = 0f
            var dY = 0f

            override fun onTouch(view: View, event: MotionEvent): Boolean {
                val targetView = infoContainer ?: return false
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        lastX = event.rawX
                        lastY = event.rawY
                        dX = targetView.translationX
                        dY = targetView.translationY
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val deltaX = event.rawX - lastX
                        val deltaY = event.rawY - lastY
                        targetView.translationX = dX + deltaX
                        targetView.translationY = dY + deltaY
                    }
                }
                return true // Consume touch so it doesn't pass through
            }
        })

        // Capture touches on the overlay to find nodes
        inspectorOverlayView?.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                // Only trigger inspect if we didn't touch the info window (handled above by consumption)
                onOverlayTouch?.invoke(event.rawX.toInt(), event.rawY.toInt())
            }
            true
        }

        windowManager.addView(inspectorOverlayView, params)
        floatingControlView?.visibility = View.GONE
    }

    fun hideInspectorOverlay() {
        if (inspectorOverlayView != null) {
            copyFeedbackView?.animate()?.cancel()
            windowManager.removeView(inspectorOverlayView)
            inspectorOverlayView = null
            inspectorView = null
            copyFeedbackView = null
        }
        floatingControlView?.visibility = View.VISIBLE
    }

    fun updateNodeInfo(node: AccessibilityNodeInfo, bounds: Rect) {
        inspectorView?.updateHighlight(bounds)
        
        val tableLayout = inspectorOverlayView?.findViewById<android.widget.TableLayout>(R.id.tl_node_properties)
        tableLayout?.removeAllViews()

        val properties = mapOf(
            "Package" to (node.packageName?.toString() ?: "N/A"),
            "Class" to (node.className?.toString() ?: "N/A"),
            "Resource ID" to (node.viewIdResourceName ?: "N/A"),
            "Text" to (node.text?.toString() ?: "N/A"),
            "Content Desc" to (node.contentDescription?.toString() ?: "N/A"),
            "Bounds" to bounds.toShortString(),
            "Clickable" to node.isClickable.toString(),
            "Focusable" to node.isFocusable.toString(),
            "Enabled" to node.isEnabled.toString(),
            "Scrollable" to node.isScrollable.toString(),
            "Checked" to node.isChecked.toString(),
            "Editable" to node.isEditable.toString(),
            "Visible to User" to node.isVisibleToUser.toString()
        )

        for ((key, value) in properties) {
            val tableRow = android.widget.TableRow(context)
            
            val keyView = TextView(context).apply {
                text = key
                setTextColor(android.graphics.Color.LTGRAY)
                textSize = 14f
                setPadding(0, 4, 16, 4)
                setTypeface(null, android.graphics.Typeface.BOLD)
            }

            val valueView = TextView(context).apply {
                text = value
                setTextColor(android.graphics.Color.WHITE)
                textSize = 14f
                setPadding(0, 4, 0, 4)
                setTextIsSelectable(false)
            }

            val longClickListener = View.OnLongClickListener {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                val clip = android.content.ClipData.newPlainText(key, value)
                clipboard.setPrimaryClip(clip)
                showCopyFeedback("$key copied to clipboard")
                true
            }

            valueView.setOnLongClickListener(longClickListener)
            tableRow.setOnLongClickListener(longClickListener) // Also allow clicking the row

            tableRow.addView(keyView)
            tableRow.addView(valueView)
            tableLayout?.addView(tableRow)
        }
    }

    fun removeAll() {
        if (floatingControlView != null) {
            windowManager.removeView(floatingControlView)
            floatingControlView = null
        }
        hideInspectorOverlay()
    }

    private fun showCopyFeedback(message: String) {
        mainHandler.post {
            val toastHost = copyFeedbackView
            if (toastHost == null) {
                Toast.makeText(context.applicationContext, message, Toast.LENGTH_SHORT).show()
                return@post
            }

            toastHost.animate().cancel()
            toastHost.text = message
            toastHost.alpha = 1f
            toastHost.visibility = View.VISIBLE

            toastHost.animate()
                .alpha(0f)
                .setStartDelay(600L)
                .setDuration(250L)
                .withEndAction {
                    toastHost.visibility = View.GONE
                    toastHost.alpha = 1f
                }
                .start()
        }
    }
}
