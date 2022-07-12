package com.milana.threads;

import java.util.concurrent.atomic.AtomicInteger;

public class IsolateList74Thread implements Runnable{
    public static AtomicInteger count = new AtomicInteger(0);
    private String[] uniqueString76;
    private String[] duplicateString76;
    private String[] occurrenceString76;
    private String[] iFString76;
    private static String[] sortie;
    private int start;
    private int end;

    public IsolateList74Thread(String[] uniqueString76, String[] duplicateString76, String[] occurrenceString76, String[] iFString76, int start, int end, String[] s) {
        this.uniqueString76 = uniqueString76;
        this.duplicateString76 = duplicateString76;
        this.occurrenceString76 = occurrenceString76;
        this.iFString76 = iFString76;
        this.start = start;
        this.end = end;
        sortie = s;
    }

    @Override
    public synchronized void run() {
        for(int i=start; i<=end; i++){
            setString74AtPosition(uniqueString76[i],duplicateString76[i],occurrenceString76[i],iFString76[i],i);
            count.getAndIncrement();
            System.out.println(i+" unique -> "+uniqueString76[i]);
            System.out.println(i+" duplicate -> "+duplicateString76[i]);
            System.out.println(i+" occurrence -> "+occurrenceString76[i]);
            System.out.println(i+" IF -> "+iFString76[i]);
        }
    }

    private void setString74AtPosition(String unique, String duplicate, String occurrence, String IF, int position) {
        StringBuilder result = new StringBuilder();
        result.append(unique+duplicate+occurrence+IF);
        sortie[position] = result.toString();
    }
}
