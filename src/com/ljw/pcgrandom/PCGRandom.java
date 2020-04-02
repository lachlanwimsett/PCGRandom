package com.ljw.pcgrandom;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class PCGRandom extends Random {

    private static final long multiplier = 6364136223846793005L;

    private AtomicLong state;
    private long inc;

    public PCGRandom() {
        super(0);
        long seed = super.nextLong();
        long seq = super.nextLong();
        init(seed, seq);
    }

    public PCGRandom(long seed, long seq) {
        super(0);
        init(seed, seq);
    }

    private void init(long seed, long seq) {
        state = new AtomicLong();
        inc = (seq << 1) | 1;
        initSeed(seed);
    }

    private void initSeed(long seed) {
        state.set(0);
        step();
        state.getAndAdd(seed);
        step();
    }

    @Override
    public synchronized void setSeed(long seed) {
        if (state != null) {
            super.setSeed(0);
            initSeed(seed);
        }
    }

    private static int permuteInt(long state) {
        int xorshifted = (int) (((state >>> 18) ^ state) >>> 27);
        int rot = (int) (state >>> 59);
        return (xorshifted >>> rot) | (xorshifted << (-rot & 31));
    }

    private long step() {
        long oldstate, nextstate;
        do {
            oldstate = state.get();
            nextstate = oldstate * multiplier + inc;
        } while (!state.compareAndSet(oldstate, nextstate));
        return oldstate;
    }

    @Override
    protected int next(int bits) {
        long oldstate = step();
        return permuteInt(oldstate) >>> (32 - bits);
    }

}
