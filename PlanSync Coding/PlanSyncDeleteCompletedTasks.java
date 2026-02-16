import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class PlanSyncDeleteCompletedTasks {

    public static void deleteCompletedTasks() {

        Map<String, PlanSyncCompletedTasks.CompletedTask> tasks =
                PlanSyncCompletedTasks.getCompletedTasks();

        if (tasks.isEmpty()) {
            System.out.println("\nNo completed tasks to delete.");
            return;
        }

        PlanSyncCompletedTasks.displayCompletedList();

        System.out.print(
                "\nChoose Task(s) to Delete (e.g. 1 2 3), or Enter 0 to Cancel: ");
        String input = ConsoleUtils.scanner.nextLine().trim();

        if (input.equals("0")) return;

        String[] tokens = input.split("\\s+");

        Map<String,
            PlanSyncCompletedTasks.CompletedTask> toDelete =
                new LinkedHashMap<>();

        for (String token : tokens) {
            try {
                int index = Integer.parseInt(token);
                var task = PlanSyncCompletedTasks.getByIndex(index);
                if (task != null) {
                    toDelete.put(task.id, task);
                }
            } catch (NumberFormatException ignored) {}
        }

        if (toDelete.isEmpty()) {
            System.out.println("No valid tasks selected.");
            return;
        }

        System.out.println("\nYou are about to delete:");
        for (var t : toDelete.values()) {
            System.out.println("- " + t.name);
        }

        System.out.print("\nAre You Sure? [Y/N]: ");
        String confirm =
                ConsoleUtils.scanner.nextLine().trim().toUpperCase(Locale.ROOT);

        if (!confirm.equals("Y")) {
            System.out.println("Cancelled.");
            return;
        }

        for (String id : toDelete.keySet()) {
            PlanSyncCompletedTasks.removeById(id);
        }

        System.out.println("\nTask(s) Deleted!");
    }

    public static void emptyCompletedTasks() {

        if (PlanSyncCompletedTasks.getCompletedTasks().isEmpty()) {
            System.out.println("\nNo completed tasks to clear.");
            return;
        }

        System.out.print(
                "\nAre You Sure You Want To Delete ALL Completed Tasks? [Y/N]: ");
        String confirm =
                ConsoleUtils.scanner.nextLine().trim().toUpperCase();

        if (!confirm.equals("Y")) {
            System.out.println("Cancelled.");
            return;
        }

        PlanSyncCompletedTasks.clearAll();
        System.out.println("\nAll completed tasks deleted.");
    }
}
