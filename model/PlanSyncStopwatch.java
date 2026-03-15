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
 * File purpose: This class supports the PlanSyncStopwatch part of PlanSync and documents the main responsibilities of the file.
 */

import java.util.ArrayList;
import java.util.List;

public class PlanSyncStopwatch {

    public interface StopwatchListener {
        // Section: Handle the logic for on tick.
        void onTick(long elapsedMillis);
        // Section: Handle the logic for on lap recorded.
        void onLapRecorded(String lapText);
        // Section: Handle the logic for on reset.
        void onReset();
        // Section: Handle the logic for on state changed.
        void onStateChanged(boolean running);
    }

    private long startTime = 0;
    private long elapsedMillis = 0;
    private boolean running = false;

    private Thread thread;
    private StopwatchListener listener;

    private final List<String> laps = new ArrayList<>();
    private int lapCounter = 0;

    // Section: Update the state used to listener.
    public void setListener(StopwatchListener listener) {
        this.listener = listener;
    }

    // Section: Handle the logic for start.
    public synchronized void start() {
        if (running) return;

        running = true;
        startTime = System.currentTimeMillis() - elapsedMillis;
        // Section: Handle the logic for notify state.
        notifyState();

        thread = new Thread(() -> {
            while (running) {
                elapsedMillis = System.currentTimeMillis() - startTime;
                // Section: Handle the logic for notify tick.
                notifyTick();
                try {
                    Thread.sleep(10); // update every 10ms
                } catch (InterruptedException ignored) {}
            }
        });

        thread.setDaemon(true);
        thread.start();
    }

    // Section: Handle the logic for stop.
    public synchronized void stop() {
        running = false;
        // Section: Handle the logic for notify state.
        notifyState();
    }

    // Section: Handle the logic for reset.
    public synchronized void reset() {
        running = false;
        elapsedMillis = 0;
        lapCounter = 0;
        laps.clear();

        // Section: Handle the logic for notify tick.
        notifyTick();
        // Section: Handle the logic for notify state.
        notifyState();

        if (listener != null) {
            listener.onReset();
        }
    }

    // Section: Handle the logic for lap.
    public synchronized void lap() {
        if (!running) return;

        lapCounter++;
        String formatted = formatTime(elapsedMillis);
        String lapText = "LAP " + lapCounter + " : ELAPSED → " + formatted;

        laps.add(lapText);

        if (listener != null) {
            listener.onLapRecorded(lapText);
        }
    }

    // Section: Report whether running.
    public synchronized boolean isRunning() {
        return running;
    }

    // Section: Handle the logic for format time.
    public static String formatTime(long millis) {
        long minutes = millis / 60000;
        long seconds = (millis % 60000) / 1000;
        long ms = millis % 1000;

        return String.format("%02d:%02d.%03d", minutes, seconds, ms);
    }

    // Section: Handle the logic for notify tick.
    private void notifyTick() {
        if (listener != null) {
            listener.onTick(elapsedMillis);
        }
    }

    // Section: Handle the logic for notify state.
    private void notifyState() {
        if (listener != null) {
            listener.onStateChanged(running);
        }
    }
}
