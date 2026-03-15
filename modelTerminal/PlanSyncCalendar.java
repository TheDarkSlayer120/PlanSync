package modelTerminal;

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
 * File purpose: This class supports the PlanSyncCalendar part of PlanSync and documents the main responsibilities of the file.
 */

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

public class PlanSyncCalendar {

    private static YearMonth currentMonth = YearMonth.now();

    // Section: Handle the logic for run.
    public static Navigation run() {
        while (true) {
            // Section: Handle the logic for print calendar.
            printCalendar();
            
            System.out.print("\nChoose option: ");
            String choice = ConsoleUtils.scanner.nextLine();

            switch (choice) {
                case "1" -> displayTime();
                case "2" -> {
                    currentMonth = currentMonth.minusMonths(1);
                }
                case "3" -> {
                    currentMonth = currentMonth.plusMonths(1);
                }
                case "4" -> { return Navigation.TIMER; }
                case "5" -> { return Navigation.STOPWATCH; }
                case "6" -> { return Navigation.TIME_CALCULATOR; }
                case "7" -> { return Navigation.ACTIVE_TASKS; }
                case "8" -> { return Navigation.RECURRING_TASKS; }
                case "9" -> { return Navigation.COMPLETED_TASKS; }
                case "0" -> { return Navigation.MAIN; }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    // Section: Handle the logic for print calendar.
    private static void printCalendar() {
        // Header menu
        System.out.println("\n--- CALENDAR ---");
        System.out.println("1. Display Time");
        System.out.println("2. Previous Month");
        System.out.println("3. Next Month");
        System.out.println("\n4. Go to Timer");
        System.out.println("5. Go to Stopwatch");
        System.out.println("6. Go to Time Calculator");
        System.out.println("\n7. Go to Active Tasks");
        System.out.println("8. Go to Recurring Tasks");
        System.out.println("9. Go to Completed Tasks");
        System.out.println("\n0. Main Menu");
        System.out.println();

        // Month header - exactly 39 chars wide
        String monthName = currentMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH).toUpperCase();
        System.out.printf("========================================================\n");
        System.out.printf("                     %s %d \n", monthName, currentMonth.getYear());
        System.out.printf("========================================================\n");

        // Days header - FIXED: single pipes, proper spacing
        System.out.println("| =MON || =TUE || =WED || =THU || =FRI || =SAT || =SUN |");

        // Separator - FIXED: consistent 39 chars
        System.out.println("--------------------------------------------------------");

        // Calendar grid
        // Section: Handle the logic for print calendar grid.
        printCalendarGrid();
    }

    // Section: Handle the logic for print calendar grid.
    private static void printCalendarGrid() {
        LocalDate today = LocalDate.now(); // Get today's date
        LocalDate firstDay = currentMonth.atDay(1);
        int firstWeekday = firstDay.getDayOfWeek().getValue(); // 1=Mon, 7=Sun
        int daysInMonth = currentMonth.lengthOfMonth();

        // Previous month info
        YearMonth prevMonth = currentMonth.minusMonths(1);
        int prevMonthDays = prevMonth.lengthOfMonth();

        int currentDay = 1;
        int nextDayCounter = 1; // Track sequential next month days

        // Row 1: Previous month trailing days + current month
        int prevDay = prevMonthDays - firstWeekday + 2;
        for (int col = 1; col <= 7; col++) {
            if (col < firstWeekday) {
                System.out.printf("| <%02d< |", prevDay++);
            } else {
                if (today.getDayOfMonth() == currentDay && today.getMonth() == currentMonth.getMonth() && today.getYear() == currentMonth.getYear()) {
                    // Highlight today's date
                    System.out.printf("|<<%02d>>|", currentDay++);
                } else {
                    System.out.printf("|  %02d  |", currentDay++);
                }
            }
        }
        System.out.println("\n--------------------------------------------------------");

        // Full weeks (rows 2-6)
        for (int row = 2; row <= 6; row++) {
            for (int col = 1; col <= 7; col++) {
                if (currentDay <= daysInMonth) {
                    if (today.getDayOfMonth() == currentDay && today.getMonth() == currentMonth.getMonth() && today.getYear() == currentMonth.getYear()) {
                        // Highlight today's date
                        System.out.printf("|<<%02d>>|", currentDay++);
                    } else {
                        System.out.printf("|  %02d  |", currentDay++);
                    }
                } else {
                    // Next month days
                    System.out.printf("| >%02d> |", nextDayCounter++);
                }
            }
            System.out.println("\n--------------------------------------------------------");
        }

        for (int row = 7; row <= 7; row++) {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMMM yyyy");
            System.out.println("          <<CURRENT DATE: " + now.format(fmt) + ">>");
            System.out.println("--------------------------------------------------------");
        }
    }

    // Section: Handle the logic for display time.
    private static void displayTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");
        System.out.printf("\n========================================================\n");
        System.out.printf("|                                                      |\n");
        System.out.println("|              << CURRENT TIME: " + now.format(fmt) + " >>               |");
        System.out.printf("|                                                      |\n");
        System.out.printf("========================================================\n");
    }
}
