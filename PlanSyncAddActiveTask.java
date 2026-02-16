import java.time.LocalDate;

public class PlanSyncAddActiveTask {

    public static void addActiveTask() {

        System.out.print("ENTER TASK NAME: ");
        String name = ConsoleUtils.scanner.nextLine();
        System.out.println();

        System.out.print("ENTER TASK DESCRIPTION: ");
        String description = ConsoleUtils.scanner.nextLine();
        System.out.println();

        System.out.print("ENTER DEADLINE DATE (DD/MM/YYYY): ");
        String dateInput = ConsoleUtils.scanner.nextLine();
        System.out.println();

        LocalDate deadline = LocalDate.parse(dateInput, PlanSyncActiveTasks.formatter);

        PlanSyncActiveTasks.activeTasks.add(
                new PlanSyncActiveTasks.Task(name, description, deadline)
        );

        System.out.println("New Active Task Added!");
        System.out.println("List Updated!");
        System.out.println("Going to Active Tasks...");
        System.out.println();
    }
}
