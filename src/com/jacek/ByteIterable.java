package com.jacek;

import java.util.Iterator;

public class ByteIterable implements Iterable<Boolean> {

    private final byte givenByte;

    public ByteIterable(byte givenByte) {
        this.givenByte = givenByte;
    }

    @Override
    public Iterator<Boolean> iterator() {
        return new Iterator<Boolean>() {

            private int index = 0;

            @Override
            public boolean hasNext() {
                return (index < 8);
            }

            @Override
            public Boolean next() {
                Boolean val = (givenByte >> (7 - index) & 1) == 1;
                index++;
                return val;
            }
        };
    }
}
