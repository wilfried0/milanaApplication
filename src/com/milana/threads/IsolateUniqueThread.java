package com.milana.threads;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class IsolateUniqueThread implements Runnable {

    private List<String> listBinaryString76;
    private static String[] sortie;
    private int start;
    private int end;

    public IsolateUniqueThread(List<String> listBinaryString76, int start, int end, String[] s) {
        this.listBinaryString76 = listBinaryString76;
        this.start = start;
        this.end = end;
        sortie = s;
    }

    @Override
    public synchronized void run() {
        for(int i=start; i<=end; i++){
            setUniqueBinaryStringAtPosition(isolateUniques(listBinaryString76.get(i)),i);
            System.out.println(i+" -> "+sortie[i]);
        }
    }

    public String isolateUniques(String string76) {
        return string76.substring(1, 7);
    }

    private void setUniqueBinaryStringAtPosition(String binaryString76, int position){
        sortie[position] = binaryString76;
    }

    public static String getUniqueString(){
        return Arrays.stream(sortie).reduce("", (a, b)->a+b);
    }
}
