package com.milana.decompression.services;

import java.util.List;

public interface DecompressionAction {
    void readNbreDescenteAndBlock(String file);
    Integer[] getOccurrences(String stringOccurrence);
    String[] getBinaryFromOccurrence(Integer[] occurrenceValues, int start, String[] string76);
    String refillOccurrencyFromSup17(String[] string76);
    String computeIF(String String76);
}
