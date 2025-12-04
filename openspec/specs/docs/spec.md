# docs Specification

## Purpose
TBD - created by archiving change update-readme-architecture-index. Update Purpose after archive.
## Requirements
### Requirement: README documents inspection architecture
The README SHALL outline how the main activity, accessibility service, overlay manager, highlight view, and info panel collaborate to inspect UI nodes, including permission flows and user interactions (inspect tap, parent jump, close).

#### Scenario: Contributor reads architecture overview
- **WHEN** a contributor opens `README.md`
- **THEN** they can read a concise architecture section describing MainActivity's permission entrypoint, InspectorService's callbacks, OverlayManager's overlay composition, and InspectorOverlayView's highlighting responsibilities.

### Requirement: README provides code index for inspection UI
The README SHALL include a code index that lists the primary runtime artifacts (Activity, AccessibilityService, overlay manager, overlay/highlight view, draggable panel layout, floating control layout) with file paths and roles.

#### Scenario: Developer locates inspection components
- **WHEN** a developer follows the code index in `README.md`
- **THEN** they can see file paths and brief purposes for the Activity, AccessibilityService, OverlayManager, overlay view/highlight, property panel layout, and floating inspect control.

