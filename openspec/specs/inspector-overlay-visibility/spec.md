# inspector-overlay-visibility Specification

## Purpose
TBD - created by archiving change fix-inspector-overlay-visibility-and-dragging. Update Purpose after archive.
## Requirements
### Requirement: Info panel SHALL remain hidden until a component is selected

The info panel (node properties, children list, and control buttons) MUST NOT be visible when the inspector overlay is first activated. It SHALL only become visible after the user selects a UI component by tapping on the screen.

#### Scenario: User activates inspector overlay without selecting a component

**Given** the accessibility service is running and the floating control button is visible
**When** the user taps the floating control button to activate the inspector overlay
**Then** the inspector overlay background and highlight view appear
**And** the info panel is not visible
**And** the user can tap anywhere on the screen to select a component

#### Scenario: User selects a component to inspect

**Given** the inspector overlay is active and no component is currently selected
**When** the user taps on a UI component on the screen
**Then** the info panel becomes visible at the center of the screen
**And** the panel displays the properties of the selected component
**And** the highlight rectangle shows the bounds of the selected component

#### Scenario: User switches between different components

**Given** the inspector overlay is active and a component is already selected
**When** the user taps on a different UI component
**Then** the info panel remains visible
**And** the panel updates to display the properties of the newly selected component
**And** the highlight rectangle updates to show the new component's bounds

#### Scenario: User closes and reopens the inspector overlay

**Given** the inspector overlay is active with a component selected and the info panel visible
**When** the user closes the inspector overlay
**And** then reopens it by tapping the floating control button again
**Then** the info panel is hidden again
**And** the info panel position is reset to center
**And** the user must select a component to make the panel visible

