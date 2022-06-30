package com.milana.compression.services;

import java.nio.file.StandardOpenOption;
import java.util.List;

public interface CompressionAction {

    public byte[] fileToByteArray(String filePath);

    public String byteArrayToBinaryString(byte[] bytes);

    public List<String> binaryStringToList76(String binaryString);

    public String isolateUniques(String string76);

    public String isolateDuplicatePositions(String string76);

    public String computeOccurrences(String string76);

    public int computeIF(String string76);

    public String get74(String unique, String doublon, String occurrence, int IF);

    public void binaryStringToFile(String binaryString, String path, StandardOpenOption option);
}
