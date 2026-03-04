package model;

import java.util.ArrayList;
import java.util.List;

public class PlanSyncStopwatch {

    public interface StopwatchListener {
        void onTick(long elapsedMillis);
        void onLapRecorded(String lapText);
        void onReset();
        void onStateChanged(boolean running);
    }

    private long startTime = 0;
    private long elapsedMillis = 0;
    private boolean running = false;

    private Thread thread;
    private StopwatchListener listener;

    private final List<String> laps = new ArrayList<>();
    private int lapCounter = 0;

    public void setListener(StopwatchListener listener) {
        this.listener = listener;
    }

    public synchronized void start() {
        if (running) return;

        running = true;
        startTime = System.currentTimeMillis() - elapsedMillis;
        notifyState();

        thread = new Thread(() -> {
            while (running) {
                elapsedMillis = System.currentTimeMillis() - startTime;
                notifyTick();
                try {
                    Thread.sleep(10); // update every 10ms
                } catch (InterruptedException ignored) {}
            }
        });

        thread.setDaemon(true);
        thread.start();
    }

    public synchronized void stop() {
        running = false;
        notifyState();
    }

    public synchronized void reset() {
        running = false;
        elapsedMillis = 0;
        lapCounter = 0;
        laps.clear();

        notifyTick();
        notifyState();

        if (listener != null) {
            listener.onReset();
        }
    }

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

    public synchronized boolean isRunning() {
        return running;
    }

    public static String formatTime(long millis) {
        long minutes = millis / 60000;
        long seconds = (millis % 60000) / 1000;
        long ms = millis % 1000;

        return String.format("%02d:%02d.%03d", minutes, seconds, ms);
    }

    private void notifyTick() {
        if (listener != null) {
            listener.onTick(elapsedMillis);
        }
    }

    private void notifyState() {
        if (listener != null) {
            listener.onStateChanged(running);
        }
    }
}