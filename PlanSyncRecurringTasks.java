import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class PlanSyncRecurringTasks {

    public static ArrayList<RecurringTask> recurringTasks = new ArrayList<>();
    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /* ================= TASK CLASS ================= */

    public static class RecurringTask {
        String name;
        String description;
        String timeDate;   // Stores time/date info depending on frequency
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

    public static Navigation run() {

        while (true) {

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
                    return Navigation.ACTIVE_TASKS;
                case "5":
                    return Navigation.COMPLETED_TASKS;
                case "6":
                    return Navigation.TIMER;
                case "7":
                    return Navigation.STOPWATCH;
                case "8":
                    return Navigation.TIME_CALCULATOR;
                case "9":
                    return Navigation.CALENDAR;
                case "0":
                    return Navigation.MAIN;
            }
        }
    }

    /* ================= DISPLAY METHOD ================= */

    public static void displayRecurringTasks() {

        System.out.println("\n--- RECURRING TASKS ---");
        System.out.println("\n====================================================================================");
        System.out.println("(Format: | TaskID | Task Name | Task Description | Time/Date | Frequency | Status |)");
        System.out.println("====================================================================================");
        System.out.println("\n                <<ACTIVE RECURRING:>> (TODAY: "
                + LocalDate.now().format(formatter) + ")");
        System.out.println("============================================================================");

        int id = 1;
        LocalDate today = LocalDate.now();

        for (RecurringTask task : recurringTasks) {

            String status;

            try {

                switch (task.frequency) {

                    /* ================= DAILY ================= */
                    case "DAILY" -> status = "REPEATS DAILY";

                    /* ================= WEEKLY ================= */
                    case "WEEKLY" -> {
                        // Format: "HH:mm MONDAY"
                        String[] parts = task.timeDate.split(" ");
                        String dayName = parts[1];
                        DayOfWeek targetDay = DayOfWeek.valueOf(dayName);

                        int daysUntil =
                                (targetDay.getValue() - today.getDayOfWeek().getValue() + 7) % 7;
                        if (daysUntil == 0) daysUntil = 7;

                        status = daysUntil + " DAYS UNTIL NEXT";
                    }

                    /* ================= MONTHLY ================= */
                    case "MONTHLY" -> {
                        // Only take the "DD/MM" part, ignore "(Start Month: ...)"
                        String datePart = task.timeDate.split(" ")[0];
                        String[] parts = datePart.split("/");
                        int day = Integer.parseInt(parts[0]);
                        int month = Integer.parseInt(parts[1]);

                        LocalDate next = LocalDate.of(today.getYear(), month, day);
                        if (!next.isAfter(today)) {
                            next = next.plusMonths(1);
                        }

                        long days = ChronoUnit.DAYS.between(today, next);
                        status = days + " DAYS UNTIL NEXT";
                    }

                    /* ================= YEARLY ================= */
                    case "YEARLY" -> {
                        // Format: "DD/MM (Start: yyyy)"
                        String[] parts = task.timeDate.split(" ");
                        String[] dateParts = parts[0].split("/");

                        int day = Integer.parseInt(dateParts[0]);
                        int month = Integer.parseInt(dateParts[1]);

                        LocalDate next = LocalDate.of(today.getYear(), month, day);
                        if (!next.isAfter(today)) {
                            next = next.plusYears(1);
                        }

                        long days = ChronoUnit.DAYS.between(today, next);
                        status = days + " DAYS UNTIL NEXT";
                    }

                    default -> status = "UNKNOWN";
                }

            } catch (Exception e) {
                status = "INVALID DATA";
            }

            System.out.println("[" + id + "] "
                    + task.name + " -> "
                    + task.description + " -> {"
                    + task.timeDate + "} -> ["
                    + task.frequency + "] -> ["
                    + status + "]");

            System.out.println("============================================================================");
            id++;
        }

        System.out.println();
        System.out.println();
    }
}
