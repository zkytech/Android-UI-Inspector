# Project Context

## Purpose
- Provide an on-device UI inspector for Android apps, similar to browser "Inspect Element", that overlays a floating control to inspect any visible view and surface its properties.

## Tech Stack
- Android app written in Kotlin.
- Accessibility Service plus `WindowManager` overlays for floating controls and highlights.
- XML layouts for floating control, inspector overlay, and node info panel under `app/src/main/res/layout/`.

## Project Conventions

### Code Style
- Kotlin/Android defaults; format with Android Studio/ktlint-style settings.
- Keep overlays and services clearly named (`*Service`, `*Manager`, `*OverlayView`) and prefer descriptive resource IDs.

### Architecture Patterns
- Single-activity entrypoint: `MainActivity` only launches accessibility and overlay permission screens, then defers runtime logic.
- Accessibility-driven core: `InspectorService` owns lifecycle, creates `OverlayManager`, and performs node hit-testing from overlay touch events rather than `onAccessibilityEvent`.
- Overlay orchestration: `OverlayManager` builds the floating control (Inspect button) and inspector overlay (highlight view + info table), wires callbacks for Inspect/Parent/Close/Touch, renders node properties, and handles long-press copy-to-clipboard feedback.
- Rendering helpers: `InspectorOverlayView` draws the highlight rectangle; `MaxWidthLinearLayout` caps info panel width for readability.
- Lifecycle: service shows floating control when connected and tears down overlays on `onInterrupt`/`onDestroy`.

### Testing Strategy
- Manual device/emulator verification: build and run, grant overlay + accessibility permissions, confirm floating control appears, inspect another app, verify highlight updates and details (class, resource ID, text/content description, bounds), and confirm long-press copy feedback.

### Git Workflow
- Not documented; default to feature branches with PR review. Update when team workflow is defined.

## Domain Context
- Use case: developers/testers need quick element introspection of other apps without modifying them.
- Data surfaced per node: class name, resource ID, text/content description, screen bounds; inspector overlay shows highlight + info table with copy support.
- Controls: Inspect toggles inspection overlay; Parent moves selection up the node tree; Close hides overlays; touch events select the deepest node containing the touch bounds.

## Important Constraints
- Requires runtime permissions: SYSTEM_ALERT_WINDOW (display over other apps) and Accessibility Service enablement (Android 9.0+ recommended).
- Overlays must respect platform overlay type limitations; service avoids using `onAccessibilityEvent` for runtime work (inspection is on-demand).
- Tear down overlays on service interruption/destruction to avoid window leaks.

## External Dependencies
- No third-party services; relies on Android framework components (AccessibilityService, WindowManager, clipboard).
