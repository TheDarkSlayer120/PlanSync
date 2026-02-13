import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class PlanSyncCompletedTasks {

    private static final String COMPLETED_FILE = "completed_tasks.txt";
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Keep order stable
    private static final Map<String, CompletedTask> completedTasks = new LinkedHashMap<>();

    static class CompletedTask {
        String id;          // e.g. C1
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
            System.out.println("1. Delete Completed Task");
            System.out.println("2. Empty Whole List");
            System.out.println();
            System.out.println("3. Go to Timer");
            System.out.println("4. Go to Stopwatch");
            System.out.println("5. Go to Time Calculator");
            System.out.println("6. Go to Calendar");
            System.out.println("7. Go to Active Tasks");
            System.out.println("0. Main Menu");
            System.out.print("\nChoose Option: ");

            String choice = ConsoleUtils.scanner.nextLine().trim();

            switch (choice) {
                case "1" -> deleteCompletedTask();
                case "2" -> emptyCompletedList();
                case "3" -> { saveCompleted(); return Navigation.TIMER; }
                case "4" -> { saveCompleted(); return Navigation.STOPWATCH; }
                case "5" -> { saveCompleted(); return Navigation.TIME_CALCULATOR; }
                case "6" -> { saveCompleted(); return Navigation.CALENDAR; }
                case "7" -> { saveCompleted(); return Navigation.ACTIVE_TASKS; }
                case "0" -> { saveCompleted(); return Navigation.MAIN; }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    /* ================= DISPLAY ================= */

    private static void displayCompletedList() {
        LocalDate today = LocalDate.now();

        System.out.println("\n--- COMPLETED TASKS ---\n");
        System.out.println("======================================================================");
        System.out.println("(Format: | # | Task Name | Task Description | Deadline | Date Completed |)");
        System.out.println("======================================================================\n");

        System.out.printf("\t\t<<COMPLETED TASKS:>> (TODAY: %s)%n", today.format(DATE_FMT));

        if (completedTasks.isEmpty()) {
            System.out.println("\nNo completed tasks yet.");
            return;
        }

        int index = 1;
        for (CompletedTask t : completedTasks.values()) {
            String line = String.format(
                    "[%d] %s -> %s -> {%s} -> [COMPLETED %s] = [V]",
                    index,
                    t.name,
                    t.description,
                    t.deadline != null ? t.deadline.format(DATE_FMT) : "--/--/----",
                    t.completedOn != null ? t.completedOn.format(DATE_FMT) : "--/--/----"
            );
            System.out.println("===========================================================================================================");
            System.out.println(line);
            if (index == completedTasks.size()) {
                System.out.println("===========================================================================================================");
            }
            index++;
        }
    }

    /* ================= DELETE ONE ================= */

    private static void deleteCompletedTask() {
        if (completedTasks.isEmpty()) {
            System.out.println("\nNo completed tasks to delete.");
            return;
        }

        while (true) {
            System.out.print("\nChoose Task to Delete (number, e.g. 1), or Enter 0 to Cancel: ");
            String input = ConsoleUtils.scanner.nextLine().trim();
            if (input.equals("0")) return;

            int idx;
            try {
                idx = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number.");
                continue;
            }

            if (idx < 1 || idx > completedTasks.size()) {
                System.out.println("Invalid index.");
                continue;
            }

            CompletedTask target = getCompletedByIndex(idx);
            if (target == null) {
                System.out.println("Task not found.");
                return;
            }

            System.out.print("\nAre You Sure? [Y/N]: ");
            String confirm = ConsoleUtils.scanner.nextLine().trim().toUpperCase(Locale.ROOT);
            if (!confirm.equals("Y")) {
                System.out.println("Cancelled.");
                return;
            }

            completedTasks.remove(target.id);
            saveCompleted();
            System.out.println("\nTask Deleted!");
            System.out.println("(" + target.name + ")");
            return;
        }
    }

    /* ================= EMPTY LIST ================= */

    private static void emptyCompletedList() {
        if (completedTasks.isEmpty()) {
            System.out.println("\nNo completed tasks to clear.");
            return;
        }

        System.out.print("\nAre You Sure You Want To Delete ALL Completed Tasks? [Y/N]: ");
        String confirm = ConsoleUtils.scanner.nextLine().trim().toUpperCase(Locale.ROOT);
        if (!confirm.equals("Y")) {
            System.out.println("Cancelled.");
            return;
        }

        completedTasks.clear();
        saveCompleted();
        System.out.println("\nAll completed tasks deleted.");
    }

    /* ================= HELPERS ================= */

    private static CompletedTask getCompletedByIndex(int index) {
        int i = 1;
        for (CompletedTask t : completedTasks.values()) {
            if (i++ == index) return t;
        }
        return null;
    }

    /* ================= PERSISTENCE ================= */

    private static void loadCompleted() {
        completedTasks.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(COMPLETED_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                // id|name|description|deadline|completedOn
                String[] p = line.split("\\|", -1);
                if (p.length < 5) continue;

                CompletedTask t = new CompletedTask();
                t.id = p[0];
                t.name = p[1];
                t.description = p[2];
                t.deadline = p[3].isEmpty() ? null : LocalDate.parse(p[3], DATE_FMT);
                t.completedOn = p[4].isEmpty() ? null : LocalDate.parse(p[4], DATE_FMT);

                completedTasks.put(t.id, t);
            }
        } catch (IOException ignored) {
        }
    }

    private static void saveCompleted() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(COMPLETED_FILE))) {
            for (CompletedTask t : completedTasks.values()) {
                String deadline = t.deadline == null ? "" : t.deadline.format(DATE_FMT);
                String completed = t.completedOn == null ? "" : t.completedOn.format(DATE_FMT);

                bw.write(t.id + "|" + t.name + "|" + t.description + "|" + deadline + "|" + completed);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving completed tasks: " + e.getMessage());
        }
    }

    /* ================= API FOR ACTIVE TASKS ================= */

    // Call this from PlanSyncActiveTasks when marking a task complete
    public static void addCompleted(String name, String description, LocalDate deadline, LocalDate completedOn) {
        loadCompleted(); // refresh from file
        String id = "C" + (completedTasks.size() + 1);

        CompletedTask t = new CompletedTask();
        t.id = id;
        t.name = name;
        t.description = description;
        t.deadline = deadline;
        t.completedOn = (completedOn != null ? completedOn : LocalDate.now());

        completedTasks.put(t.id, t);
        saveCompleted();
    }
}
