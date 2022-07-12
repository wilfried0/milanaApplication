package com.milana.compression.services;

public class BinaryService {
    private String binaryString;
    private boolean disponible = false;

    public synchronized String get() throws InterruptedException {
        while (disponible == false) {
            wait();
        }
        disponible = false;
        notifyAll();
        return binaryString;
    }

    public synchronized void put(String valeur) throws InterruptedException {
        while (disponible == true) {
            wait();
        }
        this.binaryString = valeur;
        disponible = true;
        notifyAll();
    }
}
