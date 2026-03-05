package model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Calendar model used by the GUI Calendar screen.
 *
 * Month-grid logic is intentionally aligned with modelTerminal/PlanSyncCalendar:
 * - Monday is the first column
 * - We render a 6x7 grid (42 cells)
 * - We include previous/next month spillover days
 */
public class PlanSyncCalendarModel {

    public static final class DayCell {
        private final LocalDate date;
        private final boolean inCurrentMonth;
        private final boolean today;
        private final int activeTaskCount;

        private DayCell(LocalDate date, boolean inCurrentMonth, boolean today, int activeTaskCount) {
            this.date = date;
            this.inCurrentMonth = inCurrentMonth;
            this.today = today;
            this.activeTaskCount = activeTaskCount;
        }

        public LocalDate getDate() { return date; }
        public boolean isInCurrentMonth() { return inCurrentMonth; }
        public boolean isToday() { return today; }
        public int getActiveTaskCount() { return activeTaskCount; }
        public boolean hasActiveTasks() { return activeTaskCount > 0; }
    }

    private YearMonth currentMonth;
    private LocalDate selectedDate;

    private final PlanSyncActiveTasksModel activeTasksModel;

    public PlanSyncCalendarModel(PlanSyncActiveTasksModel activeTasksModel) {
        this.activeTasksModel = activeTasksModel;
        this.currentMonth = YearMonth.now();
        this.selectedDate = LocalDate.now();
    }

    public YearMonth getCurrentMonth() {
        return currentMonth;
    }

    public LocalDate getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(LocalDate selectedDate) {
        if (selectedDate != null) {
            this.selectedDate = selectedDate;
            this.currentMonth = YearMonth.from(selectedDate);
        }
    }

    public void previousMonth() {
        currentMonth = currentMonth.minusMonths(1);
    }

    public void nextMonth() {
        currentMonth = currentMonth.plusMonths(1);
    }

    /**
     * Builds a 42-cell (6 weeks) grid for the current month.
     */
    public List<DayCell> buildMonthGrid() {
        LocalDate today = LocalDate.now();

        LocalDate firstDayOfMonth = currentMonth.atDay(1);
        int firstWeekday = firstDayOfMonth.getDayOfWeek().getValue(); // Mon=1..Sun=7

        // Find the date that appears in the first cell (Monday of the first grid week)
        LocalDate gridStart = firstDayOfMonth.minusDays(firstWeekday - DayOfWeek.MONDAY.getValue());

        List<DayCell> cells = new ArrayList<>(42);
        for (int i = 0; i < 42; i++) {
            LocalDate d = gridStart.plusDays(i);
            boolean inMonth = YearMonth.from(d).equals(currentMonth);
            boolean isToday = d.equals(today);
            int count = activeTasksModel != null ? activeTasksModel.countTasksForDate(d) : 0;
            cells.add(new DayCell(d, inMonth, isToday, count));
        }
        return Collections.unmodifiableList(cells);
    }
}