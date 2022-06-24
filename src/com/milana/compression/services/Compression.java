package com.milana.compression.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Compression implements CompressionAction {

    public static int ref = 33554431;

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
        StringBuilder binaryData = new StringBuilder();
        for(byte b : bytes){
            binaryData.append(byteToBinaryString(b));
        }
        return binaryData.toString();
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
        return string76.substring(1, 7);
    }

    @Override
    public String isolateDuplicatePositions(String string76) {
        StringBuilder result = new StringBuilder();
        if(string76.charAt(11) == '1')
            result.append('1');
        else
            result.append('0');
        if(string76.charAt(22) == '1')
            result.append('1');
        else
            result.append('0');
        if(string76.charAt(33) == '1')
            result.append('1');
        else
            result.append('0');
        if(string76.charAt(44) == '1')
            result.append('1');
        else
            result.append('0');
        if(string76.charAt(55) == '1')
            result.append('1');
        else
            result.append('0');
        if(string76.charAt(66) == '1')
            result.append('1');
        else
            result.append('0');
        System.out.println(result.toString());
        return result.toString();
    }

    @Override
    public String computeOccurrences(String string76){
        ArrayList<Integer> positions = getExistPosition(string76);
        StringBuilder result = new StringBuilder();
        for(int i=0; i<9; i++){
            int finalI = i;
            if(i >= 1 && i<8){
                result.append(convertToBinaryString(positions.stream().filter(p -> String.valueOf(p).contains(String.valueOf(finalI))).reduce(0, (a, b) -> a+b),4));
            }else
                result.append(convertToBinaryString(positions.stream().filter(p -> String.valueOf(p).contains(String.valueOf(finalI))).reduce(0, (a, b) -> a+b),3));
        }
        return result.toString();
    }

    @Override
    public int computeIF(String string76) {
        ArrayList<Integer> positions = getExistPosition(string76);
        double IF = 0;
        for(int i=positions.size()-1; i>=0; i=i-2){
            if(positions.size()%2==0)
                IF = IF + getComputeValue(i)/getComputeValue(i-1);
            else{
                if(i == 0){
                    IF = IF + getComputeValue(i)/getComputeValue(i-1);
                }else{
                    IF = IF + getComputeValue(i);
                }
            }
        }
        return getIFValue(IF);
    }

    @Override
    public String get74(char firstBit, String unique, String doublon, String occurrence, int IF) {
        StringBuilder result = new StringBuilder();
        result.append(firstBit+unique+doublon+occurrence+convertToBinaryString(IF, 25));
        return result.toString();
    }

    //conversion d'un entier en x bits
    private String convertToBinaryString(int value, int x) {
        StringBuilder result = new StringBuilder();
        for (int i = x-1; i >= 0; i--) {
            int mask = 1 << i;
            result.append((value & mask) != 0 ? "1" : "0");
        }
        return result.toString();
    }

    //convertion d'un byte/octet en bits
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

    private ArrayList<Integer> getExistPosition(String string76) {
        ArrayList<Integer> positions = new ArrayList<>();
        if(string76.charAt(0) == '1')
            positions.add(0);
        for(int i=7; i<string76.length() && (i!=11 && i!=22 && i!=33 && i!=44 && i!=55 && i!=66 && i!=70); i++){
            if(string76.charAt(i) == '1')
                positions.add(i);
        }
        return positions;
    }

    private int getIFValue(double IF) {
        int newIF;
        String tempIF = Double.toString(IF).split(".")[1];
        tempIF = tempIF+"0000000";
        if(Long.parseLong(tempIF) == 0){
            newIF = 0;
        }else if(tempIF.startsWith("0") || Long.parseLong(tempIF) > ref){
            tempIF = tempIF.substring(0, 8);
            newIF = Integer.parseInt(tempIF);
        }else{
            tempIF = tempIF.substring(0, 9);
            newIF = Integer.parseInt(tempIF);
        }
        return newIF;
    }

    private boolean isPositionDuplicate(String string76, int position) {
        return string76.charAt(position) == '1';
    }

    //Valeur de calcul d'une position donnée
    private double getComputeValue(int position){
        return position+1+(position+12)/100;
    }

    private int computeOccurrence(ArrayList<Integer> positions, int value) {
        return positions.stream().filter(p -> String.valueOf(p).contains(String.valueOf(value))).reduce(0, (a, b) -> a+b);
    }
}
