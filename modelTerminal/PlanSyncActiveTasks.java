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
 * File purpose: This class supports the PlanSyncActiveTasks part of PlanSync and documents the main responsibilities of the file.
 */

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import util.AppPaths;

public class PlanSyncActiveTasks {

    private static final String ACTIVE_FILE = AppPaths.getDataFile("active_tasks.txt").getPath();

    public static ArrayList<Task> activeTasks = new ArrayList<>();
    public static DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static class Task {
        String name;
        String description;
        LocalDate deadline;

        // Section: Handle the logic for task.
        public Task(String name, String description, LocalDate deadline) {
            this.name = name;
            this.description = description;
            this.deadline = deadline;
        }
    }

    // Section: Handle the logic for run.
    public static Navigation run() {

        loadActive(); // 🔥 Load tasks when entering

        while (true) {

            // Section: Handle the logic for display active tasks.
            displayActiveTasks();

            System.out.println("1. Display Active Tasks");
            System.out.println("2. Mark Task(s) as Complete");
            System.out.println("3. Add a New Active Task");
            System.out.println("4. Delete Active Task(s)");
            System.out.println("\n5. Go to Recurring Tasks");
            System.out.println("6. Go to Completed Tasks");
            System.out.println("\n7. Go to Timer");
            System.out.println("8. Go to Stopwatch");
            System.out.println("9. Go to Time Calculator");
            System.out.println("10. Go to Calendar");
            System.out.println("\n0. Main Menu");
            System.out.print("\nChoose Option: ");

            String choice = ConsoleUtils.scanner.nextLine();

            switch (choice) {
                case "2":
                    PlanSyncMarkCompletedTasks.markCompletedTasks();
                    break;
                case "3":
                    PlanSyncAddActiveTask.addActiveTask();
                    break;
                case "4":
                    PlanSyncDeleteActiveTasks.deleteActiveTasks();
                    break;
                case "5":
                    // Section: Persist the data used to active.
                    saveActive();
                    return Navigation.RECURRING_TASKS;
                case "6":
                    // Section: Persist the data used to active.
                    saveActive();
                    return Navigation.COMPLETED_TASKS;
                case "7":
                    // Section: Persist the data used to active.
                    saveActive();
                    return Navigation.TIMER;
                case "8":
                    // Section: Persist the data used to active.
                    saveActive();
                    return Navigation.STOPWATCH;
                case "9":
                    // Section: Persist the data used to active.
                    saveActive();
                    return Navigation.TIME_CALCULATOR;
                case "10":
                    // Section: Persist the data used to active.
                    saveActive();
                    return Navigation.CALENDAR;
                case "0":
                    // Section: Persist the data used to active.
                    saveActive();
                    return Navigation.MAIN;
            }
        }
    }

    // Section: Handle the logic for display active tasks.
    public static void displayActiveTasks() {

        System.out.println("\n--- ACTIVE TASKS ---");
        System.out.println("\n===============================================================================");
        System.out.println("(Format: | # | Task Name | Task Description | Deadline | Days Remaining |)");
        System.out.println("===============================================================================");

        System.out.println("\n<<ACTIVE TASKS:>> (TODAY: "
                + LocalDate.now().format(formatter) + ")");
        System.out.println("================================================================================================");

        if (activeTasks.isEmpty()) {
            System.out.println("\nNo active tasks found.");
            return;
        }

        int id = 1;

        for (Task task : activeTasks) {

            long days = ChronoUnit.DAYS.between(LocalDate.now(), task.deadline);

            String status = (days < 0)
                    ? Math.abs(days) + " DAYS OVERDUE"
                    : days + " DAYS REMAINING";

            System.out.println("[" + id + "] " + task.name + " -> "
                    + task.description + " -> {"
                    + task.deadline.format(formatter)
                    + "} -> [" + status + "]");

            System.out.println("================================================================================================");
            id++;
        }

        System.out.println();
        System.out.println();
    }

    /* ================= FILE HANDLING ================= */

    // Section: Persist the data used to active.
    public static void saveActive() {

        try (BufferedWriter bw =
                     new BufferedWriter(new FileWriter(ACTIVE_FILE))) {

            for (Task t : activeTasks) {

                bw.write(t.name + "|" +
                        t.description + "|" +
                        t.deadline.format(formatter));

                bw.newLine();
            }

        // Section: Handle the logic for catch.
        } catch (IOException e) {
            System.out.println("Error saving active tasks: "
                    + e.getMessage());
        }
    }

    // Section: Read and prepare the data used to active.
    public static void loadActive() {

        activeTasks.clear();

        File file = new File(ACTIVE_FILE);

        if (!file.exists()) return;

        try (BufferedReader br =
                     new BufferedReader(new FileReader(ACTIVE_FILE))) {

            String line;

            while ((line = br.readLine()) != null) {

                String[] parts = line.split("\\|");

                if (parts.length < 3) continue;

                String name = parts[0];
                String description = parts[1];
                LocalDate deadline =
                        LocalDate.parse(parts[2], formatter);

                activeTasks.add(
                        new Task(name, description, deadline)
                );
            }

        // Section: Handle the logic for catch.
        } catch (IOException e) {
            System.out.println("Error loading active tasks: "
                    + e.getMessage());
        }
    }
}
