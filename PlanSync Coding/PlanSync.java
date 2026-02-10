public class PlanSync {

    public static void main(String[] args) {
        Navigation current = Navigation.MAIN;

        while (current != Navigation.EXIT) {
            current = switch (current) {
                case MAIN -> mainMenu();
                case TIMER -> PlanSyncTimer.run();
                case STOPWATCH -> PlanSyncStopwatch.run();
                case TIME_CALCULATOR -> PlanSyncTimeCalculator.run();
                case CALENDAR -> PlanSyncCalendar.run();
                default -> Navigation.EXIT;
            };
        }

        System.out.println("\nSee you next time!");
        System.out.println("\n");
    }

    private static Navigation mainMenu() {
        System.out.println("\n=== PLANSYNC ===");
        System.out.println("1. Timer");
        System.out.println("2. Stopwatch");
        System.out.println("3. Time Calculator");
        System.out.println("4. Calendar");
        System.out.println("0. Exit");
        System.out.print("\nChoose option: ");

        return switch (ConsoleUtils.scanner.nextLine()) {
            case "1" -> Navigation.TIMER;
            case "2" -> Navigation.STOPWATCH;
            case "3" -> Navigation.TIME_CALCULATOR;
            case "4" -> Navigation.CALENDAR;
            case "0" -> Navigation.EXIT;
            default -> Navigation.MAIN;
        };
    }
}
