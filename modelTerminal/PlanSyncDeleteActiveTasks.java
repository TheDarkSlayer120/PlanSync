package modelTerminal;

/*
 *  ██████╗ ██╗      █████╗ ███╗   ██╗███████╗██╗   ██╗███╗   ██╗ ██████╗
 *  ██╔══██╗██║     ██╔══██╗████╗  ██║██╔════╝╚██╗ ██╔╝████╗  ██║██╔════╝
 *  ██████╔╝██║     ███████║██╔██╗ ██║███████╗ ╚████╔╝ ██╔██╗ ██║██║     
 *  ██╔═══╝ ██║     ██╔══██║██║╚██╗██║╚════██║  ╚██╔╝  ██║╚██╗██║██║     
 *  ██║     ███████╗██║  ██║██║ ╚████║███████║   ██║   ██║ ╚████║╚██████╗
 *  ╚═╝     ╚══════╝╚═╝  ╚═╝╚═╝  ╚═══╝╚══════╝   ╚═╝   ╚═╝  ╚═══╝ ╚═════╝
 *
 *  PlanSync source guide
 *  - This file includes a short header describing the class or interface purpose.
 *  - Method comments mark the responsibility of each section so the flow is easier to follow.
 */
/**
 * File purpose: This class supports the PlanSyncDeleteActiveTasks part of PlanSync and documents the main responsibilities of the file.
 */

import java.util.ArrayList;
import java.util.Collections;

public class PlanSyncDeleteActiveTasks {

    // Section: Remove the items involved in active tasks.
    public static void deleteActiveTasks() {

        if (PlanSyncActiveTasks.activeTasks.isEmpty()) {
            System.out.println("\nNo active tasks to delete.");
            return;
        }

        while (true) {

            PlanSyncActiveTasks.displayActiveTasks();

            System.out.println("\n--- DELETE ACTIVE TASKS ---\n");
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
                        index < PlanSyncActiveTasks.activeTasks.size()) {

                        if (!indexes.contains(index)) {
                            indexes.add(index);
                        }

                    } else {
                        System.out.println("Invalid task number: " + s);
                    }

                // Section: Handle the logic for catch.
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
                        PlanSyncActiveTasks.activeTasks.get(i).name +
                        " (ID: " + (i + 1) + ")");
            }

            System.out.print("\nAre You Sure? [Y/N]: ");
            String confirm = ConsoleUtils.scanner.nextLine().trim();

            if (!confirm.equalsIgnoreCase("Y")) {
                System.out.println("\nCancelled.\n");
                return;
            }

            // 🔥 CRITICAL FIX
            Collections.sort(indexes);

            for (int i = indexes.size() - 1; i >= 0; i--) {
                PlanSyncActiveTasks.activeTasks.remove(
                        (int) indexes.get(i)
                );
            }

            System.out.println("\nTask(s) Deleted!");
            System.out.println("\nGoing Back to Active Tasks...\n");
            return;
        }
    }
}
