package model;

public interface TimerListener {
    void onTick(int remainingSeconds);
    void onFinished();
    void onStateChanged(boolean running, boolean paused);
}