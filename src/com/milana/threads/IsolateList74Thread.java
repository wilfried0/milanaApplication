package com.milana.threads;

import java.util.List;

public class IsolateList74Thread implements Runnable{
    private List<String> uniqueString76;
    private List<String> duplicateString76;
    private List<String> occurrenceString76;
    private List<String> iFString76;
    private static List<String> sortie;
    private int start;
    private int end;

    public IsolateList74Thread(List<String> uniqueString76, List<String> duplicateString76, List<String> occurrenceString76, List<String> iFString76, int start, int end, List<String> s) {
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
            setString74AtPosition(uniqueString76.get(i),duplicateString76.get(i),occurrenceString76.get(i),iFString76.get(i),i);
        }
    }

    private void setString74AtPosition(String unique, String duplicate, String occurrence, String IF, int position) {
        StringBuilder result = new StringBuilder();
        result.append(unique+duplicate+occurrence+IF);
        sortie.add(position, result.toString());
    }

    public static String getString74(){
        return sortie.stream().reduce("", (a,b)->a+b);
    }
}
