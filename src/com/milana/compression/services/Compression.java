package com.milana.compression.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Compression implements CompressionAction {

    @Override
    public byte[] fileToByteArray(String filePath) {
        byte[] bytes = {};
        try {
            bytes = Files.readAllBytes(Paths.get(filePath));
        } catch (IOException ioe) {
            System.out.println("Une exception est survenue lors du traitement du fichier!");
        }
        return bytes;
    }

    @Override
    public String byteArrayToBinaryString(byte[] bytes) {
        String binaryData = "";
        for(byte b : bytes){
            binaryData = binaryData + byteToBinaryString(b);
        }
        return binaryData;
    }

    @Override
    public ArrayList<String> binaryStringToList76(String binaryString) {
        ArrayList<String> list76 = new ArrayList<>();
        String tmp = "";
        for(int i=0; i<binaryString.length(); i++){
            if(tmp.length() < 76){
                tmp = tmp+binaryString.charAt(i);
            }else{
                list76.add(tmp);
                tmp = "";
            }
        }
        System.out.print("\n"+ list76.toString()+"\n");
        return list76;
    }

    @Override
    public String isolateUniques(String string76) {
        System.out.println(string76.substring(1,7));
        //7 est exclusif
        return string76.substring(1, 7);
    }

    @Override
    public ArrayList<Integer> isolateDuplicatePositions(String string76) {
        ArrayList<Integer> positions = new ArrayList<>();
        if(string76.charAt(11) == '1')
            positions.add(11);
        if(string76.charAt(22) == '1')
            positions.add(22);
        if(string76.charAt(33) == '1')
            positions.add(33);
        if(string76.charAt(44) == '1')
            positions.add(44);
        if(string76.charAt(55) == '1')
            positions.add(55);
        if(string76.charAt(66) == '1')
            positions.add(66);
        System.out.println("\n"+positions.toString());
        return positions;
    }

    @Override
    public ArrayList<String> existPositions(String string76) {
        ArrayList<String> positions = new ArrayList<>();
        for(int i=0; i<string76.length(); i++){
            if(string76.charAt(i) == '1')
                positions.add(String.valueOf(i));
        }
        return positions;
    }

    @Override
    public String computeOccurrence(ArrayList<String> positions, int value) {
        return positions.stream().filter(p -> p.contains(String.valueOf(value))).reduce("", (String a, String b) -> a+b);
    }

    private String byteToBinaryString(byte b){
        byte[] masks = { -128, 64, 32, 16, 8, 4, 2, 1 };
        StringBuilder binaryData = new StringBuilder();
        for(byte m : masks){
            if((b & m) == m)
                binaryData.append('1');
            else
                binaryData.append('0');
        }
        System.out.print(b+" = ");
        System.out.print(binaryData.toString()+" ");
        return binaryData.toString();
    }
}
