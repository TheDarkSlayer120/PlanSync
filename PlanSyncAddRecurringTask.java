public class PlanSyncAddRecurringTask {

    public static void addRecurringTask() {

        System.out.println("\n--- ADD NEW RECURRING TASK ---\n");

        System.out.print("\nENTER TASK NAME: ");
        String name = ConsoleUtils.scanner.nextLine();

        System.out.print("\nENTER TASK DESCRIPTION: ");
        String description = ConsoleUtils.scanner.nextLine();

        System.out.println("\n==========");
        System.out.println("1. DAILY");
        System.out.println("2. WEEKLY");
        System.out.println("3. MONTHLY");
        System.out.println("4. YEARLY");
        System.out.println("==========");
        System.out.print("\nChoose: ");

        int choice = Integer.parseInt(ConsoleUtils.scanner.nextLine());
        System.out.println();

        String frequency = switch (choice) {
            case 1 -> "DAILY";
            case 2 -> "WEEKLY";
            case 3 -> "MONTHLY";
            case 4 -> "YEARLY";
            default -> "DAILY";
        };

        System.out.print("\nTIME (HH:MM): ");
        String time = ConsoleUtils.scanner.nextLine();

        System.out.print("\nDAY OF WEEK (e.g. FRIDAY): ");
        String day = ConsoleUtils.scanner.nextLine();

        String timeDate = time + " " + day;

        PlanSyncRecurringTasks.recurringTasks.add(
                new PlanSyncRecurringTasks.RecurringTask(name, description, timeDate, frequency)
        );

        System.out.println("\nNew Recurring Task Added!");
        System.out.println("\nList Updated!");
        System.out.println("\nGoing to Recurring Tasks...");
        System.out.println();
    }
}
