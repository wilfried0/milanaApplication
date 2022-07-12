package com.milana.compression.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Compression implements CompressionAction {

    private static int ref = 33554431;
    public static int seuil = 30;
    public static int MAX_VALUE = Integer.MAX_VALUE/4;

    public static ArrayList<String> resteBits = new ArrayList<>();

    private static final int NB_CPU = Runtime.getRuntime().availableProcessors();

    @Override
    public byte[] fileToByteArray(String filePath) {
        byte[] bytes ={};
        try {
            bytes = Files.readAllBytes(Paths.get(filePath));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return bytes;
    }

    @Override
    public String byteArrayToBinaryString(byte[] bytes) {
        ExecutorService es = Executors.newFixedThreadPool(NB_CPU);
        String[] binaryString = new String[bytes.length];
        es.execute(() -> {
            for(int i=0; i<bytes.length; i++){
                binaryString[i] = byteToBinaryString(bytes[i]);
                System.out.print("("+i+")"+" "+bytes[i]+" = "+binaryString[i]+"\n");
            }
        });
        es.shutdown();
        return Arrays.stream(binaryString).reduce("", (a, b)-> a+b);
    }

    @Override
    public List<String> binaryStringToList76(String binaryString) {
        ArrayList<String> list76 = new ArrayList<>();
        String tmp = "";
        for(int i=0; i<binaryString.length(); i++){
            if(tmp.length() < 76){
                tmp = tmp+binaryString.charAt(i);
            }else{
                list76.add(tmp);
                tmp = ""+binaryString.charAt(i);
            }
        }
        if(!tmp.equals("")){
            resteBits.add(tmp+"-"+convertToBinaryString(tmp.length(), 7)+"-"+tmp.length());
            //tmp = tmp+""+convertToBinaryString(tmp.length(), 7);
        }else{
            resteBits.add("-0000000-0");
            //tmp = convertToBinaryString(0, 7);
        }

        System.out.println("reste + taille" + resteBits.toString());
        return list76;
    }

    @Override
    public String[] isolateUniques(List<String> list76) {
        ExecutorService es = Executors.newFixedThreadPool(NB_CPU);
        String[] uniques = new String[list76.size()];
        System.out.println("----------------------- Uniques -----------------------");
        es.execute(() -> {
            for(int i=0; i<list76.size(); i++){
                uniques[i] = list76.get(i).substring(1,7);
                System.out.println(i+" => "+uniques[i]);
            }
        });
        return uniques;
    }

    @Override
    public String[] isolateDuplicatePositions(List<String> list76) {
        ExecutorService es = Executors.newFixedThreadPool(NB_CPU);
        String[] duplicates = new String[list76.size()];
        System.out.println("----------------------- Uniques -----------------------");
        es.execute(() -> {
            for(int i=0; i<list76.size(); i++){
                duplicates[i] = ""+list76.get(i).charAt(11)+list76.get(i).charAt(22)+list76.get(i).charAt(33)+list76.get(i).charAt(44)+list76.get(i).charAt(55)+list76.get(i).charAt(66);
                System.out.println(i+" => "+duplicates[i]);
            }
        });
        return duplicates;
    }

    @Override
    public String[] computeOccurrences(List<String> list76){
        StringBuilder result = new StringBuilder();
        ExecutorService es = Executors.newFixedThreadPool(NB_CPU);
        String[] occurrences = new String[list76.size()];
        System.out.println("----------------------- Occurrences -----------------------");
        es.execute(() -> {
            for(int j=0; j<list76.size(); j++){
                ArrayList<Integer> positions = getExistPosition(list76.get(j));
                for(int i=0; i<=9; i++){
                    Integer finalI = i;
                    if(i >= 1 && i<8){
                        result.append(convertToBinaryString(positions.stream().filter(p -> p.toString().contains(finalI.toString())).count(),4));
                    }else{
                        result.append(convertToBinaryString(positions.stream().filter(p -> p.toString().contains(finalI.toString())).count(),3));
                    }
                }
                occurrences[j] = result.toString();
                System.out.println(j+" => "+occurrences[j]);
            }
        });
        es.shutdown();
        return occurrences;
    }

    @Override
    public String[] computeIF(List<String> list76) {
        ExecutorService es = Executors.newFixedThreadPool(NB_CPU);
        String[] IFs = new String[list76.size()];
        es.execute(() -> {
            for(int j=0; j<list76.size(); j++){
                ArrayList<Integer> positions = getPositions(list76.get(j));
                double IF = 0;
                if(positions.size()%2==0){
                    for(int i=positions.size()-1; i>=0; i=i-2){
                        IF = IF + getComputeValue(positions.get(i))/getComputeValue(positions.get(i-1));
                    }
                }else{
                    for(int i=positions.size()-1; i>=0; i=i-2){
                        if(i != 0){
                            IF = IF + getComputeValue(positions.get(i))/getComputeValue(positions.get(i-1));
                        }else{
                            IF = IF + getComputeValue(positions.get(i));
                        }
                    }
                }
                IFs[j] = getIFValue(IF);
            }
        });
        return IFs;
    }

    @Override
    public String[] get74(String[] unique, String[] doublon, String[] occurrence, String[] IF) {
        ExecutorService es = Executors.newFixedThreadPool(NB_CPU);
        String[] list74 = new String[occurrence.length];
        es.execute(() -> {
            for(int i=0; i<occurrence.length; i++){
                list74[i] = unique[i]+doublon[i]+occurrence[i]+IF[i];
            }
        });
        return list74;
    }

    //conversion d'un entier en x bits
    private String convertToBinaryString(long value, int x) {
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
        //System.out.print(b+" = "+binaryData);
        //System.out.print(binaryData.toString()+" ");
        return binaryData.toString();
    }

    private ArrayList<Integer> getExistPosition(String string76) {
        ArrayList<Integer> positions = new ArrayList<>();
        if(string76.charAt(0) == '1')
            positions.add(0);
        for(int i=7; i<string76.length(); i++){
            if((i!=11 && i!=22 && i!=33 && i!=44 && i!=55 && i!=66) && string76.charAt(i) == '1'){
                if(i<70){
                    positions.add(i);
                }else{
                    positions.add(i+1);
                }
            }
        }
        //System.out.println("positions IF => "+positions.toString());
        return positions;
    }

    private String getIFValue(double IF) {
        int newIF;
        //System.out.println("IF => "+IF);
        String tempIF = Double.toString(IF).split("\\.")[1];
        if(tempIF.length() < 8){
            int len = 8 - tempIF.length();
            for(int i=0; i<len; i++)
                tempIF = tempIF+"0";
        }
        if(Long.parseLong(tempIF) == 0){
            newIF = 0;
        }else if(tempIF.startsWith("0") || Integer.parseInt(tempIF.substring(0, 8)) > ref){
            tempIF = tempIF.substring(0, 7);
            newIF = Integer.parseInt(tempIF);
        }else{
            tempIF = tempIF.substring(0, 8);
            newIF = Integer.parseInt(tempIF);
        }
        //System.out.println("IF final => "+newIF);
        return convertToBinaryString(newIF, 25);
    }

    //Valeur de calcul d'une position donnée
    private double getComputeValue(int position){
        double res = (double)position+1+((double)position+12)/100;
        //System.out.print(" "+position+" = "+res+" -- ");
        return res;
    }

    public ArrayList<Integer> getPositions(String string76) {
        ArrayList<Integer> positions = new ArrayList<>();
        for(int i=0; i<string76.length(); i++)
            if(string76.charAt(i) == '1')
                if(i<70)
                    positions.add(i);
                else
                    positions.add(i+1);
        return positions;
    }

    @Override
    public void binaryStringToFile(String binaryString, String path, StandardOpenOption option) {
        byte[] bytes = binaryString.getBytes();
        try {
            Files.write(Paths.get(path), bytes, option);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
