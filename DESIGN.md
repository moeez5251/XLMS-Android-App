# Design System Specification: XLMS

 

## 1. Overview & Creative North Star

The "Creative North Star" for this design system is **XLMS Library Management System**. This system moves away from the sterile, utilitarian feel of traditional database software and toward a high-end editorial experience. It treats the library’s collection not as a list of data points, but as a prestigious gallery of knowledge.

 

To break the "template" look, we employ **Intentional Asymmetry** and **Tonal Depth**. Large, offset display typography creates an authoritative, magazine-like feel, while the UI components use sophisticated layering rather than rigid lines. The aesthetic is "New Academic"—blending the weight of a physical library with the weightless fluidity of modern digital interfaces.

 

---

 

## 2. Colors & Surface Architecture

Our palette uses deep, scholarly blues and slate grays to establish trust, while utilizing a sophisticated "Surface Tier" system to define space.

 

### The "No-Line" Rule

**Explicit Instruction:** You are prohibited from using 1px solid borders to section content. Boundaries must be defined through background color shifts or tonal transitions.

*   *Example:* A sidebar should be `surface_container_low` sitting against a `surface` background. No line separates them.

 

### Surface Hierarchy & Nesting

Treat the UI as physical layers of fine stationery or frosted glass. Use the hierarchy below to create depth:

*   **Base:** `surface` (#f9f9ff) - The canvas.

*   **Secondary Zones:** `surface_container_low` (#f0f3ff) - Used for sidebars or background utility panels.

*   **Primary Content Cards:** `surface_container_lowest` (#ffffff) - Pure white to make book covers and text pop.

*   **Active/Elevated Elements:** `surface_container_high` (#dee8ff) - For hover states or active workspace areas.

 

### The "Glass & Gradient" Rule

To add "soul," primary CTAs and Hero sections should utilize a subtle gradient:

*   **Signature Gradient:** `primary` (#002045) to `primary_container` (#1a365d) at a 135-degree angle.

*   **Glassmorphism:** Use semi-transparent versions of `surface_container_lowest` with a `backdrop-blur` (12px-20px) for floating navigation bars or modals to ensure the UI feels integrated, not "pasted on."

 

---

 

## 3. Typography: The Editorial Voice

We use a tri-font system to balance authority with modern legibility.

 

*   **Display & Headlines (Manrope):** A modern geometric sans-serif that feels architectural. Use `display-lg` (3.5rem) for main dashboard greetings to establish an "Editorial" scale.

*   **Titles & Body (Work Sans):** Chosen for its excellent readability in academic contexts. Use `title-lg` (1.375rem) for book titles.

*   **Data & Labels (Inter):** High-utility typeface for the "dense" parts of the LMS—metadata, ISBNs, and status tags.

 

**Hierarchy Note:** Use high contrast in size. If a headline is `headline-lg`, the supporting text should be `body-md`. This gap creates the "signature" premium look.

 

---

 

## 4. Elevation & Depth

Depth is achieved through **Tonal Layering** rather than traditional structural shadows.

 

*   **The Layering Principle:** Place a `surface_container_lowest` card on a `surface_container_low` background. The difference in luminance provides a "soft lift" that is easier on the eyes than a shadow.

*   **Ambient Shadows:** When an element must float (e.g., a "New Loan" modal), use a shadow tinted with `on_surface` (#121c2c).

    *   *Spec:* `offset-y: 8px`, `blur: 24px`, `opacity: 6%`. Never use pure black shadows.

*   **The "Ghost Border" Fallback:** If a border is required for accessibility, use `outline_variant` (#c4c6cf) at **20% opacity**. This creates a suggestion of a container without breaking the editorial flow.

 

---

 

## 5. Components

 

### Buttons

*   **Primary:** Uses the **Signature Gradient** (`primary` to `primary_container`). `xl` roundedness (0.75rem). Text is `on_primary`.

*   **Secondary:** `surface_container_high` background with `on_secondary_fixed_variant` text. No border.

*   **States:** On hover, primary buttons should shift 2px upward with a subtle ambient shadow.

 

### Input Fields & Search

*   **The "Clean Slate" Input:** Use `surface_container_low` as the background. No border, only a 2px `primary` bottom-bar that animates out from the center on focus. 

*   **Search Bar:** Should be prominent, using `surface_container_lowest` and an `xl` corner radius (0.75rem) to feel like a modern search engine.

 

### Cards & Lists (The "Anti-Grid")

*   **Forbid Dividers:** Do not use lines between list items. Use **Vertical White Space** (`spacing-8` or `spacing-10`) or alternating subtle shifts between `surface` and `surface_container_low`.

*   **Book Cards:** Use `lg` roundedness (0.5rem). The book cover should have a subtle 4% inner-glow to mimic the edge of a physical page.

 

### Signature Component: The "Status Ribbon"

*   Instead of standard labels, use **Tonal Chips**. A "Checked Out" status uses `tertiary_container` (#4f2e00) background with `on_tertiary_fixed` (#2b1700) text. The low-contrast pairing feels more "academic" and less "alert-heavy."

 

---

 

## 6. Do's and Don'ts

 

### Do

*   **Do** embrace negative space. The `spacing-16` (4rem) value is your friend for separating major sections.

*   **Do** use `primary_fixed_dim` for subtle background highlights behind important text.

*   **Do** ensure all interactive elements have a minimum target of 44px, even if the visual "chip" is smaller.

 

### Don't

*   **Don't** use 100% black (#000000) for text. Always use `on_surface` (#121c2c) to maintain the sophisticated slate-blue tone.

*   **Don't** use "Default" 4px border radii. Use the `xl` (0.75rem) for containers and `full` for tags to maintain the modern aesthetic.

*   **Don't** use standard red for "Overdue" warnings. Use `error` (#ba1a1a) paired with `error_container` (#ffdad6) for a more controlled, professional urgency.