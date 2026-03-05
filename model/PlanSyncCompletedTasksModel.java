package model;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * GUI-facing completed tasks model.
 *
 * Mirrors the persistence + behaviour of modelTerminal.PlanSyncCompletedTasks,
 * but keeps the API small and Swing-friendly.
 */
public class PlanSyncCompletedTasksModel {

    /** Store task files under ./data/ (relative to working directory). */
    private static final String DATA_DIR = "data";
    private static final File COMPLETED_FILE = new File(DATA_DIR, "completed_tasks.txt");

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /** Preserve insertion order. */
    private final Map<String, CompletedTask> completedTasks = new LinkedHashMap<>();

    public static class CompletedTask {
        public String id;
        public String name;
        public String description;
        public LocalDate deadline;
        public LocalDate completedOn;
    }

    public synchronized Map<String, CompletedTask> getCompletedTasks() {
        load();
        return new LinkedHashMap<>(completedTasks);
    }

    public synchronized void addCompleted(
            String name,
            String description,
            LocalDate deadline,
            LocalDate completedOn
    ) {

        load();
        String id = "C" + (completedTasks.size() + 1);

        CompletedTask t = new CompletedTask();
        t.id = id;
        t.name = name;
        t.description = description;
        t.deadline = deadline;
        t.completedOn = completedOn;

        completedTasks.put(id, t);
        save();
    }

    /** Delete by 0-based indexes in the displayed order. */
    public synchronized void deleteByIndexes(Iterable<Integer> indexes0Based) {
        load();
        List<String> ids = new ArrayList<>(completedTasks.keySet());

        List<Integer> idx = new ArrayList<>();
        for (Integer i : indexes0Based) {
            if (i != null && i >= 0 && i < ids.size() && !idx.contains(i)) idx.add(i);
        }
        idx.sort(Integer::compareTo);

        for (int i = idx.size() - 1; i >= 0; i--) {
            completedTasks.remove(ids.get(idx.get(i)));
        }
        save();
    }

    /** Clear whole list. */
    public synchronized void clearAll() {
        load();
        completedTasks.clear();
        save();
    }

    /** Terminal-style list render. */
    public synchronized String formatForDisplay() {
        load();

        final int WIDTH = 93;
        String header = "<<COMPLETED TASKS:>> (TODAY: " + LocalDate.now().format(DATE_FMT) + ")";

        StringBuilder sb = new StringBuilder();
        sb.append(centerLine(header, WIDTH)).append("\n")
                .append("=============================================================================================\n");

        if (completedTasks.isEmpty()) {
            sb.append("\nNo completed tasks yet.\n");
            return sb.toString();
        }

        int index = 1;
        for (CompletedTask t : completedTasks.values()) {

            String deadline = t.deadline == null ? "--/--/----" : t.deadline.format(DATE_FMT);
            String completed = t.completedOn == null ? "--/--/----" : t.completedOn.format(DATE_FMT);

            sb.append("[").append(index++).append("] ")
                    .append(t.name).append(" -> ")
                    .append(t.description).append(" -> {")
                    .append(deadline).append("} -> [COMPLETED ")
                    .append(completed).append("] = [V]\n\n");
        }

        return sb.toString();
    }

    /* ================= FILE IO ================= */

    private static void ensureDataDir() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            //noinspection ResultOfMethodCallIgnored
            dir.mkdirs();
        }
    }

    private void load() {
        ensureDataDir();
        completedTasks.clear();
        if (!COMPLETED_FILE.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(COMPLETED_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
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

    private void save() {
        ensureDataDir();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(COMPLETED_FILE))) {
            for (CompletedTask t : completedTasks.values()) {
                String deadline = t.deadline == null ? "" : t.deadline.format(DATE_FMT);
                String completed = t.completedOn == null ? "" : t.completedOn.format(DATE_FMT);
                bw.write(t.id + "|" + safe(t.name) + "|" + safe(t.description) + "|" + deadline + "|" + completed);
                bw.newLine();
            }
        } catch (IOException ignored) {
        }
    }

    private static String safe(String s) {
        return s == null ? "" : s.replace("|", "/");
    }

    private static String centerLine(String line, int width) {
        if (line == null) line = "";
        if (line.length() >= width) return line;
        int pad = (width - line.length()) / 2;
        return " ".repeat(Math.max(0, pad)) + line;
    }
}