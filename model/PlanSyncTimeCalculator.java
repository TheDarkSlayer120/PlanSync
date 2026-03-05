package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlanSyncTimeCalculator {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATE_TIME_OUTPUT =
            DateTimeFormatter.ofPattern("d MMMM yyyy | HH:mm", Locale.ENGLISH);

    public enum DateOutputType {
        FULL_BREAKDOWN,
        DAYS_ONLY,
        WEEKS_ONLY,
        MONTHS_ONLY,
        YEARS_ONLY
    }

    public static class TimeDiffResult {
        public final long hours;
        public final long minutes;

        public TimeDiffResult(long hours, long minutes) {
            this.hours = hours;
            this.minutes = minutes;
        }

        public String toDisplayString() {
            return String.format(
                    "%d HOUR%s %d MINUTE%s",
                    hours, hours == 1 ? "" : "S",
                    minutes, minutes == 1 ? "" : "S"
            );
        }
    }

    public static class DateDiffResult {
        public final long years;
        public final long months;
        public final long weeks;
        public final long days;
        public final long hours;

        public DateDiffResult(long years, long months, long weeks, long days, long hours) {
            this.years = years;
            this.months = months;
            this.weeks = weeks;
            this.days = days;
            this.hours = hours;
        }
    }

    /* ================= 1) ADD DURATION FROM NOW ================= */

    /**
     * Parses a duration text like:
     * "1 Day 2 Hours 30 Minutes", "3 months", "1year 2days", etc.
     * Returns the resulting LocalDateTime from the provided base (usually LocalDateTime.now()).
     */
    public LocalDateTime addDurationFrom(LocalDateTime base, String input) {
        if (base == null) throw new IllegalArgumentException("Base time cannot be null.");
        if (input == null) throw new IllegalArgumentException("Duration input cannot be null.");

        String normalized = input.trim().toLowerCase();
        if (normalized.isEmpty()) throw new IllegalArgumentException("Duration input cannot be empty.");

        LocalDateTime result = base;
        boolean validInput = false;

        // Matches "1 day", "2 hours", "30 minutes", "3 months", "1 year" etc.
        Pattern pattern = Pattern.compile("(\\d+)\\s*(day|hour|minute|month|year)s?");
        Matcher matcher = pattern.matcher(normalized);

        while (matcher.find()) {
            validInput = true;
            int value = Integer.parseInt(matcher.group(1));
            String unit = matcher.group(2);

            switch (unit) {
                case "day" -> result = result.plusDays(value);
                case "hour" -> result = result.plusHours(value);
                case "minute" -> result = result.plusMinutes(value);
                case "month" -> result = result.plusMonths(value);
                case "year" -> result = result.plusYears(value);
            }
        }

        if (!validInput) {
            throw new IllegalArgumentException("Invalid duration format. Example: '1 Day 2 Hours 30 Minutes'.");
        }

        return result;
    }

    public String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return "";
        return dateTime.format(DATE_TIME_OUTPUT);
    }

    /* ================= 2) DURATION BETWEEN TIMES ================= */

    public boolean isValidTime(String timeStr) {
        try {
            if (timeStr == null) return false;
            String[] parts = timeStr.trim().split(":");
            if (parts.length != 2) return false;

            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);

            return hour >= 0 && hour <= 23 && minute >= 0 && minute <= 59;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Computes duration between two HH:MM times.
     * If end < start, assumes the end is on the next day (same logic as terminal version).
     */
    public TimeDiffResult durationBetweenTimes(String startHHMM, String endHHMM) {
        if (!isValidTime(startHHMM) || !isValidTime(endHHMM)) {
            throw new IllegalArgumentException("Invalid time. Use HH:MM (00-23:00-59).");
        }

        String[] startParts = startHHMM.trim().split(":");
        String[] endParts = endHHMM.trim().split(":");

        int startHour = Integer.parseInt(startParts[0]);
        int startMinute = Integer.parseInt(startParts[1]);
        int endHour = Integer.parseInt(endParts[0]);
        int endMinute = Integer.parseInt(endParts[1]);

        int startMinutes = startHour * 60 + startMinute;
        int endMinutes = endHour * 60 + endMinute;

        int diffMinutes;
        if (endMinutes >= startMinutes) {
            diffMinutes = endMinutes - startMinutes;
        } else {
            // Assume next day
            diffMinutes = (endMinutes + 1440) - startMinutes;
        }

        long hours = diffMinutes / 60;
        long minutes = diffMinutes % 60;

        return new TimeDiffResult(hours, minutes);
    }

    /* ================= 3) DURATION BETWEEN DATES ================= */

    public boolean isValidDate(String dateStr) {
        try {
            if (dateStr == null) return false;
            LocalDate.parse(dateStr.trim(), DATE_FORMAT);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public LocalDate parseDate(String dateStr) {
        if (!isValidDate(dateStr)) {
            throw new IllegalArgumentException("Invalid date. Use DD/MM/YYYY.");
        }
        return LocalDate.parse(dateStr.trim(), DATE_FORMAT);
    }

    public String durationBetweenDatesDisplay(LocalDate start, LocalDate end, DateOutputType type) {
        if (start == null || end == null) throw new IllegalArgumentException("Dates cannot be null.");
        if (end.isBefore(start)) throw new IllegalArgumentException("End date must be after start date.");

        long totalDays = ChronoUnit.DAYS.between(start, end);
        long totalWeeks = totalDays / 7;
        long totalMonths = ChronoUnit.MONTHS.between(start, end);
        long totalYears = ChronoUnit.YEARS.between(start, end);

        return switch (type) {
            case DAYS_ONLY -> totalDays + " DAYS";
            case WEEKS_ONLY -> totalWeeks + " WEEKS";
            case MONTHS_ONLY -> totalMonths + " MONTHS";
            case YEARS_ONLY -> totalYears + " YEARS";
            case FULL_BREAKDOWN -> buildParallelBreakdown(start, end);
        };
    }

    private String buildParallelBreakdown(LocalDate start, LocalDate end) {
        long years = ChronoUnit.YEARS.between(start, end);
        long months = ChronoUnit.MONTHS.between(start, end);
        long days = ChronoUnit.DAYS.between(start, end);
        long weeks = days / 7;
        long hours = days * 24;

        StringBuilder sb = new StringBuilder();

        if (years > 0) sb.append(years).append(years == 1 ? " YEAR\n" : " YEARS\n");
        if (months > 0) sb.append(months).append(months == 1 ? " MONTH\n" : " MONTHS\n");
        if (weeks > 0) sb.append(weeks).append(weeks == 1 ? " WEEK\n" : " WEEKS\n");
        if (days > 0) sb.append(days).append(days == 1 ? " DAY\n" : " DAYS\n");

        if (sb.length() == 0) sb.append("0 DAYS\n");

        sb.append(hours).append(hours == 1 ? " HOUR" : " HOURS");
        return sb.toString();
    }
}