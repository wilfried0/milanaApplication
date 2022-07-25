package com.milana.compression.services;

import java.util.ArrayList;
import java.util.List;

public class IsolateOccurrenceThread implements Runnable{
    private List<String> listBinaryString76;
    private static String[] sortie;
    private int start;
    private int end;

    public IsolateOccurrenceThread(List<String> listBinaryString76, int start, int end, String[] s) {
        this.listBinaryString76 = listBinaryString76;
        this.start = start;
        this.end = end;
        sortie = s;
    }

    @Override
    public synchronized void run() {
        for(int i=start; i<=end; i++){
            setOccurrenceAtPosition(computeOccurrences(this.listBinaryString76.get(i)), i);
        }
    }

    private void setOccurrenceAtPosition(String occurrenceString76, int position) {
        sortie[position] = occurrenceString76;
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
}
