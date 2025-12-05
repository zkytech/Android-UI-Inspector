# Proposal: Add Internationalization Support

## Summary
Add internationalization (i18n) configuration to support both Chinese (zh-CN) and English (en) locales across all user-facing strings in the Android UI Inspector app.

## Motivation
Currently, all user-facing text in the app is hardcoded in English. To make the app accessible to Chinese-speaking users and demonstrate proper Android localization practices, we need to:
1. Extract all hardcoded strings to resource files
2. Provide Chinese translations for all UI elements
3. Enable automatic locale switching based on device settings

## Scope
### In Scope
- Extract hardcoded strings from Kotlin source files (MainActivity.kt, OverlayManager.kt, etc.)
- Extract hardcoded strings from XML layouts (layout_node_info.xml, layout_floating_control.xml, etc.)
- Create `values-zh-rCN/strings.xml` with Chinese translations
- Update `values/strings.xml` with all English strings
- Add locale-aware string formatting utilities if needed for dynamic content

### Out of Scope
- Right-to-left (RTL) language support
- Pluralization rules (not required for current UI)
- Date/time localization (not used in current UI)
- Third-party localization management platforms

## Implementation Strategy
1. **String Extraction Phase**: Identify and extract all hardcoded user-facing strings into `values/strings.xml`
2. **Translation Phase**: Create Chinese translations in `values-zh-rCN/strings.xml`
3. **Code Migration Phase**: Update Kotlin code to use `context.getString(R.string.*)` or `stringResource()` for Compose
4. **Layout Migration Phase**: Update XML layouts to reference `@string/*` resources
5. **Testing Phase**: Verify string display in both locales by changing device language settings

## Dependencies
- None (Android platform native i18n support)

## Risks & Mitigation
- **Risk**: Missing strings during extraction could leave some UI elements untranslated
  - **Mitigation**: Systematic code review of all `.kt` and `.xml` files; use grep to find hardcoded text patterns
- **Risk**: Translation quality for technical terms
  - **Mitigation**: Use standard Android localization conventions for common UI terms (e.g., "Parent" → "父元素")

## Success Criteria
- All user-facing strings are extracted to resource files
- App displays fully translated UI when device language is set to Chinese (Simplified)
- App displays English UI when device language is set to English or unsupported locales
- No hardcoded user-facing strings remain in Kotlin or XML files
- Strings follow Android naming conventions (`action_*`, `label_*`, `description_*`, etc.)
