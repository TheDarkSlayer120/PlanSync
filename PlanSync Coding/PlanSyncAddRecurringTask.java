public class PlanSyncAddRecurringTask {

    public static void addRecurringTask() {

        System.out.print("ENTER TASK NAME: ");
        String name = ConsoleUtils.scanner.nextLine();
        System.out.println();

        System.out.print("ENTER TASK DESCRIPTION: ");
        String description = ConsoleUtils.scanner.nextLine();
        System.out.println();

        System.out.println("==========");
        System.out.println("1. DAILY");
        System.out.println("2. WEEKLY");
        System.out.println("3. MONTHLY");
        System.out.println("4. YEARLY");
        System.out.println("==========");
        System.out.println();
        System.out.print("Choose: ");

        int choice = Integer.parseInt(ConsoleUtils.scanner.nextLine());
        System.out.println();

        String frequency = switch (choice) {
            case 1 -> "DAILY";
            case 2 -> "WEEKLY";
            case 3 -> "MONTHLY";
            case 4 -> "YEARLY";
            default -> "DAILY";
        };

        System.out.print("TIME (HH:MM): ");
        String time = ConsoleUtils.scanner.nextLine();
        System.out.println();

        System.out.print("DAY OF WEEK (e.g. FRIDAY): ");
        String day = ConsoleUtils.scanner.nextLine();
        System.out.println();

        String timeDate = time + " " + day;

        PlanSyncRecurringTasks.recurringTasks.add(
                new PlanSyncRecurringTasks.RecurringTask(name, description, timeDate, frequency)
        );

        System.out.println("New Recurring Task Added!");
        System.out.println("List Updated!");
        System.out.println("Going to Recurring Tasks...");
        System.out.println();
    }
}
