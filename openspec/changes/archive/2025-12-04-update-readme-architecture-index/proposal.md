# Change: Add architecture and code index to README

## Why
The README lacks an architecture overview and code index, making it hard for contributors to understand how the accessibility service, overlays, and supporting views work together.

## What Changes
- Document the runtime architecture (Activity entrypoint, AccessibilityService, overlay manager, rendering helpers)
- Add a concise code index that points to the primary classes and layouts
- Note permissions and callback flows relevant to inspecting nodes

## Impact
- Affected specs: `docs`
- Affected code: `README.md`
