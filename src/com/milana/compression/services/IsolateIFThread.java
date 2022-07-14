package com.milana.compression.services;

import java.util.ArrayList;
import java.util.List;

public class IsolateIFThread implements Runnable {
    private static final int ref = 33554431;
    private List<String> listBinaryString76;
    private String[] sortie;
    private int start;
    private int end;

    public IsolateIFThread(List<String> listBinaryString76, int start, int end, String[] s) {
        this.listBinaryString76 = listBinaryString76;
        this.start = start;
        this.end = end;
        sortie = s;
    }

    @Override
    public synchronized void run() {
        for(int i=start; i<=end; i++){
            setIFBinaryStringAtPosition(computeIF(listBinaryString76.get(i)),i);
        }
    }

    private void setIFBinaryStringAtPosition(String binaryString76, int position){
        sortie[position] = binaryString76;
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

    private String convertToBinaryString(long value, int x) {
        StringBuilder result = new StringBuilder();
        for (int i = x-1; i >= 0; i--) {
            int mask = 1 << i;
            result.append((value & mask) != 0 ? "1" : "0");
        }
        return result.toString();
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

    private String computeIF(String string76) {
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
}
