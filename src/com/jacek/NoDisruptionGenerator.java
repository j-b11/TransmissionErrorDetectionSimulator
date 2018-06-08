package com.jacek;

import java.util.ArrayList;
import java.util.Map;

public class NoDisruptionGenerator implements DisruptionGenerator{

    private byte[] signal;

    @Override
    public byte[] disrupt(byte[] signal) {
        this.signal = signal;
        return signal;
    }

    @Override
    public byte[] getDisruptedSignal() {
        return signal;
    }

    @Override
    public int getNumberOfCreatedErrors() {
        return 0;
    }

    @Override
    public Map<Integer, ArrayList<Integer>> getDisruptedBits() {
        return null;
    }
}
