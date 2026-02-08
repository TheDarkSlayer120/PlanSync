import java.awt.Toolkit;

public class PlanSyncTimer {

    public static Navigation run() {
        while (true) {
            System.out.println("\n--- TIMER ---");
            System.out.println("1. Start timer");
            System.out.println("\n2. Go to Stopwatch");
            System.out.println("3. Go to Time Calculator");
            System.out.println("0. Main menu");
            System.out.print("\nChoose option: ");

            switch (ConsoleUtils.scanner.nextLine()) {
                case "1" -> startTimer();
                case "2" -> { return Navigation.STOPWATCH; }
                case "3" -> { return Navigation.TIME_CALCULATOR; }
                case "0" -> { return Navigation.MAIN; }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private static void startTimer() {
        System.out.print("Enter duration in seconds: ");

        int seconds;
        try {
            seconds = Integer.parseInt(ConsoleUtils.scanner.nextLine());
            if (seconds <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            System.out.println("Invalid number.");
            return;
        }

        while (seconds > 0) {
            System.out.print("\rRemaining: " + seconds + "s ");
            try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
            seconds--;
        }

        Toolkit.getDefaultToolkit().beep();
        System.out.println("\n⏰ Time’s up!");
        ConsoleUtils.pause();
    }
}
