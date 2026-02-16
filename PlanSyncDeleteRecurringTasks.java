import java.util.ArrayList;

public class PlanSyncDeleteRecurringTasks {

    public static void deleteRecurringTasks() {

        PlanSyncRecurringTasks.displayRecurringTasks();

        System.out.println("\n--- DELETE RECURRING TASKS ---\n");
        System.out.println("0. Cancel");

        System.out.print("\nChoose Task(s) to Delete (e.g. 1 2 3): ");
        String input = ConsoleUtils.scanner.nextLine();

        if (input.equals("0")) {
            return;
        }

        String[] selections = input.split(" ");
        ArrayList<Integer> indexes = new ArrayList<>();

        for (String s : selections) {
            int index = Integer.parseInt(s) - 1;
            if (index >= 0 && index < PlanSyncRecurringTasks.recurringTasks.size()) {
                indexes.add(index);
            }
        }

        System.out.println("\nYou are about to delete the following tasks:");

        for (int i : indexes) {
            System.out.println("- " +
                    PlanSyncRecurringTasks.recurringTasks.get(i).name +
                    " (ID: " + (i + 1) + ")");
        }

        System.out.print("\nAre You Sure? [Y/N]: ");
        String confirm = ConsoleUtils.scanner.nextLine();

        if (confirm.equalsIgnoreCase("Y")) {

            for (int i = indexes.size() - 1; i >= 0; i--) {
                PlanSyncRecurringTasks.recurringTasks.remove((int) indexes.get(i));
            }

            System.out.println("\nTask(s) Deleted!");
            System.out.println("Going Back to Recurring Tasks...");
            System.out.println();
        }
    }
}
