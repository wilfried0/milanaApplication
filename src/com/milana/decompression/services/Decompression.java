package com.milana.decompression.services;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Decompression implements DecompressionAction {
    public static int nbDescente, size, nbZero, length;
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
}
