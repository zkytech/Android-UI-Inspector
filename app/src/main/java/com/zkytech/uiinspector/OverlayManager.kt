package com.zkytech.uiinspector

import android.content.Context
import android.graphics.PixelFormat
import android.graphics.Rect
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Button
import android.widget.TextView

class OverlayManager(private val context: Context) {

    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private var floatingControlView: View? = null
    private var inspectorOverlayView: View? = null
    private var inspectorView: InspectorOverlayView? = null
    private var nodeInfoTextView: TextView? = null

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
        nodeInfoTextView = inspectorOverlayView?.findViewById(R.id.tv_node_info)
        val infoContainer = nodeInfoTextView?.parent as? View

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

        // Drag logic for infoContainer
        infoContainer?.setOnTouchListener(object : View.OnTouchListener {
            var lastX = 0f
            var lastY = 0f
            var dX = 0f
            var dY = 0f

            override fun onTouch(view: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        lastX = event.rawX
                        lastY = event.rawY
                        dX = view.translationX
                        dY = view.translationY
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val deltaX = event.rawX - lastX
                        val deltaY = event.rawY - lastY
                        view.translationX = dX + deltaX
                        view.translationY = dY + deltaY
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
            windowManager.removeView(inspectorOverlayView)
            inspectorOverlayView = null
            inspectorView = null
            nodeInfoTextView = null
        }
        floatingControlView?.visibility = View.VISIBLE
    }

    fun updateNodeInfo(node: AccessibilityNodeInfo, bounds: Rect) {
        inspectorView?.updateHighlight(bounds)
        
        val sb = StringBuilder()
        sb.append("Class: ${node.className}\n")
        sb.append("ID: ${node.viewIdResourceName ?: "N/A"}\n")
        sb.append("Text: ${node.text ?: "N/A"}\n")
        sb.append("Bounds: $bounds\n")
        sb.append("Clickable: ${node.isClickable}\n")
        sb.append("ContentDesc: ${node.contentDescription ?: "N/A"}")
        
        nodeInfoTextView?.text = sb.toString()
    }

    fun removeAll() {
        if (floatingControlView != null) {
            windowManager.removeView(floatingControlView)
            floatingControlView = null
        }
        hideInspectorOverlay()
    }
}
