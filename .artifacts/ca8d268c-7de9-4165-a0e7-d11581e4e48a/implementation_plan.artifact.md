# Implementation Plan - Checkout UI Structural Refinement

This plan reorders the checkout breakdown in the Cart screen to place the expansion toggle at the top and ensures the original subtotal (crossed out) is always visible when discounts are active.

## User Review Required

> [!NOTE]
> **New Breakdown Hierarchy**:
> 1. Header: Expansion Arrow + "Total" Label
> 2. Collapsible List: Subtotal, Rank Perk, Voucher Savings
> 3. Final Summary: Strikethrough Subtotal + Final Price
>
> **Smart Disabling**: If no discounts are active (user is at Sprout rank and no voucher is selected), the expansion toggle will be hidden, and the screen will show a simplified "Total" row.

## Proposed Changes

### 1. Checkout UI Reordering

#### [MODIFY] [fragment_cart.xml](file:///C:/Users/ntdan/AndroidStudioProjects/PersonalMidterm/app/src/main/res/layout/fragment_cart.xml)
- **Top Row**: Move the `total_row` (Toggle Button + "Total" label) to the top of the `bottom_bar` section.
- **Middle Section**: Place `breakdown_container` directly below the top row.
- **Bottom Section**: Group `tv_old_total_val` and `tv_estimated_total_val` into a dedicated summary row at the bottom of the calculation block.
- **Divider**: Reposition the divider to sit between the breakdown and the final summary row.

### 2. Visibility & Interaction Logic

#### [MODIFY] [CartFragment.kt](file:///C:/Users/ntdan/AndroidStudioProjects/PersonalMidterm/app/src/main/java/com/example/personalmidterm/ui/cart/CartFragment.kt)
- **Toggle Visibility**: Hide the `btn_toggle_breakdown` if `state.isRankDiscountVisible` and `state.isVoucherDiscountVisible` are both false.
- **Breakdown State**: Ensure the `breakdown_container` is forced to `GONE` if no discounts are active, preventing an empty breakdown from being expanded.
- **Total Alignment**: Adjust the "Total" label styling to act as a clear header when expanded.

### 3. Voucher Selection Feedback

#### [MODIFY] [item_voucher.xml](file:///C:/Users/ntdan/AndroidStudioProjects/PersonalMidterm/app/src/main/res/layout/item_voucher.xml)
- Ensure the `RadioButton` is vertically centered and visually prominent on the right side of the card.
- (Note: Selection logic is already implemented in `VoucherAdapter`).

## Verification Plan

### Manual Verification
1.  **Discount Active**: Apply a voucher.
    *   Verify the toggle arrow appears at the top.
    *   Verify you can expand it to see Subtotal, Rank discount, and Voucher discount.
    *   Verify the crossed-out subtotal is visible next to the final price.
2.  **No Discount**: Clear the voucher (if "Sprout").
    *   Verify the toggle arrow is hidden.
    *   Verify the detailed breakdown is hidden.
    *   Verify only the final price is shown (no strikethrough).
3.  **Visual Stack**: Confirm the vertical order: Toggle Header -> Breakdown List -> Strikethrough Summary.
