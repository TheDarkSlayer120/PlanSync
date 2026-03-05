package modelTerminal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class PlanSyncAddRecurringTask {

    private static final DateTimeFormatter TIME_FMT =
            DateTimeFormatter.ofPattern("HH:mm");

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static void addRecurringTask() {

        System.out.println("\n--- ADD NEW RECURRING TASK ---\n");

        System.out.print("ENTER TASK NAME: ");
        String name = ConsoleUtils.scanner.nextLine().trim();

        System.out.print("\nENTER TASK DESCRIPTION: ");
        String description = ConsoleUtils.scanner.nextLine().trim();

        String frequency;
        String timeDate = "";

        while (true) {

            System.out.println("\n==========");
            System.out.println("1. DAILY");
            System.out.println("2. WEEKLY");
            System.out.println("3. MONTHLY");
            System.out.println("4. YEARLY");
            System.out.println("0. Cancel");
            System.out.println("==========");
            System.out.print("\nChoose: ");

            String choice = ConsoleUtils.scanner.nextLine().trim();

            if (choice.equals("0")) {
                System.out.println("\nCancelled.\n");
                return;
            }

            switch (choice) {

                /* ================= DAILY ================= */

                case "1" -> {
                    frequency = "DAILY";

                    LocalTime time = promptForTime();
                    if (time == null) return;

                    timeDate = time.format(TIME_FMT);
                    finish(name, description, timeDate, frequency);
                    return;
                }

                /* ================= WEEKLY ================= */

                case "2" -> {
                    frequency = "WEEKLY";

                    LocalTime time = promptForTime();
                    if (time == null) return;

                    System.out.print("DAY OF WEEK (e.g. MONDAY): ");
                    String day = ConsoleUtils.scanner.nextLine()
                            .trim()
                            .toUpperCase();

                    if (day.isEmpty()) {
                        System.out.println("Invalid day of week.");
                        continue;
                    }

                    timeDate = time.format(TIME_FMT) + " " + day;
                    finish(name, description, timeDate, frequency);
                    return;
                }

                /* ================= MONTHLY ================= */
                
                case "3" -> {
                    frequency = "MONTHLY";

                    while (true) {
                        try {
                            // Prompt for day of month
                            System.out.print("DATE (DD): ");
                            String dayInput = ConsoleUtils.scanner.nextLine().trim();

                            if (dayInput.equals("0")) {
                                System.out.println("\nCancelled.\n");
                                return;
                            }

                            int day = Integer.parseInt(dayInput);
                            if (day < 1 || day > 31) {
                
                                System.out.println("Invalid day. Enter a number from 1 to 31.");
                                continue;
                            }

                            // Prompt for starting month
                            System.out.print("STARTING MONTH (MM): ");
                            String monthInput = ConsoleUtils.scanner.nextLine().trim();

                            if (monthInput.equals("0")) {
                                System.out.println("\nCancelled.\n");
                                return;
                            }

                            int month = Integer.parseInt(monthInput);
                            if (month < 1 || month > 12) {
                                System.out.println("Invalid month. Enter a number from 1 to 12.");
                                continue;
                            }

                            // Validate by creating a dummy year date
                            LocalDate.parse(
                                String.format("%02d/%02d/2000", day, month),
                                DATE_FMT
                            );

                            timeDate = String.format("%02d/%02d (Start Month: %02d)", day, month, month);

                            finish(name, description, timeDate, frequency);
                            return;

                        } catch (NumberFormatException e) {
                            System.out.println("Invalid number input.");
                        } catch (DateTimeParseException e) {
                            System.out.println("Invalid date.");
                        }
                    }
                }


                /* ================= YEARLY ================= */

                case "4" -> {
                    frequency = "YEARLY";

                    while (true) {

                        try {
                            System.out.print("DATE (DD/MM): ");
                            String dayMonth = ConsoleUtils.scanner
                                    .nextLine()
                                    .trim();

                            if (dayMonth.equals("0")) {
                                System.out.println("\nCancelled.\n");
                                return;
                            }

                            System.out.print("STARTING YEAR (YYYY): ");
                            String yearInput = ConsoleUtils.scanner
                                    .nextLine()
                                    .trim();

                            int startYear = Integer.parseInt(yearInput);

                            // Validate full date
                            LocalDate.parse(
                                    dayMonth + "/" + startYear,
                                    DATE_FMT
                            );

                            timeDate = dayMonth +
                                    " (Start: " + startYear + ")";

                            finish(name, description,
                                    timeDate, frequency);
                            return;

                        } catch (DateTimeParseException e) {
                            System.out.println("Invalid date.");
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid year.");
                        }
                    }
                }

                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    /* ================= HELPER: TIME INPUT ================= */

    private static LocalTime promptForTime() {

        while (true) {

            System.out.print("TIME (HH:MM) or 0 to Cancel: ");
            String input = ConsoleUtils.scanner.nextLine().trim();

            if (input.equals("0")) {
                System.out.println("\nCancelled.\n");
                return null;
            }

            try {
                return LocalTime.parse(input, TIME_FMT);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid time format.");
            }
        }
    }

    /* ================= FINALIZE ADD ================= */

    private static void finish(String name,
                               String description,
                               String timeDate,
                               String frequency) {

        PlanSyncRecurringTasks.recurringTasks.add(
                new PlanSyncRecurringTasks.RecurringTask(
                        name,
                        description,
                        timeDate,
                        frequency
                )
        );

        PlanSyncRecurringTasks.saveRecurring();

        System.out.println("\nNew Recurring Task Added!");
        System.out.println("\nList Updated!");
        System.out.println("\nGoing to Recurring Tasks...\n");
    }
}
