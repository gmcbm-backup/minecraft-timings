package co.aikar.timings.lib;

public abstract class MCTiming implements AutoCloseable {

    public abstract MCTiming startTiming();

    public abstract void stopTiming();

    @Override
    public void close() {
        stopTiming();
    }
}

