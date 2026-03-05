package model;

import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PlanSyncRecurringTasksModel {

    private static final String DATA_DIR = "data";
    private static final File RECURRING_FILE = new File(DATA_DIR, "recurring_tasks.txt");

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static class RecurringTask {
        public final String name;
        public final String description;
        public final String timeDate;
        public final String frequency;

        public RecurringTask(String name, String description, String timeDate, String frequency) {
            this.name = name;
            this.description = description;
            this.timeDate = timeDate;
            this.frequency = frequency;
        }
    }

    private final List<RecurringTask> tasks = new ArrayList<>();

    public synchronized List<RecurringTask> getTasks() {
        load();
        return new ArrayList<>(tasks);
    }


/**
 * Returns the recurring task by its 1-based ID as displayed in the UI.
 * @param id1Based 1..N
 * @return RecurringTask or null if out of range
 */
public synchronized RecurringTask getTaskById(int id1Based) {
    load();
    int idx = id1Based - 1;
    if (idx < 0 || idx >= tasks.size()) return null;
    return tasks.get(idx);
}

public synchronized void updateTaskById(int id1Based, String name, String description, String timeDate, String frequency) {
    load();
    int idx = id1Based - 1;
    if (idx < 0 || idx >= tasks.size()) {
        throw new IllegalArgumentException("Task ID out of range: " + id1Based);
    }
    tasks.set(idx, new RecurringTask(clean(name), clean(description), clean(timeDate), clean(frequency).toUpperCase()));
    save();
}

    public synchronized void addTask(String name, String description, String timeDate, String frequency) {
        load();
        tasks.add(new RecurringTask(clean(name), clean(description), clean(timeDate), clean(frequency).toUpperCase()));
        save();
    }

    public synchronized void deleteTasksByIndexes(Collection<Integer> indexes0Based) {
        load();
        List<Integer> idx = new ArrayList<>();
        for (Integer i : indexes0Based) {
            if (i != null && i >= 0 && i < tasks.size() && !idx.contains(i)) idx.add(i);
        }
        idx.sort(Integer::compareTo);
        for (int i = idx.size() - 1; i >= 0; i--) tasks.remove((int) idx.get(i));
        save();
    }

    public synchronized String formatForDisplay() {
        return formatForDisplay(93);
    }

    public synchronized String formatForDisplay(int widthChars) {
        load();
        int width = Math.max(40, widthChars);

        String header = "<<RECURRING TASKS:>> (TODAY: " + LocalDate.now().format(DATE_FMT) + ")";

        StringBuilder sb = new StringBuilder();
        sb.append(centerLine(header, width)).append("\n")
          .append(doubleLine(width)).append("\n\n");

        if (tasks.isEmpty()) {
            sb.append("No recurring tasks found.\n");
            return sb.toString();
        }

        LocalDate today = LocalDate.now();
        int id = 1;

        for (RecurringTask t : tasks) {
            String status = computeStatus(t, today);
            String displayDate = t.timeDate;

            if ("MONTHLY".equalsIgnoreCase(t.frequency)) {
                displayDate = formatMonthlyDisplay(t.timeDate);
            }

            sb.append("[").append(id).append("] ")
              .append(t.name).append(" -> ")
              .append(t.description).append(" -> {")
              .append(displayDate).append("} -> [")
              .append(t.frequency).append("] -> [")
              .append(status).append("]\n\n");
            id++;
        }

        return sb.toString();
    }

    public static LocalTime parseTimeHHmm(String time) throws DateTimeParseException {
        return LocalTime.parse(time, TIME_FMT);
    }

    public static void validateDayMonthYear(String dayMonth, String yearYYYY) throws DateTimeParseException {
        LocalDate.parse(dayMonth + "/" + yearYYYY, DATE_FMT);
    }

    public static void validateDayMonth(String dayDD, String monthMM) throws DateTimeParseException {
        int d = Integer.parseInt(dayDD);
        int m = Integer.parseInt(monthMM);
        LocalDate.parse(String.format("%02d/%02d/2000", d, m), DATE_FMT);
    }

    private static String computeStatus(RecurringTask task, LocalDate today) {
        try {
            return switch (task.frequency.toUpperCase()) {
                case "DAILY" -> "REPEATS DAILY";
                case "WEEKLY" -> {
                    String[] parts = task.timeDate.split(" ");
                    String dayName = parts.length >= 2 ? parts[1] : "";
                    DayOfWeek targetDay = DayOfWeek.valueOf(dayName);
                    int daysUntil = (targetDay.getValue() - today.getDayOfWeek().getValue() + 7) % 7;
                    if (daysUntil == 0) daysUntil = 7;
                    yield daysUntil + " DAYS UNTIL NEXT";
                }
                case "MONTHLY" -> {
                    String datePart = task.timeDate.split(" ")[0];
                    String[] dm = datePart.split("/");
                    int day = Integer.parseInt(dm[0]);
                    int month = Integer.parseInt(dm[1]);

                    LocalDate next;
                    if (month > today.getMonthValue()) next = LocalDate.of(today.getYear(), month, day);
                    else if (month < today.getMonthValue()) next = LocalDate.of(today.getYear() + 1, month, day);
                    else next = (day > today.getDayOfMonth())
                            ? LocalDate.of(today.getYear(), month, day)
                            : LocalDate.of(today.getYear() + 1, month, day);

                    long days = ChronoUnit.DAYS.between(today, next);
                    yield days + " DAYS UNTIL NEXT";
                }
                case "YEARLY" -> {
                    String[] parts = task.timeDate.split(" ");
                    String[] dm = parts[0].split("/");
                    int day = Integer.parseInt(dm[0]);
                    int month = Integer.parseInt(dm[1]);

                    LocalDate next = LocalDate.of(today.getYear(), month, day);
                    if (!next.isAfter(today)) next = next.plusYears(1);

                    long days = ChronoUnit.DAYS.between(today, next);
                    yield days + " DAYS UNTIL NEXT";
                }
                default -> "UNKNOWN";
            };
        } catch (Exception e) {
            return "INVALID DATA";
        }
    }

    private static String formatMonthlyDisplay(String timeDate) {
        try {
            String datePart = timeDate.split(" ")[0];
            String[] parts = datePart.split("/");
            int day = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            LocalDate temp = LocalDate.of(2000, month, day);
            return temp.format(DateTimeFormatter.ofPattern("d MMMM"));
        } catch (Exception e) {
            return timeDate;
        }
    }

    private static void ensureDataDir() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) dir.mkdirs();
    }

    private void load() {
        ensureDataDir();
        tasks.clear();
        if (!RECURRING_FILE.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(RECURRING_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|", -1);
                if (parts.length < 4) continue;
                tasks.add(new RecurringTask(parts[0], parts[1], parts[2], parts[3]));
            }
        } catch (IOException ignored) {}
    }

    private void save() {
        ensureDataDir();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(RECURRING_FILE))) {
            for (RecurringTask t : tasks) {
                bw.write(clean(t.name) + "|" + clean(t.description) + "|" + clean(t.timeDate) + "|" + clean(t.frequency));
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