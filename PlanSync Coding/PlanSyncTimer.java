package modelTerminal;
import java.awt.Toolkit;

public class PlanSyncTimer {

    private static int remainingSeconds;
    private static int initialSeconds;
    private static boolean running;
    private static boolean paused;
    private static boolean finished;   // true when timer hits 0

    private static Thread displayThread;

    public static Navigation run() {
        while (true) {
            System.out.println("\n--- TIMER ---");
            System.out.println("1. Start timer");
            System.out.println("\n2. Go to Stopwatch");
            System.out.println("3. Go to Time Calculator");
            System.out.println("4. Go to Calendar");
            System.out.println("\n5. Go to Active Tasks");
            System.out.println("6. Go to Recurring Tasks");
            System.out.println("7. Go to Completed Tasks");
            System.out.println("\n0. Main menu");
            System.out.print("\nChoose option: ");

            switch (ConsoleUtils.scanner.nextLine()) {
                case "1" -> startTimerFlow();
                case "2" -> { return Navigation.STOPWATCH; }
                case "3" -> { return Navigation.TIME_CALCULATOR; }
                case "4" -> { return Navigation.CALENDAR; }
                case "5" -> { return Navigation.ACTIVE_TASKS; }
                case "6" -> { return Navigation.RECURRING_TASKS; }
                case "7" -> { return Navigation.COMPLETED_TASKS; }
                case "0" -> { return Navigation.MAIN; }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    /* ================= TIMER FLOW ================= */

    private static void startTimerFlow() {
        int duration = getDurationFromPresetsOrCustom();
        if (duration <= 0) return;

        initialSeconds = duration;
        remainingSeconds = duration;
        running = true;
        paused = false;
        finished = false;

        Thread countdownThread = new Thread(() -> {
            while (running && remainingSeconds > 0) {
                if (!paused) {
                    try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
                    remainingSeconds--;
                    if (remainingSeconds == 0) {
                        Toolkit.getDefaultToolkit().beep();
                        stopDisplayThread();
                        System.out.println("Time's up!");
                        System.out.println("\nPress Enter to continue...");
                        finished = true;     // mark as finished
                        running = false;     // stop control loop
                    }
                    
                } else {
                    try { Thread.sleep(200); } catch (InterruptedException ignored) {}
                }
            }
        });
        countdownThread.setDaemon(true);
        countdownThread.start();

        startDisplayThread();

        liveControlLoop();      // exits when stopped or finished

        stopDisplayThread();
        System.out.println();
    }

    /* ============ LIVE DISPLAY ============ */

    private static void startDisplayThread() {
        if (displayThread != null && displayThread.isAlive()) return;

        displayThread = new Thread(() -> {
            while (running && !finished) {
                System.out.print("\rRemaining: " + formatTime(remainingSeconds) + " ");
                try { Thread.sleep(500); } catch (InterruptedException ignored) {}
            }
        });
        displayThread.setDaemon(true);
        displayThread.start();
    }

    private static void stopDisplayThread() {
        if (displayThread != null && displayThread.isAlive()) {
            displayThread.interrupt();
            try { displayThread.join(100); } catch (InterruptedException ignored) {}
            displayThread = null;
        }
        System.out.print("\r");
        System.out.println();
    }

    /* ================= LIVE CONTROLS ================= */

    private static void liveControlLoop() {
        while (running && remainingSeconds > 0) {
            stopDisplayThread();

            System.out.println("\n--- TIMER CONTROLS ---");
            System.out.println("1. Pause");
            System.out.println("2. Resume");
            System.out.println("3. Reset");
            System.out.println("0. Stop / Go back");
            System.out.println("Remaining: " + formatTime(remainingSeconds));
            System.out.print("\nChoose option: ");

            String choice = ConsoleUtils.scanner.nextLine();

            if (!running || remainingSeconds == 0 || finished) {
                break; // timer stopped or finished while typing
            }

            switch (choice) {
                case "1" -> pause();
                case "2" -> resume();
                case "3" -> reset();
                case "0" -> stop();
                default -> System.out.println("Invalid option.");
            }

            if (running && remainingSeconds > 0 && !finished) {
                startDisplayThread();
            }
        }
    }

    /* ================= ACTIONS ================= */

    private static void pause() {
        if (!paused && remainingSeconds > 0) {
            paused = true;
            System.out.println("Timer paused. Remaining: " + formatTime(remainingSeconds));
            System.out.println("\n");
            
        } else if (remainingSeconds == 0) {
            System.out.println("Timer already finished. Reset if you want to start again.");
            System.out.println("\n");
        } else {
            System.out.println("Timer is already paused.");
            System.out.println("\n");
        }
    }

    private static void resume() {
        if (paused && remainingSeconds > 0) {
            paused = false;
            System.out.println("Timer resumed. Remaining: " + formatTime(remainingSeconds));
            System.out.println("\n");
        } else if (remainingSeconds == 0) {
            System.out.println("Timer already finished. Reset first to start again.");
            System.out.println("\n");
        } else {
            System.out.println("Timer is already running. Remaining: " + formatTime(remainingSeconds));
            System.out.println("\n");
        }
    }

    private static void reset() {
        remainingSeconds = initialSeconds;
        paused = false;
        finished = false;
        System.out.println("Timer reset to " + formatTime(remainingSeconds) + ".");
        System.out.println("\n");
    }

    private static void stop() {
        running = false;
        paused = false;
        finished = false;
        System.out.println("Timer stopped.");
        System.out.println("\n");
    }

    /* ================= PRESETS + INPUT ================= */

    // New menu: presets + custom time
    private static int getDurationFromPresetsOrCustom() {
        while (true) {
            System.out.println("\nChoose preset time:");
            System.out.println("1. 00:01:00  (1 minute)");
            System.out.println("2. 00:05:00  (5 minutes)");
            System.out.println("3. 00:10:00  (10 minutes)");
            System.out.println("4. 00:15:00  (15 minutes)");
            System.out.println("5. 00:30:00  (30 minutes)");
            System.out.println("6. 01:00:00  (1 hour)");
            System.out.println("7. --:--:--  (Custom time)");
            System.out.println("0. Cancel");
            System.out.print("\nChoose option: ");

            String choice = ConsoleUtils.scanner.nextLine();

            switch (choice) {
                case "1": return 60;        // 1 minute
                case "2": return 5 * 60;    // 5 minutes
                case "3": return 10 * 60;   // 10 minutes
                case "4": return 15 * 60;   // 15 minutes
                case "5": return 30 * 60;   // 30 minutes
                case "6": return 60 * 60;   // 1 hour
                case "7": return getDurationInSeconds(); // custom time (existing menu)
                case "0": return 0;         // cancel
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    // Existing custom input menu
    private static int getDurationInSeconds() {
        System.out.println("\nChoose time unit:");
        System.out.println("1. Hours");
        System.out.println("2. Minutes");
        System.out.println("3. Seconds");
        System.out.println("0. Cancel");
        System.out.print("\nChoose option: ");

        String choice = ConsoleUtils.scanner.nextLine();
        int multiplier;
        String label;

        switch (choice) {
            case "1" -> { multiplier = 3600; label = "hours"; }
            case "2" -> { multiplier = 60; label = "minutes"; }
            case "3" -> { multiplier = 1; label = "seconds"; }
            case "0" -> { return 0; }
            default -> {
                System.out.println("Invalid option.");
                return 0;
            }
        }

        System.out.print("Enter number of " + label + ": ");
        try {
            int value = Integer.parseInt(ConsoleUtils.scanner.nextLine());
            if (value <= 0) throw new NumberFormatException();
            return value * multiplier;
        } catch (NumberFormatException e) {
            System.out.println("Invalid number.");
            return 0;
        }
    }

    /* ================= DISPLAY ================= */

    private static String formatTime(int totalSeconds) {
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02dh %02dm %02ds", hours, minutes, seconds);
    }
}