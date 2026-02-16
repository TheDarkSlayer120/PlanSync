import java.util.ArrayList;

public class PlanSyncDeleteActiveTasks {

    public static void deleteActiveTasks() {

        PlanSyncActiveTasks.displayActiveTasks();

        System.out.println("\n--- DELETE ACTIVE TASKS ---\n");
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
            if (index >= 0 && index < PlanSyncActiveTasks.activeTasks.size()) {
                indexes.add(index);
            }
        }

        System.out.println("\nYou are about to delete the following tasks:");

        for (int i : indexes) {
            System.out.println("- " +
                    PlanSyncActiveTasks.activeTasks.get(i).name +
                    " (ID: " + (i + 1) + ")");
        }

        System.out.print("\nAre You Sure? [Y/N]: ");
        String confirm = ConsoleUtils.scanner.nextLine();

        if (confirm.equalsIgnoreCase("Y")) {

            for (int i = indexes.size() - 1; i >= 0; i--) {
                PlanSyncActiveTasks.activeTasks.remove((int) indexes.get(i));
            }

            System.out.println("\nTask(s) Deleted!");
            System.out.println("\nGoing Back to Active Tasks...");
            System.out.println();
        }
    }
}
