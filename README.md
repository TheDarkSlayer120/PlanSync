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

## Download and Run

Download the correct release from the [Releases](../../releases) page:

- **Windows:** `PLANSYNC-Win.zip`
- **macOS:** `PLANSYNC-Mac.zip`
- **Linux:** `PLANSYNC-Linux.zip`

After downloading, **extract the ZIP file first**, then follow the steps for your platform.

### Windows

The Windows release includes a bundled Java runtime, so you do **not** need to install Java separately.

1. Download `PLANSYNC-Win.zip`
2. Right-click the ZIP file, select **Properties**, and click or tick **Unblock** if that option appears
3. Extract the ZIP
4. Open the extracted folder: `PLANSYNC-Win/PlanSync/`
5. Double-click **`PlanSync.exe`**

#### If Microsoft Defender / SmartScreen shows “Windows protected your PC”

If Windows shows a warning when opening `PlanSync.exe`:

1. Click **More info**
2. Click **Run anyway**

If the file is still blocked:

1. Right-click **`PlanSync.exe`**
2. Select **Properties**
3. Click or tick **Unblock**
4. Click **Apply**
5. Open **`PlanSync.exe`** again

> Only do this if you downloaded the file from this repository’s official Releases page and trust the source.

### macOS

The macOS release requires **Java 17 or newer**.

1. Download `PLANSYNC-Mac.zip`
2. Extract the ZIP
3. Open the extracted folder: `PLANSYNC-Mac/PlanSync/`
4. Double-click **`run.command`**

If macOS blocks it the first time:

- Right-click **`run.command`** and choose **Open**
- Or run it from Terminal:

```bash
chmod +x run.command
./run.command
