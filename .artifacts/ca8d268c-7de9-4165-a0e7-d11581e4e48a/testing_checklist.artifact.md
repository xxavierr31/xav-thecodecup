# Deep-Testing Checklist: The Code Cup

Use this checklist to verify every feature and edge case in your app before final submission.

## 1. Startup & Global Navigation
- [ ] **Splash Screen**: App opens to the loading screen for exactly 2 seconds.
- [ ] **Splash Backstack**: From the Home screen, press the system back button. The app should exit (it should **not** return to the splash screen).
- [ ] **Bottom Nav**: Verify all 5 tabs (Menu, Rewards, Home, Profile, Orders) navigate to the correct fragment.
- [ ] **Navbar Highlighting**: Verify icons and text labels turn brown when active and grey when inactive.
- [ ] **Navbar Visibility**: Confirm the navbar hides on "stack-only" screens: Customize, My Cart, Order Success, and Redeem.
- [ ] **Universal Header**:
    - [ ] Home: Shows Avatar (left), Greeting (center-left), and Cart (right).
    - [ ] Menu/Orders/Rewards/Profile: Shows centered Title only.
    - [ ] Customize/Cart/Redeem: Shows Back Arrow and centered Title.

## 2. Home Dashboard
- [ ] **Greeting**: Check that it says "Good Morning," "Good Afternoon," or "Good Evening" based on your local clock.
- [ ] **Hero Card**:
    - [ ] Confirmed it selects a "Special" drink.
    - [ ] Tap "Order Now": Navigates to the Customize screen for that specific drink.
    - [ ] Rotation Test: Rotate the screen; the hero drink should **not** change.
- [ ] **Loyalty Vine**: Check that the count (e.g., 0/8 Stamps) and filled flowers match your actual history.
- [ ] **Saved Recipes**:
    - [ ] Click a recipe: Verify it navigates to Customize without crashing.
    - [ ] Verify that all saved settings (sweetness, shots, flavors) are pre-filled correctly.
    - [ ] Verify the price on the card matches the base price + customizations.

## 3. Menu & Browsing
- [ ] **Category Toggles**: Tap "Seasonal" and "Classics." Verify the list filters instantly.
- [ ] **Product Cards**:
    - [ ] Tap the card body: Navigates to the Customize screen.
    - [ ] Tap the "+" button: Also navigates to the Customize screen (as per your specific requirement).

## 4. Drink Customization
- [ ] **Sweetness Slider**: Verify it snaps to 0, 25, 50, 75, 100.
- [ ] **Dynamic Labels**: Tap "Hot" -> label should say **"Heat Level"**. Tap "Iced" -> label should say **"Ice Level"**.
- [ ] **Price Calculation**:
    - [ ] Add flavors: Price increases by **+5.000đ** per flavor.
    - [ ] Add shots: Price increases by **+7.000đ** per shot.
- [ ] **Cart Preview Icon**: Tap the cart icon in the header.
    - [ ] Verify the bottom sheet opens with a "preview" of your current cart.
    - [ ] Verify the "Go to Cart" button works.
- [ ] **Add to Cart**: Verify clicking the main button adds the drink and navigates to the My Cart screen.

## 5. My Cart & Checkout
- [ ] **Quantity Steppers**: Increase and decrease quantities. Verify the total price updates.
- [ ] **Cart Merging**: Add the *exact same* customized drink twice. Verify the cart shows 1 row with `2x` quantity instead of 2 separate rows.
- [ ] **Swipe to Delete**: Swipe an item left.
    - [ ] Verify it disappears.
    - [ ] Verify the **Undo Snackbar** appears.
    - [ ] Tap "UNDO": Verify the item returns with all customizations intact.
- [ ] **Empty State**: Remove all items. Verify the "Your cart is empty" illustration and "Browse Menu" button appear.
- [ ] **Rank Discounts**:
    - [ ] Sprout (0%): Bottom bar shows only the final total.
    - [ ] Higher Rank (>0%): Bottom bar shows Rank Perk, negative discount amount, and the original subtotal (strikethrough).
- [ ] **Checkout**: Tap "Proceed to Checkout." Verify you land on the Order Success screen.

## 6. My Orders & Loyalty
- [ ] **Tabs**: Toggle between "Ongoing" and "History."
- [ ] **Order Completion**: Tap an "Ongoing" order card.
    - [ ] It should move to the "History" tab.
    - [ ] Your **Stamp count** should increase by 1.
    - [ ] Your **Points** should increase based on the total spent.
- [ ] **Rank Up**: Reach 8 stamps.
    - [ ] Verify your Rank name changes (e.g., Sprout -> Naturalist).
    - [ ] Verify the vine resets to 0/8 Stamps.
- [ ] **Address Snapshot**:
    - [ ] Edit your profile address.
    - [ ] Place an order.
    - [ ] Verify the order card shows your **current** address.
    - [ ] Edit your address again.
    - [ ] Verify the **old** order card still shows the original address (Historical Snapshot).

## 7. Profile & Settings
- [ ] **Edit Mode**: Tap the pencil icon. Verify all fields (Name, Email, Phone, Address) become editable.
- [ ] **Input Validation**:
    - [ ] Try saving a blank Name. Verify the Toast error.
    - [ ] Try saving an invalid Email (no @). Verify the Toast error.
    - [ ] Try saving an invalid Phone (less than 10 digits). Verify the Toast error.
- [ ] **Data Persistence**: Kill the app and restart. Verify your edited profile and points balance are still there.
