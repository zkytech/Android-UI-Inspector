# Tasks: Enhance Inspector UI and Navigation

- [x] Update `InspectorOverlayView.kt` to accept and draw a list of secondary bounds for all visible nodes. <!-- id: 0 -->
- [x] Modify `layout_node_info.xml` to include a container for the child node list and a toggle header. <!-- id: 1 -->
- [x] Update `OverlayManager.kt` to handle the child list UI generation, "Children" toggle logic, and expose `onChildClicked` callback. <!-- id: 2 -->
- [x] Update `OverlayManager.kt` to hide the info panel initially and show it only upon `updateNodeInfo`. <!-- id: 3 -->
- [x] Implement `collectAllBounds` logic in `InspectorService.kt` (or helper) and pass the result to `OverlayManager` when inspection starts. <!-- id: 4 -->
- [x] Connect `onChildClicked` in `InspectorService.kt` to update the inspected node using the child index. <!-- id: 5 -->
- [x] Verify the implementation by inspecting the app itself (or another target) to check boundaries, child navigation, and initial state. <!-- id: 6 -->
