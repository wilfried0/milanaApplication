package com.milana;

import com.milana.compression.services.Compression;

import java.util.ArrayList;

public class MilanaApplication {

    public static void main(String[] args) {
        Compression compression = new Compression();

        byte[] bytes = compression.fileToByteArray("/Users/sprintpay/Documents/test.txt");

        String binaryString = compression.byteArrayToBinaryString(bytes);

        ArrayList<String> list76 = compression.binaryStringToList76(binaryString);

        ArrayList<String> uniques = new ArrayList<>();

        for(String s : list76){
            uniques.add(compression.isolateUniques(s));
            compression.isolateDuplicatePositions(s);
        }
    }
}