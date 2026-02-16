import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class PlanSyncRecurringTasks {

    public static ArrayList<RecurringTask> recurringTasks = new ArrayList<>();
    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static class RecurringTask {
        String name;
        String description;
        String timeDate;
        String frequency;

        public RecurringTask(String name, String description, String timeDate, String frequency) {
            this.name = name;
            this.description = description;
            this.timeDate = timeDate;
            this.frequency = frequency;
        }
    }

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

    public static void displayRecurringTasks() {

        System.out.println("\n--- RECURRING TASKS ---");
        System.out.println("\n===========================================================================");
        System.out.println("(Format: | TaskID | Task Name | Task Description | Time/Date | Frequency |)");
        System.out.println("===========================================================================");
        System.out.println("\n                <<ACTIVE RECURRING:>> (TODAY: " + LocalDate.now().format(formatter) + ")");
        System.out.println("============================================================================");

        int id = 1;

        for (RecurringTask task : recurringTasks) {

            System.out.println("[" + id + "] " + task.name + " -> " + task.description +
                    " -> {" + task.timeDate + "} -> [" + task.frequency + "]");
            System.out.println("============================================================================");
            id++;
        }

        System.out.println();
        System.out.println();
    }
}
