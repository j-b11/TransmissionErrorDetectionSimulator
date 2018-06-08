package com.jacek;

public interface SignalGenerator {
    void setSignalLength(int length);
    byte[] generateSignal();
    byte[] getGeneratedSignal();
}
