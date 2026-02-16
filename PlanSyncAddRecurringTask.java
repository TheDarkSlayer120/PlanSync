import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class PlanSyncAddRecurringTask {

    private static final DateTimeFormatter TIME_FMT =
            DateTimeFormatter.ofPattern("HH:mm");

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static void addRecurringTask() {

        System.out.println("\n--- ADD NEW RECURRING TASK ---\n");

        System.out.print("ENTER TASK NAME: ");
        String name = ConsoleUtils.scanner.nextLine().trim();

        System.out.print("ENTER TASK DESCRIPTION: ");
        String description = ConsoleUtils.scanner.nextLine().trim();

        System.out.println("\n==========");
        System.out.println("1. DAILY");
        System.out.println("2. WEEKLY");
        System.out.println("3. MONTHLY");
        System.out.println("4. YEARLY");
        System.out.println("==========");
        System.out.print("\nChoose: ");

        String choice = ConsoleUtils.scanner.nextLine().trim();

        String timeDate = "";
        String frequency = "";

        switch (choice) {

            /* ================= DAILY ================= */

            case "1" -> {
                frequency = "DAILY";

                System.out.print("TIME (HH:MM): ");
                LocalTime time = LocalTime.parse(
                        ConsoleUtils.scanner.nextLine().trim(),
                        TIME_FMT
                );

                timeDate = time.format(TIME_FMT);
            }

            /* ================= WEEKLY ================= */

            case "2" -> {
                frequency = "WEEKLY";

                System.out.print("TIME (HH:MM): ");
                LocalTime time = LocalTime.parse(
                        ConsoleUtils.scanner.nextLine().trim(),
                        TIME_FMT
                );

                System.out.print("DAY OF WEEK (e.g. MONDAY): ");
                String day = ConsoleUtils.scanner.nextLine().trim().toUpperCase();

                timeDate = time.format(TIME_FMT) + " " + day;
            }

            /* ================= MONTHLY ================= */

            case "3" -> {
                frequency = "MONTHLY";

                System.out.print("DAY OF MONTH (1-31): ");
                String dayOfMonth = ConsoleUtils.scanner.nextLine().trim();

                System.out.print("TIME (HH:MM): ");
                LocalTime time = LocalTime.parse(
                        ConsoleUtils.scanner.nextLine().trim(),
                        TIME_FMT
                );

                timeDate = time.format(TIME_FMT) + " DAY-" + dayOfMonth;
            }

            /* ================= YEARLY ================= */

            case "4" -> {
                frequency = "YEARLY";

                System.out.print("DATE (dd/MM/yyyy): ");
                LocalDate date = LocalDate.parse(
                        ConsoleUtils.scanner.nextLine().trim(),
                        DATE_FMT
                );

                System.out.print("TIME (HH:MM): ");
                LocalTime time = LocalTime.parse(
                        ConsoleUtils.scanner.nextLine().trim(),
                        TIME_FMT
                );

                timeDate = time.format(TIME_FMT) + " " + date.format(DATE_FMT);
            }

            default -> {
                System.out.println("Invalid option.");
                return;
            }
        }

        PlanSyncRecurringTasks.recurringTasks.add(
                new PlanSyncRecurringTasks.RecurringTask(
                        name,
                        description,
                        timeDate,
                        frequency
                )
        );

        System.out.println("\nNew Recurring Task Added!");
        System.out.println("List Updated!");
        System.out.println();
    }
}
