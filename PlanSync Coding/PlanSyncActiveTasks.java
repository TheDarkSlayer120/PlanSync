import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class PlanSyncActiveTasks {

    private static final String ACTIVE_FILE = "active_tasks.txt";
    private static final String RECURRING_FILE = "active_recurring.txt";

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    // Keep insertion order stable like examples
    private static final Map<String, ActiveTask> activeTasks = new LinkedHashMap<>();
    private static final Map<String, RecurringTask> recurringTasks = new LinkedHashMap<>();

    static class ActiveTask {
        String id;
        String name;
        String description;
        LocalDate deadline;
    }

    static class RecurringTask {
        String id;
        String name;
        String description;
        RecurringType type;   // DAILY, WEEKLY, MONTHLY, YEARLY
        LocalTime time;       // for DAILY/WEEKLY
        String dayOfWeek;     // for WEEKLY (e.g. FRIDAY)
        Integer dayOfMonth;   // for MONTHLY (1–31)
        LocalDate date;       // for YEARLY/MONTHLY with explicit date
    }

    enum RecurringType {
        DAILY, WEEKLY, MONTHLY, YEARLY
    }

    public static Navigation run() {
        loadActiveTasks();
        loadRecurringTasks();

        while (true) {
            System.out.println("\n--- ACTIVE TASKS ---");
            System.out.println("1. Display Active Tasks");
            System.out.println("2. Display Active Recurring");
            System.out.println("3. Add a New Task");
            System.out.println("4. Delete a Task");
            System.out.println("\n5. Go to Timer");
            System.out.println("6. Go to Stopwatch");
            System.out.println("7. Go to Time Calculator");
            System.out.println("8. Go to Calendar");
            System.out.println("9. Go to Completed Tasks");
            System.out.println("0. Main Menu");
            System.out.print("\nChoose Option: ");

            String choice = ConsoleUtils.scanner.nextLine().trim();

            switch (choice) {
                case "1" -> {
                    Navigation next = showActiveTasksMenu();
                    return next;
                }
                case "2" -> showRecurringTasks();
                case "3" -> addTaskMenu();
                case "4" -> deleteTaskMenu();
                case "5" -> { saveAll(); return Navigation.TIMER; }
                case "6" -> { saveAll(); return Navigation.STOPWATCH; }
                case "7" -> { saveAll(); return Navigation.TIME_CALCULATOR; }
                case "8" -> { saveAll(); return Navigation.CALENDAR; }
                case "9" -> { saveAll(); return Navigation.COMPLETED_TASKS; }
                case "0" -> { saveAll(); return Navigation.MAIN; }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    /* ================= MAIN ACTIVE TASKS MENU (AFTER DISPLAY) ================= */

    private static Navigation showActiveTasksMenu() {
        displayActiveTasks();

        while (true) {
            System.out.println("\n--- ACTIVE TASKS ---");
            System.out.println("1. Mark Task As Complete (ACTIVE TASKS ONLY)");
            System.out.println("2. Add a New Task");
            System.out.println("3. Delete a Task");
            System.out.println("\n4. Display Active Tasks");
            System.out.println("5. Display Active Recurring ");
            System.out.println("\n6. Go to Completed Tasks"); 
            System.out.println("0. Go Back");
            System.out.print("\nChoose Option: ");

            String choice = ConsoleUtils.scanner.nextLine().trim();
            
            switch (choice) {
                case "1" -> markTaskComplete();
                case "2" -> addTaskMenu();
                case "3" -> deleteTaskMenu();
                case "4" -> displayActiveTasks();
                case "5" -> showRecurringTasks();
                case "6" -> { 
                    saveAll(); 
                    return Navigation.COMPLETED_TASKS;  
                    }
                    case "0" -> {
                        saveAll();
                        return Navigation.ACTIVE_TASKS;  
                        }
                        default -> System.out.println("Invalid option.");
                    }
                }
            }


    /* ================= DISPLAY ACTIVE TASKS ================= */

    private static void displayActiveTasks() {
        LocalDate today = LocalDate.now();
        System.out.println("\n=============================================================================== ");
        System.out.println("(Format: | TaskID | Task Name | Task Description | Deadline | Days Remaining |)");
        System.out.println("===============================================================================\n");

        System.out.printf("\t\t<<ACTIVE TASKS:>> (TODAY: %s)%n", today.format(DATE_FMT));

        if (activeTasks.isEmpty()) {
            System.out.println("\nNo active tasks.");
            return;
        }

        int index = 1;
        for (ActiveTask task : activeTasks.values()) {
            long days = ChronoUnit.DAYS.between(today, task.deadline);
            String status;
            if (days == 0) {
                status = "TODAY";
            } else if (days == 1) {
                status = "1 DAYS REMAINING";
            } else if (days < 0) {
                status = Math.abs(days) + " DAYS OVERDUE";
            } else {
                status = days + " DAYS REMAINING";
            }

            String line = String.format("[%d] %s -> %s -> {%s} -> [%s]",
                    index++,
                    task.name,
                    task.description,
                    task.deadline.format(DATE_FMT),
                    status);
            System.out.println("================================================================================================");
            System.out.println(line);
        }
        System.out.println("================================================================================================");
    }

    /* ================= DISPLAY RECURRING ================= */

    private static void showRecurringTasks() {
        LocalDate today = LocalDate.now();
        System.out.println("\n================================================================================");
        System.out.println("(Format: | TaskID | Task Name | Task Description | Time/Date | Days Remaining |)");
        System.out.println("================================================================================\n");

        System.out.printf("\t\t<<ACTIVE RECURRING:>> (TODAY: %s)%n", today.format(DATE_FMT));

        if (recurringTasks.isEmpty()) {
            System.out.println("\nNo active recurring tasks.");
            return;
        }

        int index = 1;
        for (RecurringTask rt : recurringTasks.values()) {
            String when;
            String freq;
            switch (rt.type) {
                case DAILY -> {
                    when = String.format("{%s}", rt.time.format(TIME_FMT));
                    freq = "DAILY";
                }
                case WEEKLY -> {
                    when = String.format("{%s %s}", rt.time.format(TIME_FMT), rt.dayOfWeek);
                    freq = "WEEKLY";
                }
                case MONTHLY -> {
                    String d = (rt.date != null ? rt.date.format(DATE_FMT)
                                                : String.format("%02d", rt.dayOfMonth));
                    when = String.format("{%s}", d);
                    freq = "MONTHLY";
                }
                case YEARLY -> {
                    when = String.format("{%s}", rt.date.format(DATE_FMT));
                    freq = "YEARLY";
                }
                default -> {
                    when = "{}";
                    freq = "";
                }
            }

            String line = String.format("[%d] %s -> %s -> %s -> [%s]",
                    index++,
                    rt.name,
                    rt.description,
                    when,
                    freq);
            System.out.println("============================================================================");
            System.out.println(line);
        }
        System.out.println("============================================================================");
    }

    /* ================= MARK MULTIPLE COMPLETE ================= */

    private static void markTaskComplete() {
        if (activeTasks.isEmpty()) {
            System.out.println("\nNo active tasks to complete.");
            return;
        }

        displayActiveTasks();

        while (true) {
            System.out.print(
                "\nChoose Task(s) to Complete (e.g. 1 2 3), or Enter 0 to Cancel: "
            );
            String inputLine = ConsoleUtils.scanner.nextLine().trim();
            if (inputLine.equals("0")) return;

            String[] tokens = inputLine.split("\\s+");
            if (tokens.length == 0) {
                System.out.println("No Task IDs entered.");
                continue;
            }

            // Collect valid targets in order, avoid duplicates
            Map<String, ActiveTask> toComplete = new LinkedHashMap<>();
            boolean anyInvalid = false;

            for (String token : tokens) {
                int idx = parseTaskIndex(token);
                if (idx == -1 || idx > activeTasks.size()) {
                    System.out.println("Invalid Task ID: " + token);
                    anyInvalid = true;
                    continue;
                }
                ActiveTask target = getActiveByIndex(idx);
                if (target == null) {
                    System.out.println("Task not found for ID: " + token);
                    anyInvalid = true;
                    continue;
                }
                toComplete.putIfAbsent(target.id, target);
            }

            if (toComplete.isEmpty()) {
                if (anyInvalid) {
                    System.out.println("No valid Task IDs entered. Try again.");
                    continue;
                } else {
                    System.out.println("No tasks selected.");
                    return;
                }
            }

            // Show summary and confirm
            System.out.println("\nYou are about to mark the following tasks as COMPLETE:");
            for (ActiveTask t : toComplete.values()) {
                System.out.println("- " + t.name + " (ID: " + t.id + ")");
            }

            System.out.print("\nAre You Sure? [Y/N]: ");
            String confirm = ConsoleUtils.scanner.nextLine().trim().toUpperCase(Locale.ROOT);
            if (!confirm.equals("Y")) {
                System.out.println("Cancelled.");
                return;
            }

            // Mark all selected tasks complete
            for (String id : toComplete.keySet()) {
                ActiveTask task = toComplete.get(id);
                PlanSyncCompletedTasks.addCompleted(
                    task.name,
                    task.description,
                    task.deadline,
                    LocalDate.now()
                );
                activeTasks.remove(id);
            }
            saveAll();

            System.out.println("\nTask(s) Marked Complete!");
            System.out.println("\nTasks moved to Completed Tasks:");
            for (ActiveTask task : toComplete.values()) {
                System.out.printf("- [%s] %s -> [COMPLETED %s]%n", 
                    task.name, task.description, LocalDate.now().format(DATE_FMT));
            }
            System.out.println("\nUpdated ACTIVE TASKS:\n");
            displayActiveTasks();
            return;
        }
    }


    /* ================= DELETE TASK ================= */

    private static void deleteTaskMenu() {
        System.out.println("\n--- ACTIVE TASKS ---");
        System.out.println("1. Active Task");
        System.out.println("2. Active Recurring ");
        System.out.println("0. Cancel");
        System.out.print("\nChoose Option: ");

        String choice = ConsoleUtils.scanner.nextLine().trim();
        switch (choice) {
            case "1" -> deleteActiveTask();
            case "2" -> deleteRecurringTask();
            case "0" -> {}
            default -> System.out.println("Invalid option.");
        }
    }

    private static void deleteActiveTask() {
        if (activeTasks.isEmpty()) {
            System.out.println("\nNo active tasks to delete.");
            return;
        }

        displayActiveTasks();

        while (true) {
            System.out.print(
                "\nChoose Task(s) to Delete (e.g. 1 2 3), or Enter 0 to Cancel: "
            );
            String inputLine = ConsoleUtils.scanner.nextLine().trim();
            if (inputLine.equals("0")) return;

            String[] tokens = inputLine.split("\\s+");
            if (tokens.length == 0) {
                System.out.println("No Task IDs entered.");
                continue;
            }

            // Collect valid targets in order, avoid duplicates
            Map<String, ActiveTask> toDelete = new LinkedHashMap<>();
            boolean anyInvalid = false;

            for (String token : tokens) {
                int idx = parseTaskIndex(token);
                if (idx == -1 || idx > activeTasks.size()) {
                    System.out.println("Invalid Task ID: " + token);
                    anyInvalid = true;
                    continue;
                }
                ActiveTask target = getActiveByIndex(idx);
                if (target == null) {
                    System.out.println("Task not found for ID: " + token);
                    anyInvalid = true;
                    continue;
                }
                toDelete.putIfAbsent(target.id, target);
            }

            if (toDelete.isEmpty()) {
                if (anyInvalid) {
                    System.out.println("No valid Task IDs entered. Try again.");
                    continue;
                } else {
                    System.out.println("No tasks selected.");
                    return;
                }
            }

            // Show summary and confirm
            System.out.println("\nYou are about to delete the following tasks:");
            for (ActiveTask t : toDelete.values()) {
                System.out.println("- " + t.name + " (ID: " + t.id + ")");
            }

            System.out.print("\nAre You Sure? [Y/N]: ");
            String confirm = ConsoleUtils.scanner.nextLine().trim().toUpperCase(Locale.ROOT);
            if (!confirm.equals("Y")) {
                System.out.println("Cancelled.");
                return;
            }

            // Delete all selected tasks
            for (String id : toDelete.keySet()) {
                activeTasks.remove(id);
            }
            saveAll();

            System.out.println("\nTask(s) Deleted!");
            System.out.println("\nUpdated ACTIVE TASKS:\n");
            displayActiveTasks();
            return;
        }
    }


    private static void deleteRecurringTask() {
        if (recurringTasks.isEmpty()) {
            System.out.println("\nNo recurring tasks to delete.");
            return;
        }

        showRecurringTasks();

        while (true) {
            System.out.print(
                "\nChoose Task(s) to Delete (e.g. 1 2 3), or Enter 0 to Cancel: "
            );
            String inputLine = ConsoleUtils.scanner.nextLine().trim();
            if (inputLine.equals("0")) return;

            String[] tokens = inputLine.split("\\s+");
            if (tokens.length == 0) {
                System.out.println("No Task IDs entered.");
                continue;
            }

            // Collect valid targets in order, avoid duplicates
            Map<String, RecurringTask> toDelete = new LinkedHashMap<>();
            boolean anyInvalid = false;

            for (String token : tokens) {
                int idx = parseTaskIndex(token);
                if (idx == -1 || idx > recurringTasks.size()) {
                    System.out.println("Invalid Task ID: " + token);
                    anyInvalid = true;
                    continue;
                }
                RecurringTask target = getRecurringByIndex(idx);
                if (target == null) {
                    System.out.println("Task not found for ID: " + token);
                    anyInvalid = true;
                    continue;
                }
                toDelete.putIfAbsent(target.id, target);
            }

            if (toDelete.isEmpty()) {
                if (anyInvalid) {
                    System.out.println("No valid Task IDs entered. Try again.");
                    continue;
                } else {
                    System.out.println("No tasks selected.");
                    return;
                }
            }

            // Show summary and confirm
            System.out.println("\nYou are about to delete the following tasks:");
            for (RecurringTask t : toDelete.values()) {
                System.out.println("- " + t.name + " (ID: " + t.id + ")");
            }

            System.out.print("\nAre You Sure? [Y/N]: ");
            String confirm = ConsoleUtils.scanner.nextLine().trim().toUpperCase(Locale.ROOT);
            if (!confirm.equals("Y")) {
                System.out.println("Cancelled.");
                return;
            }

            // Delete all selected tasks
            for (String id : toDelete.keySet()) {
                recurringTasks.remove(id);
            }
            saveAll();

            System.out.println("\nTask(s) Deleted!");
            System.out.println("\nUpdated ACTIVE RECURRING:\n");
            showRecurringTasks();
            return;
        }
    }


    /* ================= ADD TASKS ================= */

    private static void addTaskMenu() {
        System.out.println("\n--- ACTIVE TASKS ---");
        System.out.println("1. Active Task");
        System.out.println("2. Active Recurring");
        System.out.println("0. Cancel");
        System.out.print("\nChoose Option: ");

        String choice = ConsoleUtils.scanner.nextLine().trim();
        switch (choice) {
            case "1" -> addActiveTask();
            case "2" -> addRecurringTask();
            case "0" -> {}
            default -> System.out.println("Invalid option.");
        }
    }

    private static void addActiveTask() {
        System.out.print("\nTASK NAME: ");
        String name = ConsoleUtils.scanner.nextLine().trim();

        System.out.print("\nTASK DESCRIPTION: ");
        String desc = ConsoleUtils.scanner.nextLine().trim();

        LocalDate deadline;
        while (true) {
            System.out.print("\nDEADLINE DATE (DD/MM/YYYY): ");
            String ds = ConsoleUtils.scanner.nextLine().trim();
            try {
                deadline = LocalDate.parse(ds, DATE_FMT);
                break;
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format.");
            }
        }

        String id = String.valueOf(activeTasks.size() + 1);
        ActiveTask t = new ActiveTask();
        t.id = id;
        t.name = name;
        t.description = desc;
        t.deadline = deadline;
        activeTasks.put(id, t);

        saveAll();

        System.out.println("\nNew Task Added!\n");
        System.out.println("Updated ACTIVE TASKS:\n");
        displayActiveTasks();
    }

    private static void addRecurringTask() {
        System.out.print("\nTASK NAME: ");
        String name = ConsoleUtils.scanner.nextLine().trim();

        System.out.print("\nTASK DESCRIPTION: ");
        String desc = ConsoleUtils.scanner.nextLine().trim();

        System.out.println("\n==========");
        System.out.println("1. DAILY");
        System.out.println("2. WEEKLY");
        System.out.println("3. MONTHLY");
        System.out.println("4. YEARLY");
        System.out.println("==========");
        System.out.print("\nChoose: ");
        String choice = ConsoleUtils.scanner.nextLine().trim();

        RecurringTask rt = new RecurringTask();
        rt.name = name;
        rt.description = desc;

        switch (choice) {
            case "1" -> {
                rt.type = RecurringType.DAILY;
                rt.time = readTime("\nTIME (HH:MM): ");
            }
            case "2" -> {
                rt.type = RecurringType.WEEKLY;
                rt.time = readTime("\nTIME (HH:MM): ");
                System.out.print("\nDAY OF WEEK (e.g. FRIDAY): ");
                rt.dayOfWeek = ConsoleUtils.scanner.nextLine().trim().toUpperCase(Locale.ROOT);
            }
            case "3" -> {
                rt.type = RecurringType.MONTHLY;
                System.out.print("\nTASK DAY OF THE MONTH (DD): ");
                rt.dayOfMonth = Integer.parseInt(ConsoleUtils.scanner.nextLine().trim());
                System.out.print("\nNext Occurrence Date (DD/MM/YYYY): ");
                try {
                    rt.date = LocalDate.parse(ConsoleUtils.scanner.nextLine().trim(), DATE_FMT);
                } catch (Exception ignored) { }
            }
            case "4" -> {
                rt.type = RecurringType.YEARLY;
                System.out.print("\nDATE (DD/MM/YYYY): ");
                rt.date = LocalDate.parse(ConsoleUtils.scanner.nextLine().trim(), DATE_FMT);
            }
            default -> {
                System.out.println("Invalid option.");
                return;
            }
        }

        String id = String.valueOf(recurringTasks.size() + 1);
        rt.id = id;
        recurringTasks.put(id, rt);

        saveAll();

        System.out.println("\nNew Task Added!\n");
        System.out.println("Updated ACTIVE RECURRING:\n");
        showRecurringTasks();
    }

    /* ================= HELPERS ================= */

    private static int parseTaskIndex(String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static ActiveTask getActiveByIndex(int index) {
        int i = 1;
        for (ActiveTask t : activeTasks.values()) {
            if (i++ == index) return t;
        }
        return null;
    }

    private static RecurringTask getRecurringByIndex(int index) {
        int i = 1;
        for (RecurringTask t : recurringTasks.values()) {
            if (i++ == index) return t;
        }
        return null;
    }

    private static LocalTime readTime(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = ConsoleUtils.scanner.nextLine().trim();
            try {
                return LocalTime.parse(s, TIME_FMT);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid time format.");
            }
        }
    }

    /* ================= PERSISTENCE ================= */

    private static void loadActiveTasks() {
        activeTasks.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(ACTIVE_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                // id|name|description|date
                String[] parts = line.split("\\|", 4);
                if (parts.length < 4) continue;

                ActiveTask t = new ActiveTask();
                t.id = parts[0];
                t.name = parts[1];
                t.description = parts[2];
                t.deadline = LocalDate.parse(parts[3], DATE_FMT);
                activeTasks.put(t.id, t);
            }
        } catch (IOException ignored) {
        }
    }

    private static void loadRecurringTasks() {
        recurringTasks.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(RECURRING_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                // id|name|description|type|time|dayOfWeek|dayOfMonth|date
                String[] p = line.split("\\|", -1);
                if (p.length < 8) continue;

                RecurringTask rt = new RecurringTask();
                rt.id = p[0];
                rt.name = p[1];
                rt.description = p[2];
                rt.type = RecurringType.valueOf(p[3]);
                if (!p[4].isEmpty()) rt.time = LocalTime.parse(p[4], TIME_FMT);
                if (!p[5].isEmpty()) rt.dayOfWeek = p[5];
                if (!p[6].isEmpty()) rt.dayOfMonth = Integer.parseInt(p[6]);
                if (!p[7].isEmpty()) rt.date = LocalDate.parse(p[7], DATE_FMT);

                recurringTasks.put(rt.id, rt);
            }
        } catch (IOException ignored) {
        }
    }

    private static void saveAll() {
        saveActive();
        saveRecurring();
    }

    private static void saveActive() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ACTIVE_FILE))) {
            for (ActiveTask t : activeTasks.values()) {
                bw.write(t.id + "|" + t.name + "|" + t.description + "|" + t.deadline.format(DATE_FMT));
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving active tasks: " + e.getMessage());
        }
    }

    private static void saveRecurring() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(RECURRING_FILE))) {
            for (RecurringTask rt : recurringTasks.values()) {
                String time = rt.time == null ? "" : rt.time.format(TIME_FMT);
                String dow = rt.dayOfWeek == null ? "" : rt.dayOfWeek;
                String dom = rt.dayOfMonth == null ? "" : rt.dayOfMonth.toString();
                String date = rt.date == null ? "" : rt.date.format(DATE_FMT);

                bw.write(rt.id + "|" + rt.name + "|" + rt.description + "|" +
                         rt.type.name() + "|" + time + "|" + dow + "|" + dom + "|" + date);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving recurring tasks: " + e.getMessage());
        }
    }
}
