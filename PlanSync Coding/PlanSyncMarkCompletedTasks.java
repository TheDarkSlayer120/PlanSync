package modelTerminal;
import java.time.LocalDate;
import java.util.ArrayList;

public class PlanSyncMarkCompletedTasks {

    public static void markCompletedTasks() {

        if (PlanSyncActiveTasks.activeTasks.isEmpty()) {
            System.out.println("\nNo active tasks to complete.");
            return;
        }

        while (true) {

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

                    if (index >= 0 &&
                        index < PlanSyncActiveTasks.activeTasks.size()) {

                        if (!indexes.contains(index)) {
                            indexes.add(index);
                        }

                    } else {
                        System.out.println("Invalid task number: " + s);
                    }

                } catch (NumberFormatException e) {
                    System.out.println("Invalid input (not a number): " + s);
                }
            }

            if (indexes.isEmpty()) {
                System.out.println("\nNo valid tasks selected. Please try again.\n");
                continue;
            }

            System.out.println("\nYou are about to mark the following tasks as COMPLETE:");

            for (int i : indexes) {
                System.out.println("- " +
                        PlanSyncActiveTasks.activeTasks.get(i).name +
                        " (ID: " + (i + 1) + ")");
            }

            System.out.print("\nAre You Sure? [Y/N]: ");
            String confirm = ConsoleUtils.scanner.nextLine().trim();

            if (!confirm.equalsIgnoreCase("Y")) {
                System.out.println("\nCancelled.\n");
                return;
            }

            LocalDate today = LocalDate.now();

            System.out.println("\nTask(s) Marked Complete!");
            System.out.println("\nTask(s) Moved to Completed Tasks:");

            indexes.sort(Integer::compareTo);

            for (int i = indexes.size() - 1; i >= 0; i--) {

                int index = indexes.get(i);

                PlanSyncActiveTasks.Task task =
                        PlanSyncActiveTasks.activeTasks.remove(index);

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

            System.out.println("\nGoing to Active Tasks...\n");
            return;
        }
    }
}
