package com.milana;

import com.milana.compression.services.Compression;
import com.milana.threads.*;

import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MilanaApplication {
    private static int NB_CPU = Runtime.getRuntime().availableProcessors();
    public static final int seuil = 30;

    static String path = "/Users/sprintpay/Documents/";//"C:\\Users\\ASSAM\\Documents\\";
    static String filePath = "test.txt";// "Stade PAUL Biya au Cameroun.mp4";//+"miqo.PNG";// "C:\\Users\\ASSAM\\Documents\\test.txt";//"C:\\Users\\ASSAM\\Videos\\Films\\Movies\\The.Equalizer.2014.Et.II.2018.TRUEFRENCH.DVDRip.XviD.AC3-Tetine\\Equalizer 2014\\Equalizer.avi";//"C:\\Users\\ASSAM\\Documents\\test.txt"; //"/Users/sprintpay/Documents/test.txt";
    public static void main(String[] args) {

        Compression compression = new Compression();
        byte[] bytes = compression.fileToByteArray(path+filePath);
        Thread[] binaryThreads = new Thread[NB_CPU];
        Thread[] uniqueThreads = new Thread[NB_CPU];
        Thread[] duplicateThreads = new Thread[NB_CPU];
        Thread[] occurrenceThreads = new Thread[NB_CPU];
        Thread[] IFThreads = new Thread[NB_CPU];
        Thread[] list74Threads = new Thread[NB_CPU];
        String[] binaryString = new String[bytes.length];
        int niveauCompression = 1;
        //long time = System.currentTimeMillis();
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
        System.out.println(text);

        while(niveauCompression < Compression.seuil && text.length() > 74){
            List<String> list76 = compression.binaryStringToList76(text);
            System.out.println("Text => "+list76.toString());
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
            System.out.println("-------------------  Uniques  --------------------------");
            for(int i=0; i<NB_CPU; i++){
                IsolateUniqueThread iUt = new IsolateUniqueThread(list76, start, end, uniques);
                uniqueThreads[i] = new Thread(iUt);
                start = end + 1;
                end += GAP;
            }
            multiThreadProcess(uniqueThreads);
            System.out.println(IsolateUniqueThread.getUniqueString());

            start = 0; end = GAP+(list76.size()%NB_CPU)-1;
            System.out.println("-------------------  Duplicates  --------------------------");
            for(int i=0; i<NB_CPU; i++){
                IsolateDuplicateThread iDt = new IsolateDuplicateThread(list76, start, end, duplicates);
                duplicateThreads[i] = new Thread(iDt);
                start = end + 1;
                end += GAP;
            }
            multiThreadProcess(duplicateThreads);
            System.out.println(IsolateDuplicateThread.getDuplicateString());

            start = 0; end = GAP+(list76.size()%NB_CPU)-1;
            System.out.println("-------------------  Occurrences  --------------------------");
            for(int i=0; i<NB_CPU; i++){
                IsolateOccurrenceThread iOt = new IsolateOccurrenceThread(list76, start, end, occurrences);
                occurrenceThreads[i] = new Thread(iOt);
                start = end + 1;
                end += GAP;
            }
            multiThreadProcess(occurrenceThreads);
            System.out.println(IsolateOccurrenceThread.getString());

            start = 0; end = GAP+(list76.size()%NB_CPU)-1;
            System.out.println("-------------------  IF  --------------------------");
            for(int i=0; i<NB_CPU; i++){
                IsolateIFThread iFt = new IsolateIFThread(list76, start, end, IFs);
                IFThreads[i] = new Thread(iFt);
                start = end + 1;
                end += GAP;
            }
            multiThreadProcess(IFThreads);

            start = 0; end = GAP+(list76.size()%NB_CPU)-1;
            System.out.println("-------------------  list74  --------------------------");
            for(int i=0; i<NB_CPU; i++){
                IsolateList74Thread i74t = new IsolateList74Thread(uniques, duplicates, occurrences, IFs, start, end, list74);
                list74Threads[i] = new Thread(i74t);
                start = end + 1;
                end += GAP;
            }
            multiThreadProcess(list74Threads);

            niveauCompression++;
            text = IsolateList74Thread.getString74();//list74.stream().collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString();
            System.out.println("new taille: "+text);
        }
        String finalText = text+Compression.resteBits.stream().reduce("", (a,b)->a+b);
        String savePath = path+"test.lana";
        compression.binaryStringToFile(finalText, savePath, StandardOpenOption.APPEND);
    }

    private static void multiThreadProcess(Thread[] threads){
        for(int i=0; i<NB_CPU; i++){
            threads[i].start();
        }
    }

    public static void multiThreadProcessJoin(Thread[] threads)  {
        try {
            for(int i=0; i<NB_CPU; i++){
                threads[i].join();
            }
        } catch (InterruptedException ignored){

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
    }*/
}