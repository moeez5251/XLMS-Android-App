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
├── API-backed authentication
└── navigates to dashboard

AdminDashboardActivity (main admin screen)
├── ViewPager2 (5 tabs)
│   ├── DashboardContentFragment (Live Stats + Custom Charts + Recent Activity)
│   ├── ManageBooksFragment (List + Shimmers + Dynamic Filters)
│   ├── MembersFragment (User Management + Shimmers)
│   ├── NotificationsFragment (In-App List + Read All)
│   └── ProfileFragment (Real Data + Change Password)
├── Detail Fragments (pushed on back stack)
    ├── BookInfoFragment (Edit form with Spinners + Delete)
    ├── UserInfoFragment (Profile edit + Status toggle + Delete)
    ├── AddBookFragment (Form with dynamic options)
    ├── AddUserFragment (Registration form)
    └── ForgotPasswordFragment (Pre-filled email + Send instructions)

Custom Components
├── PieChartView: Real-time book availability visualization
└── StackedAreaChartView: 12-month visitor activity chart
```

## Design Language (v3.0)

### Key Components
- **Skeleton Shimmers**: Used in Books and Members tabs to replace traditional progress bars. Multi-item placeholders match the final content structure.
- **Dynamic Spinners**: Input forms (Book Info, Add Book) use server-synced or comprehensive hardcoded lists for Languages and Categories.
- **Tonal Icons**: Notifications use an informational **info icon** (`ic_help`) with a **light blue gradient** background for a modern, non-alert look.
- **ID Shortening**: Lists display only the first 4 characters of UUIDs (e.g., `8f2a...`) to maintain a clean, table-like appearance.

### Visual Styles
- **Primary Action Buttons**: Blue-to-DarkBlue gradients.
- **Danger Actions**: Solid red background with white text (e.g., Delete Book/User).
- **Chart theme**: Visitor charts use an **Orange and Green** high-contrast theme.

## Layout Inventory (Updated)

| Layout | Purpose | New Elements |
|--------|---------|--------------|
| `fragment_book_info.xml` | Book detail/edit | Vertical Save/Delete buttons, Spinners |
| `layout_skeleton_book_item.xml`| Book list loader | Shimmering placeholders |
| `layout_skeleton_member_item.xml`| Member list loader | Shimmering placeholders |
| `item_notification.xml` | Notification card | Side-by-side Title/Time layout |
| `fragment_dashboard_content.xml` | Admin Home | Legend-backed Stacked Area Chart |
| `icon_background_info_gradient.xml`| Drawable | Light blueish info icon background |

## Navigation Behavior
- **Sync-Back**: After a **Save** or **Delete** action in a detail fragment, the parent list (Books or Members) automatically re-fetches its data from the server before the detail fragment closes.
- **Wait-for-API**: Detail screens (Add Book/User) stay open with a loading state until the API response is confirmed.
