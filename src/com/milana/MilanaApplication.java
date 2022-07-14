package com.milana;

import com.milana.compression.services.Compression;
import com.milana.threads.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

public class MilanaApplication {
    private static int NB_CPU = Runtime.getRuntime().availableProcessors();
    public static final int seuil = 30;
    public static final int offset = 5*1024*1024;

    static String path = "C:\\Users\\ASSAM\\Videos\\Films\\Movies\\The.Equalizer.2014.Et.II.2018.TRUEFRENCH.DVDRip.XviD.AC3-Tetine\\Equalizer 2014\\";//"/Users/sprintpay/Documents/";//
    static String filePath = "Equalizer.avi";// "Stade PAUL Biya au Cameroun.mp4";//+"miqo.PNG";// "C:\\Users\\ASSAM\\Documents\\test.txt";//"C:\\Users\\ASSAM\\Videos\\Films\\Movies\\The.Equalizer.2014.Et.II.2018.TRUEFRENCH.DVDRip.XviD.AC3-Tetine\\Equalizer 2014\\Equalizer.avi";//"C:\\Users\\ASSAM\\Documents\\test.txt"; //"/Users/sprintpay/Documents/test.txt";
    public static void main(String[] args) {
        long fileLen = new File(path+filePath).length();
        StringBuilder text = new StringBuilder();
        InputStream in = null;
        try {
            in = new FileInputStream(path+filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if(fileLen < offset){
            byte[] bytes = {};
            bytes = readByteBlock(in, 0, (int) fileLen-1);
            text.append(milanisation(bytes));
        }else{
            for(long i=0; i<fileLen; i++){
                byte[] bytes = {};
                if((fileLen - i) < offset){
                    bytes = readByteBlock(in, (int) i, (int) ((int) fileLen-i));
                }else
                bytes = readByteBlock(in, 0, 5*1024*1024);
                text.append(milanisation(bytes));
            }
        }
        String finalText = text+Compression.resteBits.stream().reduce("", (a,b)->a+b);
        String savePath = path+filePath.split("\\.")[0]+".lana";
        binaryStringToFile(finalText, savePath, StandardOpenOption.APPEND);
    }

    private static String milanisation(byte[] bytes){
        Compression compression = new Compression();
        Thread[] binaryThreads = new Thread[NB_CPU];
        Thread[] uniqueThreads = new Thread[NB_CPU];
        Thread[] duplicateThreads = new Thread[NB_CPU];
        Thread[] occurrenceThreads = new Thread[NB_CPU];
        Thread[] IFThreads = new Thread[NB_CPU];
        Thread[] list74Threads = new Thread[NB_CPU];
        String[] binaryString = new String[bytes.length];

        int niveauCompression = 1;
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
        System.out.println("Text initial => "+text+"-"+text.length());

        while(niveauCompression < seuil && text.length() > 74){
            List<String> list76 = compression.binaryStringToList76(text);
            System.out.println("Text découpé => "+list76.toString());
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
            System.out.println("-------------------  Occurrences  --------------------------");
            for(int i=0; i<NB_CPU; i++){
                IsolateOccurrenceThread iOt = new IsolateOccurrenceThread(list76, start, end, occurrences);
                occurrenceThreads[i] = new Thread(iOt);
                start = end + 1;
                end += GAP;
            }
            multiThreadProcess(occurrenceThreads);

            start = 0; end = GAP+(list76.size()%NB_CPU)-1;
            System.out.println("-------------------  Uniques  --------------------------");
            for(int i=0; i<NB_CPU; i++){
                IsolateUniqueThread iUt = new IsolateUniqueThread(list76, start, end, uniques);
                uniqueThreads[i] = new Thread(iUt);
                start = end + 1;
                end += GAP;
            }
            multiThreadProcess(uniqueThreads);

            start = 0; end = GAP+(list76.size()%NB_CPU)-1;
            System.out.println("-------------------  Duplicates  --------------------------");
            for(int i=0; i<NB_CPU; i++){
                IsolateDuplicateThread iDt = new IsolateDuplicateThread(list76, start, end, duplicates);
                duplicateThreads[i] = new Thread(iDt);
                start = end + 1;
                end += GAP;
            }
            multiThreadProcess(duplicateThreads);

            start = 0; end = GAP+(list76.size()%NB_CPU)-1;
            System.out.println("-------------------  IF  --------------------------");
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
            System.out.println("-------------------  list74  --------------------------");
            for(int i=0; i<NB_CPU; i++){
                IsolateList74Thread i74t = new IsolateList74Thread(uniques, duplicates, occurrences, IFs, start, end, list74);
                list74Threads[i] = new Thread(i74t);
                start = end + 1;
                end += GAP;
            }
            multiThreadProcess(list74Threads);
            multiThreadProcessJoin(list74Threads);

            niveauCompression++;
            text = Arrays.stream(list74).collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString();
            System.out.println("new text: "+text+"-"+text.length());
        }
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
                Thread.currentThread().interrupt();
            }
        }
    }

    private static byte[] readByteBlock(InputStream in, int start, int size){
        byte[] result = new byte[size];
        try {
            in.read(result, start, size);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static void binaryStringToFile(String binaryString, String path, StandardOpenOption option) {
        byte[] bytes = binaryString.getBytes();
        try {
            Files.write(Paths.get(path), bytes, option);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*public static void milanisation(String filePath){

        Compression compression = new Compression();
        int niveauCompression = 1;
        long start = System.currentTimeMillis();

        if(new File(filePath).length() <= Compression.MAX_VALUE){
            //Conversion du fichier en tableau de bytes
            byte[] bytes = compression.fileToByteArray(filePath);

            //conversion du tableau de bytes en binaire sous forme de texte
            String binaryString = compression.byteArrayToBinaryString(bytes);

            //System.out.println("initial taille: "+binaryString.length());
            while(niveauCompression < Compression.seuil && binaryString.length() > 74)
            {
                System.out.println("Descente N° "+niveauCompression);
                List<String> list76 = compression.binaryStringToList76(binaryString);
                String[] uniques = new String[list76.size()];
                String[] duplicates = new String[list76.size()];
                String[] list74 = new String[list76.size()];
                String[] IFs = new String[list76.size()];
                String[] occurrences = new String[list76.size()];

                uniques = compression.isolateUniques(list76);
                duplicates = compression.isolateDuplicatePositions(list76);
                IFs = compression.computeIF(list76);
                occurrences = compression.computeOccurrences(list76);

                list74 = compression.get74(uniques, duplicates, occurrences, IFs);

                niveauCompression++;
                binaryString = Arrays.stream(list74).reduce("", (a,b)->a+b);
                System.out.println("new taille: "+binaryString);
            }
            String finalText = binaryString+Compression.resteBits.stream().collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString();
            String savePath = path+"test.lana";
            compression.binaryStringToFile(finalText, savePath, StandardOpenOption.CREATE);
        }
    }*/
}