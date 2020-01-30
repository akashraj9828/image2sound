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
import java.text.DecimalFormat;

class img2sound {

    public static void main(String[] args) {

        print(args[0]);
        // wav obj = new wav("temp.png", "out.wav");
        wav obj = new wav(args[0]+".png", args[0]+".wav");
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

    public static void print(Object o) {
        System.out.println(o);
    }
}
