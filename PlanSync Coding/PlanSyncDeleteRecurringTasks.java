package modelTerminal;
import java.util.ArrayList;
import java.util.Collections;

public class PlanSyncDeleteRecurringTasks {

    public static void deleteRecurringTasks() {

        if (PlanSyncRecurringTasks.recurringTasks.isEmpty()) {
            System.out.println("\nNo recurring tasks to delete.");
            return;
        }

        while (true) {

            PlanSyncRecurringTasks.displayRecurringTasks();

            System.out.println("\n--- DELETE RECURRING TASKS ---\n");
            System.out.println("0. Cancel");

            System.out.print("\nChoose Task(s) to Delete (e.g. 1 2 3): ");
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
                        index < PlanSyncRecurringTasks.recurringTasks.size()) {

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

            System.out.println("\nYou are about to delete the following tasks:");

            for (int i : indexes) {
                System.out.println("- " +
                        PlanSyncRecurringTasks.recurringTasks.get(i).name +
                        " (ID: " + (i + 1) + ")");
            }

            System.out.print("\nAre You Sure? [Y/N]: ");
            String confirm = ConsoleUtils.scanner.nextLine().trim();

            if (!confirm.equalsIgnoreCase("Y")) {
                System.out.println("\nCancelled.\n");
                return;
            }

            // 🔥 Critical Fix
            Collections.sort(indexes);

            for (int i = indexes.size() - 1; i >= 0; i--) {
                PlanSyncRecurringTasks.recurringTasks.remove(
                        (int) indexes.get(i)
                );
            }

            System.out.println("\nTask(s) Deleted!");
            System.out.println("Going Back to Recurring Tasks...\n");
            return;
        }
    }
}
