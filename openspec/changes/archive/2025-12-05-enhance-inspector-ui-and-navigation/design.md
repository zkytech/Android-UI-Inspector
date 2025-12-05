# Design: Inspector UI and Navigation

## Architecture

### Global Boundary Visualization
- **Data Collection:** `InspectorService` will perform a full tree traversal of `rootInActiveWindow` to collect `Rect` bounds of all visible nodes. This collection happens when the inspector overlay is shown or refreshed.
- **Rendering:** `InspectorOverlayView` will be updated to accept a list of `allBounds` in addition to the `highlightRect`.
- **Drawing:** `onDraw` will iterate `allBounds` to draw a light stroke (e.g., `#44AAAAAA`) before drawing the primary `highlightRect` (which remains bold and distinct).

### Child Node Navigation
- **UI Component:** `layout_node_info.xml` will receive a new section, "Children", containing a toggleable `LinearLayout` (or `TableLayout`).
- **Interaction:** `OverlayManager.updateNodeInfo` will iterate the children of the passed `AccessibilityNodeInfo`.
- **Callback:** A new callback `onChildClicked(index: Int)` will be exposed by `OverlayManager`.
- **Logic:** `InspectorService` will implement this callback. Since it holds `currentInspectedNode`, it can safely call `getChild(index)` to retrieve and select the target child node.

### Initial State & Visibility
- **State:** `OverlayManager` will initialize the `layout_node_info` container as `View.GONE`.
- **Transition:** 
    - `showInspectorOverlay()`: Shows the full screen overlay (capturing touches) but keeps the info panel hidden. Triggers "All Bounds" calculation.
    - `updateNodeInfo()`: Makes the info panel `View.VISIBLE`.
    - `hideInspectorOverlay()`: Resets state.

## Implementation Details
- **Safe Node Handling:** We will pass child *indices* back to the service to avoid holding stale `AccessibilityNodeInfo` references in UI listeners. `InspectorService` is responsible for node lifecycle.
- **Performance:** Boundary collection will be efficient, avoiding object allocation where possible (reusing `Rect` if feasible, though `getBoundsInScreen` requires a new one or passed one). Given the limited number of visible nodes in typical apps, a full traversal is acceptable for a "snapshot" inspector.
