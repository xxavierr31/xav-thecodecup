# Implementation Plan - Menu Search Bar

This plan adds a search bar to the Menu screen, allowing users to filter the coffee catalog by name while maintaining their selected category (Specials/Classics).

## User Review Required

> [!NOTE]
> The search bar will filter the menu in real-time as the user types. It works in conjunction with the existing category tabs.

## Proposed Changes

### 1. Menu Layout Enhancement

#### [MODIFY] [fragment_menu.xml](file:///C:/Users/ntdan/AndroidStudioProjects/PersonalMidterm/app/src/main/res/layout/fragment_menu.xml)
- Add a `MaterialCardView` containing a `TextInputEditText` for the search bar between the screen subtitle and the filter tabs.
- Style the search bar with rounded corners, a subtle stroke, and a search icon.
- Update the top constraints of the filter tabs to anchor to the bottom of the new search bar.

### 2. ViewModel Logic

#### [MODIFY] [MenuViewModel.kt](file:///C:/Users/ntdan/AndroidStudioProjects/PersonalMidterm/app/src/main/java/com/example/personalmidterm/ui/menu/MenuViewModel.kt)
- Add `private val _searchQuery = MutableStateFlow("")`.
- Add `val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()`.
- Implement `fun updateSearchQuery(query: String)`.
- Refactor `filteredCoffees` to combine `coffeeRepository.allCoffees`, `_selectedCategory`, and `_searchQuery`. The logic will first filter by category, then filter by name containing the query (case-insensitive).

### 3. Fragment Wiring

#### [MODIFY] [MenuFragment.kt](file:///C:/Users/ntdan/AndroidStudioProjects/PersonalMidterm/app/src/main/java/com/example/personalmidterm/ui/menu/MenuFragment.kt)
- Add a `doOnTextChanged` listener (or `TextWatcher`) to the search `EditText` to update the ViewModel's search query.
- Ensure the search bar state is preserved if needed (though `ViewModel` handles this automatically).

## Verification Plan

### Manual Verification
1.  **Typing**: Type "Latte" in the search bar. Verify the list only shows drinks with "Latte" in the name.
2.  **Category + Search**:
    - Select "Specials".
    - Type "Matcha".
    - Verify only Special Matcha drinks are shown.
3.  **Clear Search**: Clear the search bar and verify the list returns to showing all drinks in the selected category.
4.  **Case Insensitivity**: Verify that searching for "latte" also finds "Classic Latte".
