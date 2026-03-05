package modelTerminal;
import java.util.LinkedHashMap;
import java.util.Map;

public class PlanSyncStopwatch {

    private static long startTime;
    private static long pausedAt;
    private static long pausedDuration;

    private static boolean running;
    private static boolean paused;
    private static boolean displayRunning;

    private static int lapCount = 1;

    // store laps: lap number -> elapsed ms
    private static Map<Integer, Long> laps = new LinkedHashMap<>();

    public static Navigation run() {

        startTime = 0;
        pausedAt = 0;
        pausedDuration = 0;
        running = false;
        paused = false;
        displayRunning = true;
        lapCount = 1;
        laps.clear();

        Thread displayThread = new Thread(() -> {
            while (displayRunning) {
                if (running) {
                    System.out.print("\r");
                    printTime(getElapsed());
                }
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ignored) {}
            }
        });

        displayThread.setDaemon(true);
        displayThread.start();

        while (true) {

            if (!running) {
                // PRE‑START OR AFTER RESET: only Start, Go to Timer, Main Menu
                printPreStartMenu();
                String choice = ConsoleUtils.scanner.nextLine();

                switch (choice) {
                    case "1" -> start();                         // Start stopwatch
                    case "2" -> {                                // Go to Timer
                        displayRunning = false;
                        return Navigation.TIMER;
                    }
                    case "3" -> {                                // Go to Time Calculator
                        displayRunning = false;
                        return Navigation.TIME_CALCULATOR;
                    }
                    case "4" -> {                                // Go to Calendar
                        displayRunning = false;
                        return Navigation.CALENDAR;
                    }
                    case "5" -> {                                // Go to Active Tasks
                        displayRunning = false;
                        return Navigation.ACTIVE_TASKS;
                    }
                    case "6" -> {                                // Go to Recurring Tasks
                        displayRunning = false;
                        return Navigation.RECURRING_TASKS;
                    }
                    case "7" -> {                                // Go to Completed Tasks
                        displayRunning = false;
                        return Navigation.COMPLETED_TASKS;
                    }
                    case "0" -> {                                // Main menu
                        displayRunning = false;
                        return Navigation.MAIN;
                    }
                    default -> System.out.println("\nInvalid option.");
                }

            } else {
                // RUNNING OR PAUSED: Pause, Resume, Lap, Reset, Go Back, Show Laps
                printRunningMenu();
                String choice = ConsoleUtils.scanner.nextLine();

                switch (choice) {
                    case "1" -> pause();                         // Pause
                    case "2" -> resume();                        // Resume
                    case "3" -> lap();                           // Lap
                    case "4" -> showLaps();                      // Display stored laps
                    case "5" -> reset();                         // Reset (stop and clear time + laps)
                    case "6" -> {                                // Go back (stopwatch menu / caller)
                        reset();                                  // ensure stopwatch is stopped and cleared
                        System.out.println("\nReturning to Stopwatch menu...");
                        // fall through to outer loop; caller will decide where to go next
                    }
                    
                    default -> System.out.println("\nInvalid option.");
                }
            }
        }
    }

    /* ================= MENUS ================= */

    // Menu shown when stopwatch has NOT started (or after reset)
    private static void printPreStartMenu() {
        System.out.println("\n\n--- STOPWATCH ---");
        System.out.println("Elapsed: 00:00.000");

        System.out.println("\n1. Start");
        System.out.println("\n2. Go to Timer");
        System.out.println("3. Go to Time Calculator");
        System.out.println("4. Go to Calendar");
        System.out.println("\n5. Go to Active Tasks");
        System.out.println("6. Go to Recurring Tasks");
        System.out.println("7. Go to Completed Tasks");
        System.out.println("\n0. Main Menu");
        System.out.print("\nChoose option: ");
    }

    // Menu shown ONLY after stopwatch has started
    private static void printRunningMenu() {
        System.out.println("\n\n--- STOPWATCH ---");
        printTime(getElapsed());
        System.out.println();

        System.out.println("\n1. Pause");
        System.out.println("2. Resume");
        System.out.println("3. Lap");
        System.out.println("4. Show Laps");
        System.out.println("5. Reset");
        System.out.println("\n6. Go Back");
        System.out.print("\nChoose option: ");
    }

    /* ================= LOGIC ================= */

    private static void start() {
        if (running) {
            System.out.println("\nStopwatch already running.");
            System.out.println("\n");
            return;
        }
        startTime = System.currentTimeMillis();
        pausedDuration = 0;
        paused = false;
        running = true;
        lapCount = 1;
        laps.clear();
    }

    private static void pause() {
        if (!running || paused) {
            System.out.println("\nStopwatch is not running.");
            System.out.println("\n");
            return;
        }
        pausedAt = System.currentTimeMillis();
        paused = true;
    }

    private static void resume() {
        if (!paused) {
            System.out.println("\nStopwatch is not paused.");
            System.out.println("\n");
            return;
        }
        pausedDuration += System.currentTimeMillis() - pausedAt;
        paused = false;
    }

    private static void reset() {
        running = false;
        paused = false;
        startTime = 0;
        pausedAt = 0;
        pausedDuration = 0;
        lapCount = 1;
        laps.clear();
        System.out.println("\nStopwatch reset and laps cleared.");
        System.out.println("\n");
    }

    private static void lap() {
        if (!running || paused) {
            System.out.println("\nStopwatch must be running to lap.");
            System.out.println("\n");
            return;
        }
        long elapsed = getElapsed();
        laps.put(lapCount, elapsed);

        System.out.print("\nLap " + lapCount + ": ");
        printTime(elapsed);
        System.out.println();

        lapCount++;
    }

    private static void showLaps() {
        if (laps.isEmpty()) {
            System.out.println("\nNo laps recorded yet.");
            return;
        }
        System.out.println("\nRecorded laps:");
        for (Map.Entry<Integer, Long> entry : laps.entrySet()) {
            System.out.print("Lap " + entry.getKey() + ": ");
            printTime(entry.getValue());
            System.out.println();
        }
    }

    /* ================= TIME ================= */

    private static long getElapsed() {
        long now = paused ? pausedAt : System.currentTimeMillis();
        return now - startTime - pausedDuration;
    }

    private static void printTime(long ms) {
        long min = ms / 60000;
        long sec = (ms % 60000) / 1000;
        long milli = ms % 1000;
        System.out.printf("Elapsed: %02d:%02d.%03d", min, sec, milli);
    }
}
