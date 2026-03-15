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
 * File purpose: This class supports the PlanSyncRecurringTasks part of PlanSync and documents the main responsibilities of the file.
 */

import java.io.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import util.AppPaths;

public class PlanSyncRecurringTasks {

    private static final String RECURRING_FILE = AppPaths.getDataFile("recurring_tasks.txt").getPath();

    public static ArrayList<RecurringTask> recurringTasks = new ArrayList<>();
    public static DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /* ================= TASK CLASS ================= */

    public static class RecurringTask {
        String name;
        String description;
        String timeDate;
        String frequency;

        public RecurringTask(String name,
                             String description,
                             String timeDate,
                             String frequency) {
            this.name = name;
            this.description = description;
            this.timeDate = timeDate;
            this.frequency = frequency;
        }
    }

    /* ================= NAVIGATION LOOP ================= */

    // Section: Handle the logic for run.
    public static Navigation run() {

        // Section: Read and prepare the data used to recurring.
        loadRecurring();

        while (true) {

            // Section: Handle the logic for display recurring tasks.
            displayRecurringTasks();

            System.out.println("1. Display Recurring Tasks");
            System.out.println("2. Add a New Recurring Task");
            System.out.println("3. Delete Recurring Task(s)");
            System.out.println("\n4. Go to Active Tasks");
            System.out.println("5. Go to Completed Tasks");
            System.out.println("\n6. Go to Timer");
            System.out.println("7. Go to Stopwatch");
            System.out.println("8. Go to Time Calculator");
            System.out.println("9. Go to Calendar");
            System.out.println("\n0. Main Menu");
            System.out.print("\nChoose Option: ");

            String choice = ConsoleUtils.scanner.nextLine();

            switch (choice) {
                case "2":
                    PlanSyncAddRecurringTask.addRecurringTask();
                    break;
                case "3":
                    PlanSyncDeleteRecurringTasks.deleteRecurringTasks();
                    break;
                case "4":
                    // Section: Persist the data used to recurring.
                    saveRecurring();
                    return Navigation.ACTIVE_TASKS;
                case "5":
                    // Section: Persist the data used to recurring.
                    saveRecurring();
                    return Navigation.COMPLETED_TASKS;
                case "6":
                    // Section: Persist the data used to recurring.
                    saveRecurring();
                    return Navigation.TIMER;
                case "7":
                    // Section: Persist the data used to recurring.
                    saveRecurring();
                    return Navigation.STOPWATCH;
                case "8":
                    // Section: Persist the data used to recurring.
                    saveRecurring();
                    return Navigation.TIME_CALCULATOR;
                case "9":
                    // Section: Persist the data used to recurring.
                    saveRecurring();
                    return Navigation.CALENDAR;
                case "0":
                    // Section: Persist the data used to recurring.
                    saveRecurring();
                    return Navigation.MAIN;
            }
        }
    }

    /* ================= DISPLAY METHOD ================= */

    // Section: Handle the logic for display recurring tasks.
    public static void displayRecurringTasks() {

        System.out.println("\n--- RECURRING TASKS ---");
        System.out.println("\n====================================================================================");
        System.out.println("(Format: | # | Task Name | Task Description | Time/Date | Frequency | Status |)");
        System.out.println("====================================================================================");

        System.out.println("\n<<ACTIVE RECURRING:>> (TODAY: "
                + LocalDate.now().format(formatter) + ")");
        System.out.println("============================================================================");

        if (recurringTasks.isEmpty()) {
            System.out.println("\nNo recurring tasks found.");
            return;
        }

        int id = 1;
        LocalDate today = LocalDate.now();

        for (RecurringTask task : recurringTasks) {

            String status;

            try {

                switch (task.frequency) {

                    case "DAILY" -> status = "REPEATS DAILY";

                    case "WEEKLY" -> {
                        String[] parts = task.timeDate.split(" ");
                        String dayName = parts[1];
                        DayOfWeek targetDay = DayOfWeek.valueOf(dayName);

                        int daysUntil =
                                (targetDay.getValue()
                                        - today.getDayOfWeek().getValue()
                                        + 7) % 7;

                        if (daysUntil == 0) daysUntil = 7;

                        status = daysUntil + " DAYS UNTIL NEXT";
                    }

                    case "MONTHLY" -> {

                        String datePart = task.timeDate.split(" ")[0];
                        String[] parts = datePart.split("/");

                        int day = Integer.parseInt(parts[0]);
                        int month = Integer.parseInt(parts[1]);

                        LocalDate next;

                        if (month > today.getMonthValue()) {
                            next = LocalDate.of(today.getYear(), month, day);

                        // Section: Handle the logic for if.
                        } else if (month < today.getMonthValue()) {
                            next = LocalDate.of(today.getYear() + 1, month, day);

                        } else {
                            if (day > today.getDayOfMonth()) {
                                next = LocalDate.of(today.getYear(), month, day);
                            } else {
                                next = LocalDate.of(today.getYear() + 1, month, day);
                            }
                        }

                        long days = ChronoUnit.DAYS.between(today, next);
                        status = days + " DAYS UNTIL NEXT";
                    }

                    case "YEARLY" -> {
                        String[] parts = task.timeDate.split(" ");
                        String[] dateParts = parts[0].split("/");

                        int day = Integer.parseInt(dateParts[0]);
                        int month = Integer.parseInt(dateParts[1]);

                        LocalDate next =
                                LocalDate.of(today.getYear(), month, day);

                        if (!next.isAfter(today)) {
                            next = next.plusYears(1);
                        }

                        long days =
                                ChronoUnit.DAYS.between(today, next);

                        status = days + " DAYS UNTIL NEXT";
                    }

                    default -> status = "UNKNOWN";
                }

            // Section: Handle the logic for catch.
            } catch (Exception e) {
                status = "INVALID DATA";
            }

            // 🔥 Format MONTHLY display with month name
            String displayDate = task.timeDate;

            if (task.frequency.equals("MONTHLY")) {
                displayDate = formatMonthlyDisplay(task.timeDate);
            }

            System.out.println("[" + id + "] "
                    + task.name + " -> "
                    + task.description + " -> {"
                    + displayDate + "} -> ["
                    + task.frequency + "] -> ["
                    + status + "]");

            System.out.println("============================================================================");
            id++;
        }

        System.out.println();
        System.out.println();
    }

    /* ================= FORMAT MONTH NAME ================= */

    // Section: Handle the logic for format monthly display.
    private static String formatMonthlyDisplay(String timeDate) {

        try {
            String datePart = timeDate.split(" ")[0];
            String[] parts = datePart.split("/");

            int day = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);

            LocalDate temp = LocalDate.of(2000, month, day);

            return temp.format(DateTimeFormatter.ofPattern("d MMMM"));

        // Section: Handle the logic for catch.
        } catch (Exception e) {
            return timeDate;
        }
    }

    /* ================= FILE HANDLING ================= */

    // Section: Persist the data used to recurring.
    public static void saveRecurring() {

        try (BufferedWriter bw =
                     new BufferedWriter(new FileWriter(RECURRING_FILE))) {

            for (RecurringTask t : recurringTasks) {

                bw.write(t.name + "|" +
                        t.description + "|" +
                        t.timeDate + "|" +
                        t.frequency);

                bw.newLine();
            }

        // Section: Handle the logic for catch.
        } catch (IOException e) {
            System.out.println("Error saving recurring tasks: "
                    + e.getMessage());
        }
    }

    // Section: Read and prepare the data used to recurring.
    public static void loadRecurring() {

        recurringTasks.clear();

        File file = new File(RECURRING_FILE);

        if (!file.exists()) return;

        try (BufferedReader br =
                     new BufferedReader(new FileReader(RECURRING_FILE))) {

            String line;

            while ((line = br.readLine()) != null) {

                String[] parts = line.split("\\|");

                if (parts.length < 4) continue;

                recurringTasks.add(
                        new RecurringTask(
                                parts[0],
                                parts[1],
                                parts[2],
                                parts[3]
                        )
                );
            }

        // Section: Handle the logic for catch.
        } catch (IOException e) {
            System.out.println("Error loading recurring tasks: "
                    + e.getMessage());
        }
    }
}
