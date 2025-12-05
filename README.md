# Android UI Inspector

A tool to inspect UI elements of other Android applications, similar to "Inspect Element" in web browsers.

## Features
- Floating "Inspect" button.
- Tap to select any element on the screen.
- View element details:
    - Class Name
    - Resource ID
    - Text / Content Description
    - Screen Bounds
- Visual highlighting of selected elements.
- Internationalization support for English and Chinese (Simplified).

## Prerequisites
- Android Device (Android 9.0+ recommended)
- Permissions:
    - **Display over other apps** (SYSTEM_ALERT_WINDOW)
    - **Accessibility Service**

## Setup
1. Clone the repository.
2. Open in Android Studio.
3. Build and Run on your device.
4. Follow the on-screen instructions to grant permissions.

## Architecture
- **Entry point (MainActivity)**: Launches system screens for accessibility and overlay permissions via the two buttons, then defers all runtime logic to the service.
- **Core service (InspectorService)**: An `AccessibilityService` that instantiates `OverlayManager`, wires callbacks, and searches the active window for nodes. It reacts to overlay events:
  - *Inspect* → shows the inspector overlay.
  - *Parent* → jumps to the selected node's parent and refreshes details.
  - *Close* → hides overlays and clears the current selection.
  - *Touch* → receives raw x/y from the overlay, walks the node tree to find the deepest node containing the touch bounds, and updates the highlight.
- **Overlay orchestration (OverlayManager)**: Builds two overlays with `WindowManager`:
  - Floating control (`layout_floating_control.xml`) with the "Inspect" entrypoint.
  - Inspector overlay (`layout_inspector_overlay.xml`) composed of the highlight view plus the info panel (`layout_node_info.xml`). It renders node properties into a `TableLayout`, supports long-press copy to clipboard with feedback, and coordinates visibility between overlays.
- **Rendering helpers**:
  - `InspectorOverlayView` draws the red stroke around the selected node.
  - `MaxWidthLinearLayout` caps the info panel width (~300dp) so the floating window stays legible.
- **Lifecycle**: The service shows the floating control when it connects, tears down overlays on `onInterrupt`/`onDestroy`, and leaves `onAccessibilityEvent` unused (inspection happens on demand via touch callbacks).

## Code Index
- `app/src/main/AndroidManifest.xml`: Declares `MainActivity`, `InspectorService`, and the overlay/accessibility permissions.
- `app/build/tmp/kotlin-classes/release/com/zkytech/uiinspector/MainActivity.class` (from `MainActivity.kt`): Opens accessibility and overlay permission settings.
- `app/build/tmp/kotlin-classes/release/com/zkytech/uiinspector/InspectorService.class` (from `InspectorService.kt`): Accessibility service that owns the inspection lifecycle and node hit-testing.
- `app/build/tmp/kotlin-classes/release/com/zkytech/uiinspector/OverlayManager.class` (from `OverlayManager.kt`): Creates overlays, binds button/touch listeners, renders node properties, and handles copy-to-clipboard feedback.
- `app/build/tmp/kotlin-classes/release/com/zkytech/uiinspector/InspectorOverlayView.class` (from `InspectorOverlayView.kt`): Custom view that draws the highlight rectangle.
- `app/build/tmp/kotlin-classes/release/com/zkytech/uiinspector/MaxWidthLinearLayout.class` (from `MaxWidthLinearLayout.kt`): Restricts the floating panel width.
- Layouts: `app/src/main/res/layout/layout_floating_control.xml`, `app/src/main/res/layout/layout_inspector_overlay.xml`, `app/src/main/res/layout/layout_node_info.xml` define the floating button, overlay container, and properties panel.
- Config: `app/src/main/res/xml/accessibility_service_config.xml` holds the service metadata for accessibility registration.

## Usage
1. Launch the app and grant permissions.
2. A "Inspect" button will appear on the screen.
3. Navigate to the app you want to inspect.
4. Click "Inspect".
5. Tap on any element to see its details.

## Localization

The app supports internationalization with English and Chinese (Simplified) languages. The language switches automatically based on your device's system language setting.

### Supported Languages
- **English (en)** - Default
- **Chinese Simplified (zh-CN)** - 简体中文

### Adding New Languages

To add support for additional languages:

1. Create a new values directory for your locale:
   ```
   app/src/main/res/values-{locale}/
   ```
   For example: `values-es` (Spanish), `values-fr` (French), `values-ja` (Japanese)

2. Create a `strings.xml` file in the new directory:
   ```
   app/src/main/res/values-{locale}/strings.xml
   ```

3. Copy all string keys from `app/src/main/res/values/strings.xml`

4. Translate the values to your target language while keeping all keys and format specifiers (like `%s`, `%d`) unchanged

5. Test by changing your device language settings
