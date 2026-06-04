# UI Structure

## Android App — Screen Hierarchy

```
SplashActivity (launcher)
├── Animated branding + progress bar (2.5s)
└── Auto-navigate →
    ├── AdminDashboardActivity (if logged in + ADMIN)
    ├── ClientDashboardActivity (if logged in + CLIENT)
    └── LoginActivity (if not logged in)

LoginActivity
├── API-backed credentials verification
└── Navigates to appropriate dashboard

AdminDashboardActivity (Main Admin Screen)
├── ViewPager2 (5 tabs)
│   ├── DashboardContentFragment (Live Stats counters + Pie & Stacked Area Charts)
│   ├── ManageBooksFragment (List + Shimmers + Category/Language Spinner filters)
│   ├── MembersFragment (User Management list + Shimmer loading placeholders)
│   ├── NotificationsFragment (In-App notification list + Mark All as Read)
│   └── ProfileFragment (User Profile + Change Password settings)
├── Navigation links (Sidebar Sheet / bottom navigation tabs)
└── Detail Fragments (pushed on back stack)
    ├── BookInfoFragment (Edit form with dropdown spinners + Delete action)
    ├── UserInfoFragment (Profile editor + Status activate/deactivate toggle + Delete)
    ├── AddBookFragment (Add form with dynamic spinner configurations)
    ├── AddUserFragment (Registration form)
    ├── LendedBooksFragment (Lended records search + Status tabs + Lend Book shortcut)
    ├── LendedBookInfoFragment (Lending data sheet)
    ├── LendBookFragment (Form to issue book copies with date calendars)
    ├── ResourcesFragment (Form to insert third-party libraries and website URLs)
    └── ForgotPasswordFragment (Email validation OTP + instructions)

ClientDashboardActivity (Main Client Screen)
├── ViewPager2 (5 tabs)
│   ├── ClientDashboardContentFragment (Greeting + Cards + Circular Chart + Bar Chart)
│   ├── ClientCatalogFragment (Book Search list + Category/Language spinners)
│   ├── ClientAccountFragment (Lendings list + Return book triggers + Change Password + Profile)
│   ├── ClientHelpFragment (Contact Form to send support tickets)
│   └── ClientNotificationsFragment (Persistent message checklist)
└── Detail Fragments (pushed on back stack)
    ├── ClientBookInfoFragment (Summary info + checkout / reservation forms)
    ├── CheckoutFragment (Lending configuration with date fields)
    ├── ReservationFragment (Personal book reservations log)
    └── ForgotPasswordFragment (Pre-filled reset pages)
```

## Design Language (v4.0)

### Key Components
- **Skeleton Shimmers**: Applied during list loading in Books, Members, and Lended Books tabs. Matching XML loaders override default loading wheels.
- **Dynamic Spinners**: Spinner components bind directly to server categories/languages, or comprehensive defaults.
- **Tonal Icons**: Notifications, active checkouts, and details utilize custom color tinting and rounded background gradients (`icon_background_info_gradient.xml`).
- **ID Shortening**: Shortens all UUID fields to the first 4 characters (e.g. `e3d4...`) inside RecyclerView cells to keep spacing aligned.

### Visual Styles
- **Primary Action Buttons**: Material primary coloration or Blue-to-DarkBlue gradients.
- **Danger Actions**: High contrast solid red with white text (e.g. Delete, Deactivate).
- **Chart themes**: 
  - Admin Visitor Stacked Area uses an **Orange and Green** theme.
  - Client Lending vertical bar chart uses **Primary/Surface Tinting** based on the highest month's value.

## Layout Inventory (Updated)

| Layout | Purpose | Elements / Custom Widgets |
|--------|---------|---------------------------|
| `fragment_book_info.xml` | Book details & editing | Spinners, Save & Delete action buttons |
| `layout_skeleton_book_item.xml`| Book list placeholder loader | Shimmering item boxes |
| `layout_skeleton_member_item.xml`| Member list placeholder loader | Shimmering circular initials and text |
| `item_notification.xml` | Notification list cell | Side-by-side layout, Informational gradient background |
| `fragment_dashboard_content.xml`| Admin Home | `PieChartView`, `StackedAreaChartView` |
| `fragment_client_dashboard_content.xml` | Client Home | `CircularProgressIndicator`, programmatically drawn LinearLayout bar graphs |
| `fragment_client_catalog.xml` | Client Catalog | Search view, spinners, list grid |
| `fragment_checkout.xml` | Borrowing configurations | Calendar dates picker, count selector |
| `fragment_client_account.xml` | Client account & profile | Active lendings recycler view, return triggers |
| `fragment_client_help.xml` | Help ticket form | Contact fields, description editor |
| `fragment_resources.xml` | Admin Resource additions | Resource Name, Email, and URL editors |
| `fragment_lended_books.xml` | Admin Lending lists | Search fields, status chip filters, lend button |

## Navigation Behavior
- **Sync-Back**: When Save, Delete, or Return actions complete inside detail fragments, they invoke callback callbacks to trigger parent fragments (e.g., `ManageBooksFragment`, `LendedBooksFragment`) to refresh from database before popping off the backstack.
- **Wait-for-API**: Operations keep loading indicator animations active and block fields until the server resolves requests.
