package co.aikar.timings.lib;

import javax.annotation.Nonnull;

class EmptyTiming extends MCTiming {

    EmptyTiming() {
        super();
    }

    @Override
    public final @Nonnull
    MCTiming startTiming() {
        return this;
    }

    @Override
    public final void stopTiming() {
        throw new UnsupportedOperationException();
    }
}
