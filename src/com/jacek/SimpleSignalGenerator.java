package com.jacek;

import java.util.Random;

public class SimpleSignalGenerator implements SignalGenerator{

    private byte[] generatedSignal;
    private int signalLength;

    public SimpleSignalGenerator(int signalLength) {
        this.signalLength = signalLength;
    }

    @Override
    public void setSignalLength(int length) {
        this.signalLength = length;
    }

    @Override
    public byte[] generateSignal() {
        generatedSignal = new byte[signalLength];
        new Random().nextBytes(generatedSignal);

        for(int i = 0; i < generatedSignal.length; i++){
            generatedSignal[i] = (byte)(generatedSignal[i] & ~(1 << 7));
        }

        return generatedSignal;
    }

    @Override
    public byte[] getGeneratedSignal() {
        return generatedSignal;
    }

}
