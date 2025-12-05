# Inspector Panel Dragging Constraints

## ADDED Requirements

### Requirement: Info panel dragging SHALL be constrained within window bounds

When the user drags the info panel using the drag handle, the panel MUST remain fully visible within the screen bounds. The dragging operation SHALL NOT allow any part of the panel to move outside the visible window area.

#### Scenario: User drags info panel toward screen edge

**Given** the inspector overlay is active and a component is selected
**And** the info panel is visible at the center of the screen
**When** the user drags the panel using the drag handle toward the left edge of the screen
**Then** the panel moves with the drag gesture
**And** the panel stops moving when its left edge reaches the screen's left edge
**And** the panel cannot be dragged further left beyond the screen boundary

#### Scenario: User attempts to drag panel completely off screen

**Given** the info panel is visible and positioned near the right edge of the screen
**When** the user continues dragging the panel to the right
**Then** the panel's right edge stops at the screen's right boundary
**And** the entire panel remains fully visible on screen
**And** the panel cannot be moved off screen in any direction

#### Scenario: User drags panel in multiple directions

**Given** the info panel is visible at any position on screen
**When** the user drags the panel upward, downward, left, and right
**Then** the panel follows the drag gesture in all directions
**And** the panel is constrained at all four screen edges (top, bottom, left, right)
**And** the panel always remains fully visible and accessible

#### Scenario: Content inside the scrollable area does not interfere with window dragging

**Given** the info panel is visible with a scrollable properties list
**When** the user touches the drag handle area to drag the panel
**Then** only the entire panel window moves with the drag gesture
**And** the content inside the ScrollView does not scroll when dragging the handle
**And** the user can still scroll the content by touching inside the scrollable area

#### Scenario: Panel position resets when overlay is reopened

**Given** the user has dragged the info panel to a corner of the screen
**When** the user closes the inspector overlay
**And** reopens it and selects a component
**Then** the info panel appears at the default center position
**And** the panel's translation values are reset to zero
**And** the previous drag position is not retained
