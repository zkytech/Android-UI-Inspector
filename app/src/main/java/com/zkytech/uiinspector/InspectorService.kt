package com.zkytech.uiinspector

import android.accessibilityservice.AccessibilityService
import android.graphics.Rect
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class InspectorService : AccessibilityService() {

    private lateinit var overlayManager: OverlayManager
    private var currentInspectedNode: AccessibilityNodeInfo? = null

    override fun onServiceConnected() {
        super.onServiceConnected()
        overlayManager = OverlayManager(this)
        
        overlayManager.onInspectClicked = {
            overlayManager.showInspectorOverlay()
            refreshAllBounds()
        }

        overlayManager.onCloseInspectorClicked = {
            overlayManager.hideInspectorOverlay()
            currentInspectedNode = null
        }

        overlayManager.onParentClicked = {
            currentInspectedNode?.parent?.let { parent ->
                updateInspectedNode(parent)
            }
        }
        
        overlayManager.onChildClicked = { index ->
            currentInspectedNode?.getChild(index)?.let { child ->
                updateInspectedNode(child)
            }
        }

        overlayManager.onOverlayTouch = { x, y ->
            findNodeAtPoint(x, y)
        }

        overlayManager.showFloatingControl()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // We can listen to events if needed, but for now we rely on active inspection
    }

    override fun onInterrupt() {
        overlayManager.removeAll()
    }

    override fun onDestroy() {
        super.onDestroy()
        overlayManager.removeAll()
    }

    private fun refreshAllBounds() {
        val root = rootInActiveWindow ?: return
        val allBounds = mutableListOf<Rect>()
        collectBoundsRecursive(root, allBounds)
        overlayManager.updateAllBounds(allBounds)
        root.recycle()
    }

    private fun collectBoundsRecursive(node: AccessibilityNodeInfo, list: MutableList<Rect>) {
        val bounds = Rect()
        node.getBoundsInScreen(bounds)
        if (!bounds.isEmpty) {
            list.add(bounds)
        }

        for (i in 0 until node.childCount) {
            val child = node.getChild(i)
            if (child != null) {
                collectBoundsRecursive(child, list)
                child.recycle()
            }
        }
    }

    private fun findNodeAtPoint(x: Int, y: Int) {
        val rootNode = rootInActiveWindow ?: return
        val foundNode = findNodeRecursive(rootNode, x, y)
        
        if (foundNode != null) {
            updateInspectedNode(foundNode)
        }
    }

    private fun updateInspectedNode(node: AccessibilityNodeInfo) {
        currentInspectedNode = node
        val bounds = Rect()
        node.getBoundsInScreen(bounds)
        overlayManager.updateNodeInfo(node, bounds)
    }

    private fun findNodeRecursive(node: AccessibilityNodeInfo, x: Int, y: Int): AccessibilityNodeInfo? {
        val bounds = Rect()
        node.getBoundsInScreen(bounds)

        if (!bounds.contains(x, y)) {
            return null
        }

        // Check children (last child on top usually, so iterate reverse if we want topmost, 
        // but standard traversal is usually fine. Let's try to find the smallest leaf that contains the point)
        var bestMatch: AccessibilityNodeInfo = node

        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            val result = findNodeRecursive(child, x, y)
            if (result != null) {
                // If a child contains the point, it's a more specific match than the parent
                bestMatch = result
                // We don't break here because we want to check all children to find the "topmost" one visually?
                // Actually, in view hierarchy, later children draw on top.
                // So we should probably continue and take the last one that matches.
            }
        }
        
        return bestMatch
    }
}
