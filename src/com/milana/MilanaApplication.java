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
import java.util.Collections;
import java.util.List;

public class MilanaApplication {
    private static int NB_CPU = Runtime.getRuntime().availableProcessors();
    private static final int seuil = 30;
    private static final int offset = 100000;//65536;
    private static ArrayList<String> resteBits = new ArrayList<>();
    private static int nbDescente = 0;
    private static int size = 0;

    static String path = "/Users/sprintpay/Documents/";//"C:\\Users\\ASSAM\\Documents\\";//"C:\\Users\\ASSAM\\Videos\\Films\\Movies\\The.Equalizer.2014.Et.II.2018.TRUEFRENCH.DVDRip.XviD.AC3-Tetine\\Equalizer 2014\\";//
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

        String[] goodText = new String[Decompression.nbDescente];
        for(int k=0; k<Decompression.nbDescente; k++){
            String lastText = text.substring(0,Decompression.size);
            System.out.print("Latest: "+lastText+"-"+lastText.length()+"\n");
            text = text.substring(Decompression.size);
            String reste = getRestBinary(text.substring(0, 76));
            text = text.substring(76);
            String uniques = lastText.substring(0,6);
            String duplicates = ""+lastText.charAt(11)+lastText.charAt(22)+lastText.charAt(33)+lastText.charAt(44)+lastText.charAt(55)+lastText.charAt(66);
            String occurrences = lastText.substring(12, 49);
            System.out.print("occurrence bits: "+occurrences+"-"+occurrences.length()+"\n");
            Integer[] occurrenceValues = decompression.getOccurrences(occurrences);
            for(int i=0; i<occurrenceValues.length; i++){
                System.out.print(occurrenceValues[i]+" ");
            }
            System.out.print("\n");

            String[] string76 = new String[76];
            string76[1] = uniques.charAt(0)+"";
            string76[2] = uniques.charAt(1)+"";
            string76[3] = uniques.charAt(2)+"";
            string76[4] = uniques.charAt(3)+"";
            string76[5] = uniques.charAt(4)+"";
            string76[6] = uniques.charAt(5)+"";
            string76[11] = duplicates.charAt(0)+"";
            string76[22] = duplicates.charAt(1)+"";
            string76[33] = duplicates.charAt(2)+"";
            string76[44] = duplicates.charAt(3)+"";
            string76[55] = duplicates.charAt(4)+"";
            string76[66] = duplicates.charAt(5)+"";

            int first = 0;
            Integer po = null;
            boolean check = true;
            while (check){
                decompression.getBinaryFromOccurrence(occurrenceValues, first==0?76:po, string76);
                Arrays.asList(string76).forEach(System.out::print);
                po = Arrays.asList(occurrenceValues).stream().filter(o -> o!=0).findAny().orElse(null);
                if(po == null){
                    //Calcul de l'IF
                    String IF74 = lastText.substring(49);
                    String computeIF = first == 0? decompression.computeIF(lastText): decompression.computeIF(Arrays.stream(string76).reduce("", (a,b)->a+b));
                    if(IF74.equals(computeIF)){
                        goodText[k] = Arrays.stream(string76).reduce("", (a,b)->a+b);
                        check = false;
                    }else{
                        String occurrences2 = decompression.refillOccurrencyFromSup17(string76);
                        Integer[] occurrenceValues2 = decompression.getOccurrences(occurrences2);
                        for(int o=0; o<occurrenceValues2.length; o++){
                            occurrenceValues[o] = occurrenceValues2[o]+occurrenceValues[o];
                        }
                    }
                }else{
                    String occurrences2 = decompression.refillOccurrencyFromSup17(string76);
                    Integer[] occurrenceValues2 = decompression.getOccurrences(occurrences2);
                    for(int o=0; o<occurrenceValues2.length; o++){
                        occurrenceValues[o] = occurrenceValues2[o]+occurrenceValues[o];
                    }
                }
                first++;
            }
        }
        System.out.println("Final text => \n"+Arrays.stream(goodText).reduce("", (a,b)->a+b));

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
        Collections.reverse(resteBits);
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
            System.out.println("Text "+text+"\n");
            niveauCompression++;
            nbDescente++;
        }
        size = text.length();
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

    private static String getRestBinary(String reste){
        int index = 0;
        for(int i=reste.length()-2; i>=0; i--){
            if(reste.charAt(i+1) != reste.charAt(i)){
                index = i;
            }
        }
        return reste.substring(0,index);
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