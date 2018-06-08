package com.jacek;

import java.util.ArrayList;
import java.util.Map;

public class Simulation {

    private byte[] input;
    private byte[] encoded;
    private byte[] disrupted;
    private byte[] decoded;
    private double errorDetectedToActualRatio;
    private int numberOfErrorsDetected;
    private int actualNumberOfErrors;
    private String errorLog;
    private Map<Integer, ArrayList<Integer>> disruptedBits;

    public byte[] getInput() {
        return input;
    }

    public void setInput(byte[] input) {
        this.input = input.clone();
    }

    public byte[] getEncoded() {
        return encoded;
    }

    public void setEncoded(byte[] encoded) {
        this.encoded = encoded.clone();
    }

    public byte[] getDisrupted() {
        return disrupted;
    }

    public void setDisrupted(byte[] disrupted) {
        this.disrupted = disrupted.clone();
    }

    public byte[] getDecoded() {
        return decoded;
    }

    public void setDecoded(byte[] decoded) {
        this.decoded = decoded.clone();
    }

    public double getErrorDetectedToActualRatio() {
        this.errorDetectedToActualRatio = (double) numberOfErrorsDetected / (double) actualNumberOfErrors;
        return errorDetectedToActualRatio;
    }

    public int getNumberOfErrorsDetected() {
        return numberOfErrorsDetected;
    }

    public void setNumberOfErrorsDetected(int numberOfErrorsDetected) {
        this.numberOfErrorsDetected = numberOfErrorsDetected;
    }

    public int getActualNumberOfErrors() {
        return actualNumberOfErrors;
    }

    public void setActualNumberOfErrors(int actualNumberOfErrors) {
        this.actualNumberOfErrors = actualNumberOfErrors;
    }

    public Map<Integer, ArrayList<Integer>> getDisruptedBits() {
        return disruptedBits;
    }

    public void setDisruptedBits(Map<Integer, ArrayList<Integer>> disruptedBits) {
        this.disruptedBits = disruptedBits;
    }

}
