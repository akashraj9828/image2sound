import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.channels.FileChannel;
import java.nio.ByteBuffer;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;

import javafx.scene.paint.Color;

import java.util.*;

class img2sound {

    public static void main(String[] args) {

        wav obj = new wav("temp.png", "out.wav");
        obj.init();
    }

    static void print(Object o) {
        System.out.println(o);
    }
}

class wav {

    // audio file related
    public static String fileName;
    public static int SampleCount;
    // image related
    public static String imgFileName;
    public static int pixelpersecond = 2;

    // wav file
    /* size(byte) offset(byte) endian */
    // 4 0 big endian
    public static String ChunkID;
    // 4 4 little endian
    public static int ChunkSize;
    // 4 8 big endian
    public static String Format;
    // 4 12 big endian
    public static String SubChunk1ID;
    // 4 16 little endian
    public static int SubChunk1Size;
    // 2 20 little endian
    public static short AudioFormat;
    // 2 22 little endian
    public static short NumChannels;
    // 4 24 little endian
    public static int SampleRate;
    // 4 28 little endian
    public static int ByteRate;
    // 2 32 little endian
    public static short BlockAlign;
    // 2 34 little endian
    public static short BitsPerSample;
    // 4 36 big endian
    public static String SubChunk2ID;
    // 4 40 little endian
    public static int SubChunk2Size;
    // unlimited 44 little endian
    public static short[] RawData;
    /**************************************/

    // every value here is modified later
    public static int seconds, amplitude, minFreq = 0, maxFreq = 21000, freqGap;
    //

    public wav(String imgfile, String wavfile) {
        imgFileName = imgfile;
        fileName = wavfile;
        ChunkID = "RIFF"; // don't change
        ChunkSize = 0;
        Format = "WAVE"; // don't change
        SubChunk1ID = "fmt "; // don't change
        SubChunk1Size = 16; // don't change
        AudioFormat = 1; // don't change
        NumChannels = 1; // don't change
        SampleRate = 44100;
        BitsPerSample = 8 * 2;
        ByteRate = SampleRate * NumChannels * (BitsPerSample / 8);
        BlockAlign = (short) (NumChannels * BitsPerSample / 8);
        SubChunk2ID = "data"; // don't change
        SubChunk2Size = 0;
    }

    public void init() {
        processImage();

    }

    public static void processImage() {
        double ar[][];
        int row = 0, col = 0;
        String s;

        int width = 0; // width of the image
        int height = 0; // height of the image
        BufferedImage img = null;
        File f = null;
        int color;
        double r;
        double g;
        double b;
        double brightness;

        // read image
        try {
            f = new File(imgFileName); // image file path
            img = ImageIO.read(f);
            row = img.getHeight();
            col = img.getWidth();
            freqGap = (maxFreq - minFreq) / col;
            seconds = row / (pixelpersecond);

            print("width:" + img.getWidth());
            print("height:" + img.getHeight()+"\n");
            print("Seconds:  " + seconds + "s");
            print("pixel/s:  " + pixelpersecond);

            ar = new double[row][col];
            for (int i = 0; i < row; i++) {
                for (int j = 0; j < col; j++) {
                    color = img.getRGB(j, i);
                    b = color & 0xff;
                    g = (color & 0xff00) >> 8;
                    r = (color & 0xff0000) >> 16;
                    brightness = ((r + g + b) / 768);
                    brightness = BigDecimal.valueOf(brightness).setScale(2, RoundingMode.UP).doubleValue();
                    // mapping brightness ranging from 0-1 to 0-32700
                    brightness = map(brightness, 0, 1, 0, 32700);
                    brightness = 32700 - brightness;

                    // ar[row-i-1][col-j-1] = brightness;
                    ar[i][j] = brightness;
                }
            }
            fillRawData(ar, img.getWidth(), img.getHeight(), seconds);
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }

    }

    public static void fillRawData(double ar[][], int width, int height, int seconds) {
        int NumSamples = seconds * SampleRate;
        SampleCount = NumSamples;
        RawData = new short[NumSamples];
        int freqGapCalc = (maxFreq - minFreq) / width;

        try {
            int index = 0;
            int samplesPerRow = SampleRate / pixelpersecond;
            int samplePerPixel = samplesPerRow / width;

            print("Min freq: " + minFreq + "Hz");
            print("max freq: " + maxFreq + "Hz\n");
            print("Num samples: " + NumSamples);
            print("Samples/row: " + samplesPerRow);
            print("Samples/pixel: " + samplePerPixel);
            print("freq gap per pixel: " + freqGapCalc + "Hz");

            for (int i = 0; i < seconds; i++) {
                for (int j = 0; j < pixelpersecond; j++) {
                    for (int k = 0; k < width; k++) {
                        int col = k;
                        int row = i * pixelpersecond + j;
                        double BaseFreq = ((double) minFreq + col * freqGapCalc);
                        for (int l = 0; l < samplePerPixel; l++) {
                            double offset = ((double) freqGapCalc / (double) samplePerPixel) * (double) l;
                            Double freq = (BaseFreq + offset) / pixelpersecond;
                            double amp = ar[row][col];
                            double omega = (((double) (freq) * (double) 360));
                            double time = map(col, 0, width, 0, 1);

                            double angle = Math.toRadians((omega * time));
                            RawData[index] = (short) (amp * Math.sin((angle)));
                            index++;

                        }
                    }
                }

            }
            print("total samples taken:: " + index);
            print("expected samples:: " + SampleRate * seconds+"\n");
            SubChunk2Size = NumSamples * NumChannels * (BitsPerSample / 8);
            generateAudio();
        } catch (Exception e) {
            print(e);
        }

    }

    public static void generateAudio() {
        File f = new File(fileName);
        try {
            f.createNewFile();
            try {
                ChunkSize = 4 + (8 + SubChunk1Size) + (8 + SubChunk2Size);
                FileChannel rwChannel = new RandomAccessFile(fileName, "rw").getChannel();
                ByteBuffer wrBuf = rwChannel.map(FileChannel.MapMode.READ_WRITE, 0, ChunkSize + 4);
                wrBuf.put(ChunkID.getBytes());
                // dos.writeBytes(ChunkID);

                int bufferLen = 4 + 20 + 4 + (RawData.length * 2) + 1000; // 1000 bytes were jsut given to prevent
                                                                          // overflow sometime

                // little endian
                ByteBuffer buff = ByteBuffer.allocate(bufferLen);
                buff.order(ByteOrder.LITTLE_ENDIAN);
                buff.putInt(ChunkSize);
                // format subchunk1id
                buff.putInt(SubChunk1Size);
                buff.putShort(AudioFormat);
                buff.putShort(NumChannels);
                buff.putInt(SampleRate);
                buff.putInt(ByteRate);
                buff.putShort(BlockAlign);
                buff.putShort(BitsPerSample);
                // subchunk2id
                buff.putInt(SubChunk2Size);
                for (int i = 0; i < SampleCount; i++) {
                    buff.putShort(RawData[i]);
                }

                byte[] buffer = buff.array();
                print("Raw data size: " + RawData.length * 2);
                print("buff size: " + buff.limit());
                print("Chunk size: " + ChunkSize);
                print("Output file size: " + (double) (ChunkSize + 4) / 1024 / 1024 + " mb");
                int j = 0;
                for (int i = 4; i < ChunkSize;) {
                    if (i == 8) {
                        wrBuf.put(Format.getBytes());
                        wrBuf.put(SubChunk1ID.getBytes());
                        i = i + 8;
                        continue;
                    }
                    if (i == 36) {
                        wrBuf.put(SubChunk2ID.getBytes());
                        i = i + 4;
                        continue;
                    }
                    wrBuf.put(buffer[j]);
                    j++;
                    i++;
                }

                rwChannel.close();

            } catch (IOException exp) {
                System.err.println("File output stream error : " + exp.getMessage());
            }

        } catch (IOException exp) {
            System.err.println("Creat new file error : " + exp.getMessage());
        }
    }

    public static double map(double n, double start1, double stop1, double start2, double stop2) {
        double val = ((n - start1) / (stop1 - start1)) * (stop2 - start2) + start2;
        return val;
    }

    public static void print(Object o) {
        System.out.println(o);
    }
}
