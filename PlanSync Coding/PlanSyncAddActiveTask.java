package modelTerminal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class PlanSyncAddActiveTask {

    public static void addActiveTask() {

        System.out.println("\n--- ADD NEW ACTIVE TASK ---\n");

        System.out.print("ENTER TASK NAME: ");
        String name = ConsoleUtils.scanner.nextLine().trim();

        System.out.print("\nENTER TASK DESCRIPTION: ");
        String description = ConsoleUtils.scanner.nextLine().trim();

        LocalDate deadline = null;

        while (true) {

            System.out.print("\nENTER DEADLINE DATE (DD/MM/YYYY) or 0 to Cancel: ");
            String dateInput = ConsoleUtils.scanner.nextLine().trim();

            if (dateInput.equals("0")) {
                System.out.println("\nCancelled.\n");
                return;
            }

            try {
                deadline = LocalDate.parse(
                        dateInput,
                        PlanSyncActiveTasks.formatter
                );
                break; // valid date, exit loop

            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please use DD/MM/YYYY.");
            }
        }

        PlanSyncActiveTasks.activeTasks.add(
                new PlanSyncActiveTasks.Task(name, description, deadline)
        );

        PlanSyncActiveTasks.saveActive();

        System.out.println("\nNew Active Task Added!");
        System.out.println("\nList Updated!");
        System.out.println("\nGoing to Active Tasks...\n");
    }
}
