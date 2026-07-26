# Notable UX Features Summary — The Code Cup

This document summarizes the user experience (UX) enhancements and "smart" UI behaviors implemented during this development session.

---

## 🚀 Navigation & Onboarding

### **1. Professional Splash Screen**
- **First Impression**: A branded loading sequence that lingers for 2 seconds to establish the app's identity.
- **Clean Backstack**: Automatically removes itself from the history so pressing "Back" from the Home screen exits the app instead of returning to the logo.

### **2. Immersive Navigation Bar**
- **Full-Height Targets**: Each tab column is a single large touch target, making it effortless to switch screens even with one hand.
- **Haptic Ripple Feedback**: Native ripple effects provide instant visual confirmation of every tap.
- **Active State Highlighting**: Both icons and labels dynamically shift color to clearly indicate the current screen.

### **3. Universal Contextual Header**
- **One Header, Many Shapes**: A single component that reconfigures itself based on the screen (e.g., showing a Back button on sub-screens but a Profile Greeting on the Dashboard).
- **Time-Based Greetings**: Greets the user with "Good Morning," "Good Afternoon," or "Good Evening" based on their local time.

---

## ☕ Ordering & Customization

### **4. Smart Customization Labels**
- **Contextual Terminology**: The intensity slider dynamically renames itself to **"Heat Level"** or **"Ice Level"** depending on whether you're ordering a Hot or Iced drink.
- **Granular Snapping**: Precise 5-step snapping (0%, 25%, 50%, 75%, 100%) for sweetness and temperature levels.

### **5. Real-Time Price Engine**
- **Instant Updates**: The total price recalculates instantly as the user toggles flavors (+5.000đ) or adds extra espresso shots (+7.000đ).

### **6. Live Cart Preview**
- **Check Without Leaving**: A bottom-sheet preview allows users to peek at their cart from the Customization screen without losing their progress on their current drink.

### **7. Real-Time Menu Search**
- **Dual-Filtering**: A search bar that works *simultaneously* with category tabs (Specials/Classics), updating the product grid instantly as the user types.

---

## 🛒 Cart & Checkout

### **8. Forgiving Deletion (Undo)**
- **Accidental Protection**: When an item is swiped away, a Snackbar appears with an **"UNDO"** action to instantly restore the drink and its specific customizations.

### **9. Smart Cart Merging**
- **Clean Inventory**: If a user adds the exact same customized drink twice, the cart intelligently increments the quantity instead of cluttering the list with duplicate rows.

### **10. Intelligent Discount Breakdown**
- **Rank Perks**: Users with higher loyalty ranks see a clear breakdown of their savings, featuring a negative discount value and a strikethrough effect on the original subtotal.

---

## 🌸 Loyalty & Rewards

### **11. Interactive Flower Vine**
- **Visual Progress**: A row of flower icons that "bloom" as the user completes orders, providing a satisfying sense of progression.
- **Celebratory Milestones**: Triggers a celebratory toast at 8 stamps and a system-level notification when the user "Ranks Up."

### **12. Historical Address Snapshots**
- **Accuracy over Time**: Each order captures the user's *exact* address at the moment of purchase. If the user moves and updates their profile later, their past order history remains historically accurate.

### **13. Smart Marketplace Buttons**
- **Affordability Logic**: "Redeem" buttons automatically gray out and prices turn red if the user doesn't have enough points, preventing frustration at checkout.

---

## 👤 Profile & Persistence

### **14. Robust Input Validation**
- **Data Integrity**: Prevents malformed emails or blank names/addresses with helpful Toast notifications, ensuring the user's profile is always valid.

### **15. Smart Recipe Recognition**
- **Personalized Library**: The app "remembers" every unique combination of drink and settings you've saved. The heart icon on the Customize screen will automatically light up the moment your current sliders match a previously saved favorite.
