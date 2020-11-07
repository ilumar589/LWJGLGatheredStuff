package engine;

public class Timer {
    private double lastLoopTime;

    public Timer() {}

    public void init() {
        this.lastLoopTime = getTime();
    }

    public double getTime() {
        return System.nanoTime() / 1_000_000_000.0;
    }

    public double getElapsedTime() {
        double time = getTime();
        double elapsedTime = time - lastLoopTime;
        lastLoopTime = time;
        return elapsedTime;
    }

    public double getLastLoopTime() {
        return lastLoopTime;
    }
}
