# i18n-resource-management Specification

## Purpose
Enable the Android UI Inspector to display all user-facing text in Chinese (Simplified) or English based on the device's language settings through Android's standard resource localization mechanism.

## ADDED Requirements

### Requirement: All user-facing strings SHALL be externalized to string resources
All text visible to users in the application UI (buttons, labels, descriptions, titles, instructions) SHALL be defined in `res/values/strings.xml` and `res/values-zh-rCN/strings.xml` rather than hardcoded in source code or layout files.

#### Scenario: Developer changes device language to Chinese
- **GIVEN** the app is installed and running
- **WHEN** the user changes the device system language to Chinese (Simplified)
- **THEN** all UI text (MainActivity buttons, inspector overlay titles, property labels, action buttons) SHALL display in Chinese
- **AND** no English text remains visible in the UI

#### Scenario: Developer changes device language to English
- **GIVEN** the app is installed and running with Chinese language
- **WHEN** the user changes the device system language to English
- **THEN** all UI text SHALL display in English
- **AND** no Chinese text remains visible in the UI

#### Scenario: App runs on device with unsupported locale
- **GIVEN** the device system language is set to a language other than Chinese or English (e.g., Spanish, French)
- **WHEN** the app launches
- **THEN** all UI text SHALL display in English (default fallback)

### Requirement: Kotlin source files SHALL reference string resources programmatically
All Kotlin source files that display user-facing text SHALL use `context.getString(R.string.*)` (for View-based code) or `stringResource(R.string.*)` (for Compose code) instead of hardcoded string literals.

#### Scenario: MainActivity displays localized button text
- **GIVEN** MainActivity.kt contains buttons for permission requests
- **WHEN** the app renders the UI
- **THEN** button text SHALL be loaded from `R.string.enable_accessibility_service` and `R.string.grant_overlay_permission`
- **AND** no hardcoded button text like "Enable Accessibility Service" appears in the Kotlin code

#### Scenario: OverlayManager displays localized inspector UI
- **GIVEN** OverlayManager.kt creates inspector overlay views with labels
- **WHEN** the inspector overlay is shown
- **THEN** all labels (e.g., "Element Inspector", property labels, button text) SHALL be loaded from string resources
- **AND** no hardcoded label strings appear in OverlayManager.kt

### Requirement: XML layout files SHALL reference string resources declaratively
All XML layout files SHALL use `@string/*` references for `android:text`, `android:contentDescription`, and `android:hint` attributes instead of hardcoded text values.

#### Scenario: layout_node_info.xml uses localized strings
- **GIVEN** layout_node_info.xml defines the inspector panel UI
- **WHEN** the layout is inflated
- **THEN** all TextView text attributes SHALL reference `@string/*` resources (e.g., `@string/inspector_title`, `@string/action_parent`)
- **AND** no hardcoded text like "Element Inspector" or "Parent" appears in the XML

#### Scenario: layout_floating_control.xml uses localized content description
- **GIVEN** layout_floating_control.xml defines the floating inspect button
- **WHEN** the button is created
- **THEN** the contentDescription SHALL reference `@string/action_inspect_description`
- **AND** accessibility services announce the localized description

### Requirement: Chinese string resource file SHALL provide complete translations
The file `res/values-zh-rCN/strings.xml` SHALL contain Chinese (Simplified) translations for every string key defined in `res/values/strings.xml`.

#### Scenario: String keys match between English and Chinese resources
- **GIVEN** `res/values/strings.xml` defines 15 string keys
- **WHEN** a developer inspects `res/values-zh-rCN/strings.xml`
- **THEN** it SHALL contain exactly the same 15 string keys with Chinese translations
- **AND** no untranslated keys are missing

#### Scenario: Technical terms use standard Android Chinese conventions
- **GIVEN** UI contains common Android terms like "Accessibility", "Permission", "Inspector"
- **WHEN** Chinese translations are provided
- **THEN** they SHALL use standard terminology (e.g., "辅助功能" for Accessibility, "权限" for Permission)
- **AND** translations are consistent throughout the app

### Requirement: String naming SHALL follow Android conventions
All string resource keys SHALL follow Android naming patterns: `action_*` for action buttons, `label_*` for static labels, `description_*` for content descriptions, `title_*` for titles, and `message_*` for instructional text.

#### Scenario: String keys are semantically organized
- **GIVEN** the app defines string resources for various UI elements
- **WHEN** a developer reviews `res/values/strings.xml`
- **THEN** action buttons use keys like `action_inspect`, `action_close`, `action_parent`
- **AND** titles use keys like `title_inspector`, `title_permissions_required`
- **AND** descriptions use keys like `description_accessibility_service`, `description_inspect_button`
- **AND** messages use keys like `message_permission_instruction`

## Related Capabilities
- None (standalone capability)
