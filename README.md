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
- **InspectorService**: An `AccessibilityService` that retrieves `AccessibilityNodeInfo` from the current window.
- **OverlayManager**: Manages the floating views using `WindowManager`.
- **InspectorOverlayView**: Draws the highlight rectangle.

## Usage
1. Launch the app and grant permissions.
2. A "Inspect" button will appear on the screen.
3. Navigate to the app you want to inspect.
4. Click "Inspect".
5. Tap on any element to see its details.
