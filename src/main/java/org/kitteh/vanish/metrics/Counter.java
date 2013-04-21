package org.kitteh.vanish.metrics;

public final class Counter extends Metrics.Plotter {
    public Counter(String name) {
        super(name);
    }

    @Override
    public int getValue() {
        return 1;
    }
}