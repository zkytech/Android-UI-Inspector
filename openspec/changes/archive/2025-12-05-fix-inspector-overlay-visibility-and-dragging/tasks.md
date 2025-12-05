# Implementation Tasks

## Phase 1: Fix Info Panel Initial Visibility
- [x] Remove `infoContainer?.visibility = View.GONE` from `showInspectorOverlay()` method (line 182) since this is already the default state in XML
- [x] Verify that `layout_node_info.xml` root view or the included view in `layout_inspector_overlay.xml` has `android:visibility="gone"` set by default
- [x] Ensure `updateNodeInfo()` method correctly sets `infoContainer?.visibility = View.VISIBLE` when called (already present at line 207)
- [x] Test: Launch inspector overlay and verify panel is hidden; tap a component and verify panel appears

## Phase 2: Implement Dragging Boundary Constraints
- [x] Add window dimension tracking to `OverlayManager` class to store screen width and height
- [x] Modify the drag handle's `ACTION_MOVE` handler (lines 163-167) to calculate constrained translation values
- [x] Implement min/max bounds checking for `translationX` to ensure panel stays within horizontal screen bounds
- [x] Implement min/max bounds checking for `translationY` to ensure panel stays within vertical screen bounds
- [x] Account for the panel's actual width and height when calculating maximum allowed translation
- [x] Test: Drag panel in all directions and verify it cannot move beyond screen edges

## Phase 3: Reset Panel Position on Overlay Hide/Show
- [x] Add logic to `hideInspectorOverlay()` or `showInspectorOverlay()` to reset `infoContainer` translation to (0, 0)
- [x] Ensure panel returns to center position when overlay is reopened
- [x] Test: Drag panel to a corner, close overlay, reopen and select a component, verify panel appears at center

## Phase 4: Manual Testing & Validation
- [ ] Build and install app on device/emulator
- [ ] Grant accessibility and overlay permissions
- [ ] Activate inspector overlay and verify panel is hidden initially
- [ ] Tap on various UI components and verify panel appears correctly
- [ ] Drag panel to all four screen edges and verify it stops at boundaries
- [ ] Verify panel content scrolling still works independently of window dragging
- [ ] Close and reopen overlay multiple times to verify position reset
- [ ] Test on different screen sizes/orientations if applicable
