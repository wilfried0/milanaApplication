package com.milana.compression.services;

import java.util.ArrayList;

public interface CompressionAction {

    public byte[] fileToByteArray(String filePath);

    public String byteArrayToBinaryString(byte[] bytes);

    public ArrayList<String> binaryStringToList76(String binaryString);

    public String isolateUniques(String string76);

    public ArrayList<Integer> isolateDuplicatePositions(String string76);

    public ArrayList<String> existPositions(String string76);

    public String computeOccurrence(ArrayList<String> positions, int value);
}
