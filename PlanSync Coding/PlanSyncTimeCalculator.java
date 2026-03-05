package modelTerminal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class PlanSyncTimeCalculator {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static Navigation run() {
        while (true) {
            System.out.println("\n--- TIME CALCULATOR ---");
            System.out.println("1. Add Duration From Now");
            System.out.println("2. Duration Between Times");
            System.out.println("3. Duration Between Dates");
            System.out.println("\n4. Go to Timer");
            System.out.println("5. Go to Stopwatch");
            System.out.println("6. Go to Calendar");
            System.out.println("\n7. Go to Active Tasks");
            System.out.println("8. Go to Recurring Tasks");
            System.out.println("9. Go to Completed Tasks");
            System.out.println("\n0. Main Menu");
            System.out.print("\nChoose option: ");

            switch (ConsoleUtils.scanner.nextLine()) {
                case "1" -> addDurationFromNow();
                case "2" -> durationBetweenTimes();
                case "3" -> durationBetweenDates();
                case "4" -> { return Navigation.TIMER; }
                case "5" -> { return Navigation.STOPWATCH; }
                case "6" -> { return Navigation.CALENDAR; }
                case "7" -> { return Navigation.ACTIVE_TASKS; }
                case "8" -> { return Navigation.RECURRING_TASKS; }
                case "9" -> { return Navigation.COMPLETED_TASKS; }
                case "0" -> { return Navigation.MAIN; }
                default -> System.out.println("\nInvalid option.");
            }
        }
    }

    /* ================= 1. ADD DURATION FROM NOW ================= */

    private static void addDurationFromNow() {
        while (true) {
            System.out.print("\nEnter duration (e.g. '1 Day 2 Hours 30 Minutes'): ");
            String input = ConsoleUtils.scanner.nextLine().trim().toLowerCase();
            
            if (input.equals("0")) return; // Back to main menu
            
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime result = now;
            boolean validInput = false;

            // Regex matches "1 day", "2 hours", "30 minutes", etc.
            Pattern pattern = Pattern.compile("(\\d+)\\s*(day|hour|minute|month|year)s?");
            Matcher matcher = pattern.matcher(input);

            while (matcher.find()) {
                validInput = true;
                int value = Integer.parseInt(matcher.group(1));
                String unit = matcher.group(2);

                switch (unit) {
                    case "day": result = result.plusDays(value); break;
                    case "hour": result = result.plusHours(value); break;
                    case "minute": result = result.plusMinutes(value); break;
                    case "month": result = result.plusMonths(value); break;
                    case "year": result = result.plusYears(value); break;
                }
            }

            if (validInput) {
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("d MMMM yyyy | HH:mm");
                System.out.println("\nResult: " + result.format(fmt));
                return;
            } else {
                System.out.println("\nInvalid format. Use '1 day', '2 hours', etc. or '0' to go back.");
            }
        }
    }

    /* ================= 2. DURATION BETWEEN TIMES ================= */

    private static void durationBetweenTimes() {
        // Get valid start time
        String startInput;
        while (true) {
            System.out.print("\nStart time (HH:MM) or '0' to go back: ");
            startInput = ConsoleUtils.scanner.nextLine().trim();
            if (startInput.equals("0")) return;
            if (isValidTime(startInput)) break;
            System.out.println("\nInvalid format. Use HH:MM (00-23:00-59)");
        }

        // Get valid end time
        String endInput;
        while (true) {
            System.out.print("End time (HH:MM) or '0' to go back: ");
            endInput = ConsoleUtils.scanner.nextLine().trim();
            if (endInput.equals("0")) return;
            if (isValidTime(endInput)) break;
            System.out.println("\nInvalid format. Use HH:MM (00-23:00-59)");
        }

        try {
            String[] startParts = startInput.split(":");
            String[] endParts = endInput.split(":");
            
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

            System.out.printf("\nResult: %d HOUR%s %d MINUTE%s\n", 
                            hours, hours == 1 ? "" : "S", 
                            minutes, minutes == 1 ? "" : "S");
        } catch (NumberFormatException e) {
            System.out.println("\nError processing times.");
        }
    }

    /* ================= 3. DURATION BETWEEN DATES ================= */

    private static void durationBetweenDates() {
        // Get valid start date
        String startDateStr;
        while (true) {
            System.out.print("\nStart date (DD/MM/YYYY) or '0' to go back: ");
            startDateStr = ConsoleUtils.scanner.nextLine().trim();
            if (startDateStr.equals("0")) return;
            if (isValidDate(startDateStr)) break;
            System.out.println("\nInvalid date. Use DD/MM/YYYY format");
        }

        // Get valid end date
        String endDateStr;
        while (true) {
            System.out.print("End date (DD/MM/YYYY) or '0' to go back: ");
            endDateStr = ConsoleUtils.scanner.nextLine().trim();
            if (endDateStr.equals("0")) return;
            if (isValidDate(endDateStr)) break;
            System.out.println("\nInvalid date. Use DD/MM/YYYY format");
        }

        // Get output format
        String outputType;
        while (true) {
            System.out.println("\nOutput format:");
            System.out.println("1. Full breakdown");
            System.out.println("2. Days only");
            System.out.println("3. Weeks only");
            System.out.println("4. Months only");
            System.out.println("5. Years only");
            System.out.print("\nChoose (1-5) or '0' to go back: ");
            outputType = ConsoleUtils.scanner.nextLine().trim();
            if (outputType.equals("0")) return;
            if (outputType.matches("[1-5]")) break;
            System.out.println("\nInvalid choice. Enter 1-5 or 0 to go back.");
        }

        try {
            LocalDate start = LocalDate.parse(startDateStr, DATE_FORMAT);
            LocalDate end = LocalDate.parse(endDateStr, DATE_FORMAT);

            if (end.isBefore(start)) {
                System.out.println("\nEnd date must be after start date");
                return;
            }

            long totalDays = ChronoUnit.DAYS.between(start, end);
            long totalWeeks = totalDays / 7;
            long totalMonths = ChronoUnit.MONTHS.between(start, end);
            long totalYears = ChronoUnit.YEARS.between(start, end);

            switch (outputType) {
                case "2":
                    System.out.printf("%d DAYS\n", totalDays);
                    break;
                case "3":
                    System.out.printf("%d WEEKS\n", totalWeeks);
                    break;
                case "4":
                    System.out.printf("%d MONTHS\n", totalMonths);
                    break;
                case "5":
                    System.out.printf("%d YEARS\n", totalYears);
                    break;
                case "1":
                default:
                    System.out.println(buildParallelBreakdown(start, end));
                    break;
            }
        } catch (Exception e) {
            System.out.println("\nError processing dates.");
        }
    }

    /* ================= VALIDATION HELPERS ================= */

    private static boolean isValidTime(String timeStr) {
        try {
            String[] parts = timeStr.split(":");
            if (parts.length != 2) return false;
            
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);
            
            return hour >= 0 && hour <= 23 && minute >= 0 && minute <= 59;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean isValidDate(String dateStr) {
        try {
            LocalDate.parse(dateStr, DATE_FORMAT);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /* ================= HELPER METHODS ================= */

    private static String buildParallelBreakdown(LocalDate start, LocalDate end) {
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
