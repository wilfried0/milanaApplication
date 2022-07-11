package com.milana.threads;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class IsolateDuplicateThread implements Runnable {
    private List<String> listBinaryString76;
    private static String[] sortie;
    private int start;
    private int end;

    public IsolateDuplicateThread(List<String> listBinaryString76, int start, int end, String[] s) {
        this.listBinaryString76 = listBinaryString76;
        this.start = start;
        this.end = end;
        sortie = s;
    }

    @Override
    public synchronized void run() {
        for(int i=start; i<=end; i++){
            setDuplicateAtPosition(this.listBinaryString76.get(i), i);
            System.out.println(i+" -> "+sortie[i]);
        }
    }

    private void setDuplicateAtPosition(String uniqueString76, int position) {
        sortie[position] = ""+uniqueString76.charAt(11)+uniqueString76.charAt(22)+uniqueString76.charAt(33)+uniqueString76.charAt(44)+uniqueString76.charAt(55)+uniqueString76.charAt(66);
    }

    public static String getDuplicateString(){
        return Arrays.stream(sortie).reduce("", (a,b)->a+b);
    }
}
