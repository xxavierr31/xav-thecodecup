# Implementation Plan - Wiring App Logic

This plan outlines the steps to implement the business logic and UI interactions for "The Code Cup" Android app, building upon the existing XML layouts and data layer.

## User Review Required

> [!IMPORTANT]
> The app currently uses a "fallback" approach for the Loyalty Stamp Card (8 static `include`s in `layout_loyalty.xml`). This matches the plan's recommendation for a realistic 4-day build. I will proceed with this approach.

> [!NOTE]
> Navigation between screens will use the Jetpack Navigation Component. I will update `nav_graph.xml` with the necessary actions and arguments (e.g., `coffeeId` for the Details screen).

## Proposed Changes

### 1. Data & Dependency Injection

#### [MODIFY] [AppDatabase.kt](file:///C:/Users/ntdan/AndroidStudioProjects/PersonalMidterm/app/src/main/java/com/example/personalmidterm/data/local/AppDatabase.kt)
- Add a `RoomDatabase.Callback` to seed the `coffees` and `redeemables` tables on first creation.

### 2. Navigation

#### [MODIFY] [nav_graph.xml](file:///C:/Users/ntdan/AndroidStudioProjects/PersonalMidterm/app/src/main/res/navigation/nav_graph.xml)
- Define actions for all navigation paths (e.g., `homeToDetails`, `menuToDetails`, `detailsToCart`, `cartToOrderSuccess`).
- Add a `coffeeId` argument to `detailsFragment`.

### 3. Menu Screen (Day 1-2 Focus)

#### [NEW] [CoffeeAdapter.kt](file:///C:/Users/ntdan/AndroidStudioProjects/PersonalMidterm/app/src/main/java/com/example/personalmidterm/ui/menu/CoffeeAdapter.kt)
- Standard `RecyclerView.Adapter` for the coffee grid.
- Implements two click listeners: one for the whole card (to Details) and one for the "+" button (quick-add to cart).

#### [MODIFY] [MenuViewModel.kt](file:///C:/Users/ntdan/AndroidStudioProjects/PersonalMidterm/app/src/main/java/com/example/personalmidterm/ui/menu/MenuViewModel.kt)
- Expose `Flow<List<Coffee>>` from `CoffeeRepository`.
- Add logic for the Specials/Classics filter.

#### [MODIFY] [MenuFragment.kt](file:///C:/Users/ntdan/AndroidStudioProjects/PersonalMidterm/app/src/main/java/com/example/personalmidterm/ui/menu/MenuFragment.kt)
- Bind the `RecyclerView` with `GridLayoutManager`.
- Observe `allCoffees` and update the adapter.

### 4. Details/Customize Screen (Day 2 Focus)

#### [MODIFY] [DetailsViewModel.kt](file:///C:/Users/ntdan/AndroidStudioProjects/PersonalMidterm/app/src/main/java/com/example/personalmidterm/ui/details/DetailsViewModel.kt)
- Hold the current `Customization` state (sweetness, temperature, flavors, etc.).
- Expose a `StateFlow` for the dynamic price calculation.

#### [MODIFY] [DetailsFragment.kt](file:///C:/Users/ntdan/AndroidStudioProjects/PersonalMidterm/app/src/main/java/com/example/personalmidterm/ui/details/DetailsFragment.kt)
- Bind `SeekBar`s, `RadioGroup`s, and `CheckBox`es to the ViewModel.
- Implement "Add to Cart" logic.
- Handle the `CartPreviewBottomSheet` display.

#### [NEW] [CartPreviewBottomSheet.kt](file:///C:/Users/ntdan/AndroidStudioProjects/PersonalMidterm/app/src/main/java/com/example/personalmidterm/ui/details/CartPreviewBottomSheet.kt)
- `BottomSheetDialogFragment` to show a quick summary of the current cart.

### 5. Cart Screen (Day 2 Focus)

#### [NEW] [CartAdapter.kt](file:///C:/Users/ntdan/AndroidStudioProjects/PersonalMidterm/app/src/main/java/com/example/personalmidterm/ui/cart/CartAdapter.kt)
- `RecyclerView.Adapter` for cart items with quantity steppers.

#### [MODIFY] [CartViewModel.kt](file:///C:/Users/ntdan/AndroidStudioProjects/PersonalMidterm/app/src/main/java/com/example/personalmidterm/ui/cart/CartViewModel.kt)
- Expose `cartItems` and `totalPrice` (including rank discount).

#### [MODIFY] [CartFragment.kt](file:///C:/Users/ntdan/AndroidStudioProjects/PersonalMidterm/app/src/main/res/layout/fragment_cart.xml)
- Implement `ItemTouchHelper` for swipe-to-delete.

### 6. Home & Rewards (Day 3 Focus)

#### [MODIFY] [HomeViewModel.kt](file:///C:/Users/ntdan/AndroidStudioProjects/PersonalMidterm/app/src/main/java/com/example/personalmidterm/ui/home/HomeViewModel.kt)
- Logic for selecting a random "Hero" coffee.
- Expose Saved Recipes (Favorites) and Loyalty state.

#### [MODIFY] [HomeFragment.kt](file:///C:/Users/ntdan/AndroidStudioProjects/PersonalMidterm/app/src/main/java/com/example/personalmidterm/ui/home/HomeFragment.kt)
- Update the "Flower Vine" (Loyalty Card) based on `stamps` count.
- Bind Hero card and Saved Recipes list.

#### [MODIFY] [RewardsFragment.kt](file:///C:/Users/ntdan/AndroidStudioProjects/PersonalMidterm/app/src/main/java/com/example/personalmidterm/ui/rewards/RewardsFragment.kt)
- Display point history and current rank.

### 7. Orders & Profile (Day 3 Focus)

#### [MODIFY] [MyOrdersFragment.kt](file:///C:/Users/ntdan/AndroidStudioProjects/PersonalMidterm/app/src/main/java/com/example/personalmidterm/ui/myorders/MyOrdersFragment.kt)
- `TabLayout` integration to filter Ongoing vs. History.

#### [MODIFY] [ProfileFragment.kt](file:///C:/Users/ntdan/AndroidStudioProjects/PersonalMidterm/app/src/main/java/com/example/personalmidterm/ui/profile/ProfileFragment.kt)
- Implement edit-mode toggle to update `ProfilePrefs`.

## Verification Plan

### Automated Tests
- N/A (Focus is on manual verification via Emulator as per project scope).

### Manual Verification
1.  **Menu**: Verify Specials/Classics toggle works.
2.  **Details**: Change flavors and verify price updates live.
3.  **Cart**: Add items, change quantity, swipe to delete, and verify total price.
4.  **Checkout**: Complete an order and verify:
    -   Navigation to Order Success.
    -   Stamps increment on the flower vine (Home/Rewards).
    -   Points increase in Rewards history.
5.  **Loyalty**: Complete 8 orders and verify `rankIndex` increases and stamps reset.
6.  **Profile**: Edit profile and verify persistence after app restart.
