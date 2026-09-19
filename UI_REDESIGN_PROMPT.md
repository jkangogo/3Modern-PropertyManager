# PManager UI redesign prompt

Copy everything below the line into Claude.

---

You are a senior Android UI/UX engineer. Redesign the **PManager** app (`com.threemsystems.rentmanager`) so it looks like a premium real-estate / property-management product. This is a **Java + XML** Android app (not Compose, not Kotlin). Keep all business logic, networking, PHP API calls, activity class names, and existing view IDs so Java `findViewById` still works.

## Brand / theme (match LitShelf exactly)

Use LitShelf’s teal palette as the system identity:

| Token | Hex | Role |
|---|---|---|
| Primary | `#018786` (`teal_700`) | App bars, primary buttons, key icons, FAB, selected states |
| Primary dark | `#00695C` (`teal_800`) | Status bar, pressed states, nav header end |
| Secondary / accent | `#03DAC5` (`teal_200`) | Highlights, chips, icon tints, floating accents |
| Teal gradient (nav/hero) | `#4DB6AC` → `#009688` → `#00695C` | Hero headers, login hero, drawer/header |
| On-primary | `#FFFFFF` | Text/icons on teal |
| Surface | `#FFFFFF` | Cards, sheets |
| Page background | `#F4F7F7` (soft off-white, not stark white) | Screen background |
| Text primary | `#1A1A1A` | Titles, values |
| Text secondary | `#4C4E52` (`dark_gray`) | Captions, hints |
| Divider / border | `#D3D3D3` (`light_gray`) | Inputs, card outlines |
| Destructive | a restrained red, used only for Logout / Exit | |

Update `colors.xml`, `themes.xml`, and `values-night/themes.xml`.

Theme parent: `Theme.MaterialComponents.DayNight.NoActionBar` (`Theme.ThreePManager`).

Set:

- `colorPrimary` = `@color/teal_700`
- `colorPrimaryVariant` = `@color/teal_800`
- `colorSecondary` = `@color/teal_200`
- status bar = `colorPrimaryVariant`
- opt out of edge-to-edge on API 35 if needed (`windowOptOutEdgeToEdgeEnforcement`)

Do **not** keep the current purple Material defaults (`purple_500` / `purple_700`). Do **not** keep the old blue `#125688` as the live brand.

## Product context

PManager is a landlord/property-owner app for Kenyan rental operations:

- Login / Register / Change password
- Home: Data Entry, Reports, Change Password, Log Out
- Data Entry: New Property, New Unit, New Tenant, New Payment, New Invoice, Journal Entry, New Expense, Send Message, Unit Service
- Reports: Properties, Units, Tenancy, Invoices, Payments, Statements, Expenditure
- Forms: add property/unit/tenant/payment/invoice/expense/journal/message
- List screens currently use old green-on-black `TableLayout` rows (`#00ff00` on `#000000`) — replace those with modern cards/rows

App display name: **PManager**. Toolbar titles can stay, but use sentence case where it looks more professional (`Property Manager`, `Data capture`, `Reports`).

## Design direction

Make it feel like a 2026 PropTech app (think a clean mix of Airbnb host tools + a professional property ERP), not a 2014 government form.

Visual rules:

- Plenty of whitespace, 16–24dp screen padding, 12–16dp card radius, 8dp button radius (LitShelf used 8dp buttons and 10–12dp input/card radius).
- Elevation 2–8dp; no heavy shadows.
- Material Components: `MaterialCardView`, `MaterialButton`, `TextInputLayout` + `TextInputEditText`, `MaterialToolbar`.
- Inputs: white fill, 10dp corners, 1.5–2dp teal stroke (like LitShelf `edit_textbg.xml`). Password fields get `endIconMode="password_toggle"` tinted teal.
- Primary buttons: filled teal (`#018786`), white 14–16sp medium text, 48dp min height, 8dp corners.
- Secondary buttons: outlined teal or text buttons.
- Destructive (Logout / Exit): outlined or text, not the same weight as primary.
- Typography: one family, clear hierarchy (screen title 20–22sp medium, section 14sp secondary, body 16sp).
- Lists: white cards on `#F4F7F7`, teal accent bar or icon, dark text, subtle divider. Kill green-on-black tables.
- Empty / loading / error states should look intentional.

## Navigation tiles (this is the signature of the redesign)

Replace the old 195×80dp `Button` grids (`@drawable/mybutton`) on Home, Data Entry, and Reports with a **2-column responsive grid** of **custom ImageButtons / icon tiles**.

Each tile:

- `MaterialCardView` (or ImageButton inside a card), ~16dp radius, white surface, light elevation
- A **custom vector icon that is obviously real-estate / property management**, tinted `#018786`
- Short label under the icon, 13–14sp, dark gray, not ALL CAPS
- Equal width via `GridLayout` or weighted `LinearLayout` — **no HorizontalScrollView of fixed 195dp buttons**
- Pressed state: slight teal wash (`#E0F2F1`) or hairline `#018786` stroke

Suggested icon language (create XML vectors in `res/drawable/`; do not depend on missing PNGs):

| Screen | Tile | Icon idea |
|---|---|---|
| Home | Data entry | clipboard + building |
| Home | Reports | bar chart + house |
| Home | Change password | shield / key |
| Home | Log out | door / logout |
| Data entry | New property | apartment building + plus |
| Data entry | New unit | door / floor plan |
| Data entry | New tenant | person + home |
| Data entry | New payment | receipt / mobile money |
| Data entry | New invoice | document + KES |
| Data entry | Journal entry | ledger |
| Data entry | New expense | wrench / invoice-out |
| Data entry | Send message | chat / SMS |
| Data entry | Unit service | tools / water-electric |
| Reports | Property | city buildings |
| Reports | Units | grid of rooms |
| Reports | Tenancy | lease / handshake-home |
| Reports | Invoices | stacked documents |
| Reports | Payments | check / wallet |
| Reports | Statements | statement / timeline |
| Reports | Expenditure | pie chart / outflow |

Login / Register:

- Teal toolbar or compact hero band with app mark + “PManager”
- Centered card, Material text fields, primary Login, secondary Register, tertiary Exit
- Keep IDs: `etusername`, `etPassword`, `btnLogin`, `btnRegister`, `btnCancel`, `progress`, `copyrightTV`

Home:

- Optional modest hero (existing `@drawable/residential` can stay if present, but overlay a teal scrim and do not let it dominate)
- 2×2 tile grid
- Keep IDs: `btnDataentry`, `btnReports`, `btnresetPassword`, `btnExit`, `copyrightTV2`

If you change a `Button` into a card/`ImageButton`/`MaterialButton`, **keep the same ID** and update Java only if the type changes (then change the field type and `findViewById`). Prefer keeping them as `MaterialButton`/`Button` with `app:icon` so Java `Button` fields still compile.

## Hard constraints

1. Do not change package name, applicationId, activity names, PHP endpoints, or `Config.getSERVERURL()`.
2. Preserve every existing view ID used in Java. Search before renaming.
3. UI-only: no new features, no auth rewrite, no Compose migration.
4. Min SDK 23, compile/target 35. Use Material Components already in the project.
5. Create missing drawables as **vector XML**, not huge bitmaps.
6. Night theme: same teal brand, dark surfaces, readable contrast.
7. Do not break SMS, Volley, or PutData flows in `MessageActivity` and form screens.
8. After layouts, fix any Java type mismatches from Button → MaterialButton / ImageButton.
9. Keep copyright footers, restyle them as quiet 12sp secondary text.

## Implementation order

1. `colors.xml` + `themes.xml` + night theme
2. Shared drawables: primary button, outlined button, input background, tile background, vectors
3. Login, Register, Reset password
4. Home (`activity_main.xml`)
5. Data Entry + Reports grids
6. All add/form screens (toolbar + Material fields + primary Save)
7. All list/report screens (replace green TableLayouts with card rows)
8. Compile check: every `findViewById` still resolves

## Quality bar

When finished, the app should look like one product: teal LitShelf brand, property-management iconography, consistent cards/type/spacing, no leftover purple theme, no 195dp green gradient buttons, no green-on-black tables. A landlord should immediately understand each tile.

Start by reading layouts and Java `findViewById` usage, then implement the theme and Home/Data Entry/Reports tiles first.
