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
 * File purpose: This class supports the PlanSyncCompletedTasksModel part of PlanSync and documents the main responsibilities of the file.
 */

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import util.AppPaths;

public class PlanSyncCompletedTasksModel {

    private static final File DATA_DIR = AppPaths.getDataDir().toFile();
    private static final File COMPLETED_FILE = AppPaths.getDataFile("completed_tasks.txt");
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final Map<String, CompletedTask> completedTasks = new LinkedHashMap<>();

    public static class CompletedTask {
        public String id;
        public String name;
        public String description;
        public LocalDate deadline;
        public LocalDate completedOn;
    }

    // Section: Return the data used to completed tasks.
    public synchronized Map<String, CompletedTask> getCompletedTasks() {
        // Section: Read and prepare the data used to load.
        load();
        return new LinkedHashMap<>(completedTasks);
    }

    // Section: Add the data or behavior needed to completed.
    public synchronized void addCompleted(String name, String description, LocalDate deadline, LocalDate completedOn) {
        // Section: Read and prepare the data used to load.
        load();
        String id = "C" + (completedTasks.size() + 1);

        CompletedTask t = new CompletedTask();
        t.id = id;
        t.name = name;
        t.description = description;
        t.deadline = deadline;
        t.completedOn = completedOn;

        completedTasks.put(id, t);
        // Section: Persist the data used to save.
        save();
    }

    // Section: Remove the items involved in by indexes.
    public synchronized void deleteByIndexes(Iterable<Integer> indexes0Based) {
        // Section: Read and prepare the data used to load.
        load();
        List<String> ids = new ArrayList<>(completedTasks.keySet());

        List<Integer> idx = new ArrayList<>();
        for (Integer i : indexes0Based) {
            if (i != null && i >= 0 && i < ids.size() && !idx.contains(i)) idx.add(i);
        }
        idx.sort(Integer::compareTo);

        for (int i = idx.size() - 1; i >= 0; i--) completedTasks.remove(ids.get(idx.get(i)));
        // Section: Persist the data used to save.
        save();
    }

    // Section: Handle the logic for clear all.
    public synchronized void clearAll() {
        // Section: Read and prepare the data used to load.
        load();
        completedTasks.clear();
        // Section: Persist the data used to save.
        save();
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

        String header = "<<COMPLETED TASKS:>> (TODAY: " + LocalDate.now().format(DATE_FMT) + ")";

        StringBuilder sb = new StringBuilder();
        sb.append(centerLine(header, width)).append("\n")
          .append(doubleLine(width)).append("\n\n");

        if (completedTasks.isEmpty()) {
            sb.append("No completed tasks yet.\n");
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

    // Section: Handle the logic for ensure data dir.
    private static void ensureDataDir() {
        File dir = DATA_DIR;
        if (!dir.exists()) dir.mkdirs();
    }

    // Section: Read and prepare the data used to load.
    private void load() {
        // Section: Handle the logic for ensure data dir.
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
        } catch (IOException ignored) {}
    }

    // Section: Persist the data used to save.
    private void save() {
        // Section: Handle the logic for ensure data dir.
        ensureDataDir();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(COMPLETED_FILE))) {
            for (CompletedTask t : completedTasks.values()) {
                String deadline = t.deadline == null ? "" : t.deadline.format(DATE_FMT);
                String completed = t.completedOn == null ? "" : t.completedOn.format(DATE_FMT);
                bw.write(t.id + "|" + safe(t.name) + "|" + safe(t.description) + "|" + deadline + "|" + completed);
                bw.newLine();
            }
        } catch (IOException ignored) {}
    }

    // Section: Handle the logic for safe.
    private static String safe(String s) {
        return s == null ? "" : s.replace("|", "/");
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
