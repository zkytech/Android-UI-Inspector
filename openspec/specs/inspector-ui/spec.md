# inspector-ui Specification

## Purpose
TBD - created by archiving change enhance-inspector-ui-and-navigation. Update Purpose after archive.
## Requirements
### Requirement: Visualize all element boundaries
The inspector SHALL draw light-colored boundaries around all visible UI elements on the screen when the inspection mode is active, in addition to the highlighted selected element.

#### Scenario: User activates inspector
- **GIVEN** the floating control is visible
- **WHEN** the user taps "Inspect"
- **THEN** the screen overlay appears
- **AND** all visible UI elements are outlined with a light border
- **AND** no specific element is selected initially (no bold highlight).

#### Scenario: User selects an element
- **GIVEN** the inspector overlay is active with all boundaries visible
- **WHEN** the user taps a specific UI element
- **THEN** that element's boundary is highlighted with a bold, distinct color (e.g., blue)
- **AND** the light boundaries for other elements remain visible.

### Requirement: Display child nodes
The inspector details panel SHALL list the child nodes of the currently selected element, allowing users to navigate down the view hierarchy.

#### Scenario: Viewing element details
- **GIVEN** an element with children is selected
- **WHEN** the details panel is displayed
- **THEN** a "Children" section is available
- **AND** the list of children is collapsed by default.

#### Scenario: Navigating to a child
- **GIVEN** the "Children" list is expanded
- **WHEN** the user taps on a child item (e.g., "TextView: id/title")
- **THEN** the inspector selects that child node
- **AND** the highlight updates to the child's bounds
- **AND** the details panel updates to show the child's properties.

### Requirement: Hide details on entry
The inspector details panel SHALL be hidden when the inspector is first opened, appearing only after an element is selected.

#### Scenario: Initial activation
- **WHEN** the inspector is activated
- **THEN** the details panel is not visible
- **BUT** the full-screen touch overlay is active (to intercept touches).

