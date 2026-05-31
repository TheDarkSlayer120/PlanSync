package model;


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
 * File purpose: This class supports the PlanSyncActiveTasksModel part of PlanSync and documents the main responsibilities of the file.
 */

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;
import util.AppPaths;

public class PlanSyncActiveTasksModel {

    private static final File DATA_DIR = AppPaths.getDataDir().toFile();
    private static final File ACTIVE_FILE = AppPaths.getDataFile("active_tasks.txt");
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static class Task {
        public final String name;
        public final String description;
        public final LocalDate deadline;

        // Section: Handle the logic for task.
        public Task(String name, String description, LocalDate deadline) {
            this.name = name;
            this.description = description;
            this.deadline = deadline;
        }
    }

    private final List<Task> activeTasks = new ArrayList<>();

    // Section: Return the data used to tasks.
    public synchronized List<Task> getTasks() {
        // Section: Read and prepare the data used to load.
        load();
        return new ArrayList<>(activeTasks);
    }

    /**
     * Returns a list of active tasks due on the given date.
     * This is used by the Calendar screen for highlighting and popups.
     */
    // Section: Return the data used to tasks for date.
    public synchronized List<Task> getTasksForDate(LocalDate date) {
        if (date == null) return Collections.emptyList();
        // Section: Read and prepare the data used to load.
        load();
        List<Task> out = new ArrayList<>();
        for (Task t : activeTasks) {
            if (t != null && date.equals(t.deadline)) {
                out.add(t);
            }
        }
        return out;
    }

    /**
     * Returns how many active tasks are due on the given date.
     */
    // Section: Handle the logic for count tasks for date.
    public synchronized int countTasksForDate(LocalDate date) {
        if (date == null) return 0;
        // Section: Read and prepare the data used to load.
        load();
        int c = 0;
        for (Task t : activeTasks) {
            if (t != null && date.equals(t.deadline)) c++;
        }
        return c;
    }


/**
 * Returns the task by its 1-based ID as displayed in the UI.
 * @param id1Based 1..N
 * @return Task or null if out of range
 */
// Section: Return the data used to task by id.
public synchronized Task getTaskById(int id1Based) {
    // Section: Read and prepare the data used to load.
    load();
    int idx = id1Based - 1;
    if (idx < 0 || idx >= activeTasks.size()) return null;
    return activeTasks.get(idx);
}

public synchronized void updateTaskById(int id1Based, String name, String description, String deadlineDDMMYYYY)
        throws DateTimeParseException {
    LocalDate deadline = LocalDate.parse(deadlineDDMMYYYY, DATE_FMT);
    // Section: Refresh or recompute the state used to task by id.
    updateTaskById(id1Based, name, description, deadline);
}

// Section: Refresh or recompute the state used to task by id.
public synchronized void updateTaskById(int id1Based, String name, String description, LocalDate deadline) {
    // Section: Read and prepare the data used to load.
    load();
    int idx = id1Based - 1;
    if (idx < 0 || idx >= activeTasks.size()) {
        throw new IllegalArgumentException("Task ID out of range: " + id1Based);
    }
    activeTasks.set(idx, new Task(clean(name), clean(description), deadline));
    // Section: Persist the data used to save.
    save();
}

    public synchronized void addTask(String name, String description, String deadlineDDMMYYYY)
            throws DateTimeParseException {
        LocalDate deadline = LocalDate.parse(deadlineDDMMYYYY, DATE_FMT);
        // Section: Add the data or behavior needed to task.
        addTask(name, description, deadline);
    }

    // Section: Add the data or behavior needed to task.
    public synchronized void addTask(String name, String description, LocalDate deadline) {
        // Section: Read and prepare the data used to load.
        load();
        activeTasks.add(new Task(clean(name), clean(description), deadline));
        // Section: Persist the data used to save.
        save();
    }

    // Section: Remove the items involved in tasks by indexes.
    public synchronized void deleteTasksByIndexes(Collection<Integer> indexes0Based) {
        // Section: Read and prepare the data used to load.
        load();
        List<Integer> idx = new ArrayList<>();
        for (Integer i : indexes0Based) {
            if (i != null && i >= 0 && i < activeTasks.size() && !idx.contains(i)) idx.add(i);
        }
        idx.sort(Integer::compareTo);
        for (int i = idx.size() - 1; i >= 0; i--) activeTasks.remove((int) idx.get(i));
        // Section: Persist the data used to save.
        save();
    }

    public synchronized void markCompletedByIndexes(
            Collection<Integer> indexes0Based,
            PlanSyncCompletedTasksModel completedModel
    ) {
        if (completedModel == null) throw new IllegalArgumentException("completedModel cannot be null");
        // Section: Read and prepare the data used to load.
        load();

        List<Integer> idx = new ArrayList<>();
        for (Integer i : indexes0Based) {
            if (i != null && i >= 0 && i < activeTasks.size() && !idx.contains(i)) idx.add(i);
        }
        idx.sort(Integer::compareTo);

        LocalDate today = LocalDate.now();

        for (int i = idx.size() - 1; i >= 0; i--) {
            int index = idx.get(i);
            Task t = activeTasks.remove(index);
            completedModel.addCompleted(t.name, t.description, t.deadline, today);
        }
        // Section: Persist the data used to save.
        save();
    }

    // Section: Return the data used to date formatter.
    public static DateTimeFormatter getDateFormatter() {
        return DATE_FMT;
    }

    // Section: Handle the logic for format for display.
    public synchronized String formatForDisplay() {
        return formatForDisplay(93);
    }

    // Section: Handle the logic for format for display.
    public synchronized String formatForDisplay(int widthChars) {
        // Section: Read and prepare the data used to load.
        load();
        int width = Math.max(40, widthChars);

        String header = "<<ACTIVE TASKS:>> (TODAY: " + LocalDate.now().format(DATE_FMT) + ")";

        StringBuilder sb = new StringBuilder();
        sb.append(centerLine(header, width)).append("\n")
          .append(doubleLine(width)).append("\n\n");

        if (activeTasks.isEmpty()) {
            sb.append("No active tasks found.\n");
            return sb.toString();
        }

        int id = 1;
        for (Task task : activeTasks) {
            long days = ChronoUnit.DAYS.between(LocalDate.now(), task.deadline);
            String status = (days < 0) ? Math.abs(days) + " DAYS OVERDUE" : days + " DAYS REMAINING";

            sb.append("[").append(id).append("] ")
              .append(task.name).append(" -> ")
              .append(task.description).append(" -> {")
              .append(task.deadline.format(DATE_FMT))
              .append("} -> [")
              .append(status)
              .append("]\n\n");
            id++;
        }
        return sb.toString();
    }

    // Section: Handle the logic for ensure data dir.
    private static void ensureDataDir() {
        File dir = DATA_DIR;
        if (!dir.exists()) dir.mkdirs();
    }

    // Section: Read and prepare the data used to load.
    private void load() {
        // Section: Handle the logic for ensure data dir.
        ensureDataDir();
        activeTasks.clear();
        if (!ACTIVE_FILE.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(ACTIVE_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|", -1);
                if (parts.length < 3) continue;
                String name = parts[0];
                String description = parts[1];
                LocalDate deadline = LocalDate.parse(parts[2], DATE_FMT);
                activeTasks.add(new Task(name, description, deadline));
            }
        } catch (IOException ignored) {}
    }

    // Section: Persist the data used to save.
    private void save() {
        // Section: Handle the logic for ensure data dir.
        ensureDataDir();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ACTIVE_FILE))) {
            for (Task t : activeTasks) {
                bw.write(clean(t.name) + "|" + clean(t.description) + "|" + t.deadline.format(DATE_FMT));
                bw.newLine();
            }
        } catch (IOException ignored) {}
    }

    // Section: Handle the logic for clean.
    private static String clean(String s) {
        if (s == null) return "";
        return s.trim().replace("|", "/");
    }

    // Section: Handle the logic for center line.
    private static String centerLine(String line, int width) {
        if (line == null) line = "";
        if (line.length() >= width) return line;
        int pad = (width - line.length()) / 2;
        return " ".repeat(Math.max(0, pad)) + line;
    }

    // Section: Handle the logic for double line.
    private static String doubleLine(int width) {
        int w = Math.max(1, width);
        String line = "=".repeat(w);
        return line + "\n" + line;
    }
}
