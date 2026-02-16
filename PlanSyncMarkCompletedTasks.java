import java.time.LocalDate;
import java.util.ArrayList;

public class PlanSyncMarkCompletedTasks {

    public static void markCompletedTasks() {

        PlanSyncActiveTasks.displayActiveTasks();

        System.out.println("\n--- MARK TASKS AS COMPLETE ---\n");
        System.out.println("0. Cancel");

        System.out.print("\nChoose Task(s) to Complete (e.g. 1 2 3): ");
        String input = ConsoleUtils.scanner.nextLine().trim();

        if (input.equals("0")) {
            return;
        }

        String[] selections = input.split("\\s+");
        ArrayList<Integer> indexes = new ArrayList<>();

        for (String s : selections) {
            try {
                int index = Integer.parseInt(s) - 1;
                if (index >= 0 && index < PlanSyncActiveTasks.activeTasks.size()) {
                    if (!indexes.contains(index)) {
                        indexes.add(index);
                    }
                }
            } catch (NumberFormatException ignored) {}
        }

        if (indexes.isEmpty()) {
            System.out.println("\nNo valid tasks selected.");
            return;
        }

        System.out.println();
        System.out.println("You are about to mark the following tasks as COMPLETE:");

        for (int i : indexes) {
            System.out.println("- " +
                    PlanSyncActiveTasks.activeTasks.get(i).name +
                    " (ID: " + (i + 1) + ")");
        }

        System.out.println();
        System.out.print("Are You Sure? [Y/N]: ");
        String confirm = ConsoleUtils.scanner.nextLine();

        if (!confirm.equalsIgnoreCase("Y")) {
            System.out.println("Cancelled.");
            return;
        }

        System.out.println();
        System.out.println("\nTask(s) Marked Complete!");
        System.out.println("\nTask(s) Moved to Completed Tasks:");

        LocalDate today = LocalDate.now();

        // remove from highest index downward
        for (int i = indexes.size() - 1; i >= 0; i--) {
            int index = indexes.get(i);

            PlanSyncActiveTasks.Task task =
                    PlanSyncActiveTasks.activeTasks.remove(index);

            // ✅ Correct integration with your file-based system
            PlanSyncCompletedTasks.addCompleted(
                    task.name,
                    task.description,
                    task.deadline,
                    today
            );

            System.out.println("\n- [" + task.name + "] " + task.description +
                    " -> [COMPLETED " +
                    today.format(PlanSyncActiveTasks.formatter) + "]");
        }

        System.out.println("\nGoing to Active Tasks...");
        System.out.println();
    }
}
