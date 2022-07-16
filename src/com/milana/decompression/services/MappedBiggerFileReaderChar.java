package com.milana.decompression.services;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class MappedBiggerFileReaderChar {
    private MappedByteBuffer[] mappedBufArray;
    private int count = 0;
    private int number;
    private FileInputStream fileIn;
    private long fileLength;
    private int arraySize;
    private byte[] array;

    public MappedBiggerFileReaderChar(String fileName, int arraySize, int start) throws IOException {
        this.fileIn = new FileInputStream(fileName);
        FileChannel fileChannel = fileIn.getChannel();
        this.fileLength = fileChannel.size();
        this.number = (int) Math.ceil((double) fileLength / (double) Integer.MAX_VALUE);
        this.mappedBufArray = new MappedByteBuffer[number];// Memory file mapping array
        long preLength = start;
        long regionSize = (long) Integer.MAX_VALUE;// The size of the mapping area
        for (int i = 0; i < number; i++) {// Maps contiguous areas of files to memory file mapping arrays
            if (fileLength - preLength < (long) Integer.MAX_VALUE) {
                regionSize = fileLength - preLength;// The size of the last area
            }
            mappedBufArray[i] = fileChannel.map(FileChannel.MapMode.READ_ONLY, preLength, regionSize);
            preLength += regionSize;// The beginning of the next area
        }
        this.arraySize = arraySize;
    }

    public int read() {
        if (count >= number) {
            return -1;
        }
        int limit = mappedBufArray[count].limit();
        int position = mappedBufArray[count].position();
        if (limit - position > arraySize) {
            array = new byte[arraySize];
            mappedBufArray[count].get(array);
            return arraySize;
        } else {// The last read data of this memory file mapping
            array = new byte[limit - position];
            mappedBufArray[count].get(array);
            if (count < number) {
                count++;// Convert to next memory file mapping
            }
            return limit - position;
        }
    }

    public void close() throws IOException {
        fileIn.close();
        array = null;
    }

    public byte[] getArray() {
        return array;
    }

    public long getFileLength() {
        return fileLength;
    }
}
