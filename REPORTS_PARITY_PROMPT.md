# PManager reports parity prompt

Copy everything below the line into Claude (or Cursor Agent). Attach / open the listed folders so it can read the web grids and PHP.

---

You are a senior full-stack engineer (Android Java + XML + PHP/MySQL). Implement **web-parity reports** in the PManager Android app so every report screen queries, filters, and displays the same data the Ext JS web system shows — then let the user **Download PDF** or **Share** that PDF through the Android share sheet (Email, WhatsApp, Drive, etc.).

This is **not** a visual restyle. It is a product feature: match the web reports’ data contracts, columns, filters, totals, and print documents.

## Codebases (read these first, do not invent APIs)

**Android app (edit this)**  
`/Users/kangogojoel/StudioProjects/3Modern-PropertyManager`  
Package `com.threemsystems.rentmanager`. Java + XML. AGP 8.x. Volley + `FormRequest`. API host is `http://3modernsystems.co.ke/threepmobileserver/` in `Config.java`.

**Web frontend (source of truth for UX and columns)**  
`/Users/kangogojoel/StudioProjects/3modernsystems-server/threepmanager/app/`  
Especially:
- `view/propertiesGrid.js`
- `view/propertyUnitsgrid.js`
- `view/tenantsGrid.js`
- `view/tenantsInvoiceGrid.js`
- `view/tenantsPaymentsGrid.js`
- `view/tenantstatementGrid.js`
- `view/expenditureGrid.js`
- matching stores in `app/store/*.js`

**Web PHP (how the desktop app actually loads grids)**  
`/Users/kangogojoel/StudioProjects/3modernsystems-server/threepmanager-server-src/`  
Stores call `server/list_*.php` with **GET** params and read JSON `{ "items": [ ... ] }`.

**Mobile PHP (what the Android app currently calls — you may change these files, then deploy)**  
`/Users/kangogojoel/StudioProjects/3modernsystems-server/threepmobileserver-src/`  
Most `select_*.php` scripts use **`$_POST`** (some also need query-string because of how the app sends params). JSON is `{ "result": [ ... ] }`. TCPDF printers already exist here: `printPaymentReceipt.php`, `printTenantPayments.php`, `printTenancyStatement.php`, `printTenancyDebts.php`, `printTenancyHistory.php`, `printTenantsInvoices.php`. Copy any missing printers from `threepmanager-server-src` (e.g. `printExpenses.php`, `printTenantsInvoiceItems.php`) into the mobile server and keep TCPDF includes as `$_SERVER['DOCUMENT_ROOT'].'/tcpdf/...'`.

**Do not** rewrite `db.php` credentials. **Do not** commit `db.php`. After PHP edits, say exactly which files to `scp` back to `/var/www/html/threepmobileserver/` on `212.71.251.143`.

## How the web system loads a report (you must replicate this loop)

1. User opens a grid tab (PROPERTY, UNITS, TENANCY, RENT INVOICES, RENT PAYMENT, RENT STATEMENT, EXPENDITURE).
2. Toolbar combos load lookup stores:
   - Properties: `list_properties.php` with `owner_id` = session `idNo` (and optional `property_type`).
   - Units: `list_propertyunits.php` with `property_code`.
   - Tenants: `list_tenants.php` / tenant id-name lists with `unit_code` or owner.
3. Dates are `Y-m-d`. Changing From, To, property, unit, tenant, or type **reloads the grid store** with the same param names the PHP function expects.
4. The grid binds **every visible column** from the JSON row (plus computed money format `# ,##0.00` and a **TOTAL** summary row).
5. Actions open a TCPDF URL in a new window, e.g.  
   `server/printTenantPayments.php?tenantid=&startDate=&endDate=&pcode=&ucode=`  
   Receipts need a selected row: `printPaymentReceipt.php?paymentid=&ownerid=`.

The Android app already has the same seven report activities (`Reports.java` tiles → `Holder.Property|Units|Tenancy|Invoices|Payments|Statements|Expenditure`). They currently show a **thin** subset of columns, sometimes wrong JSON keys, and “Print/Export” only shares CSV. Replace that with web-parity lists + PDF download/share.

## Non-negotiable Android rules

- Stay on **Java + XML**. No Compose, no Kotlin unless a tiny file is unavoidable.
- Keep activity class names. You **may add** view IDs and XML components. If you change a row layout, update `SimpleAdapter` `from`/`to` in the same change. Unique header IDs (`hdrCount`, `hdrDate`, …) must not collide with row IDs.
- Keep `FormRequest` for list/select calls (PHP on this host often reads `$_GET`; `FormRequest` copies `getParams()` onto the query string). For PDF bytes, use a dedicated download (HttpURLConnection or Volley `InputStream`) — do not parse PDF as JSON.
- Session: `SessionManager.get(ctx).getOwnerId()` is the web’s `idNo` / `owner_id` / `ownerid`.
- Dates: `DateUi.bindPicker` writes ISO `YYYY-MM-DD`. Default From = one month ago, To = yesterday (already used on payments/invoices/expenditure).
- Spinners: `item_spinner.xml` / `item_spinner_dropdown.xml`, `ReportSupport.spinnerAdapter` / `selected()`. Empty filter ids stay `""` meaning All.
- Empty states: `@android:id/empty`. TOTAL/BALANCE footer rows only when there is at least one real row.
- UI: existing teal PManager styles (`page_background`, `bg_filter_field`, `Widget.PManager.*`). Filter strip stays a **card**, not a broken HorizontalScrollView.
- Money: `ReportSupport.money`. Tenant names: `ReportSupport.firstTwoNames` only when the web also shortens names; otherwise show the full `tenant_name`.
- Networking errors: `VolleyErrors.show`. Do not crash on missing JSON keys (`optString`).
- `minSdk 23`. FileProvider for sharing PDFs. Add `res/xml/file_paths.xml` if missing.

## Actions UX (every report screen)

Replace the current action spinner items (Print Receipt, Export excel, Email, …) with exactly:

1. **Select action** (placeholder, no-op)
2. **Download PDF**
3. **Share**

Behavior:

- **Download PDF**: GET the matching TCPDF PHP with the **current filters** (and selected row when the web requires it). Save under `getExternalFilesDir(DIRECTORY_DOWNLOADS)` or app files, notify with a Snackbar “Saved to …”. Optional: `DownloadManager` + notification. Filename like `payments_2026-08-14_2026-09-13.pdf`.
- **Share**: download the same PDF, then `Intent.ACTION_SEND` with `application/pdf` and a `FileProvider` URI. The user picks WhatsApp, Gmail, Drive, etc. Do **not** invent a custom email form. Do **not** share CSV as the primary action.
- If the web requires a selected row (payment receipt, credit note) and nothing is selected, Snackbar: “Select a row first.” Long-press or tap a list row to mark it selected (highlight). Download/Share of the **list** PDF does not require a row.
- Journal Entry on Statements: keep a separate button or list item **only if** it already exists as navigation to `JounalEntry` — do not hide it inside Download/Share.
- Never silently no-op.

## PHP / mobile-server work

Update files under `threepmobileserver-src` (and document scp paths) so the app can:

1. **List endpoints return the same fields the web grids show**, not a truncated subset. Prefer enhancing `select_*.php` (keep `{ "result": [] }` for the app) by joining the same tables the web `list_*.php` uses (`property_name`, `unit_name`, `mpesacode`, `pay_refno`, `pay_mode`, `payeeExp`, `expense_desc`, `servicefee`, `totalamount_invoiced`, `property_tel`, `paymentChannel`, `short_name`, `vehicle_regno`, `rent_deposit_amount`, `active`, debit/credit already derived from `description` on the client if PHP returns `amount`/`balance`/`description`).
2. Accept **both** `$_GET` and `$_POST` for every filter (`$p = $_GET['x'] ?? $_POST['x'] ?? ''`) so FormRequest and browsers both work.
3. Treat empty property/unit/tenant as “all for this owner”, matching web `flag=dates` / owner-level queries.
4. **PDF**: each report has a print URL that uses current filters. If a printer exists only in `threepmanager-server-src`, copy it into `threepmobileserver-src` and point TCPDF the same way. Return `Content-Type: application/pdf` (already true for TCPDF). Do not HTML-wrap the PDF.
5. `select_properties.php` currently requires `p_type` exactly; when `p_type` is `All` or empty, return all properties for `owner_id` (web does this). Do not return placeholder empty-name rows.
6. Keep SQL scoped to `owner_identifier` / session owner. Do not weaken auth.

## Report-by-report spec

Implement **all seven**. Expand XML list rows (horizontal scroll or two-line cards) so extra columns fit on a phone. Header row in the activity layout must match.

### 1. Property — `Holder.Property` + `activity_property.xml` + `list_properties.xml`

Web: `propertiesGrid.js` + `list_properties.php` (`owner_id`, `property_type`).

Show: #, Owner ID, Property name, Short name, Description, Payment details (`paymentChannel`), Contact (`property_tel`), Status (`property_status`). Filter: category Cooperate / Individual / All.

PDF: if no dedicated printer, generate a clean TCPDF `printProperties.php` (owner + type) **or** a well-formatted on-device PDF of the loaded rows. Prefer server PDF for consistency.

### 2. Units — `Holder.Units` + `activity_units.xml` + `list_units.xml`

Web: `propertyUnitsgrid.js` + `list_propertyunits.php`.

Show: #, Unit name, Status, Deposit (`unit_rentdeposit_amount`), Rent (`unit_rent_amount`), Elect. meter, Water meter, Service fee. Filters: Property, Status (All/Occupied/Vacant), Unit, name search (already sketched).

PDF: `printTenancyHistory` is tenancy, not units — add `printPropertyUnits.php` from the loaded query or copy web if it exists. Same filters.

### 3. Tenancy — `Holder.Tenancy` + `activity_tenancy.xml` + `list_tenants.xml`

Web: `tenantsGrid.js` + `list_tenants.php`.

Show: #, ID No (`tenant_identifier`), Names, Tel, Unit (name not only code), Vehicle, Rent, Deposit, Status (`active`). Filters: Property, Unit, Tenant, optional date from/to (`dateflag=YES` when both dates set).

PDF: `printTenancyHistory.php?pcode=&unitcode=&startdate=&enddate=&tenantid=&reportype=` (already on mobile server).

### 4. Invoices — `Holder.Invoices` + `activity_invoices.xml` + `list_invoices.xml`

Web: `tenantsInvoiceGrid.js` + `list_tenantinvoices.php`.

Show: #, Month (`invoice_month`), ID No, Names, Unit name, Rent (`rent_payable`), Services (`servicefee`), Invoiced (`totalamount_invoiced`). Filters: From, To, Tenant (All + list by owner).

PDF: `printTenantsInvoiceItems.php` (copy from web server if missing on mobile) with `tenantid`, `startdate`, `enddate`. Enhance `select_tenantinvoices.php` joins so unit/service/total fields are present.

### 5. Payments — `Holder.Payments` + `activity_payments.xml` + `list_payments.xml`

Web: `tenantsPaymentsGrid.js` + `list_tenantpayments.php` (`pcode`, `ucode`, `id`, `ownerid`, `startdate`, `enddate`, `flag`).

Show: #, Date, M-Pesa code, Ref.No, ID No, Property, Unit, Tenant, Pay mode, Amount (formatted), optional Validity. Keep TOTAL row.

Filters: From, To, Property, Unit (load on property change, not only on touch), Tenant (load on unit change).

PDF list: `printTenantPayments.php?tenantid=&startDate=&endDate=&pcode=&ucode=`  
PDF one row: `printPaymentReceipt.php?paymentid=&ownerid=` — Download/Share of receipt when a payment row is selected; otherwise the list PDF.

Wire `select_tenantpayments.php` to return `mpesacode`, `pay_refno`, `pay_mode`, `property_name`, `unit_code`, `payment_id`.

### 6. Statements — `Holder.Statements` + `activity_statements.xml` + `list_statements.xml`

Web: `tenantstatementGrid.js` + `list_tenantstatement.php`. Type combo: All / Payments / Unpaid / Invoices / Credit Note / Bounced. Columns: Date (`action_date`), Tenant, Description, Debit/Invoices vs Credit/Payments (split by `description` first word, same rules as the JS renderer), Balance.

Mobile `select_tenantstatement.php` uses `id`, `start_date`, `end_date`, `stype` plus property/unit in the Android caller (`pcode`, `ucode`) — make PHP honor property/unit when tenant id is empty (owner-wide), matching web.

PDF: `printTenancyStatement.php?tenantid=&startdate=&enddate=&reportype=&ownerid=` (tenant required for this printer — if no tenant selected, Snackbar). Debts: `printTenancyDebts.php?ownerid=&pcode=&enddate=&debttype=` when type is Unpaid/Debt.

Reload when **any** filter changes (property, unit, tenant, dates, type).

### 7. Expenditure — `Holder.Expenditure` + `activity_expenditure.xml` + `list_expenditure.xml`

Web: `expenditureGrid.js` + `list_expenditurepayment.php`.

Show: Date, Ref.No, MM code, Payee (`payeeExp`), Pay mode, Amount, Desc, Property, Unit. TOTAL on amount.

PDF: copy `printExpenses.php` onto the mobile server (`tenantid`, `startDate`, `endDate`, `pcode`, `ucode`, `oid`).

## Shared implementation (do not copy-paste 7 downloaders)

Add something like `ReportPdf.java`:

- `download(Activity, urlWithQuery, filename, callback File)`
- `share(Activity, File)` via FileProvider
- `saveToDownloads(Activity, File)`
- Timeout 60s, no cache, auth cookies not used (same as today)

Add `ReportFilters` helper to collect spinner ids + dates so print URLs stay in sync with the last successful list query.

Keep `ReportSupport.bindList` / `fillSpinner` / `FormRequest`.

## Manifest / Gradle

- `FileProvider` with `@xml/file_paths` (`files-path` + `external-files-path`).
- If you add iText/PDFBox, prefer **server TCPDF** instead. No new PDF library unless a printer truly cannot be copied.

## Out of scope unless trivial

Bills, shareholders, capital, accounts P&L, entry recordings, water metering — web has extra tabs. Do **not** build those screens unless you finish the seven core reports first.

Do not change login, data-entry save forms, or SMS except if a shared helper needs it.

## Done when

- Each of the seven reports shows the web columns for a real owner who has data on `3modernsystems.co.ke`.
- Changing filters refetches and the list matches the web grid for the same owner/dates/property/unit/tenant.
- Download PDF opens a valid PDF (not HTML, not empty).
- Share offers WhatsApp/Gmail/etc. and sends that PDF.
- Empty filters still show owner-wide data.
- `./gradlew assembleDebug` succeeds.
- You list every PHP file to upload to `/var/www/html/threepmobileserver/`.

Work through the reports in this order: Payments, Statements, Invoices, Expenditure, Tenancy, Units, Property. Read the matching `.js` + `list_*.php` + `select_*.php` + `print*.php` before editing each screen.
