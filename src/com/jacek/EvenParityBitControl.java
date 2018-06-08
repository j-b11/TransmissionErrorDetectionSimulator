package com.jacek;

import java.util.ArrayList;
import java.util.Iterator;

public class EvenParityBitControl implements Algorithm {

    private int errorsDetectedNumber = 0;

    @Override
    public byte[] encode(byte[] inputData) {
        return addParityBit(inputData);
    }

    private byte[] addParityBit(byte[] data){
        for(int i = 0; i < data.length; i++){
            if(!countOnesAndCheckIfEven(data[i])){
                data[i] = (byte) (data[i] | (1 << 7));
            }
        }
        return data;
    }

    private boolean countOnesAndCheckIfEven(byte data) {

        int numberOfOnes = 0;

        ByteIterable iterable = new ByteIterable(data);
        for (Boolean anIterable : iterable) {
            if (anIterable) {
                numberOfOnes++;
            }
        }

        return numberOfOnes % 2 == 0;
    }

    @Override
    public byte[] decode(byte[] receivedData) {

        ArrayList<Byte> decodedBytes = new ArrayList<>();

        for (byte aReceivedData : receivedData) {
            if (!countOnesAndCheckIfEven(aReceivedData)) {
                errorsDetectedNumber++;
            } else {
                decodedBytes.add(stripParityBit(aReceivedData));
            }
        }

        return  byteArrayObjectToPrimitive(decodedBytes.toArray(new Byte[decodedBytes.size()]));
    }


    private byte stripParityBit(byte sample){
        return (byte) (sample & ~(1 << 7));
    }


    @Override
    public int getErrorsCount() {
        return errorsDetectedNumber;
    }

    @Override
    public int getCorrectedErrorsCount() {
        return 0;
    }

    private byte[] byteArrayObjectToPrimitive(Byte[] bytes){
        byte[] primitive = new byte[bytes.length];
        int j=0;
        for(Byte b: bytes)
            primitive[j++] = b.byteValue();
        return primitive;
    }

}
