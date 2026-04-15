# PlanSync

PlanSync is a **Time Management and Task Scheduling Application** designed to help users manage workload more fluently and efficiently while keeping track of progress.

Built in **Java**, PlanSync provides a desktop-based interface for organising tasks, tracking deadlines, managing recurring schedules, and using built-in productivity tools such as a **timer**, **stopwatch**, **calendar**, and **time calculator**.

<p align="left">
  <img src="icons/PlanSync.png" alt="PlanSync logo" width="200">
</p>

## Overview

PlanSync was developed as a personal productivity application focused on combining **task management** with **time-planning utilities** in one place.

The application allows users to:

- manage active tasks with deadlines
- keep track of completed tasks
- schedule recurring tasks
- view deadlines visually in a calendar
- calculate time and date differences
- use a built-in countdown timer and stopwatch
- personalise the application with themes, dark mode, and username settings

---

## Features

### Task Management
- Add active tasks with:
  - task name
  - task description
  - deadline date
- Edit existing active tasks
- Delete active tasks
- Mark active tasks as completed
- View how many days remain until a deadline
- See overdue task status automatically

### Completed Task Tracking
- Move finished tasks into a completed tasks section
- Store both:
  - original deadline
  - completion date
- View completed task history
- Delete selected completed tasks
- Clear all completed tasks

### Recurring Task Scheduling
- Add recurring tasks with:
  - name
  - description
  - frequency settings
- Supported recurring frequencies:
  - Daily
  - Weekly
  - Monthly
  - Yearly
- Edit recurring tasks
- Delete recurring tasks
- Display next-occurrence style status information for recurring tasks

### Calendar Integration
- Monthly calendar view
- Monday-first calendar layout
- Highlights dates with active tasks
- Shows task counts per day
- Lets users inspect deadlines visually
- Supports month-to-month navigation

### Productivity Tools
- **Countdown Timer**
  - set custom durations
  - start
  - stop
  - pause/resume behaviour
  - reset timer
  - completion alert sound

- **Stopwatch**
  - start
  - stop
  - reset
  - lap recording
  - live elapsed time updates

- **Time Calculator**
  - add a duration from the current time
  - calculate duration between two times
  - calculate duration between two dates
  - display results in:
    - full breakdown
    - days
    - weeks
    - months
    - years

### Home Dashboard
- Welcome screen with personalised username
- Live clock and current date display
- Quick-access tool shortcuts
- Active task overview
- Sort active tasks by deadline order
- Filter recurring tasks by frequency

### Personalisation & Settings
- Custom username
- Theme selection
- Light mode / dark mode
- Settings saved locally using a properties file

### Data Persistence
- Task and settings data saved locally using text/property files
- Stores:
  - active tasks
  - completed tasks
  - recurring tasks
  - user settings

---

## Screenshots

### Home Dashboard
![Home Dashboard](screenshots/HOME.png)

### Timer
![Timer](screenshots/TIMER.png)

### Stopwatch
![Stopwatch](screenshots/STOPWATCH.png)

### Time Calculator
![Time Calculator](screenshots/TIME_CALCULATOR.png)

### Calendar
![Calendar](screenshots/CALENDAR.png)

### Active Tasks
![Active Tasks](screenshots/ACTIVE_TASKS.png)

### Recurring Tasks
![Recurring Tasks](screenshots/RECURRING_TASKS.png)

### Completed Tasks
![Completed Tasks](screenshots/COMPLETED_TASKS.png)

### Settings
![Settings](screenshots/SETTINGS.png)

---

## Tech Stack

- **Language:** Java
- **GUI Framework:** Java Swing / AWT
- **Architecture Style:** MVC-style separation using controllers, models, and views
- **Storage:** Local file-based persistence (`.txt` and `.properties` files)

---

## Project Structure

```text
PlanSync/
│
├── Main.java
├── controller/
├── model/
├── views/
├── components/
├── modelTerminal/
├── data/
└── README.md
