PlanSync Linux-friendly build

What changed:
- Replaced fragile relative asset loading with app-root/classpath loading.
- Switched home-screen tool buttons to Linux-safe fallbacks when emoji glyphs are unavailable.
- Moved data/settings file resolution to the app folder, so launching from another working directory does not break saves or icons.
- Added run.sh for Linux.

How to run on Linux:
1. Install Java (JRE/JDK) 17 or newer.
2. Open a terminal in this folder.
3. Run: ./run.sh
   or: java -jar PlanSync.jar

Notes:
- The original bundled jre folder in the source package was Windows-only. This Linux package does not rely on it.
- Your app data is stored in the local data/ folder next to the jar.
