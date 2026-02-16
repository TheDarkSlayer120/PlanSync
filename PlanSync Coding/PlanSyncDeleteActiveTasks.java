import java.util.ArrayList;

public class PlanSyncDeleteActiveTasks {

    public static void deleteActiveTasks() {

        PlanSyncActiveTasks.displayActiveTasks();

        System.out.print("Choose Task(s) to Delete (e.g. 1 2 3), or Enter 0 to Cancel: ");
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

        System.out.println();
        System.out.println("You are about to delete the following tasks:");

        for (int i : indexes) {
            System.out.println("- " +
                    PlanSyncActiveTasks.activeTasks.get(i).name +
                    " (ID: " + (i + 1) + ")");
        }

        System.out.println();
        System.out.print("Are You Sure? [Y/N]: ");
        String confirm = ConsoleUtils.scanner.nextLine();

        if (confirm.equalsIgnoreCase("Y")) {

            for (int i = indexes.size() - 1; i >= 0; i--) {
                PlanSyncActiveTasks.activeTasks.remove((int) indexes.get(i));
            }

            System.out.println();
            System.out.println("Task(s) Deleted!");
            System.out.println("Going Back to Active Tasks...");
            System.out.println();
        }
    }
}
