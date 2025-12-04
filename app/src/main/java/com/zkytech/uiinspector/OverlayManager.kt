package com.zkytech.uiinspector

import android.content.Context
import android.graphics.PixelFormat
import android.graphics.Rect
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.widget.TableRow
import android.widget.TableLayout

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

    private var floatingControlParams: WindowManager.LayoutParams? = null

    fun showFloatingControl() {
        if (!Settings.canDrawOverlays(context)) {
            Toast.makeText(context, "Overlay permission not granted", Toast.LENGTH_SHORT).show()
            return
        }

        if (floatingControlView != null) return

        floatingControlView = LayoutInflater.from(context).inflate(R.layout.layout_floating_control, null)
        
        floatingControlParams = WindowManager.LayoutParams(
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

        val btnInspect = floatingControlView?.findViewById<View>(R.id.btn_inspect)
        btnInspect?.setOnTouchListener(object : View.OnTouchListener {
            private var initialX = 0
            private var initialY = 0
            private var initialTouchX = 0f
            private var initialTouchY = 0f
            private val touchSlop = 10

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                val params = floatingControlParams ?: return false
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialX = params.x
                        initialY = params.y
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        val dX = (event.rawX - initialTouchX).toInt()
                        val dY = (event.rawY - initialTouchY).toInt()
                        if (Math.abs(dX) < touchSlop && Math.abs(dY) < touchSlop) {
                            v.performClick()
                        }
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        params.x = initialX + (event.rawX - initialTouchX).toInt()
                        params.y = initialY + (event.rawY - initialTouchY).toInt()
                        windowManager.updateViewLayout(floatingControlView, params)
                        return true
                    }
                }
                return false
            }
        })
        
        btnInspect?.setOnClickListener {
            onInspectClicked?.invoke()
        }

        windowManager.addView(floatingControlView, floatingControlParams)
    }

    fun showInspectorOverlay() {
        if (!Settings.canDrawOverlays(context)) {
            Toast.makeText(context, "Overlay permission not granted", Toast.LENGTH_SHORT).show()
            return
        }

        if (inspectorOverlayView != null) return

        inspectorOverlayView = LayoutInflater.from(context).inflate(R.layout.layout_inspector_overlay, null)
        inspectorView = inspectorOverlayView?.findViewById(R.id.inspector_view)
        copyFeedbackView = inspectorOverlayView?.findViewById(R.id.tv_copy_feedback)
        
        val propertiesContainer = inspectorOverlayView?.findViewById<View>(R.id.tl_node_properties)
        // Parent: ScrollView -> Parent: LinearLayout (inside MaxWidthLinearLayout) -> Parent: MaxWidthLinearLayout (root of layout_node_info)
        // With the new layout_node_info:
        // Root is MaxWidthLinearLayout.
        // It is included in layout_inspector_overlay inside a FrameLayout.
        // So finding the root of the included layout might be tricky if it's not merged.
        // But since we can traverse up from a known child (tl_node_properties), let's do that to find the draggable container.
        // tl_node_properties is inside ScrollView.
        // ScrollView is inside MaxWidthLinearLayout (root of node info).
        // So propertiesContainer.parent is ScrollView, parent.parent is MaxWidthLinearLayout.
        val infoContainer = propertiesContainer?.parent?.parent as? View

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        )

        inspectorOverlayView?.findViewById<View>(R.id.btn_close_inspector)?.setOnClickListener {
            onCloseInspectorClicked?.invoke()
        }

        inspectorOverlayView?.findViewById<View>(R.id.btn_parent)?.setOnClickListener {
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
                return true
            }
        })

        inspectorOverlayView?.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
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
        
        val tableLayout = inspectorOverlayView?.findViewById<TableLayout>(R.id.tl_node_properties)
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
            val tableRow = TableRow(context)
            tableRow.setPadding(0, 8, 0, 8)
            
            val keyView = TextView(context).apply {
                text = key
                setTextColor(context.getColor(R.color.element_text_secondary))
                textSize = 12f
                setPadding(0, 0, 16, 0)
            }

            val valueView = TextView(context).apply {
                text = value
                setTextColor(context.getColor(R.color.element_text_main))
                textSize = 12f
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
            tableRow.setOnLongClickListener(longClickListener)

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