import java.awt.Toolkit;

public class PlanSyncTimer {

    private static int remainingSeconds;
    private static int initialSeconds;
    private static boolean running;
    private static boolean paused;

    // single display thread for the live countdown
    private static Thread displayThread;

    public static Navigation run() {
        while (true) {
            System.out.println("\n--- TIMER ---");
            System.out.println("1. Start timer");
            System.out.println("\n2. Go to Stopwatch");
            System.out.println("3. Go to Time Calculator");
            System.out.println("0. Main menu");
            System.out.print("\nChoose option: ");

            switch (ConsoleUtils.scanner.nextLine()) {
                case "1" -> startTimerFlow();
                case "2" -> { return Navigation.STOPWATCH; }
                case "3" -> { return Navigation.TIME_CALCULATOR; }
                case "0" -> { return Navigation.MAIN; }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    /* ================= TIMER FLOW ================= */

    private static void startTimerFlow() {
        int duration = getDurationInSeconds();
        if (duration <= 0) return;

        initialSeconds = duration;
        remainingSeconds = duration;
        running = true;
        paused = false;

        Thread countdownThread = new Thread(() -> {
            while (running) {
                if (!paused && remainingSeconds > 0) {
                    try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
                    remainingSeconds--;
                    if (remainingSeconds == 0 && running) {
                        Toolkit.getDefaultToolkit().beep();
                        stopDisplayThread();          // clean up live line
                        System.out.println("Time’s up!");
                        running = false;
                    }
                } else {
                    try { Thread.sleep(200); } catch (InterruptedException ignored) {}
                }
            }
        });
        countdownThread.setDaemon(true);
        countdownThread.start();

        startDisplayThread();   // start live countdown line

        liveControlLoop();      // pause/resume/reset/stop

        running = false;
        stopDisplayThread();    // make sure display stops
        System.out.println();
    }

    /* ============ LIVE DISPLAY ============ */

    private static void startDisplayThread() {
        // do not start a new one if it is already running
        if (displayThread != null && displayThread.isAlive()) {
            return;
        }

        displayThread = new Thread(() -> {
            while (running) {
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
        // clear the live line and move to a new line
        System.out.print("\r");
        System.out.println();
    }

    /* ================= LIVE CONTROLS ================= */

    private static void liveControlLoop() {
        while (running) {
            // freeze the live line so menu + input are clean
            stopDisplayThread();

            System.out.println("\n--- TIMER CONTROLS ---");
            System.out.println("1. Pause");
            System.out.println("2. Resume");
            System.out.println("3. Reset");
            System.out.println("0. Stop / Go back");
            System.out.println("Remaining: " + formatTime(remainingSeconds));
            System.out.print("\nChoose option: ");

            String choice = ConsoleUtils.scanner.nextLine();

            switch (choice) {
                case "1" -> pause();
                case "2" -> resume();
                case "3" -> reset();
                case "0" -> stop();
                default -> System.out.println("Invalid option.");
            }

            // after handling input, resume live display if timer still running
            if (running) {
                startDisplayThread();
            }
        }
    }

    /* ================= ACTIONS ================= */

    private static void pause() {
        if (!paused && remainingSeconds > 0) {
            paused = true;
            System.out.println("Timer paused. Remaining: " + formatTime(remainingSeconds));
        } else if (remainingSeconds == 0) {
            System.out.println("Timer already finished. Reset if you want to start again.");
        } else {
            System.out.println("Timer is already paused.");
        }
    }

    private static void resume() {
        if (paused && remainingSeconds > 0) {
            paused = false;
            System.out.println("Timer resumed. Remaining: " + formatTime(remainingSeconds));
        } else if (remainingSeconds == 0) {
            System.out.println("Timer already finished. Reset first to start again.");
        } else {
            System.out.println("Timer is already running. Remaining: " + formatTime(remainingSeconds));
        }
    }

    private static void reset() {
        remainingSeconds = initialSeconds;
        paused = false;
        System.out.println("Timer reset to " + formatTime(remainingSeconds) + ".");
    }

    private static void stop() {
        running = false;
        paused = false;
        System.out.println("Timer stopped.");
    }

    /* ================= INPUT ================= */

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
