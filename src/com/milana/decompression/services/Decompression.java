package com.milana.decompression.services;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Decompression implements DecompressionAction {
    public static int nbDescente, size, nbZero, length;
    private static final int ref = 33554431;

    @Override
    public void readNbreDescenteAndBlock(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String str;
            str = br.readLine();
            nbDescente = Integer.parseInt(str.split("\\.")[0]);
            size = Integer.parseInt(str.split("\\.")[1]);
            nbZero = Integer.parseInt(str.split("\\.")[2]);
            length = ("0"+nbDescente+"."+size+"."+nbZero+".").length();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(nbDescente);
        System.out.println(size);
        System.out.println(nbZero);
        System.out.println(length);
        System.out.println("\n");
    }

    @Override
    public Integer[] getOccurrences(String stringOccurrence) {
        Integer[] occurrenceValues = new Integer[10];
        int p = 0;
        for(int i=0; i<=9; i++){
            if(i >= 1 && i<8){
                occurrenceValues[i] = Integer.parseInt(stringOccurrence.substring(p, p+4), 2);
                p = p+4;
            }else{
                occurrenceValues[i] = Integer.parseInt(stringOccurrence.substring(p,p+3), 2);
                p = p+3;
            }
        }
        return occurrenceValues;
    }

    @Override
    public String[] getBinaryFromOccurrence(Integer[] occurrenceValues, int start, String[] string76) {
        System.out.println("Start process "+ Arrays.stream(string76).reduce("", (a, b)->a+b));
        for(int i=start; i>=0; i--){
            if((i!=11 && i!=22 && i!=33 && i!=44 && i!=55 && i!=66 && i!=1 && i!=2 && i!=3 && i!=4 && i!=5 && i!=6)){
                StringBuilder keepNumber = new StringBuilder();
                if(i<70 && i>9){
                    String finalI = i+"";
                    if(occurrenceValues[Integer.parseInt(finalI.charAt(0)+"")] > 0){
                        for(int p = Integer.parseInt(finalI.charAt(1)+""); p>=0; p--){
                            if(p!=Integer.parseInt(finalI.charAt(0)+"")){
                                keepNumber.append(finalI.charAt(0)); keepNumber.append(p);
                                if(occurrenceValues[p] > 0){
                                    string76[Integer.parseInt(keepNumber.toString())] = "1";
                                    System.out.println("apos="+keepNumber+"; oc="+occurrenceValues[Integer.parseInt(finalI.charAt(0)+"")]+" oc="+occurrenceValues[p]+"; text="+Arrays.stream(string76).reduce("", (a, b)->a+b));
                                    i = Integer.parseInt(keepNumber.toString());
                                    occurrenceValues[Integer.parseInt(finalI.charAt(0)+"")]--;
                                    occurrenceValues[p]--;
                                    p = 0;
                                }else {
                                    string76[Integer.parseInt(keepNumber.toString())] = "0";
                                    System.out.println("bpos="+keepNumber+"; oc="+occurrenceValues[Integer.parseInt(finalI.charAt(0)+"")]+" oc="+occurrenceValues[p]+"; text="+Arrays.stream(string76).reduce("", (a, b)->a+b));
                                    i = Integer.parseInt(keepNumber.toString());
                                }
                            }
                            keepNumber.setLength(0);
                        }
                    }else{
                        int ii = (Integer.parseInt(finalI.charAt(0) + "")) * 10;
                        StringBuilder sb = new StringBuilder(); sb.append(finalI.charAt(0)); sb.append(finalI.charAt(0));
                        for(int o = i; o>= ii; o--){
                            if(o!=Integer.parseInt(sb.toString())){
                                string76[o] = "0";
                                System.out.println("cpos="+o+"; oc="+occurrenceValues[Integer.parseInt(finalI.charAt(0)+"")]+" oc="+occurrenceValues[Integer.parseInt(String.valueOf(o).charAt(1)+"")]+"; text="+Arrays.stream(string76).reduce("", (a, b)->a+b));
                            }
                        }
                        i = ii;
                    }
                }else if(i>=70){
                    String finalI = i+"";
                    if(occurrenceValues[Integer.parseInt(finalI.charAt(0)+"")] > 0){
                        occurrenceValues[Integer.parseInt(finalI.charAt(0)+"")]--;
                        for(int p=Integer.parseInt(finalI.charAt(1)+""); p>=1; p--){
                            keepNumber.append(finalI.charAt(0)); keepNumber.append(p);
                            if(occurrenceValues[p] > 0){
                                string76[Integer.parseInt(keepNumber.toString()) - 1] = "1";
                                System.out.println("dpos="+keepNumber+"; oc="+occurrenceValues[Integer.parseInt(finalI.charAt(0)+"")]+" oc="+occurrenceValues[p]+"; text="+Arrays.stream(string76).reduce("", (a, b)->a+b));
                                i = Integer.parseInt(keepNumber.toString())-1;
                                keepNumber.setLength(0);
                                occurrenceValues[Integer.parseInt(finalI.charAt(0)+"")]--;
                                occurrenceValues[p]--;
                                p = 1;
                            }else {
                                string76[Integer.parseInt(keepNumber.toString()) - 1] = "0";
                                System.out.println("epos="+keepNumber+"; oc="+occurrenceValues[Integer.parseInt(finalI.charAt(0)+"")]+"; text="+Arrays.stream(string76).reduce("", (a, b)->a+b));
                                i = Integer.parseInt(keepNumber.toString()) - 1;
                                keepNumber.setLength(0);
                            }
                        }
                    }else{
                        int ii = (Integer.parseInt(finalI.charAt(0) + "")) * 10;
                        for(int o = i; o> ii; o--){
                            string76[o-1] = "0";
                            System.out.println("fpos="+o+"; oc="+occurrenceValues[Integer.parseInt(finalI.charAt(0)+"")]+" oc="+occurrenceValues[Integer.parseInt(String.valueOf(o).charAt(1)+"")]+"; text="+Arrays.stream(string76).reduce("", (a, b)->a+b));
                        }
                        i = ii;
                    }
                }else{
                    if(occurrenceValues[i] > 0){
                        occurrenceValues[i]--;
                        string76[i] = "1";
                    }else{
                        string76[i] = "0";
                    }
                    System.out.println("gpos="+i+"; oc="+occurrenceValues[i]+"; text="+Arrays.stream(string76).reduce("", (a, b)->a+b));
                }
            }
        }
        System.out.println("End process "+ Arrays.stream(string76).reduce("", (a, b)->a+b));
        return string76;
    }

    @Override
    public String refillOccurrencyFromSup17(String[] string76) {
        String newString76 = Arrays.stream(string76).reduce("", (a,b)->a+b);
        ArrayList<Integer> positions = getExistPosition(newString76);
        Collections.reverse(positions);
        int sup17 = positions.stream().filter(p -> (17-p)<=0).findAny().orElse(null);
        System.out.println("\nSup => "+sup17);
        computeOccurrences(newString76.substring(0, positions.indexOf(sup17)));
        return computeOccurrences(newString76.substring(0, positions.indexOf(sup17)));
    }

    @Override
    public String computeIF(String string76) {
        ArrayList<Integer> positions = getPositions(string76);
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
        return getIFValue(IF);
    }

    private String computeOccurrences(String string76){
        ArrayList<Integer> positions = getExistPosition(string76);
        StringBuilder result = new StringBuilder();
        for(int i=0; i<=9; i++){
            int finalI = i;
            if(i >= 1 && i<8){
                result.append(convertToBinaryString(positions.stream().filter(p -> p.toString().contains(Integer.toString(finalI))).count(),4));
            }else{
                result.append(convertToBinaryString(positions.stream().filter(p -> p.toString().contains(Integer.toString(finalI))).count(),3));
            }
        }
        return result.toString();
    }

    private ArrayList<Integer> getExistPosition(String string76)   {
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
        return positions;
    }

    private String convertToBinaryString(long value, int x) {
        StringBuilder result = new StringBuilder();
        for (int i = x-1; i >= 0; i--) {
            int mask = 1 << i;
            result.append((value & mask) != 0 ? "1" : "0");
        }
        return result.toString();
    }

    private ArrayList<Integer> getPositions(String string76) {
        ArrayList<Integer> positions = new ArrayList<>();
        for(int i=0; i<string76.length(); i++)
            if(string76.charAt(i) == '1')
                if(i<70)
                    positions.add(i);
                else
                    positions.add(i+1);
        return positions;
    }

    private double getComputeValue(int position){
        return (double)position+1+((double)position+12)/100;
    }

    private String getIFValue(double IF) {
        int newIF;
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
        return convertToBinaryString(newIF, 25);
    }
}
