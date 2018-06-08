package com.jacek;

public interface Algorithm {

    byte[] encode(byte[] data);
    byte[] decode(byte[] data);
    int getErrorsCount();
    int getCorrectedErrorsCount();

}
