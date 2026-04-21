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
├── Email + Password fields
├── Password visibility toggle
├── Remember Me switch
├── "Sign Up" link → SignUpActivity
├── "Forgot Password" link → ForgotPasswordActivity
└── Login → saves session → navigates to dashboard

SignUpActivity (hosts 3-step flow)
├── SignUpFragment → EmailVerificationFragment → SignUpSuccessFragment
└── Success → back to LoginActivity

ForgotPasswordActivity
├── Email input
└── Sends reset request (toast confirmation, no actual API call)

AdminDashboardActivity (main admin screen)
├── ViewPager2 (5 tabs)
│   ├── DashboardContentFragment
│   ├── ManageBooksFragment
│   ├── MembersFragment
│   ├── NotificationsFragment
│   └── ProfileFragment
├── BottomNavigationView (5 items)
├── Slide-out Bottom Sheet (7 nav links)
├── Notification PopupWindow
└── Detail Fragments (pushed on back stack)
    ├── BookInfoFragment
    ├── LendBookFragment
    ├── UserInfoFragment
    ├── LendedBooksFragment
    ├── AddBookFragment
    ├── AddUserFragment
    ├── ForgotPasswordFragment
    └── ResourcesFragment

ClientDashboardActivity (main client screen)
├── DrawerLayout (root)
│   ├── AppBarLayout (hamburger, title, bell, avatar)
│   ├── FrameLayout (mainContentFrame)
│   │   └── ClientDashboardContentFragment (Welcome + metrics + charts)
│   ├── BottomNavigationView (6 tabs: Dashboard, Catalog, Account, Search, Help, Exit)
│   └── NavigationView (sidebar drawer with 5 items + logout)
└── Detail fragments (stubbed)
    ├── ClientCatalogFragment (coming soon)
    ├── ClientAccountFragment (coming soon)
    └── ClientSearchFragment (coming soon)
```

## Layout Files (~38 total)

### Activity Layouts
| Layout | Description |
|--------|-------------|
| `activity_splash.xml` | Splash screen with logo, branding content, animated progress bar |
| `activity_login.xml` | Login form with email, password, remember me, sign up link |
| `activity_admin_dashboard.xml` | Main admin container with ViewPager2, bottom nav, bottom sheet overlay |
| `activity_client_dashboard.xml` | Main client container with DrawerLayout, AppBar, BottomNav, NavigationView |
| `activity_forgot_password.xml` | Password recovery email input |
| `activity_sign_up.xml` | Container for signup fragments |

### Fragment Layouts
| Layout | Description |
|--------|-------------|
| `fragment_dashboard_content.xml` | Admin: Stats cards, bar chart container, activity feed |
| `fragment_client_dashboard_content.xml` | Client: Welcome, metric cards, 12-month bar chart |
| `fragment_manage_books.xml` | Admin: Search bar, filter chips, RecyclerView for books |
| `fragment_members.xml` | Admin: Search bar, filter chips, RecyclerView for members |
| `fragment_notifications.xml` | RecyclerView for notifications, clear/refresh buttons |
| `fragment_profile.xml` | Profile header, password change form |
| `fragment_book_info.xml` | Full book details display |
| `fragment_lend_book.xml` | Lending form with spinners, date picker |
| `fragment_user_info.xml` | User profile view/edit toggle |
| `fragment_lended_books.xml` | Lent books list with filters |
| `fragment_add_book.xml` | Book addition form with spinners |
| `fragment_add_user.xml` | User creation form with role radio buttons |
| `fragment_forgot_password.xml` | In-dashboard password reset |
| `fragment_resources.xml` | Placeholder "coming soon" |
| `fragment_placeholder.xml` | Generic centered text on solid background |
| `fragment_sign_up.xml` | Registration form |
| `fragment_email_verification.xml` | 6-digit OTP input with auto-focus |
| `fragment_sign_up_success.xml` | Success confirmation screen |

### Popup/Reusable Layouts
| Layout | Description |
|--------|-------------|
| `popup_notifications.xml` | Notification preview list for PopupWindow |
| `bottom_sheet_nav.xml` | Admin sidebar navigation with 7 links |
| `nav_header_client.xml` | Client drawer header (avatar + name + role) |
| `card_book.xml` | Book card item for RecyclerView |
| `card_member.xml` | Member card item |
| `card_notification.xml` | Notification card with type icon |
| `card_lended_book.xml` | Lent book record card |
| `metric_card_item.xml` | (Placeholder) reusable metric card for client |

## Design System (from DESIGN.md)

### Typography
| Purpose | Font | Usage |
|---------|------|-------|
| Display/Headlines | **Manrope** | Dashboard greetings, large titles (3.5rem) |
| Titles/Body | **Work Sans** | Book titles, body text (1.375rem) |
| Data/Labels | **Inter** | Metadata, ISBNs, status tags |

### Color Palette
| Token | Value | Usage |
|-------|-------|-------|
| `surface` | #f9f9ff | Base canvas |
| `surface_container_low` | #f0f3ff | Sidebars, utility panels |
| `surface_container_lowest` | #ffffff | Primary content cards |
| `surface_container_high` | #dee8ff | Hover states, active areas |
| `primary` | #002045 | Primary buttons, accent |
| `primary_container` | #1a365d | Gradient end color |
| `on_surface` | #121c2c | Text color (never pure black) |
| `outline_variant` | #c4c6cf | Ghost borders (20% opacity) |
| `error` | #ba1a1a | Overdue warnings |
| `error_container` | #ffdad6 | Error backgrounds |
| `tertiary_container` | #4f2e00 | Status chips |

### Key Design Rules
- **No-Line Rule**: No 1px borders; use tonal layering instead
- **Signature Gradient**: Primary → Primary_container at 135° for CTAs
- **Glassmorphism**: Semi-transparent surfaces with backdrop-blur for floating elements
- **Status Ribbons**: Tonal chips instead of standard labels
- **Anti-Grid**: Vertical white space instead of dividers between list items
- **Rounded Corners**: xl (0.75rem) for containers, full for tags
- **Shadows**: Tinted with `on_surface`, 8px offset, 24px blur, 6% opacity

## Animation Files (~14 total)
- `splash_progress_sweep.xml` — Progress bar sweep on splash
- `slide_right_in.xml` — Detail fragment enter animation
- `slide_right_out.xml` — Detail fragment exit animation
- `slide_left_in.xml` — Back transition
- `slide_left_out.xml` — Back transition
- Additional fade/scale animations for various UI transitions

## Fonts (3 families)
- **Manrope**: `.ttf` + `.xml` font family definition
- **Work Sans**: `.ttf` + `.xml`
- **Inter**: `.ttf` + `.xml`

## Menu Files
- `bottom_nav_menu.xml` — 5 items (Dashboard, Books, Members, Alerts, Profile)
- `nav_drawer_menu.xml` — 7 items (Dashboard, Resources, Manage Books, Lended Books, Members, Notifications, Profile)

## Navigation Behavior
- Bottom nav switches ViewPager2 tab
- Sidebar nav links trigger same tab switch or open specific detail screens
- Detail screens push onto Fragment back stack with slide animation
- Back button: dismisses popup → closes drawer → pops back stack → goes to first tab → exits app
- Notification bell shows PopupWindow with 3 latest notifications + "View All" link
