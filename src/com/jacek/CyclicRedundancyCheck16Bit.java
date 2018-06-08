package com.jacek;

public class CyclicRedundancyCheck16Bit implements Algorithm {

    private int generator_polynomial;

    private static final int crc16 = 0x00008005;
    private static final int crcSDLC = 0x00001021;
    private static final int crcSDLCReverse = 0x00008408;

    private int numberOfErrors = 0;
    private int numberOfCorrectedErrors = 0;
    private int[] lookupTable;
    private String crcCode;

    public CyclicRedundancyCheck16Bit(String crcType) {
        switch(crcType){
            case "crc16" :
                generator_polynomial = crc16;
                break;
            case "sdlc" :
                generator_polynomial = crcSDLC;
                break;
            case "sdlc reverse" :
                generator_polynomial = crcSDLCReverse;
                break;
            default :
                generator_polynomial = crc16;
                break;

        }
        calculateLookupTable();
    }

    private void calculateLookupTable() {
        lookupTable = new int[256];

        for(int divident = 0; divident < 256; divident++) {

            int currentByte = divident << 8;

            for(byte bit = 0; bit < 8; bit++){
                if((currentByte & 0x8000) != 0){
                    currentByte <<= 1;
                    currentByte ^= generator_polynomial;
                }else {
                    currentByte <<= 1;
                }
                currentByte &= ~(1 << 16);
            }
            lookupTable[divident] = currentByte;
        }

    }

    @Override
    public byte[] encode(byte[] data) {
        return appendCRC(data, computeCRCWithLookupTableFor(data));
    }

    private int computeCRCWithLookupTableFor(byte[] data){
        int crc = 0;
        for (byte b : data) {
            byte pos = (byte) ((crc >> 8) ^ b);
            crc = ((crc << 8) & ~(0xFFFF0000)) ^ lookupTable[(int)pos & 0xFF];
        }
        crcCode = "Computed CRC code : " + String.format("0x%08X",crc);
        return crc;
    }

    private byte[] appendCRC(byte[] data, int crc){
        byte[] dataWithAppendedCRC = new byte[data.length + 2];
        System.arraycopy(data, 0, dataWithAppendedCRC, 0, data.length);
        dataWithAppendedCRC[data.length + 1] = (byte)(crc & ~(0x11111100));
        dataWithAppendedCRC[data.length] = (byte)((crc >> 8) & ~(0x11111100));
        return dataWithAppendedCRC;
    }

    @Override
    public byte[] decode(byte[] data) {

        int crc = computeCRCWithLookupTableFor(data);

        if(crc == 0){
            byte[] decodedData = new byte[data.length - 2];
            System.arraycopy(data, 0, decodedData, 0, data.length - 2);
            return decodedData;
        }else{
            numberOfErrors = 1;
            return new byte[0];
        }
    }

    @Override
    public int getErrorsCount() {
        return numberOfErrors;
    }

    @Override
    public int getCorrectedErrorsCount() {
        return numberOfCorrectedErrors;
    }

    public String getCrcCode() {
        return crcCode;
    }
}
