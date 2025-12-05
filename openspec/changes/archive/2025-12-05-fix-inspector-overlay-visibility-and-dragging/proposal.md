# Fix Inspector Overlay Visibility and Dragging

## Overview
Fix two UX issues in the inspector overlay: (1) the info panel should remain hidden until a user selects a UI component, and (2) prevent dragging the info panel contents outside the window bounds when using the drag handle.

## Motivation
Currently, when the inspector overlay is activated, the info panel appears immediately even when no component is selected, creating a confusing initial state. Additionally, users can drag the info panel content outside the visible window area, making it inaccessible or partially hidden. These issues negatively impact the user experience and make the inspector less intuitive to use.

## Requirements
- The info panel MUST remain hidden when the inspector overlay is first shown
- The info panel MUST only become visible after the user taps on a UI component to inspect
- Dragging the info panel via the drag handle MUST be constrained to keep the panel within the visible window bounds
- The info panel MUST remain fully visible and accessible after being dragged

## Scope
**In-scope:**
- Modify `OverlayManager.showInspectorOverlay()` to keep info panel hidden on initial display
- Modify `OverlayManager.updateNodeInfo()` to show the info panel when a node is selected
- Add boundary constraints to the drag handle touch listener to prevent content from being dragged outside the parent view bounds
- Ensure proper reset of panel position when overlay is hidden and re-shown

**Out-of-scope:**
- Changing the overall layout or styling of the info panel
- Adding animation effects beyond what's already implemented
- Modifying the floating control button behavior
- Adding additional gestures or controls

## Affected Components
- `app/src/main/java/com/zkytech/uiinspector/OverlayManager.kt` - Core overlay management logic
- `app/src/main/res/layout/layout_inspector_overlay.xml` - Overlay layout structure
- `app/src/main/res/layout/layout_node_info.xml` - Info panel layout

## Related Changes
None - this is a self-contained bug fix.

## Capabilities Delivered
- [`inspector-overlay-visibility`](./specs/inspector-overlay-visibility/spec.md) - Info panel visibility management based on selection state
- [`inspector-panel-dragging-constraints`](./specs/inspector-panel-dragging-constraints/spec.md) - Constrained dragging behavior for info panel
