package modelTerminal;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

public class PlanSyncCompletedTasks {

    private static final String COMPLETED_FILE = "completed_tasks.txt";
    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Keep order stable
    private static final Map<String, CompletedTask> completedTasks =
            new LinkedHashMap<>();

    public static class CompletedTask {
        String id;
        String name;
        String description;
        LocalDate deadline;
        LocalDate completedOn;
    }

    public static Navigation run() {
        loadCompleted();

        while (true) {
            displayCompletedList();

            System.out.println("\n--- COMPLETED TASKS ---");
            System.out.println("1. Delete Completed Task(s)");
            System.out.println("2. Empty Whole List");
            System.out.println("\n3. Go to Active Tasks");
            System.out.println("4. Go to Recurring Tasks");
            System.out.println("\n5. Go to Timer");
            System.out.println("6. Go to Stopwatch");
            System.out.println("7. Go to Time Calculator");
            System.out.println("8. Go to Calendar");
            System.out.println("\n0. Main Menu");
            System.out.print("\nChoose Option: ");

            String choice = ConsoleUtils.scanner.nextLine().trim();

            switch (choice) {
                case "1" -> PlanSyncDeleteCompletedTasks.deleteCompletedTasks();
                case "2" -> PlanSyncDeleteCompletedTasks.emptyCompletedTasks();
                case "3" -> { saveCompleted(); return Navigation.ACTIVE_TASKS; }
                case "4" -> { saveCompleted(); return Navigation.RECURRING_TASKS; }
                case "5" -> { saveCompleted(); return Navigation.TIMER; }
                case "6" -> { saveCompleted(); return Navigation.STOPWATCH; }
                case "7" -> { saveCompleted(); return Navigation.TIME_CALCULATOR; }
                case "8" -> { saveCompleted(); return Navigation.CALENDAR; }
                case "0" -> { saveCompleted(); return Navigation.MAIN; }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    /* ================= PUBLIC API ================= */

    public static Map<String, CompletedTask> getCompletedTasks() {
        return completedTasks;
    }

    public static CompletedTask getByIndex(int index) {
        int i = 1;
        for (CompletedTask t : completedTasks.values()) {
            if (i++ == index) return t;
        }
        return null;
    }

    public static void removeById(String id) {
        completedTasks.remove(id);
    }

    public static void clearAll() {
        completedTasks.clear();
    }

    public static void addCompleted(String name, String description,
                                    LocalDate deadline, LocalDate completedOn) {
        loadCompleted();

        String id = "C" + (completedTasks.size() + 1);

        CompletedTask t = new CompletedTask();
        t.id = id;
        t.name = name;
        t.description = description;
        t.deadline = deadline;
        t.completedOn = completedOn;

        completedTasks.put(t.id, t);
        saveCompleted();
    }

    /* ================= DISPLAY ================= */

    public static void displayCompletedList() {
        loadCompleted();

        LocalDate today = LocalDate.now();

        System.out.println("\n--- COMPLETED TASKS ---\n");
        System.out.println("==========================================================================");
        System.out.println("(Format: | # | Task Name | Task Description | Deadline | Date Completed |)");
        System.out.println("==========================================================================\n");

        System.out.printf("\t\t<<COMPLETED TASKS:>> (TODAY: %s)%n",
                today.format(DATE_FMT));

        if (completedTasks.isEmpty()) {
            System.out.println("\nNo completed tasks yet.");
            return;
        }

        int index = 1;
        for (CompletedTask t : completedTasks.values()) {

            System.out.println("===========================================================================================================");
            System.out.printf("[%d] %s -> %s -> {%s} -> [COMPLETED %s] = [V]%n",
                    index++,
                    t.name,
                    t.description,
                    t.deadline != null ? t.deadline.format(DATE_FMT) : "--/--/----",
                    t.completedOn != null ? t.completedOn.format(DATE_FMT) : "--/--/----"
            );
        }

        System.out.println("===========================================================================================================");
    }

    /* ================= FILE HANDLING ================= */

    private static void loadCompleted() {
        completedTasks.clear();
        try (BufferedReader br =
                     new BufferedReader(new FileReader(COMPLETED_FILE))) {

            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split("\\|", -1);
                if (p.length < 5) continue;

                CompletedTask t = new CompletedTask();
                t.id = p[0];
                t.name = p[1];
                t.description = p[2];
                t.deadline = p[3].isEmpty() ? null :
                        LocalDate.parse(p[3], DATE_FMT);
                t.completedOn = p[4].isEmpty() ? null :
                        LocalDate.parse(p[4], DATE_FMT);

                completedTasks.put(t.id, t);
            }
        } catch (IOException ignored) {}
    }

    private static void saveCompleted() {
        try (BufferedWriter bw =
                     new BufferedWriter(new FileWriter(COMPLETED_FILE))) {

            for (CompletedTask t : completedTasks.values()) {

                String deadline = t.deadline == null ?
                        "" : t.deadline.format(DATE_FMT);
                String completed = t.completedOn == null ?
                        "" : t.completedOn.format(DATE_FMT);

                bw.write(t.id + "|" + t.name + "|" +
                        t.description + "|" + deadline + "|" + completed);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving completed tasks: "
                    + e.getMessage());
        }
    }
}
