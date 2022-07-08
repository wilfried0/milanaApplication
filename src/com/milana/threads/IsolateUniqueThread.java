package com.milana.threads;

import java.util.List;

public class IsolateUniqueThread implements Runnable {

    private List<String> listBinaryString76;
    private List<String> sortie;
    private int start;
    private int end;

    public IsolateUniqueThread(List<String> listBinaryString76, int start, int end, List<String> s) {
        this.listBinaryString76 = listBinaryString76;
        this.start = start;
        this.end = end;
        sortie = s;
    }

    @Override
    public void run() {
        for(int i=start; i<=end; i++){
            setUniqueBinaryStringAtPosition(listBinaryString76.get(i),i);
        }
    }

    private void setUniqueBinaryStringAtPosition(String binaryString76, int position){
        sortie.add(position, binaryString76);
        System.out.println("("+position+") "+" =>"+binaryString76);
    }
}
