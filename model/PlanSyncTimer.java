package model;

import java.awt.Toolkit;

/**
 * UI‑friendly countdown timer model.
 * No Swing or console code; all communication is via TimerListener.
 */
public class PlanSyncTimer {

    private int remainingSeconds;
    private int initialSeconds;

    private boolean running;
    private boolean paused;

    private Thread countdownThread;
    private TimerListener listener;

    public PlanSyncTimer() {
        this.remainingSeconds = 0;
        this.initialSeconds = 0;
        this.running = false;
        this.paused = false;
    }

    /** Register a listener (typically your Swing view). */
    public synchronized void setListener(TimerListener listener) {
        this.listener = listener;
    }

    /** Set the countdown duration in seconds; only when not running. */
    public synchronized void setDuration(int seconds) {
        if (seconds < 0) seconds = 0;
        if (running) return;

        this.initialSeconds = seconds;
        this.remainingSeconds = seconds;
        notifyTick();
    }

    /** Start the countdown from the current remaining time. */
    public synchronized void start() {
        if (running || remainingSeconds <= 0) {
            return;
        }

        running = true;
        paused = false;
        notifyState();

        countdownThread = new Thread(() -> {
            while (true) {
                synchronized (PlanSyncTimer.this) {
                    if (!running) break;
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }

                synchronized (PlanSyncTimer.this) {
                    if (!running) break;

                    if (!paused && remainingSeconds > 0) {
                        remainingSeconds--;
                        notifyTick();

                        if (remainingSeconds == 0) {
                            running = false;
                            paused = false;
                            Toolkit.getDefaultToolkit().beep();
                            notifyState();
                            notifyFinished();
                            break;
                        }
                    }
                }
            }
        });

        countdownThread.setDaemon(true);
        countdownThread.start();
    }

    /** Pause the timer. */
    public synchronized void pause() {
        if (!running || paused) return;
        paused = true;
        notifyState();
    }

    /** Resume after pause. */
    public synchronized void resume() {
        if (!running || !paused) return;
        paused = false;
        notifyState();
    }

    /** Reset back to the original duration and stop. */
    public synchronized void reset() {
        running = false;
        paused = false;
        remainingSeconds = initialSeconds;
        notifyTick();
        notifyState();
    }

    /** Stop the timer and keep remainingSeconds as‑is. */
    public synchronized void stop() {
        running = false;
        paused = false;
        notifyState();
    }

    public synchronized int getRemainingSeconds() {
        return remainingSeconds;
    }

    public synchronized boolean isRunning() {
        return running;
    }

    public synchronized boolean isPaused() {
        return paused;
    }

    /** Utility: format seconds as HH:MM:SS for labels. */
    public static String formatTime(int totalSeconds) {
        if (totalSeconds < 0) totalSeconds = 0;
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    /* ===== listener helpers ===== */

    private void notifyTick() {
        if (listener != null) listener.onTick(remainingSeconds);
    }

    private void notifyFinished() {
        if (listener != null) listener.onFinished();
    }

    private void notifyState() {
        if (listener != null) listener.onStateChanged(running, paused);
    }
}
