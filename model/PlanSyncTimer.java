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
 * File purpose: This class supports the PlanSyncTimer part of PlanSync and documents the main responsibilities of the file.
 */

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
    // Section: Update the state used to listener.
    public synchronized void setListener(TimerListener listener) {
        this.listener = listener;
    }

    /** Set the countdown duration in seconds; only when not running. */
    // Section: Update the state used to duration.
    public synchronized void setDuration(int seconds) {
        if (seconds < 0) seconds = 0;
        if (running) return;

        this.initialSeconds = seconds;
        this.remainingSeconds = seconds;
        // Section: Handle the logic for notify tick.
        notifyTick();
    }

    /** Start the countdown from the current remaining time. */
    // Section: Handle the logic for start.
    public synchronized void start() {
        if (running || remainingSeconds <= 0) {
            return;
        }

        running = true;
        paused = false;
        // Section: Handle the logic for notify state.
        notifyState();

        countdownThread = new Thread(() -> {
            while (true) {
                // Section: Handle the logic for synchronized.
                synchronized (PlanSyncTimer.this) {
                    if (!running) break;
                }

                try {
                    Thread.sleep(1000);
                // Section: Handle the logic for catch.
                } catch (InterruptedException e) {
                    break;
                }

                // Section: Handle the logic for synchronized.
                synchronized (PlanSyncTimer.this) {
                    if (!running) break;

                    if (!paused && remainingSeconds > 0) {
                        remainingSeconds--;
                        // Section: Handle the logic for notify tick.
                        notifyTick();

                        if (remainingSeconds == 0) {
                            running = false;
                            paused = false;
                            Toolkit.getDefaultToolkit().beep();
                            // Section: Handle the logic for notify state.
                            notifyState();
                            // Section: Handle the logic for notify finished.
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
    // Section: Handle the logic for pause.
    public synchronized void pause() {
        if (!running || paused) return;
        paused = true;
        // Section: Handle the logic for notify state.
        notifyState();
    }

    /** Resume after pause. */
    // Section: Handle the logic for resume.
    public synchronized void resume() {
        if (!running || !paused) return;
        paused = false;
        // Section: Handle the logic for notify state.
        notifyState();
    }

    /** Reset back to the original duration and stop. */
    // Section: Handle the logic for reset.
    public synchronized void reset() {
        running = false;
        paused = false;
        remainingSeconds = initialSeconds;
        // Section: Handle the logic for notify tick.
        notifyTick();
        // Section: Handle the logic for notify state.
        notifyState();
    }

    /** Stop the timer and keep remainingSeconds as‑is. */
    // Section: Handle the logic for stop.
    public synchronized void stop() {
        running = false;
        paused = false;
        // Section: Handle the logic for notify state.
        notifyState();
    }

    // Section: Return the data used to remaining seconds.
    public synchronized int getRemainingSeconds() {
        return remainingSeconds;
    }

    // Section: Report whether running.
    public synchronized boolean isRunning() {
        return running;
    }

    // Section: Report whether paused.
    public synchronized boolean isPaused() {
        return paused;
    }

    /** Utility: format seconds as HH:MM:SS for labels. */
    // Section: Handle the logic for format time.
    public static String formatTime(int totalSeconds) {
        if (totalSeconds < 0) totalSeconds = 0;
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    /* ===== listener helpers ===== */

    // Section: Handle the logic for notify tick.
    private void notifyTick() {
        if (listener != null) listener.onTick(remainingSeconds);
    }

    // Section: Handle the logic for notify finished.
    private void notifyFinished() {
        if (listener != null) listener.onFinished();
    }

    // Section: Handle the logic for notify state.
    private void notifyState() {
        if (listener != null) listener.onStateChanged(running, paused);
    }
}
