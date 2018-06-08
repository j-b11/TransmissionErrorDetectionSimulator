package com.jacek;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class OneBitDisruptionGenerator implements DisruptionGenerator{

    private byte[] disruptedSignal;
    private Map<Integer, ArrayList<Integer>> disruptedBits;
    private ArrayList<Integer> pickedBytes;
    private ArrayList<Integer> affectedBits;

    public OneBitDisruptionGenerator() {
        disruptedBits = new HashMap<>();
        affectedBits = new ArrayList<>();
        pickedBytes = new ArrayList<>();
    }

    @Override
    public byte[] disrupt(byte[] signal) {

        byte[] input = signal.clone();

        int chosenBitInStream = pickBitToDisrupt((input.length * 8 )- 1);
        int bytePosition = (int)Math.floor(chosenBitInStream / 8);
        pickedBytes.add(bytePosition);
        affectedBits.add(chosenBitInStream % 8);

        input[bytePosition] = disrupt(input[bytePosition], chosenBitInStream % 8);

        disruptedSignal = input;

        return disruptedSignal;
    }

    private int pickBitToDisrupt(int max){
        return ThreadLocalRandom.current().nextInt(0, max);
    }

    private byte disrupt(byte data, int bit){
        byte disruptedByte;

        if(((data >> bit) & 1) == 1) {
            disruptedByte = (byte)(data & ~(1 << bit));
        }else {
            disruptedByte = (byte)(data | (1<<bit));
        }

        return disruptedByte;
    }

    @Override
    public byte[] getDisruptedSignal() {
        return disruptedSignal;
    }

    @Override
    public int getNumberOfCreatedErrors() {
        return 1;
    }

    @Override
    public Map<Integer, ArrayList<Integer>> getDisruptedBits() {
        disruptedBits.put(pickedBytes.get(0), (ArrayList<Integer>) affectedBits.clone());
        return disruptedBits;
    }
}
