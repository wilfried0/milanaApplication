package com.milana.compression.services;

import java.util.Arrays;

public class ByteArrayToBinaryStringThread implements Runnable {

    private byte[] bytes;
    private static String[] sortie;
    private int start;
    private int end;


    public ByteArrayToBinaryStringThread(byte[] bytes, int start, int end, String[] s) {
        this.bytes = bytes;
        this.start = start;
        this.end = end;
        sortie = s;
    }

    @Override
    public synchronized void run() {
        for(int i=start; i<=end; i++){
            setBinaryStringAtPosition(byteToBinaryString(this.bytes[i]), i);
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
    }

    public static String getBinaryString(){
        return Arrays.asList(sortie).stream().reduce("", (a,b)->a+b);
    }
}
