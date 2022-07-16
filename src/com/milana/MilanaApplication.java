package com.milana;

import static java.nio.file.StandardOpenOption.APPEND;

import com.milana.compression.services.*;
import com.milana.decompression.services.Decompression;
import com.milana.decompression.services.MappedBiggerFileReaderChar;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MilanaApplication {
    private static int NB_CPU = Runtime.getRuntime().availableProcessors();
    private static final int seuil = 30;
    private static final int offset = 100000;//65536;
    private static ArrayList<String> resteBits = new ArrayList<>();
    private static int nbDescente = 0;
    private static int size = 0;

    static String path ="C:\\Users\\ASSAM\\Documents\\";//"/Users/sprintpay/Downloads/";//"C:\\Users\\ASSAM\\Videos\\Films\\Movies\\The.Equalizer.2014.Et.II.2018.TRUEFRENCH.DVDRip.XviD.AC3-Tetine\\Equalizer 2014\\";//
    static String filePath = "test.txt.lana";// "Stade PAUL Biya au Cameroun.mp4";//+"miqo.PNG";// "C:\\Users\\ASSAM\\Documents\\test.txt";//"C:\\Users\\ASSAM\\Videos\\Films\\Movies\\The.Equalizer.2014.Et.II.2018.TRUEFRENCH.DVDRip.XviD.AC3-Tetine\\Equalizer 2014\\Equalizer.avi";//"C:\\Users\\ASSAM\\Documents\\test.txt"; //"/Users/sprintpay/Documents/test.txt";
    public static void main(String[] args) throws IOException {
        Decompression decompression = new Decompression();
        decompression.readNbreDescenteAndBlock(path+filePath);
        MappedBiggerFileReaderChar reader = new MappedBiggerFileReaderChar(path+filePath, offset, Decompression.length+1);
        int j=0;
        String text="";
        while(reader.read() != -1){
            for(int i=0; i<reader.getArray().length; i++){
                text = text + Integer.toBinaryString((reader.getArray()[i] & 0xFF) + 256).substring(1);
            }
            if(j == 0){
                text = text.substring(Decompression.nbZero);
            }
            j++;
        }



        /*long startTime = System.currentTimeMillis();
        StringBuilder text = new StringBuilder();
        MappedBiggerFileReader reader = new MappedBiggerFileReader(path+filePath, offset);
        int i = 0;
        while(reader.read() != -1){
            i++;
            System.out.println("Traitement bloc "+i+" de "+offset+" octets sur "+reader.getFileLength()/offset+" blocs");
            text.append(milanisation(reader.getArray()));
        }
        reader.close();
        String finalText = nbDescente+"."+size+"."+text+resteBits.stream().reduce("", (a,b)->a+b);
        String savePath = path+filePath+".lana";
        File f = new File(savePath);
        System.out.println(finalText);
        if(f.exists() && !f.isDirectory()) {
            binaryStringToFile(finalText, savePath, APPEND);
        }else{
            binaryStringToFile(finalText, savePath, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Temps mis " + (endTime - startTime)/1000 + " secondes");*/
    }

    private static String milanisation(byte[] bytes){
        Thread[] binaryThreads = new Thread[NB_CPU];
        Thread[] uniqueThreads = new Thread[NB_CPU];
        Thread[] duplicateThreads = new Thread[NB_CPU];
        Thread[] occurrenceThreads = new Thread[NB_CPU];
        Thread[] IFThreads = new Thread[NB_CPU];
        Thread[] list74Threads = new Thread[NB_CPU];
        String[] binaryString = new String[bytes.length];

        int niveauCompression = 0;
        int GAP = bytes.length/NB_CPU;
        int start = 0, end = GAP+(bytes.length%NB_CPU)-1;

        // Conversion du tableau de bytes en bits (sous forme de chaine de carractères ex: 00110100)
        for(int i=0; i<NB_CPU; i++){
            ByteArrayToBinaryStringThread bTbst = new ByteArrayToBinaryStringThread(bytes, start, end, binaryString);
            binaryThreads[i] = new Thread(bTbst);
            start = end + 1;
            end += GAP;
        }
        multiThreadProcess(binaryThreads);
        multiThreadProcessJoin(binaryThreads);

        String text = ByteArrayToBinaryStringThread.getBinaryString();

        while(niveauCompression < seuil && text.length() > 74){
            List<String> list76 = binaryStringToList76(text);
            GAP = list76.size()/NB_CPU;
            if(list76.size() < NB_CPU){
                GAP = 1;
                NB_CPU = list76.size();
            }
            String[] uniques = new String[list76.size()];
            String[] duplicates = new String[list76.size()];
            String[] occurrences = new String[list76.size()];
            String[] IFs = new String[list76.size()];
            String[] list74 = new String[list76.size()];

            start = 0; end = GAP+(list76.size()%NB_CPU)-1;
            for(int i=0; i<NB_CPU; i++){
                IsolateOccurrenceThread iOt = new IsolateOccurrenceThread(list76, start, end, occurrences);
                occurrenceThreads[i] = new Thread(iOt);
                start = end + 1;
                end += GAP;
            }
            multiThreadProcess(occurrenceThreads);

            start = 0; end = GAP+(list76.size()%NB_CPU)-1;
            for(int i=0; i<NB_CPU; i++){
                IsolateUniqueThread iUt = new IsolateUniqueThread(list76, start, end, uniques);
                uniqueThreads[i] = new Thread(iUt);
                start = end + 1;
                end += GAP;
            }
            multiThreadProcess(uniqueThreads);

            start = 0; end = GAP+(list76.size()%NB_CPU)-1;
            for(int i=0; i<NB_CPU; i++){
                IsolateDuplicateThread iDt = new IsolateDuplicateThread(list76, start, end, duplicates);
                duplicateThreads[i] = new Thread(iDt);
                start = end + 1;
                end += GAP;
            }
            multiThreadProcess(duplicateThreads);

            start = 0; end = GAP+(list76.size()%NB_CPU)-1;
            for(int i=0; i<NB_CPU; i++){
                IsolateIFThread iFt = new IsolateIFThread(list76, start, end, IFs);
                IFThreads[i] = new Thread(iFt);
                start = end + 1;
                end += GAP;
            }
            multiThreadProcess(IFThreads);

            multiThreadProcessJoin(occurrenceThreads);
            multiThreadProcessJoin(duplicateThreads);
            multiThreadProcessJoin(uniqueThreads);
            multiThreadProcessJoin(IFThreads);

            start = 0; end = GAP+(list76.size()%NB_CPU)-1;
            for(int i=0; i<NB_CPU; i++){
                IsolateList74Thread i74t = new IsolateList74Thread(uniques, duplicates, occurrences, IFs, start, end, list74);
                list74Threads[i] = new Thread(i74t);
                start = end + 1;
                end += GAP;
            }
            multiThreadProcess(list74Threads);
            multiThreadProcessJoin(list74Threads);

            text = Arrays.stream(list74).collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString();
            System.out.println("Descente N° "+niveauCompression);
            niveauCompression++;
            nbDescente++;
        }
        size = text.length();
        System.out.println("Size "+size);
        return text;
    }

    private static void multiThreadProcess(Thread[] threads){
        for(int i=0; i<NB_CPU; i++){
            threads[i].start();
        }
    }

    public static void multiThreadProcessJoin(Thread[] threads)  {
        for(int i=0; i<NB_CPU; i++){
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
                Thread.currentThread().interrupt();
            }
        }
    }

    private static void binaryStringToFile(String binaryString, String path, StandardOpenOption... option) {
        byte[] bytes;
        StringBuilder tp = new StringBuilder();
        if(binaryString.substring(5).length()%8 == 0){
            bytes = new BigInteger(binaryString.substring(5), 2).toByteArray();
            System.out.println("=> "+binaryString.substring(5));
        }else{
            int r = binaryString.substring(5).length()%8;
            for(int i=0; i<8-r; i++){
                tp.append('1');
            }
            bytes = new BigInteger(tp.toString()+binaryString.substring(5), 2).toByteArray();
            System.out.println("=> "+tp.toString()+binaryString.substring(5));
        }
        try {
            String tmp = "0"+binaryString.substring(0,5)+tp.length()+".";
            Files.write(Paths.get(path), tmp.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            Files.write(Paths.get(path), bytes, APPEND);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String convertToBinaryString(long value, int x) {
        StringBuilder result = new StringBuilder();
        for (int i = x-1; i >= 0; i--) {
            int mask = 1 << i;
            result.append((value & mask) != 0 ? "1" : "0");
        }
        return result.toString();
    }

    private static String completeWithZero(String str){
        StringBuilder strBuilder = new StringBuilder(str);
        for(int i = 0; i<76; i++){
            strBuilder.append('0');
        }
        str = strBuilder.toString();
        return str;
    }

    private static String completeTo76Bits(String str){
        int add = 76 - str.length();
        if(str.charAt(str.length()-1) == '1'){
            StringBuilder strBuilder = new StringBuilder(str);
            for(int i = 0; i<add; i++){
                strBuilder.append('0');
            }
            str = strBuilder.toString();
        }else{
            StringBuilder strBuilder = new StringBuilder(str);
            for(int i = 0; i<add; i++){
                strBuilder.append(1);
            }
            str = strBuilder.toString();
        }
        return str;
    }

    private static List<String> binaryStringToList76(String binaryString) {
        ArrayList<String> list76 = new ArrayList<>();
        StringBuilder tmp = new StringBuilder();
        for(int i=0; i<binaryString.length(); i++){
            if(tmp.length() < 76){
                tmp.append(binaryString.charAt(i));
            }else{
                list76.add(tmp.toString());
                tmp = new StringBuilder("" + binaryString.charAt(i));
            }
        }
        if(!tmp.toString().equals("")){
            resteBits.add(completeTo76Bits(tmp.toString()));
        }else{
            resteBits.add(completeWithZero(tmp.toString()));
        }
        return list76;
    }
}