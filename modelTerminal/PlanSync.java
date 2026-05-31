package modelTerminal;

/*
 *  ██████╗ ██╗      █████╗ ███╗   ██╗███████╗██╗   ██╗███╗   ██╗ ██████╗
 *  ██╔══██╗██║     ██╔══██╗████╗  ██║██╔════╝╚██╗ ██╔╝████╗  ██║██╔════╝
 *  ██████╔╝██║     ███████║██╔██╗ ██║███████╗ ╚████╔╝ ██╔██╗ ██║██║     
 *  ██╔═══╝ ██║     ██╔══██║██║╚██╗██║╚════██║  ╚██╔╝  ██║╚██╗██║██║     
 *  ██║     ███████╗██║  ██║██║ ╚████║███████║   ██║   ██║ ╚████║╚██████╗
 *  ╚═╝     ╚══════╝╚═╝  ╚═╝╚═╝  ╚═══╝╚══════╝   ╚═╝   ╚═╝  ╚═══╝ ╚═════╝
 *
 *  PlanSync source guide
 *  - This file includes a short header describing the class or interface purpose.
 *  - Method comments mark the responsibility of each section so the flow is easier to follow.
 */
/**
 * File purpose: This class supports the PlanSync part of PlanSync and documents the main responsibilities of the file.
 */

public class PlanSync {

    // Section: Launch the PlanSync entry flow for this file.
    public static void main(String[] args) {
        Navigation current = Navigation.MAIN;

        while (current != Navigation.EXIT) {
            current = switch (current) {
                case MAIN -> mainMenu();
                case TIMER -> PlanSyncTimer.run();
                case STOPWATCH -> PlanSyncStopwatch.run();
                case TIME_CALCULATOR -> PlanSyncTimeCalculator.run();
                case CALENDAR -> PlanSyncCalendar.run();
                case ACTIVE_TASKS -> PlanSyncActiveTasks.run();
                case RECURRING_TASKS -> PlanSyncRecurringTasks.run();
                case COMPLETED_TASKS -> PlanSyncCompletedTasks.run();
                default -> Navigation.EXIT;
            };
        }

        System.out.println("\nClosing PlanSync...");
        System.out.println("\n");
    }

    // Section: Handle the logic for main menu.
    private static Navigation mainMenu() {
        System.out.println("\n=== PLANSYNC ===");
        System.out.println("1. Timer");
        System.out.println("2. Stopwatch");
        System.out.println("3. Time Calculator");
        System.out.println("4. Calendar");
        System.out.println("\n5. Active Tasks");
        System.out.println("6. Recurring Tasks");
        System.out.println("7. Completed Tasks");
        System.out.println("\n0. Exit");
        System.out.print("\nChoose option: ");

        return switch (ConsoleUtils.scanner.nextLine()) {
            case "1" -> Navigation.TIMER;
            case "2" -> Navigation.STOPWATCH;
            case "3" -> Navigation.TIME_CALCULATOR;
            case "4" -> Navigation.CALENDAR;
            case "5" -> Navigation.ACTIVE_TASKS;
            case "6" -> Navigation.RECURRING_TASKS;
            case "7" -> Navigation.COMPLETED_TASKS;
            case "0" -> Navigation.EXIT;
            default -> Navigation.MAIN;
        };
    }
}
