import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class PlanSyncActiveTasks {

    public static ArrayList<Task> activeTasks = new ArrayList<>();
    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static class Task {
        String name;
        String description;
        LocalDate deadline;

        public Task(String name, String description, LocalDate deadline) {
            this.name = name;
            this.description = description;
            this.deadline = deadline;
        }
    }

    public static Navigation run() {

        while (true) {

            displayActiveTasks();

            System.out.println("1. Display Active Tasks");
            System.out.println("2. Mark Task(s) as Complete");
            System.out.println("3. Add a New Active Task");
            System.out.println("4. Delete Active Task(s)");
            System.out.println();
            System.out.println("5. Go to Recurring Tasks");
            System.out.println("6. Go to Completed Tasks");
            System.out.println();
            System.out.println("7. Go to Timer");
            System.out.println("8. Go to Stopwatch");
            System.out.println("9. Go to Time Calculator");
            System.out.println("10. Go to Calendar");
            System.out.println("0. Main Menu");
            System.out.println();
            System.out.print("Choose Option: ");

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
                    return Navigation.RECURRING_TASKS;
                case "6":
                    return Navigation.COMPLETED_TASKS;
                case "7":
                    return Navigation.TIMER;
                case "8":
                    return Navigation.STOPWATCH;
                case "9":
                    return Navigation.TIME_CALCULATOR;
                case "10":
                    return Navigation.CALENDAR;
                case "0":
                    return Navigation.MAIN;
            }
        }
    }

    public static void displayActiveTasks() {

        System.out.println("\n--- ACTIVE TASKS ---");
        System.out.println("\n===============================================================================");
        System.out.println("(Format: | TaskID | Task Name | Task Description | Deadline | Days Remaining |)");
        System.out.println("===============================================================================");
        System.out.println("\n                <<ACTIVE TASKS:>> (TODAY: " + LocalDate.now().format(formatter) + ")");
        System.out.println("================================================================================================");

        int id = 1;

        for (Task task : activeTasks) {

            long days = ChronoUnit.DAYS.between(LocalDate.now(), task.deadline);
            String status = (days < 0)
                    ? Math.abs(days) + " DAYS OVERDUE"
                    : days + " DAYS REMAINING";

            System.out.println("[" + id + "] " + task.name + " -> " + task.description +
                    " -> {" + task.deadline.format(formatter) + "} -> [" + status + "]");
            System.out.println("================================================================================================");
            id++;
        }

        System.out.println();
        System.out.println();
    }
}
