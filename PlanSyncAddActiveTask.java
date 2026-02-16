import java.time.LocalDate;

public class PlanSyncAddActiveTask {

    public static void addActiveTask() {

        System.out.println("\n--- ADD NEW ACTIVE TASK ---\n");

        System.out.print("\nENTER TASK NAME: ");
        String name = ConsoleUtils.scanner.nextLine();

        System.out.print("\nENTER TASK DESCRIPTION: ");
        String description = ConsoleUtils.scanner.nextLine();

        System.out.print("\nENTER DEADLINE DATE (DD/MM/YYYY): ");
        String dateInput = ConsoleUtils.scanner.nextLine();

        LocalDate deadline = LocalDate.parse(dateInput, PlanSyncActiveTasks.formatter);

        PlanSyncActiveTasks.activeTasks.add(
                new PlanSyncActiveTasks.Task(name, description, deadline)
        );

        System.out.println("\nNew Active Task Added!");
        System.out.println("\nList Updated!");
        System.out.println("\nGoing to Active Tasks...");
        System.out.println();
    }
}
