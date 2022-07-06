package com.milana;

import com.milana.compression.services.Compression;
import com.milana.threads.ByteArrayToBinaryStringThread;

import java.io.File;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MilanaApplication {
    private static final int NB_CPU = Runtime.getRuntime().availableProcessors();

    static String path = "/Users/sprintpay/Documents/"; //"C:\\Users\\ASSAM\\Pictures\\";
    static String filePath = "test.txt";// "Stade PAUL Biya au Cameroun.mp4";//+"miqo.PNG";// "C:\\Users\\ASSAM\\Documents\\test.txt";//"C:\\Users\\ASSAM\\Videos\\Films\\Movies\\The.Equalizer.2014.Et.II.2018.TRUEFRENCH.DVDRip.XviD.AC3-Tetine\\Equalizer 2014\\Equalizer.avi";//"C:\\Users\\ASSAM\\Documents\\test.txt"; //"/Users/sprintpay/Documents/test.txt";
    public static void main(String[] args) {

        Compression compression = new Compression();
        byte[] bytes = compression.fileToByteArray(path+filePath);
        Thread[] myThreads = new Thread[bytes.length];
        String[] binaryString = new String[bytes.length];
        final int GAP = bytes.length/NB_CPU;
        int start = 0, end = GAP - 1;

        for(int i=0; i<NB_CPU; i++){
            ByteArrayToBinaryStringThread bTbst = new ByteArrayToBinaryStringThread(bytes, start, end, binaryString);
            myThreads[i] = new Thread(bTbst);
            start = end + 1;
            end += GAP;
        }
        multiThreadConvertToBinaryStringProcess(myThreads);
        System.out.println(ByteArrayToBinaryStringThread.getBinaryString());
    }

    private static void multiThreadConvertToBinaryStringProcess(Thread[] threads){
        for(int i=0; i<NB_CPU; i++){
            threads[i].start();
        }
    }

    public static void milanisation(String filePath){

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
                //Découpage du texte binaire en liste de texte de 76 bits
                List<String> list76 = compression.binaryStringToList76(binaryString);
                int size = list76.size()/76;
                if(size>1) size--;
                List<String> uniques = Arrays.asList(new String[size]);
                List<String> duplicates = Arrays.asList(new String[size]);
                List<String> list74 = Arrays.asList(new String[size]);
                List<Integer> IFs = Arrays.asList(new Integer[size]);
                List<String> occurrences = Arrays.asList(new String[size]);

                List<String> finalList76 = Collections.synchronizedList(list76);
                list76.parallelStream()
                        .map((String string76) -> {
                            System.out.println("Rang N° "+ finalList76.indexOf(string76));

                            //Récupération des uniques de chaque bloc de 76bits
                            uniques.add(finalList76.indexOf(string76), compression.isolateUniques(string76));

                            //Récupération des doublons de chaque bloc
                            duplicates.add(finalList76.indexOf(string76), compression.isolateDuplicatePositions(string76));

                            //Récupération des occurrences de 0 à 9
                            occurrences.add(finalList76.indexOf(string76), compression.computeOccurrences(string76));

                            //Calcul de l'IF
                            IFs.add(finalList76.indexOf(string76), compression.computeIF(string76));

                            //Récupération en texte de 74 bits
                            list74.add(finalList76.indexOf(string76), compression.get74(uniques.get(finalList76.indexOf(string76)), duplicates.get(finalList76.indexOf(string76)), occurrences.get(finalList76.indexOf(string76)), IFs.get(finalList76.indexOf(string76))));

                            System.out.println("==================================================================================================");
                            return null;
                        }).collect(Collectors.toList());


                niveauCompression++;
                binaryString = list74.stream().collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString();
                System.out.println("new taille: "+binaryString);
            }
            String finalText = binaryString+Compression.resteBits.stream().collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString();
            String savePath = path+"test.lana";// "Stade PAUL Biya au Cameroun.lana";
            compression.binaryStringToFile(finalText, savePath, StandardOpenOption.CREATE);
        }else{
            for(int i=0; i<new File(filePath).length(); i=i+Compression.MAX_VALUE){
                //Conversion du fichier en tableau de bytes
                byte[] bytes = compression.fileToByteArray(filePath);

                //conversion du tableau de bytes en binaire sous forme de texte
                String binaryString = compression.byteArrayToBinaryString(bytes);

                //System.out.println("initial taille: "+binaryString.length());
                while(niveauCompression < Compression.seuil && binaryString.length() > 74)
                {
                    System.out.println("Descente N° "+niveauCompression);
                    //Découpage du texte binaire en liste de texte de 76 bits
                    List<String> list76 = compression.binaryStringToList76(binaryString);
                    int size = list76.size()/76;
                    if(size>1) size--;
                    List<String> uniques = Arrays.asList(new String[size]);
                    List<String> duplicates = Arrays.asList(new String[size]);
                    List<String> list74 = Arrays.asList(new String[size]);
                    List<Integer> IFs = Arrays.asList(new Integer[size]);
                    List<String> occurrences = Arrays.asList(new String[size]);

                    //int i=0;
                    //On parcourt chaque bloc de 76 bits
                    List<String> finalList76 = list76;
                    list76.parallelStream().forEach(string76 -> {
                        System.out.println("Rang N° "+ finalList76.indexOf(string76));

                        //Récupération des uniques de chaque bloc de 76bits
                        uniques.add(finalList76.indexOf(string76), compression.isolateUniques(string76));

                        //Récupération des doublons de chaque bloc
                        duplicates.add(finalList76.indexOf(string76), compression.isolateDuplicatePositions(string76));

                        //Récupération des occurrences de 0 à 9
                        occurrences.add(finalList76.indexOf(string76), compression.computeOccurrences(string76));

                        //Calcul de l'IF
                        IFs.add(finalList76.indexOf(string76), compression.computeIF(string76));

                        //Récupération en texte de 74 bits
                        list74.add(finalList76.indexOf(string76), compression.get74(uniques.get(finalList76.indexOf(string76)), duplicates.get(finalList76.indexOf(string76)), occurrences.get(finalList76.indexOf(string76)), IFs.get(finalList76.indexOf(string76))));

                        System.out.println("==================================================================================================");
                    });
                    niveauCompression++;
                    binaryString = list74.stream().collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString();
                    System.out.println("new taille: "+binaryString);
                }
                String finalText = binaryString+Compression.resteBits.stream().collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString();
                String savePath = path+ "Stade PAUL Biya au Cameroun.lana";
                compression.binaryStringToFile(finalText, savePath, StandardOpenOption.APPEND);
            }
        }
        System.out.println("----"+(System.currentTimeMillis() - start));
    }
}