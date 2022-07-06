package com.milana.compression.services;

import com.milana.threads.ByteArrayToBinaryStringThread;

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
    long start = System.currentTimeMillis();

    private static byte[] bytes;

    private static final int NB_CPU = Runtime.getRuntime().availableProcessors();

    @Override
    public byte[] fileToByteArray(String filePath) {
        try {
            bytes = Files.readAllBytes(Paths.get(filePath));
        } catch (IOException ioe) {
            ioe.printStackTrace();
            //System.out.println("Une exception est survenue lors du traitement du fichier!");
        }
        return bytes;
    }

    @Override
    public String byteArrayToBinaryString(byte[] bytes) {
        ExecutorService es = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        String[] binaryString = new String[bytes.length];
        es.execute(() -> {
            for(int i=0; i<bytes.length; i++){
                binaryString[i] = byteToBinaryString(bytes[i])+" ";
                System.out.print("("+i+")"+" "+bytes[i]+" = "+binaryString[i]+"\n");
            }
        });
        es.shutdown();
        /*mBytes.parallelStream().forEach(b -> {
            binaryString[mBytes.indexOf(b)] = byteToBinaryString(b)+" ";
            System.out.print("("+mBytes.indexOf(b)+")"+" "+b+" = "+byteToBinaryString(b)+"\n");
        });*/
        String sortie = Arrays.asList(binaryString).stream().reduce("", (a,b)-> a+b);
        System.out.println(sortie);
        System.out.println("----"+(System.currentTimeMillis() - start));
        return sortie;
    }

    @Override
    public List<String> binaryStringToList76(String binaryString) {
        List<String> list76 = new ArrayList<>();
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
            tmp = tmp+convertToBinaryString(tmp.length(), 7);
        }else{
            tmp = convertToBinaryString(0, 7);
        }
        resteBits.add(tmp);
        //System.out.println("reste + taille" + resteBits.toString());
        return list76;
    }

    @Override
    public String isolateUniques(String string76) {
        //System.out.println("Uniques => "+string76.substring(1,7));
        return string76.substring(1, 7);
    }

    @Override
    public String isolateDuplicatePositions(String string76) {
        //System.out.println("Doublons => "+result.toString());
        return ""+string76.charAt(11)+string76.charAt(22)+string76.charAt(33)+string76.charAt(44)+string76.charAt(55)+string76.charAt(66);
    }

    @Override
    public String computeOccurrences(String string76){
        ArrayList<Integer> positions = getExistPosition(string76);
        StringBuilder result = new StringBuilder();
        for(int i=0; i<=9; i++){
            Integer finalI = i;
            if(i >= 1 && i<8){
                //System.out.print(""+i+" ("+positions.stream().filter(p -> p.toString().contains(finalI.toString())).count()+") = "+convertToBinaryString(positions.stream().filter(p -> p.toString().contains(finalI.toString())).count(),4)+", ");
                result.append(convertToBinaryString(positions.stream().filter(p -> p.toString().contains(finalI.toString())).count(),4));
            }else{
                //System.out.print(""+i+" ("+positions.stream().filter(p -> p.toString().contains(finalI.toString())).count()+") = "+convertToBinaryString(positions.stream().filter(p -> p.toString().contains(finalI.toString())).count(),3)+", ");
                result.append(convertToBinaryString(positions.stream().filter(p -> p.toString().contains(finalI.toString())).count(),3));
            }
        }
        //System.out.println("\nOccurrence bits => "+result.toString());
        return result.toString();
    }

    @Override
    public int computeIF(String string76) {
        ArrayList<Integer> positions = getPositions(string76);
        double IF = 0;
        //System.out.print("All positions => "+positions.toString()+"\n");
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
        return getIFValue(IF);
    }

    @Override
    public String get74(String unique, String doublon, String occurrence, int IF) {
        StringBuilder result = new StringBuilder();
        result.append(unique+doublon+occurrence+convertToBinaryString(IF, 25));
        //System.out.println("74 bits: "+result.toString()+"\nLength = "+result.length());
        return result.toString();
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

    private int getIFValue(double IF) {
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
        return newIF;
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
