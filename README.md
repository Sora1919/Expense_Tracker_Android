# Expense Tracker (Android) — Room Database

A simple Android Expense Tracker app built with **Kotlin** and **Room Database**.  
Users can add, edit, delete, and view daily expenses with **persistent storage**, including a **category** field. The app also supports **category filtering** and **sorting** for a better user experience.

---

## Features

### Core (CRUD)
- Add a new expense (name, amount, date, category)
- Edit existing expenses
- Delete expenses (with confirmation)
- View all expenses in a clean list UI

### Persistence (Room)
- Expenses are saved using **Room (SQLite)**  
- Data is automatically loaded when the app is reopened

### UX Improvements
- **Total Spending** summary card (auto updates)
- **Category dropdown** (predefined list)
- **Filter by Category** (e.g., Food, Transportation, Utilities, etc.)
- **Sort** expenses by:
  - Date (Newest / Oldest)
  - Amount (High → Low / Low → High)
  - Name (A → Z)
- Date input uses **DatePicker** (ensures correct `YYYY-MM-DD` format)
- Input validation for all fields
- Clean spacing + Material UI styling

---

## Screens / Flow
1. Home screen shows:
   - Total spending
   - Expense list
   - Filter + Sort controls
2. Tap **+** to add a new expense
3. Tap an expense item to:
   - Edit
   - Delete
4. Data remains saved after closing and reopening the app

---

## Tech Stack
- **Kotlin**
- **Android Views (XML)**
- **Room Database (KSP)**
- **RecyclerView**
- **Material Components**
- **Coroutines + Flow** (live updates from database)

---

## Data Fields
Each expense includes:
- **Name** (String)
- **Amount** (Double)
- **Date** (String, format: `YYYY-MM-DD`)
- **Category** (String from predefined list)

---

## Validation Rules
- Name must not be empty
- Amount must be a valid number and not negative
- Date must be valid (`YYYY-MM-DD`) and selected via DatePicker
- Category must be selected

---

## How to Run
1. Clone this repository
2. Open in **Android Studio**
3. Sync Gradle (Room uses **KSP**)
4. Run on an emulator or Android device

---

## Project Structure (Main Files)
- `MainActivity.kt` — UI logic, dialogs, filter/sort, Room Flow collection
- `Expense.kt` — Room Entity (Expense model)
- `ExpenseDao.kt` — DAO queries (CRUD + filter/sort queries)
- `AppDatabase.kt` — Room database instance
- `ExpenseAdapter.kt` — RecyclerView adapter
- `dialog_expense.xml` — Add/Edit dialog UI (includes category dropdown)
- `activity_main.xml` — Main screen UI (total + filter/sort + list)

---

## Future Improvements (Optional)
- Search by expense name
- Monthly/weekly analytics
- Charts by category (pie/bar)
- Export expenses to CSV

---

## Author
**Kaung Set Linn**  
(Replace with your name/class info if needed)
