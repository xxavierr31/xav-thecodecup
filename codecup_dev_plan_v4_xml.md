# The Code Cup — Android Build Plan & AI Assistant Prompt (v4 — Traditional Views/XML)
### CS426 Midterm Project — paste this whole document into your coding assistant (Claude Code or similar) as the opening message of your build session. Attach the 9 UI reference screenshots (Home, Menu, Customize, My Cart, My Orders, Rewards, Redeem, Profile, Order Success) alongside it.

---

## 0. How to use this document

This is both a **spec** and a **prompt**. Paste it in full at the start of a new coding session, with the reference screenshots attached. The instructions in Section 1 are written *to the assistant*. Everything after that is the technical plan.

**v4 changes vs v3: Jetpack Compose is removed entirely.** UI is built with traditional Android Views — XML layouts, Fragments, ViewBinding, RecyclerView/Adapter/ViewHolder, Navigation Component (XML graph), and standard `onPause`/`onResume`/`onStop` overrides. Reasoning: Compose's learning curve doesn't fit a 4-day build for someone new to Android, and the rubric's own wording (`ListView`/`RecyclerView`, explicit lifecycle callbacks) was written for the Views world anyway — this removes a translation layer, not just a framework. Data model (Section 5) and business logic (Section 8) are framework-agnostic and **unchanged** from v3.

Today is **July 25**, the project is due **July 29**. 4 working days plus the deadline day itself for recording/submission.

---

## 1. Instructions to the AI assistant (read this first)

You are helping Xavier build "The Code Cup," a native Android coffee-ordering app for a CS426 midterm. He has zero prior Kotlin/Android experience but has built HTML/web apps before — the XML layout syntax will feel more familiar to him than Compose's DSL did, but the View system's plumbing (Adapters, ViewHolders, ViewBinding, Fragment lifecycles) is still new. A few things about how to work with him:

- **He's comfortable with frontend/UI markup generally** (XML tags won't be the hard part), but the Android View *system* — Fragments, the Adapter/ViewHolder pattern, ViewBinding, Navigation Component — is new. Don't assume familiarity with any of it the first time it shows up.
- **Backend, data persistence, and state management are his stated weak spot.** Slow down here. Before writing Room entities, DAOs, repositories, or ViewModel state logic, explain *why* the piece is structured that way, not just *what* it does. He has SQL/relational-modeling background, so it's fine to bridge Room concepts to that ("a DAO query is just a named SQL statement Room compiles for you") rather than starting from zero.
- **The Adapter/ViewHolder pattern deserves the same care as Room did in v3.** It's the single new concept most likely to trip him up (view recycling, `onBindViewHolder` semantics, why we're not just creating N views for N items). Explain it once, clearly, the first time a RecyclerView is built (Day 1's Menu grid), and the rest of the app's lists reuse the same explained pattern.
- **He wants to actually understand the code, not just receive it.** His instructor can ask him to explain any part of the submission during review — confirm with him when that review happens (at submission, or later) since it changes how much can safely be "understand later." Build incrementally, check in after each meaningful chunk, and flag the 2-3 sentence "why this works" for any non-obvious logic. Prioritize deep understanding of the checkout → order-completion → stamp/points/rank pipeline above all else — that's the piece most likely to come up in review, since it's the single place where the most rubric items intersect.
- **Prefer targeted, incremental changes over big rewrites** once a file exists.
- **Do not silently deviate from the architecture in Section 3 or the data model in Section 5.** If a different approach seems better, say so and why, and let Xavier decide.
- **Follow the build order in Section 10**, including the vertical-slice step on Day 1 before writing the rest of the Room layer blind.
- **Where this plan already made a scope compromise to keep Views-based development realistic in 4 days** (skipping Safe Args, DiffUtil, ViewPager2, custom-attribute Views — see Section 3), don't re-introduce them even if they'd be "more correct" — that's time you don't have.
- Kick off with the Day 1 setup steps once Xavier confirms he's ready.

---

## 2. Project snapshot

- **App**: "The Code Cup" — coffee shop ordering app. Browse coffee → customize → cart → checkout → order tracking → loyalty rewards (stamps + rank) → redeem → profile.
- **9 screens**: Home, Menu, Details/Customize, My Cart, Order Success, My Orders, Rewards, Redeem Rewards, Profile.
- **Platform**: Must target Android. **Individual project. Deadline July 29.**
- **Submission**: `StudentID.zip` containing `source/`, `StudentID-demo.mp4` (or `demo.txt` w/ YouTube link), `StudentID-app.apk`/`.aab`, `StudentID-report.pdf`. Source cleaned of build artifacts before zipping.
- **Scoring**: `min(150, sum of rubric items)` + quality adjustment for code/report/demo, then `final score = min(10, (total + adjustment) / 15)`. **Heads up:** the assignment PDF itself is inconsistent about the quality adjustment range — it says "±10 Points" in one place and shows "[-15,+15]" right next to it. Worth a quick confirmation on the class Facebook group; doesn't change how you build, just how much report/demo polish is worth relative to features.
- **Rubric math**: named, well-defined requirements (all screens + state/lifecycle + persistence) sum to **91 points**. "User-Defined Features" is a single **50-point** bucket — get the required 91 solid before it matters.
- AI tool use is permitted; Xavier must be able to explain any part of the code, and third-party libraries must be attributed in the report.

---

## 3. Architecture decision

**Stack: Kotlin + traditional Android Views (XML layouts + Fragments + ViewBinding) + MVVM + Room (for relational data) + SharedPreferences (for singleton data) + Navigation Component (XML nav graph) + Coroutines/Flow. Manual dependency injection (no Hilt).**

- **Kotlin + Views/XML, no Compose.** Matches the rubric's literal `ListView`/`RecyclerView` wording directly — no substitution note needed in the report this time. Layouts are `.xml` under `res/layout/`, one Fragment per screen, `ViewBinding` (not `findViewById`) for view references — same null-safety benefit as Compose without the new DSL.
- **MVVM** — matches the rubric's own language (`onPause`/`onResume`/`onStop`, "state management") and works cleanly with Fragments. View = Fragment + XML layout, ViewModel = one per screen holding UI state as `StateFlow` (observed via `lifecycleScope.launch { flow.collect { ... } }` in `onViewCreated`, or plain `LiveData` + `observe()` if that reads simpler to him — either is fine, pick one and stay consistent), Model = Repository + Room/SharedPreferences.
- **Room for genuinely relational data only: Coffee catalog, Cart, Orders, OrderItems, RewardTransactions, Favorites.** Unchanged from v3 — these have real relationships (orders contain items, favorites reference coffees).
- **SharedPreferences for the two singleton-row cases: Profile and Loyalty state (stamps, points, lifetime completed orders).** Unchanged from v3 — wrap reads/writes in a small repository (`LoyaltyPrefs`, `ProfilePrefs`) that also holds a `MutableStateFlow`.
- **No Hilt/Dagger.** A small manual `AppContainer` class is fully transparent code you can read top to bottom — a real asset given the oral-review requirement.
- **Navigation Component with an XML nav graph** (`res/navigation/nav_graph.xml`) — single-Activity, one `NavHostFragment`, all 9 screens as Fragment destinations. Handles the back-stack for free, same as Compose Navigation did.
- **Currency: VND everywhere, via a global `CurrencyFormatter`.** Unchanged from v3. `1.000đ` styling, whole-number `Long` amounts, `util/CurrencyFormatter.format(amount: Long): String` is the only place formatting logic lives.

**Scope compromises made specifically to keep a Views-based build realistic in 4 days** — don't add these back in even if they'd be "more correct" Android practice:
- **No Safe Args plugin.** It's an extra Gradle/codegen step layered on top of Room's KSP step — one generated-code system is enough to explain. Pass navigation arguments via a plain `Bundle` (`fragment.arguments = bundleOf("coffeeId" to id)`), read with `arguments?.getLong("coffeeId")`.
- **No `DiffUtil`/`ListAdapter`.** Plain `RecyclerView.Adapter` + `notifyDataSetChanged()` on every data change. The dataset sizes here (a coffee menu, a cart, an order history) are small enough that the performance case for `DiffUtil` doesn't matter, and it's one fewer concept to teach under time pressure.
- **No `ViewPager2` for My Orders tabs.** A `TabLayout` with two tabs (Ongoing/History) driving a single `RecyclerView`, re-filtered on tab-select — not a swipeable-pager-of-fragments. Simpler, and the mockup doesn't require swipe-between-tabs behavior.
- **No custom-attribute reusable View class for the header.** Building a true parameterized custom View (`declare-styleable`, `obtainStyledAttributes`) is a real, separate thing to learn. Instead: one shared `include`-able XML layout (`layout_app_header.xml`) with all possible elements (back arrow, title, heart icon, cart icon, greeting/avatar variant) present but `visibility`-toggled per screen in each Fragment's `onViewCreated`. Slightly less elegant, much less new API surface.

---

## 4. Project structure

```
app/src/main/java/com/xavier/thecodecup/
├── CodeCupApplication.kt
├── MainActivity.kt                    // single Activity, hosts NavHostFragment
├── di/
│   └── AppContainer.kt
├── data/
│   ├── local/
│   │   ├── AppDatabase.kt
│   │   ├── Converters.kt              // TypeConverters — see Section 5
│   │   ├── entity/
│   │   │   ├── CoffeeEntity.kt        // + category: Category (SPECIAL | CLASSIC)
│   │   │   ├── CartItemEntity.kt      // + quantity, sweetness, temperature, intensity, shots, flavors
│   │   │   ├── OrderEntity.kt
│   │   │   ├── OrderItemEntity.kt     // same customization fields, snapshot at order time
│   │   │   ├── RewardTransactionEntity.kt
│   │   │   ├── FavoriteEntity.kt      // NEW — bonus feature 1
│   │   │   └── RedeemableItemEntity.kt // NEW — Redeem screen catalog, separate from Coffee
│   │   └── dao/
│   │       ├── CoffeeDao.kt
│   │       ├── CartDao.kt
│   │       ├── OrderDao.kt
│   │       ├── RewardDao.kt
│   │       ├── FavoriteDao.kt         // NEW
│   │       └── RedeemableDao.kt       // NEW
│   ├── prefs/
│   │   ├── ProfilePrefs.kt            // SharedPreferences wrapper + StateFlow
│   │   └── LoyaltyPrefs.kt            // stamps (0-8, the vine), totalPoints, rankIndex + StateFlow
│   └── repository/
│       ├── CoffeeRepository.kt
│       ├── CartRepository.kt
│       ├── OrderRepository.kt          // touches LoyaltyPrefs on order completion
│       ├── RewardRepository.kt
│       ├── FavoriteRepository.kt       // NEW
│       ├── RedeemableRepository.kt     // NEW
│       └── ProfileRepository.kt        // wraps ProfilePrefs
├── model/
│   ├── Coffee.kt, CartItem.kt, Order.kt, RewardTransaction.kt, Profile.kt
│   ├── Favorite.kt                    // NEW
│   ├── RedeemableItem.kt              // NEW
│   ├── Customization.kt               // NEW — shared shape: sweetness/temperature/intensity/shots/flavors
│   └── RankTier.kt                    // NEW — plain hardcoded list, not DB-backed (see Section 8)
├── util/
│   └── CurrencyFormatter.kt           // single global VND formatter — import everywhere a price is shown
└── ui/
    ├── home/
    │   ├── HomeFragment.kt, HomeViewModel.kt
    │   └── SavedRecipeAdapter.kt      // small horizontal RecyclerView
    ├── menu/
    │   ├── MenuFragment.kt, MenuViewModel.kt
    │   └── CoffeeAdapter.kt           // GridLayoutManager, Specials/Classics filter
    ├── details/
    │   ├── DetailsFragment.kt, DetailsViewModel.kt
    │   └── CartPreviewBottomSheet.kt  // BottomSheetDialogFragment
    ├── cart/
    │   ├── CartFragment.kt, CartViewModel.kt
    │   └── CartAdapter.kt             // + ItemTouchHelper callback for swipe-remove
    ├── ordersuccess/
    │   └── OrderSuccessFragment.kt
    ├── myorders/
    │   ├── MyOrdersFragment.kt, MyOrdersViewModel.kt
    │   └── OrderAdapter.kt            // filtered by TabLayout selection, not ViewPager2
    ├── rewards/
    │   ├── RewardsFragment.kt, RewardsViewModel.kt
    │   └── ActivityAdapter.kt
    ├── redeem/
    │   ├── RedeemFragment.kt, RedeemViewModel.kt
    │   └── RedeemableAdapter.kt
    ├── profile/
    │   └── ProfileFragment.kt, ProfileViewModel.kt
    └── components/                    // shared, reused across the above
        ├── LoyaltyVineView.kt         // custom View drawing the 8-flower vine, or a bound XML layout — see note below
        └── PriceSummaryBinder.kt      // small helper, not a class hierarchy — binds a price row's views

res/
├── layout/
│   ├── activity_main.xml              // just a NavHostFragment
│   ├── layout_app_header.xml          // shared <include>, visibility-toggled per screen
│   ├── fragment_home.xml, fragment_menu.xml, fragment_details.xml, fragment_cart.xml,
│   │   fragment_order_success.xml, fragment_my_orders.xml, fragment_rewards.xml,
│   │   fragment_redeem.xml, fragment_profile.xml
│   ├── item_coffee_card.xml, item_cart_row.xml, item_order.xml,
│   │   item_redeemable.xml, item_saved_recipe.xml, item_activity.xml
│   └── bottomsheet_cart_preview.xml
├── navigation/
│   └── nav_graph.xml
├── menu/
│   └── bottom_nav_menu.xml            // 5-item BottomNavigationView menu resource
├── values/
│   ├── colors.xml, themes.xml, strings.xml
└── font/                              // optional serif display font, see palette note below
```

**`layout_app_header.xml` note**: your screens use different header shapes — Home has a greeting + avatar (no icons), Customize has back-arrow + title + heart + cart icons, Menu/Cart/Redeem have back-arrow + centered title only. Build one XML layout with every element present, then in each Fragment's `onViewCreated`, set `visibility = View.GONE` on whatever that screen doesn't use. Cheaper than a real custom-attribute View, and every screen's header logic is just a few `.visibility =` lines you can read at a glance.

**`LoyaltyVineView` note**: this is the one place a genuine custom `View` subclass (with `onDraw` for the flower vine) is worth it over a static XML layout, since it's a visual component reused on Home, Rewards, and Profile with a dynamic fill state. Keep it simple — a `View` with a public `fillCount: Int` property that triggers `invalidate()` on change, drawn with basic `Canvas`/`Paint` calls (or, if that's too much new surface area for the time budget, fall back to 8 static `ImageView` flower icons in a `LinearLayout` and toggle each one's drawable between filled/unfilled — functionally identical, zero custom-drawing code). Decide based on how Day 1 is going.

**Theme palette** (sampled from your actual screenshots, not the assignment's navy mockups — fine-tune exact hex against your design file):
| Token | Approx. color | Used for |
|---|---|---|
| Background | `#FBF3E7` (warm cream) | screen backgrounds |
| Surface/Card | `#FFFFFF` / `#F4E9DA` | cards, list rows |
| Primary | `#7A4419` (rich brown) | buttons — "Add to Cart," "Order Now," "Redeem Points" |
| Heading text | `#5C3317` | serif-styled titles — "The Code Cup," "Rewards," coffee names |
| Body/muted text | `#8A8478` | descriptions, secondary labels |
| Accent | `#7D2E46` (deep maroon) | stamp/rank icons |
| Success | sage green | "Delivered" status badge |

Define these once in `res/values/colors.xml` and reference them from `themes.xml` / layout XML — never hardcode a hex in a layout file.

Headings in your mockups use a serif display face; body text is a plain sans-serif. If you want to match that (optional polish, not rubric-scored), pull a free serif like Playfair Display or Lora into `res/font/` and reference it via a `TextAppearance` style in `themes.xml` — small effort, nice visual payoff for the demo video.

---

## 5. Data model & persistence

**Unchanged from v3 — entirely framework-agnostic.** Room entities, SharedPreferences wrappers, and TypeConverters don't care whether the UI layer is Compose or Views.

All monetary fields (`basePrice`, `unitPrice`, `totalPrice`, `pointsCost` is points not currency) are `Long` VND, formatted only via `CurrencyFormatter` — see Section 3.

| Entity/Store | Purpose | Notes |
|---|---|---|
| `CoffeeEntity` (Room) | menu items | seeded once; `category: SPECIAL \| CLASSIC` drives the Menu toggle — named `SPECIAL` rather than the mockups' "Seasonal" since these are permanent curated sections, not calendar-based rotation |
| `CartItemEntity` (Room) | items in the cart | `quantity`, `sweetness`, `temperature`, `intensity`, `shots`, `flavors` (List<Flavor>), `unitPrice` snapshot; cleared on checkout |
| `OrderEntity` (Room) | one row per placed order | `status: ONGOING \| HISTORY`, `resultStatus: DELIVERED \| CANCELLED` (cosmetic, defaults to DELIVERED on completion — purely for the History badge, not separately graded), `totalPrice`, `timestamp` |
| `OrderItemEntity` (Room) | permanent snapshot of items at order time | same customization fields as `CartItemEntity`, FK → `OrderEntity` |
| `RewardTransactionEntity` (Room) | points-earned history log | FK → `OrderEntity` |
| `FavoriteEntity` (Room) | a saved drink **with its full customization**, not just a coffee reference | needs its own copy of the customization because it must survive independently of the cart (which clears on checkout) — same reasoning as `OrderItemEntity` being a snapshot rather than a live reference |
| `RedeemableItemEntity` (Room) | Redeem screen catalog | separate from `CoffeeEntity` because some redeemables (e.g. "Botanical Gift Set") aren't drinks; fields: `name`, `description`, `pointsCost`, `imageRes` |
| `ProfilePrefs` (SharedPreferences) | single-row user profile | plain key-value, no schema needed |
| `LoyaltyPrefs` (SharedPreferences) | `stamps` (0–8, the vine — resets to 0 and advances `rankIndex` on the 8th fill), `totalPoints`, `rankIndex` | exposed as a `StateFlow` from the repository so Home/Rewards/Profile all observe the same source |
| `RankTier` (plain Kotlin list, not persisted) | static rank ladder, indexed by `rankIndex` | see Section 8 — no thresholds needed, since the vine itself paces one rank-up per 8 completed orders |

**TypeConverters** (`Converters.kt`, registered via `@TypeConverters(Converters::class)` on `AppDatabase`): Room needs these for anything beyond primitives — your enums (`Temperature`, `Category`, order `status`) and the `List<Flavor>` field on cart/order items.
```kotlin
class Converters {
    @TypeConverter
    fun fromFlavorList(flavors: List<Flavor>): String = flavors.joinToString(",") { it.name }

    @TypeConverter
    fun toFlavorList(data: String): List<Flavor> =
        if (data.isBlank()) emptyList() else data.split(",").map { Flavor.valueOf(it) }
}
```

**Seeding**: `RoomDatabase.Callback.onCreate` inserts the coffee catalog (with categories) and the redeemable catalog. `LoyaltyPrefs`/`ProfilePrefs` just need sensible defaults on first read (`stamps=0`, `totalPoints=0`, `rankIndex=0`) — no seeding step required since they're not a table.

---

## 6. Navigation map

**Bottom-nav (persistent) destinations, 5 items**: Menu · Rewards · Home · Profile · Orders — a `BottomNavigationView` in `activity_main.xml`, wired to the `NavController` via `NavigationUI.setupWithNavController()`.
**Stack-only destinations** (back arrow, no bottom bar): Details/Customize/{coffeeId} · Cart · Order Success · Redeem Rewards

```
Home ──hero "Order Now" / tap Saved Recipe──▶ Details ──Add to Cart──▶ Cart ──Checkout──▶ Order Success
  │                                              │  (cart icon = bottom-sheet preview)        │
  │                                              └──back──▶ (previous screen)          Track My Orders──▶ My Orders
  └──bottom nav──▶ Menu ──tap card body──▶ Details                                     Back to Home──▶ Home
  └──bottom nav──▶ Rewards ──Redeem Points──▶ Redeem Rewards
  └──bottom nav──▶ My Orders (Ongoing / History tabs)
  └──bottom nav──▶ Profile
```

- **Cart Preview**: `CartPreviewBottomSheet` (`BottomSheetDialogFragment`), shown via `.show(childFragmentManager, "cart_preview")` from the Details cart icon — overlays rather than navigates, satisfying "without navigating away."
- **Home vs. Menu split**: Home is a dashboard (hero card + saved recipes + loyalty view); Menu is the full browsable catalog. The rubric's "Home Screen" section lists "Coffee List View" and "Navigation Intent" as Home items, but the redesign moves the full grid to its own Menu tab. To stay aligned with the rubric: the Home hero card and each Saved Recipe card must be individually tappable → Details (this is what satisfies "Navigation Intent" on Home), and the report should note in one sentence that "Coffee List View" is implemented on Menu as a deliberate structural choice.
- Bottom-bar visibility: `NavigationUI` handles this automatically for destinations in the bottom-nav menu resource; for the 4 stack-only destinations, hide the `BottomNavigationView` in `MainActivity`'s `OnDestinationChangedListener` when the current destination isn't one of the 5 top-level IDs.
- Navigation args passed via `Bundle` (no Safe Args — see Section 3 compromise list).

---

## 7. Screen-by-screen build spec

| Screen | Rubric item | Pts | Implementation note |
|---|---|---|---|
| Home | UI Implementation | 1 | `HomeFragment` — greeting header, hero card, saved-recipes row |
| Home | Header Component | 2 | `layout_app_header.xml` greeting/avatar variant, other icons hidden |
| Home | Bottom Navigation Bar | 3 | `BottomNavigationView` (Material Components), 5 items, `bottom_nav_menu.xml` |
| Home | Loyalty Card View | 3 | `LoyaltyVineView` — 8-flower vine, doubles as required stamp card + rank display, see Section 8 |
| Home | Coffee List View | 3 | satisfied primarily on **Menu** (see Section 6); Home's hero + saved recipes are the supporting navigable list on this screen |
| Home | Navigation Intent | 3 | hero "Order Now" button and each Saved Recipe item's click listener → `navigate()` to Details |
| Menu | *(new screen, houses the above two items in full)* | — | `RecyclerView` + `GridLayoutManager(spanCount = 2)` + `CoffeeAdapter` over `CoffeeRepository`; Specials/Classics toggle (bonus feature 3) re-filters the adapter's list and calls `notifyDataSetChanged()`. Tapping a card's *body* navigates to Details (required Navigation Intent); the small "+" icon inside the same item view is a separate click target (adds with default customization, skips Details) — give both views their own `setOnClickListener` in `onBindViewHolder`. Swap in clean coffee photos before building the real cards — the star rating / review count / "ADD TO CART" text baked into the mockup photos are stock-image artifacts, not real UI; don't build a review system to match them. |
| Details | Product Customization | 3 | Sweetness `SeekBar` (stepped, see Section 11), Temperature `ToggleButton`/`RadioGroup`, Intensity `SeekBar`, Shots stepper (two `ImageButton`s + `TextView`), Flavor `CheckBox`es |
| Details | Add to Cart | 3 | `CartRepository.addItem()`, then `navigate()` to Cart |
| Details | Cart Preview | 3 | `CartPreviewBottomSheet` (`BottomSheetDialogFragment`) |
| Details | Dynamic Price Calculation | 3 | live recompute from flavor `CheckBox` listeners — see Section 8 |
| Details | Back Navigation | 1 | `findNavController().navigateUp()` / system back — free with Navigation Component |
| My Cart | UI Implementation | 1 | — |
| My Cart | Cart Item Rendering | 7 | `RecyclerView` + `CartAdapter` over `CartRepository` Flow, +/- quantity `ImageButton`s per row |
| My Cart | Total Price Display | 3 | derived `sumOf { unitPrice * quantity }` |
| My Cart | Gesture-Based Removal | 3 | `ItemTouchHelper` with a `SimpleCallback(0, ItemTouchHelper.LEFT)`, `onSwiped()` removes the row — independent of the quantity steppers, which decrement to 0 and remove the line separately. The Cart mockup only shows steppers; keep both so the redesign doesn't quietly drop this scored requirement (3 pts). |
| My Cart | Checkout Navigation | 1 | `OrderRepository.placeOrder()` → navigate to Order Success |
| Order Success | UI | 1 | confirmation `Fragment`; "Track My Orders" + "Back to Home" buttons |
| Order Success | Track Order Navigation | 1 | → My Orders |
| My Orders | UI | 1 | `TabLayout` (Ongoing / History) above one `RecyclerView` |
| My Orders | Order History Display | 3 | `OrderAdapter`'s list re-filtered by `status` on tab-select, `notifyDataSetChanged()` |
| My Orders | Order Status Transition | 3 | tap an ongoing order row → `OrderRepository.completeOrder(id)` — this is what fires stamp/vine (auto rank-up) + points logic together |
| Rewards | UI | 1 | `LoyaltyVineView` (required stamp card + rank display, unified) + Recent Activity `RecyclerView` |
| Rewards | Loyalty Stamp Logic | 3 | +1 flower on the vine per completed order, cap at 8 |
| Rewards | Loyalty Card Reset ("Rank Up") | 3 | filling the 8th flower auto-advances `rankIndex` and resets the vine to 0 — the fill itself is the event |
| Rewards | Points Calculation & Display | 3 | see formula in Section 8 |
| Rewards | Total Points Aggregation | 2 | sum from `LoyaltyPrefs` |
| Redeem Rewards | Points Redemption | 3 | per-item `pointsCost` from `RedeemableItemEntity` via `RedeemableAdapter`; `Button.isEnabled = totalPoints >= cost` per row |
| Profile | UI Implementation | 3 | avatar `ImageView`, name, rank badge, personal detail `TextView`s |
| Profile | Editing Functionality | 3 | edit-mode toggle via pencil `ImageButton` — swap `TextView`s for `EditText`s (or toggle visibility between a display group and an edit group) |

---

## 8. Core business logic — pin these down before coding

**Unchanged from v3 — entirely framework-agnostic.**

**Per-unit price** (Details screen, live-updates as flavors toggle — temperature/sweetness/intensity/shots don't affect price, matching your mockup). Quantity control lives in Cart, not Details; that's fine — the rubric's "Dynamic Price Calculation" line asks for live updates as the user changes "quantity **or** custom options" (it's an *or*), so live-updating price from flavor selections alone on Details satisfies it:
```
unitPrice = coffee.basePrice + selectedFlavors.size × FLAVOR_SURCHARGE   // FLAVOR_SURCHARGE = 5.000đ
```

**Cart total**:
```
lineSubtotal = cartItem.unitPrice × cartItem.quantity
cartTotal = sum(lineSubtotal for all items)
```

**Checkout total** (rank discount applied last, after everything else):
```
finalTotal = cartTotal × (1 − currentRankTier.discountPercent)
```

**Points earned per completed order**: with prices in VND, a clean ratio reads better than the original mockup's dollar-based multiplier — suggested: `pointsEarned = floor(order.finalTotal / 1000)`, i.e. 1 point per 1.000đ spent. Adjust once you see real numbers on screen.

**Stamp / Rank logic (unified — the Flower Vine *is* the required stamp card, not a second system)**:
```
stamps += 1                                                         // on every completed order
if (stamps == 8) {
    rankIndex = (rankIndex + 1).coerceAtMost(rankTiers.lastIndex)   // Rank Up — capped at the top tier
    stamps = 0                                                       // vine resets
}
```
Filling the 8th flower is itself the rubric's required "reset on reaching eight" event — no separate manual reset button needed, the fill is the trigger. That same fill also fires Rank Up, advancing `rankIndex` before resetting the vine to 0. One field (`stamps`), one code path: this satisfies the rubric's required "increment up to eight / reset on reaching eight" behavior *and* is the bonus rank-progression mechanic — nothing duplicated. `rankIndex` caps at the last tier, so once Master Gardener is reached, further vine fills keep resetting `stamps` but rank stays put.

Ladder — indexed by `rankIndex`, no thresholds needed since the vine paces exactly one rank-up per 8 completed orders. "Naturalist" and "Botanist" are already in your mockups, kept in place:

| `rankIndex` | Rank | Discount |
|---|---|---|
| 0 | Sprout | 0% |
| 1 | Naturalist | 5% |
| 2 | Botanist | 10% |
| 3 | Master Gardener (ceiling) | 15% |

**Redemption cost (per item, not flat — your Redeem mockup shows 100–3,000 pts across items)**:
```
canRedeem = totalPoints >= item.pointsCost   // disable button otherwise
```

All of this lives in `OrderRepository`/`RewardRepository`, not scattered across ViewModels.

---

## 9. State & lifecycle management (12 pts)

- **Configuration changes (rotation)**: `ViewModel`s scoped via `by viewModels()` (Fragment) survive this automatically — the `ViewModelStore` is retained across recreation. This alone satisfies the rubric's literal wording ("preserve data across configuration changes"). Same guarantee as v3, unaffected by the Compose→Views change.
- **Process death**: not literally required by the rubric text (it only names configuration changes), but good practice. Treat as a **stretch goal for Day 4, not a must-have** — first thing to cut if the schedule slips. If you do it: anything persisted (cart, orders, points) is already safe via Room/SharedPreferences; only an in-progress, unsaved Details customization would need `SavedStateHandle`.
- **Ephemeral UI-only state** (selected My Orders tab, which bottom sheet is open) — plain Fragment/instance fields are fine here since Fragments backed by the Navigation Component survive normal back-stack navigation; use `onSaveInstanceState(Bundle)` only if you specifically need to survive rotation for something not already in the ViewModel.
- **Explicit `onPause`/`onResume`/`onStop`**: this is actually *simpler* now than the Compose version — since the rubric names these directly, override them straightforwardly in `MainActivity` (or a base Fragment class if you want it per-screen), recording a "last active" timestamp. No `DisposableEffect`/`LocalLifecycleOwner` workaround needed; these are the Activity's own lifecycle methods. Small, cheap, and a clean answer if asked about it in review — keep this regardless of time pressure.

---

## 10. Build order (4 days to July 29)

**Day 1 (today)**
- *Environment + Kotlin sanity check first*: confirm Android Studio/emulator or device works, create a throwaway project with one Fragment and a button that increments a `TextView` counter, get it running. This validates the whole toolchain before you invest real time on top of it.
- Gradle setup, package skeleton, theme (Section 4's palette in `colors.xml`/`themes.xml`), `nav_graph.xml` with all 9 screens as empty placeholder Fragments, `MainActivity` hosting the `NavHostFragment` + `BottomNavigationView`.
- Room entities/DAOs + `Converters.kt` + `AppDatabase` + seed callback for the **relational** data only (Coffee, Cart, Order, OrderItem, RewardTransaction, Favorite, Redeemable). `ProfilePrefs`/`LoyaltyPrefs` (plain SharedPreferences wrappers) + `AppContainer`.
- **Vertical slice**: get *just* Coffee → DAO → seed → repository → `MenuViewModel` → `MenuFragment` showing a real `RecyclerView` grid pulled from Room, running on the emulator, **including the Specials/Classics toggle** (cheap, do it now rather than scheduling it separately — see Section 11). This is also where you teach the Adapter/ViewHolder pattern once, properly. This proves Gradle + KSP + ViewBinding + Navigation Component + Room all work together before you replicate the pattern for the rest.

**Day 2** — Core purchase flow end to end: Menu (already started) → Details/Customize (stepped `SeekBar`s + toggle + stepper + flavor checkboxes + live price + add to cart + `CartPreviewBottomSheet`; **add slider snapping here too**, see Section 11) → Cart (`RecyclerView` rendering + qty steppers + `ItemTouchHelper` swipe-remove + total + checkout). Also stand up Home's basic version (greeting header, bottom nav, required stamp card) *without* the hero card or saved recipes yet — those come Day 3/4.

**Day 3** — Order Success → My Orders (`TabLayout` + status transition, wired to the vine's stamp/rank-up and points together) → Rewards (vine + points + total aggregation) → Redeem Rewards (per-item cost + disable logic) → Profile (view + edit). Then the state/lifecycle pass from Section 9 (`onSaveInstanceState` where actually needed, lifecycle overrides; `SavedStateHandle` only if time allows).

**Day 4** — Layer the remaining bonus features onto the now-working core, in the priority order from Section 11 (Home hero card → rank checkout discount → Favorites/Saved Recipes, **reordered from v3** — see Section 11 note — cut from the end if short on time). Then full manual QA: rotation + backgrounding test, currency consistency check (Section 3), confirm swipe-to-delete actually works (not just the steppers), confirm tapping a Menu card body navigates to Details (not just the "+"), confirm the hero card doesn't re-randomize on rotation, confirm the vine actually resets and rank advances together on the 8th fill.

**Day 5 (July 29)** — Record the demo, write the report (library attributions, the Room+SharedPreferences split rationale, the Home/Menu split rationale, note that RecyclerView/Views satisfy the rubric's terminology directly), clean build artifacts, zip, submit with buffer.

---

## 11. Your bonus features — implementation notes & priority

Priority order if time runs short — cheapest and most self-contained first, most cross-cutting and risky last. **Reordered from v3**: favoriting-with-retention drops below the checkout discount, since it now touches 3 screens' worth of Adapter/ViewHolder/click-listener plumbing instead of 3 composables, making it the more expensive of the two under the Views approach.

1. **Specials/Classics toggle on Menu.** `category: Category` on `CoffeeEntity`, seed some of each, toggle is two `Button`s (or a `ToggleGroup`) that re-filter `CoffeeAdapter`'s backing list and call `notifyDataSetChanged()`. Near-zero risk — build it as part of Menu on Day 1, not as a separate bonus pass.

2. **Slider snapping to 0/25/50/75/100.** `SeekBar` doesn't have Compose's `steps` parameter, but the same effect is easy: `seekBar.max = 4`, and in `onProgressChanged`, map `progress * 25` to the actual sweetness value (display and store the mapped value, not the raw 0-4 progress). Set `android:max="4"` in XML too so a screen-reader/keyboard doesn't imply finer granularity than you support. A few lines, build it while doing Details on Day 2.

3. **Home hero card.** Pick a random coffee — filter to `category == SPECIAL` — with an "Order Now" button navigating to Details. The hero badge copy should read **"Special"** rather than the mockup's "Seasonal Special," to match the category rename (no seasonal/rotation implication — see Section 5). One real gotcha: pick the random coffee **once** (in `HomeViewModel`'s `init`, not in `onCreateView`/`onViewCreated`), and just re-bind the already-chosen value if the Fragment view is recreated — otherwise it re-randomizes on every rotation and the card flickers/changes while the user is just looking at the screen.

4. **Checkout discount from rank.** The rank *tracking* itself (`rankIndex`, badge display) now comes essentially free with the required vine (Section 8) — it's the same increment. What's still genuinely optional is actually *applying* `currentRankTier.discountPercent` at checkout (Section 8's `finalTotal` formula). Pure logic + one price line in `CartFragment` — cheap relative to feature 5 now that both are competing for Day 4 time. Since "User-Defined Features" is one undifferentiated 50-point bucket, a rank badge that doesn't affect the price isn't worth much more than not building this specific piece — the vine/rank tracking underneath it stays either way since that's required.

5. **Favoriting with customization retention.** `FavoriteEntity` stores the full customization snapshot, not just a coffee reference (see Section 5). Heart `ImageButton` on Details toggles it; Home's "Saved Recipes" `RecyclerView` reads from `FavoriteDao`. Tapping a saved recipe item should pre-fill Details with that exact saved config (pass the `Customization` via the navigation `Bundle`) — that's the actual value of this feature, not just a bookmark icon. Under the Views approach this touches an `Adapter` on Home, click-handling on Details, plus the pre-fill logic on Details' inbound args — genuinely the most cross-cutting bonus item now. Cut this first if Day 4 runs short.

---

## 12. Pre-submission polish checklist

- [ ] Currency symbol is consistent across every screen (Section 3)
- [ ] Product images are clean — no baked-in rating/price/"ADD TO CART" text from stock photos (Section 7, Menu row)
- [ ] Swipe-to-delete actually works on Cart rows, independent of the quantity steppers
- [ ] Tapping a Menu card's body (not just the "+" icon) navigates to Details
- [ ] The Home hero card doesn't change on rotation or Fragment view recreation
- [ ] The vine actually resets to 0 *and* `rankIndex` advances together on the 8th fill — not just one or the other
- [ ] Checkout discount reflects the current `rankIndex` at time of purchase, if you kept feature 4
- [ ] Every `RecyclerView` list actually scrolls/recycles correctly with more items than fit on screen (a common first-time Adapter bug is items rendering fine at small counts but breaking on `onBindViewHolder` reuse — test with the full seeded catalog, not 2 items)

---

## 13. Submission checklist

- [ ] `source/` — cleaned of `build/`, `.gradle/`, local caches
- [ ] `StudentID-demo.mp4` (or `demo.txt` with a YouTube link)
- [ ] `StudentID-app.apk` or `.aab` — test-export this by end of Day 2, not deadline day, to catch any build-config surprise early
- [ ] `StudentID-report.pdf` — architecture explanation (MVVM + Views/XML + Room/SharedPreferences split + why), third-party library attributions, the Home/Menu split note from Section 6
- [ ] Everything zipped as `StudentID.zip` with the exact required folder structure
- [ ] Can explain, out loud, how the checkout → order-completion → stamp/points/rank pipeline works end to end
