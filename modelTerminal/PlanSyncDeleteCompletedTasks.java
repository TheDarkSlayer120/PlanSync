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
 * File purpose: This class supports the PlanSyncDeleteCompletedTasks part of PlanSync and documents the main responsibilities of the file.
 */

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class PlanSyncDeleteCompletedTasks {

    // Section: Remove the items involved in completed tasks.
    public static void deleteCompletedTasks() {

        while (true) {

            Map<String, PlanSyncCompletedTasks.CompletedTask> tasks =
                    PlanSyncCompletedTasks.getCompletedTasks();

            if (tasks.isEmpty()) {
                System.out.println("\nNo completed tasks to delete.");
                return;
            }

            PlanSyncCompletedTasks.displayCompletedList();

            System.out.println("\n--- DELETE COMPLETED TASKS ---");
            System.out.println("0. Cancel");

            System.out.print("\nChoose Task(s) to Delete (e.g. 1 2 3): ");
            String input = ConsoleUtils.scanner.nextLine().trim();

            if (input.equals("0")) {
                System.out.println("Cancelled.");
                return;
            }

            if (input.isEmpty()) {
                System.out.println("\nInvalid input. Please enter at least one number.");
                continue;
            }

            String[] tokens = input.split("\\s+");

            Map<String, PlanSyncCompletedTasks.CompletedTask> toDelete =
                    new LinkedHashMap<>();

            boolean invalidFound = false;

            for (String token : tokens) {
                try {
                    int index = Integer.parseInt(token);

                    if (index <= 0) {
                        invalidFound = true;
                        continue;
                    }

                    var task = PlanSyncCompletedTasks.getByIndex(index);

                    if (task != null) {
                        toDelete.put(task.id, task);
                    } else {
                        invalidFound = true;
                    }

                // Section: Handle the logic for catch.
                } catch (NumberFormatException e) {
                    invalidFound = true;
                }
            }

            if (toDelete.isEmpty()) {
                System.out.println("\nNo valid tasks selected. Try again.");
                continue;
            }

            if (invalidFound) {
                System.out.println("\nSome inputs were invalid and ignored.");
            }

            System.out.println("\nYou are about to delete:");
            for (var t : toDelete.values()) {
                System.out.println("- " + t.name);
            }

            System.out.print("\nAre You Sure? [Y/N]: ");
            String confirm =
                    ConsoleUtils.scanner.nextLine().trim().toUpperCase(Locale.ROOT);

            if (!confirm.equals("Y")) {
                System.out.println("\nDeletion cancelled.");
                return;
            }

            for (String id : toDelete.keySet()) {
                PlanSyncCompletedTasks.removeById(id);
            }

            // 🔥 IMPORTANT: Save immediately after deletion
            // Section: Persist the data used to now.
            saveNow();

            System.out.println("\nTask(s) successfully deleted.");
            return;
        }
    }

    // Section: Handle the logic for empty completed tasks.
    public static void emptyCompletedTasks() {

        while (true) {

            if (PlanSyncCompletedTasks.getCompletedTasks().isEmpty()) {
                System.out.println("\nNo completed tasks to clear.");
                return;
            }

            System.out.print(
                    "\nAre You Sure You Want To Delete ALL Completed Tasks? [Y/N]: ");

            String confirm =
                    ConsoleUtils.scanner.nextLine().trim().toUpperCase(Locale.ROOT);

            if (confirm.equals("Y")) {
                PlanSyncCompletedTasks.clearAll();
                // Section: Persist the data used to now.
                saveNow();
                System.out.println("\nAll completed tasks deleted.");
                return;
            }

            if (confirm.equals("N")) {
                System.out.println("Cancelled.");
                return;
            }

            System.out.println("Invalid input. Please enter Y or N.");
        }
    }

    // Calls save method from PlanSyncCompletedTasks
    // Section: Persist the data used to now.
    private static void saveNow() {
        try {
            var method = PlanSyncCompletedTasks.class
                    .getDeclaredMethod("saveCompleted");
            method.setAccessible(true);
            method.invoke(null);
        } catch (Exception ignored) {}
    }
}
