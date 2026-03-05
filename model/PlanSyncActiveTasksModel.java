package model;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class PlanSyncActiveTasksModel {

    private static final String DATA_DIR = "data";
    private static final File ACTIVE_FILE = new File(DATA_DIR, "active_tasks.txt");
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static class Task {
        public final String name;
        public final String description;
        public final LocalDate deadline;

        public Task(String name, String description, LocalDate deadline) {
            this.name = name;
            this.description = description;
            this.deadline = deadline;
        }
    }

    private final List<Task> activeTasks = new ArrayList<>();

    public synchronized List<Task> getTasks() {
        load();
        return new ArrayList<>(activeTasks);
    }

    public synchronized void addTask(String name, String description, String deadlineDDMMYYYY)
            throws DateTimeParseException {
        LocalDate deadline = LocalDate.parse(deadlineDDMMYYYY, DATE_FMT);
        addTask(name, description, deadline);
    }

    public synchronized void addTask(String name, String description, LocalDate deadline) {
        load();
        activeTasks.add(new Task(clean(name), clean(description), deadline));
        save();
    }

    public synchronized void deleteTasksByIndexes(Collection<Integer> indexes0Based) {
        load();
        List<Integer> idx = new ArrayList<>();
        for (Integer i : indexes0Based) {
            if (i != null && i >= 0 && i < activeTasks.size() && !idx.contains(i)) idx.add(i);
        }
        idx.sort(Integer::compareTo);
        for (int i = idx.size() - 1; i >= 0; i--) activeTasks.remove((int) idx.get(i));
        save();
    }

    public synchronized void markCompletedByIndexes(
            Collection<Integer> indexes0Based,
            PlanSyncCompletedTasksModel completedModel
    ) {
        if (completedModel == null) throw new IllegalArgumentException("completedModel cannot be null");
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
        save();
    }

    public static DateTimeFormatter getDateFormatter() {
        return DATE_FMT;
    }

    public synchronized String formatForDisplay() {
        return formatForDisplay(93);
    }

    public synchronized String formatForDisplay(int widthChars) {
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

    private static void ensureDataDir() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) dir.mkdirs();
    }

    private void load() {
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

    private void save() {
        ensureDataDir();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ACTIVE_FILE))) {
            for (Task t : activeTasks) {
                bw.write(clean(t.name) + "|" + clean(t.description) + "|" + t.deadline.format(DATE_FMT));
                bw.newLine();
            }
        } catch (IOException ignored) {}
    }

    private static String clean(String s) {
        if (s == null) return "";
        return s.trim().replace("|", "/");
    }

    private static String centerLine(String line, int width) {
        if (line == null) line = "";
        if (line.length() >= width) return line;
        int pad = (width - line.length()) / 2;
        return " ".repeat(Math.max(0, pad)) + line;
    }

    private static String doubleLine(int width) {
        int w = Math.max(1, width);
        String line = "=".repeat(w);
        return line + "\n" + line;
    }
}