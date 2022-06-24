package com.milana;

import com.milana.compression.services.Compression;

import java.util.ArrayList;
import java.util.List;

public class MilanaApplication {

    public static void main(String[] args) {
        String filePath = "/Users/sprintpay/Documents/test.txt";
        milanisation(filePath);
    }

    public static void milanisation(String filePath){

        Compression compression = new Compression();
        ArrayList<String> uniques = new ArrayList<>();
        List<String> duplicates = new ArrayList<>();
        ArrayList<String> list74 = new ArrayList<>();
        ArrayList<Integer> IFs = new ArrayList<>();
        ArrayList<String> occurrences = new ArrayList<>();

        //Conversion du fichier en tableau de bytes
        byte[] bytes = compression.fileToByteArray(filePath);

        //conversion du tableau de bytes en binaire sous forme de texte
        String binaryString = compression.byteArrayToBinaryString(bytes);

        //Découpage du texte binaire en liste de texte de 76 bits
        ArrayList<String> list76 = compression.binaryStringToList76(binaryString);

        //On parcourt chaque bloc de 76 bits
        for(int i=0; i<list76.size(); i++){

            //Récupération des uniques de chaque bloc de 76bits
            uniques.add(compression.isolateUniques(list76.get(i)));

            //Récupération des doublons de chaque bloc
            duplicates.add(compression.isolateDuplicatePositions(list76.get(i)));

            //Récupération des occurrences de 0 à 9
            occurrences.add(compression.computeOccurrences(list76.get(i)));

            //Calcul de l'IF
            IFs.add(compression.computeIF(list76.get(i)));

            //Récupération en texte de 74 bits
            list74.add(compression.get74(list76.get(i).charAt(0), uniques.get(i), duplicates.get(i), occurrences.get(i), IFs.get(i)));
        }
    }
}