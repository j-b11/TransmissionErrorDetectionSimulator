package com.jacek;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class SimpleDisruptionGenerator implements DisruptionGenerator{

    private byte[] disruptedSignal;
    private float disruptionPotency = 3;
    private int numberOfCreatedErrors = 0;
    private Map<Integer, ArrayList<Integer>> disruptedBits;
    private ArrayList<Integer> pickedBytes;
    private ArrayList<Integer> affectedBits;
    private int currentDataIndex;

    public SimpleDisruptionGenerator() {
        disruptedBits = new HashMap<>();
        affectedBits = new ArrayList<>();
    }

    @Override
    public byte[] disrupt(byte[] input) {

        byte[] signal = input.clone();

        ArrayList<Integer> bytesToDisrupt = pickRandomBytesToDisrupt(signal.length * disruptionPotency, signal.length);

        for(int i = 0; i < signal.length; i++){
            if(bytesToDisrupt.contains(i)){
                currentDataIndex = i;
                affectedBits.clear();
                signal[i] = disruptByte(signal[i]);
            }
        }

        disruptedSignal = signal;
        return signal;
    }

    private ArrayList<Integer> pickRandomBytesToDisrupt(float amount, int total){
        int numberOfBytes = Math.round(amount);
        pickedBytes = new ArrayList<>();
        for(int i = 0; i < numberOfBytes ; i++){
            int randomByteIndex = ThreadLocalRandom.current().nextInt(0, total);
            pickedBytes.add(randomByteIndex);
        }
        return pickedBytes;
    }

    private byte disruptByte(byte b){

        boolean createdError = true;

        byte disruptedByte = b;

        int randomBit1 =  ThreadLocalRandom.current().nextInt(1, 8);
        disruptedByte = (byte) (disruptedByte | (1 << randomBit1));


        int randomBit2 = ThreadLocalRandom.current().nextInt(1, 8);
        disruptedByte = (byte) (disruptedByte & ~(1 << randomBit2));

        if(disruptedByte == b){
            createdError = false;
        }

        if(createdError){
            affectedBits.add(randomBit1);
            affectedBits.add(randomBit2);
            numberOfCreatedErrors++;
        }

        disruptedBits.put(currentDataIndex, (ArrayList<Integer>) affectedBits.clone());

        return disruptedByte;
    }

    public void setDisruptionPotency(float disruptionPotency) {
        this.disruptionPotency = disruptionPotency;
    }

    public byte[] getDisruptedSignal() {
        return disruptedSignal;
    }

    public int getNumberOfCreatedErrors() {
        return numberOfCreatedErrors;
    }

    @Override
    public Map<Integer, ArrayList<Integer>> getDisruptedBits() {
        return disruptedBits;
    }
}
