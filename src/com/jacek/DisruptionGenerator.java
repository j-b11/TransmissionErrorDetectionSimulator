package com.jacek;

import java.util.ArrayList;
import java.util.Map;

public interface DisruptionGenerator {

    byte[] disrupt(byte[] signal);
    byte[] getDisruptedSignal();
    int getNumberOfCreatedErrors();
    Map<Integer, ArrayList<Integer>> getDisruptedBits();
}
