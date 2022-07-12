package com.milana.threads;

import com.milana.compression.services.BinaryService;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class ByteArrayToBinaryStringThread implements Runnable {

    public static AtomicInteger count = new AtomicInteger(0);
    private byte[] bytes;
    private static String[] sortie;
    private int start;
    private int end;
    private BinaryService binaryService;


    public ByteArrayToBinaryStringThread(byte[] bytes, int start, int end, String[] s, BinaryService binaryService) {
        this.bytes = bytes;
        this.start = start;
        this.end = end;
        sortie = s;
        this.binaryService = binaryService;
    }

    @Override
    public synchronized void run() {
        while (!Thread.currentThread().isInterrupted()){
            for(int i=start; i<=end; i++){
                setBinaryStringAtPosition(byteToBinaryString(this.bytes[i]), i);
                try {
                    binaryService.put(getBinaryString());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
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
        return binaryData.toString();
    }

    private void setBinaryStringAtPosition(String binaryString, int position){
        sortie[position] = binaryString;
        //System.out.println("("+position+") "+" =>"+binaryString);
    }

    public static String getBinaryString(){
        return Arrays.asList(sortie).stream().reduce("", (a,b)->a+b);
    }
}
