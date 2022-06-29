package com.milana;

import com.milana.compression.services.Compression;

import java.util.ArrayList;
import java.util.List;

public class MilanaApplication {

    public static void main(String[] args) {
        String path = "C:\\Users\\ASSAM\\Pictures\\";
        String filePath = path+"miqo.PNG";// "C:\\Users\\ASSAM\\Documents\\test.txt";//"C:\\Users\\ASSAM\\Videos\\Films\\Movies\\The.Equalizer.2014.Et.II.2018.TRUEFRENCH.DVDRip.XviD.AC3-Tetine\\Equalizer 2014\\Equalizer.avi";//"C:\\Users\\ASSAM\\Documents\\test.txt"; //"/Users/sprintpay/Documents/test.txt";
        milanisation(filePath);
    }

    public static void milanisation(String filePath){

        Compression compression = new Compression();
        int niveauCompression = 1;

        //Conversion du fichier en tableau de bytes
        byte[] bytes = compression.fileToByteArray(filePath);

        //conversion du tableau de bytes en binaire sous forme de texte
        String binaryString = compression.byteArrayToBinaryString(bytes);

        //System.out.println("initial taille: "+binaryString.length());
        while(niveauCompression < Compression.seuil && binaryString.length() > 74) 
        {
            System.out.println("Descente N° "+niveauCompression);
            //Découpage du texte binaire en liste de texte de 76 bits
            ArrayList<String> uniques = new ArrayList<>();
            List<String> duplicates = new ArrayList<>();
            ArrayList<String> list74 = new ArrayList<>();
            ArrayList<Integer> IFs = new ArrayList<>();
            ArrayList<String> occurrences = new ArrayList<>();
            ArrayList<String> list76 = compression.binaryStringToList76(binaryString);

            //int i=0;
            //On parcourt chaque bloc de 76 bits
            list76.parallelStream().forEach(string76 -> {
                System.out.println("Rang N° "+ list76.indexOf(string76));

                //Récupération des uniques de chaque bloc de 76bits
                uniques.add(list76.indexOf(string76), compression.isolateUniques(string76));

                //Récupération des doublons de chaque bloc
                duplicates.add(list76.indexOf(string76), compression.isolateDuplicatePositions(string76));

                //Récupération des occurrences de 0 à 9
                occurrences.add(list76.indexOf(string76), compression.computeOccurrences(string76));

                //Calcul de l'IF
                IFs.add(list76.indexOf(string76), compression.computeIF(string76));

                //Récupération en texte de 74 bits
                list74.add(list76.indexOf(string76), compression.get74(uniques.get(list76.indexOf(string76)), duplicates.get(list76.indexOf(string76)), occurrences.get(list76.indexOf(string76)), IFs.get(list76.indexOf(string76))));

                System.out.println("==================================================================================================");
            });
            //i = 0;
            //System.out.println(list74.toString());
            niveauCompression++;
            binaryString = list74.stream().collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString();
            System.out.println("new taille: "+binaryString);
        }
        String finalText = binaryString+Compression.resteBits.stream().collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString();
        String savePath = "C:\\Users\\ASSAM\\Pictures\\miqo.lana";
        compression.binaryStringToFile(finalText, savePath);
    }
}