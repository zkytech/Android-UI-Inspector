# Proposal: Enhance Inspector UI and Navigation

## Summary
This proposal introduces significant enhancements to the UI Inspector's visualization and navigation capabilities. It adds a feature to visualize the boundaries of all visible elements on the screen simultaneously, provides a collapsible list of child nodes for the currently selected element to facilitate tree traversal, and refines the initial state of the inspector overlay to hide details until an element is selected.

## Motivation
Currently, the inspector only highlights the single selected element. Users often need to see the structure of the surrounding UI to understand layout relationships (padding, margins, nesting). Navigating down the tree (to children) is currently not possible via the UI, only navigating up (to parent). Additionally, the inspector overlay covers the screen immediately, which can be obtrusive before a selection is made.

## Proposed Changes
1.  **Global Boundary Visualization:** Draw light, subtle borders around all visible nodes in the active window when the inspector is active.
2.  **Child Node Navigation:** Display a list of child nodes in the details panel. This list will be collapsible (default folded). Clicking a child will select it.
3.  **Initial State Refinement:** The details panel (info window) will remain hidden when the inspector overlay is first opened, appearing only after a node is selected via touch.
