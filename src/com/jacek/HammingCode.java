package com.jacek;

public class HammingCode implements Algorithm {

    private int encoded_data_length = 0;
    private int parity_bits_added = 0;
    private byte[] inputData;
    private byte[] encodedData;

    private int numberOfErrors = 0;
    private int numberOfCorrectedErrors = 0;
    private int errorPosition = -1;


    @Override
    public byte[] encode(byte[] data) {
        inputData = data;
        parity_bits_added = countNeededParityBits();
        encoded_data_length = data.length * 8 + parity_bits_added;
        encodedData = new byte[data.length + (int)Math.ceil(parity_bits_added / 8.0)];
        rewriteDataWithAddedParityBits();
        calculateAndInsertParityBitsValues();
        return encodedData;
    }

    private int countNeededParityBits() {
        int counter = 0;
        int parity_counter = 0;

        while(counter < (inputData.length * 8)){
            if(Math.pow(2, parity_counter) == counter + parity_counter + 1) {
                parity_counter ++;
            }else{
                counter ++;
            }
        }
        return parity_counter;
    }

    private void rewriteDataWithAddedParityBits() {
        int j = 0;

        Counter destination = new Counter();
        Counter source = new Counter();

        for(int i = 1; i <= encodedData.length * 8; i++){
            if(Math.pow(2, j) != i){

                byte currentDestinationByte = encodedData[destination.currentByte()];

                if(source.currentByte() < inputData.length){

                    byte currentSourceByte = inputData[source.currentByte()];

                    if(isBitSet(currentSourceByte, source.currentBit())){
                        currentDestinationByte = setBit(currentDestinationByte, destination.currentBit());
                    }else {
                        currentDestinationByte = resetBit(currentDestinationByte, destination.currentBit());
                    }
                }else{
                    currentDestinationByte = resetBit(currentDestinationByte, destination.currentBit());
                }

                encodedData[destination.currentByte()] = currentDestinationByte;

                source.nextBit();
            }else{
                j++;
            }
            destination.nextBit();
        }
    }

    private boolean isBitSet(byte sourceByte, int bitInByte) {
        return ((sourceByte >> bitInByte) & 1) == 1;
    }

    private byte setBit(byte changedByte, int bitPosition){
        return (byte) (changedByte | (1<<bitPosition));
    }

    private byte resetBit(byte changedByte, int bitPosition){
        return (byte) (changedByte & ~(1<<bitPosition));
    }


    private void calculateAndInsertParityBitsValues(){
        for(int i = 0; i < parity_bits_added; i++){
            boolean parity_bit_value = getParity(i, encodedData);
            int position = (int) Math.pow(2,i) - 1;
            int bit_position = position%8;
            int byte_position = (int)Math.floor(position/8);
            if(parity_bit_value){
                encodedData[byte_position] = setBit(encodedData[byte_position], bit_position);
            }else{
                encodedData[byte_position] = resetBit(encodedData[byte_position], bit_position);
            }
        }
    }

    private boolean getParity(int power, byte[] data) {

        boolean parity = false;
        int parity_counter = 0;
        Counter counter = new Counter();

        for(int i = 0; i < encoded_data_length; i++){
            if(!isPowerOfTwo(i)) {
                if(isPositionCovered(i, power)) {
                    if(isBitSet(counter, data)){
                        parity_counter = (parity_counter + 1)%2;
                    }
                }
            }
            counter.nextBit();
        }

        if(parity_counter == 1){
            parity = true;
        }

        return parity;
    }

    private boolean isPowerOfTwo(int i) {
        return ((i + 1) & i) == 0;
    }

    private boolean isPositionCovered(int position, int power) {
        position++;
        String s = Integer.toBinaryString(position);
        int x = ((Integer.parseInt(s))/((int)Math.pow(10, power)))%10;
        if(x == 1){
            return true;
        }else {
            return false;
        }
    }

    private boolean isBitSet(Counter counter, byte[] data){
        return ((data[counter.currentByte()] >> counter.currentBit()) & 1) == 1;
    }

    @Override
    public byte[] decode(byte[] data) {
        int error_location = checkForErrors(data);
        if(error_location != 0){
            errorPosition = error_location;
            numberOfErrors++;
            data = getCorrectedData(data, error_location);
        }

        return getOriginalData(data, parity_bits_added);
    }

    private int checkForErrors(byte[] data) {
        int[] parity = new int[parity_bits_added];

        String syndrome = "";

        for(int power = 0; power < parity_bits_added; power++){
            for(int i = 0; i < encoded_data_length; i++){
                if(isPositionCovered(i ,power)){
                    if(isBitSet(data, i)){
                        parity[power] = (parity[power] + 1)%2;
                    }
                }
            }
            syndrome = parity[power] + syndrome;
        }

        return Integer.parseInt(syndrome, 2);
    }

    private boolean isBitSet(byte[] data, int bit){
        int bytePosition = (int)Math.floor(bit/8);
        int bitPosition = bit%8;
        if(((data[bytePosition] >> (bitPosition)) & 1) == 1){
            return true;
        }else {
            return false;
        }
    }

    private byte[] getCorrectedData(byte[] data, int error_location){
        data[(int) Math.floor((error_location-1)/8)] ^= (1 << ((error_location-1)%8));
        numberOfCorrectedErrors++;
        return data;
    }

    private byte[] getOriginalData(byte[] dataWithAppendedBits, int numberOfAppendedBits){
        int numberOfDataBits = encoded_data_length - numberOfAppendedBits;
        byte[] originalData = new byte[(int)Math.ceil((numberOfDataBits)/8)];

        int counter = 0;

        for(int i = 0; i < encoded_data_length; i++){
            if(!isPowerOfTwo(i)){
                if(isBitSet(dataWithAppendedBits, i)){
                    originalData = setBit(originalData, counter);
                }
                counter++;
            }
        }

        return originalData;
    }

    private byte[] setBit(byte[] data, int bit){
        int byte_position = (int) Math.floor(bit/8);
        int bit_position = bit%8;
        data[byte_position] = (byte) (data[byte_position] | (1 << bit_position));
        return data;
    }

    @Override
    public int getErrorsCount() {
        return numberOfErrors;
    }

    @Override
    public int getCorrectedErrorsCount() {
        return numberOfCorrectedErrors;
    }

    private class Counter{
        private int byteCounter = 0;
        private int bitCounter = 0;

        public int currentByte(){
            return byteCounter;
        }

        public int currentBit(){
            return bitCounter;
        }

        public void nextBit(){
            if(bitCounter < 7){
                bitCounter++;
            }else{
                bitCounter = 0;
                byteCounter++;
            }
        }
    }

}


