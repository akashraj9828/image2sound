import java.io.BufferedReader;
import java.io.DataOutputStream;
// import java.io.file
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
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
        // print("" + wav.map(0.5, 0, 1, 0, 100));
        wav obj = new wav("out.wav");
        // obj.generate();
        obj.fillData();
        // ar[0][1] = 3;
        // b.print(":::::::::::::::::::::: aksdjaojudoqwdaosij" + ar[0][1]);
        // print(obj);
    }

    static void print(wav o) {
        File f = new File("out.wav");
        print("*******");
        print("p:" + o.ChunkSize);
        print("f:" + f.length());
    }

    static void print(Object o) {
        System.out.println(o);
    }
}

class wav {
    public static String fileName;
    public static int SampleCount;

    // wav file
    /* size offset endian */
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

    public static int seconds, amplitude;
    public static int minFreq = 10000, maxFreq = 16000;

    // image related
    public static String imgFileName = "in/temp2.png";
    public static int pixelpersecond =2;

    public wav(String a) {
        fileName = a;

        ChunkID = "RIFF";
        ChunkSize = 0;
        Format = "WAVE";
        SubChunk1ID = "fmt ";
        SubChunk1Size = 16;
        AudioFormat = 1;
        NumChannels = 1;
        SampleRate = 44100;
        BitsPerSample = 8 * 2;
        ByteRate = SampleRate * NumChannels * (BitsPerSample / 8);
        BlockAlign = (short) (NumChannels * BitsPerSample / 8);
        SubChunk2ID = "data";
        SubChunk2Size = 0;
    }

    public static void generate() {
        File f = new File(fileName);
        try {
            f.createNewFile();
            try {
                FileOutputStream fos = new FileOutputStream(fileName);
                DataOutputStream dos = new DataOutputStream(fos);

                // generateRawData();
                ChunkSize = 4 + (8 + SubChunk1Size) + (8 + SubChunk2Size);
                // print("ChunkSuze:"+ChunkSize);
                // big endian
                dos.writeBytes(ChunkID);

                // little endian
                ByteBuffer buffer1 = ByteBuffer.allocate(4);
                buffer1.order(ByteOrder.LITTLE_ENDIAN);
                buffer1.putInt(ChunkSize);
                for (int i = 0; i < buffer1.capacity(); i++) {
                    dos.writeByte(buffer1.get(i));
                }

                // big endian
                dos.writeBytes(Format);
                dos.writeBytes(SubChunk1ID);

                // little endian
                ByteBuffer buffer2 = ByteBuffer.allocate(20);
                buffer2.order(ByteOrder.LITTLE_ENDIAN);
                buffer2.putInt(SubChunk1Size);
                buffer2.putShort(AudioFormat);
                buffer2.putShort(NumChannels);
                buffer2.putInt(SampleRate);
                buffer2.putInt(ByteRate);
                buffer2.putShort(BlockAlign);
                buffer2.putShort(BitsPerSample);

                for (int i = 0; i < buffer2.capacity(); i++) {
                    dos.writeByte(buffer2.get(i));
                }

                // big endian
                dos.writeBytes(SubChunk2ID);

                // little endian
                ByteBuffer buffer3 = ByteBuffer.allocate(4);
                buffer3.order(ByteOrder.LITTLE_ENDIAN);
                buffer3.putInt(SubChunk2Size);
                for (int i = 0; i < buffer3.capacity(); i++) {
                    dos.writeByte(buffer3.get(i));
                }

                // little endian
                int i = 0;
                ByteBuffer buffer4 = ByteBuffer.allocate(RawData.length * 2);
                buffer4.order(ByteOrder.LITTLE_ENDIAN);
                a.print("Bufflen: " + buffer4.capacity());
                for (i = 0; i < SampleCount; i++) {
                    buffer4.putShort(RawData[i]);
                    // a.print(RawData[i]);
                }

                // FileWriter fout = new FileWriter("out.log");
                // int count = 0;
                for (i = 0; i < buffer4.capacity(); i++) {
                    dos.writeByte(buffer4.get(i));
                }
                // fout.close();

            } catch (IOException exp) {
                System.err.println("File output stream error : " + exp.getMessage());
            }

        } catch (IOException exp) {
            System.err.println("Creat new file error : " + exp.getMessage());
        }
    }

    public static void fillData() {
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
            maxFreq=minFreq+col*50;
            seconds = row / (pixelpersecond);
            // seconds= row<col ? row:col;
            // SampleRate= row>col ?row:col;
            
            ar = new double[row][col];
            print("width:" + img.getWidth());
            print("height:" + img.getHeight());
            print("Seconds:  "+seconds+"s");
            print("pixel/s:  "+pixelpersecond);
            // color = img.getRGB(61, 83);
            // FileWriter fo=new FileWriter("bri-inv.txt");
            for (int i = 0; i < row; i++) {
                // fo.write("\n");
                for (int j = 0; j < col; j++) {
                    // print(i+","+j);
                    color = img.getRGB(j, i);
                    b = color & 0xff;
                    g = (color & 0xff00) >> 8;
                    r = (color & 0xff0000) >> 16;
                    brightness =((r + g + b) / 768);
                    // brightness =
                    // BigDecimal.valueOf(brightness).setScale(2,RoundingMode.UNNECESSARY).doubleValue();
                    brightness = BigDecimal.valueOf(brightness).setScale(2, RoundingMode.UP).doubleValue();
                    // System.out.print(brightness+",");
                    // mapping brightness ranging from 0-1 to 0-32700
                    brightness = map(brightness, 0, 1, 0, 32700);
                    // brightness =(int) brightness/32700;
                    brightness =32700-brightness;
                    // print(brightness);
                    ar[i][j] = brightness;
                    // fo.write((brightness)+",");
                    // print("r:" + red + " g:" +green + " b:" +blue);
                    // System.out.print(brightness + ",");

                }
                // print("");
            }
            // fo.close();
            fill(ar, img.getWidth(), img.getHeight(), seconds);
            // generateRawData(ar);
        } catch (IOException e) {
            e.printStackTrace();
            // e.getCause().getCause().getCause();
            System.out.println("Error: " + e);
        }

    }

    public static void fill(double ar[][], int width, int height, int seconds) {
        // 1s> pixelpersecond
        // per pixel samples > SampleRate / pixelpersecond
        int NumSamples = seconds * SampleRate;
        SampleCount = NumSamples;
        RawData = new short[NumSamples];
        int freqGap = (maxFreq - minFreq) / width;
        
        try {
            
            int index = 0;
            int samplesPerRow = SampleRate / pixelpersecond;
            int samplePerPixel = samplesPerRow / width;
            print("Min freq: "+minFreq+"Hz");
            print("max freq: "+maxFreq+"Hz");
            print("Num samples: "+ NumSamples );
            print("Samples/row: "+samplesPerRow);
            print("Samples/pixel: "+samplePerPixel);
            print("freq gap: "+freqGap+"Hz");

            for (int i = 0; i < seconds; i++) {
                for (int j = 0; j < pixelpersecond; j++) {
                    // print(".."+j);
                    for (int k = 0; k < width; k++) {
                        // print(".."+k);
                        for (int l = 0; l < samplePerPixel; l++) {
                            // l++;
                            int row = i * pixelpersecond + j;
                            int col = k;
                            // if (col < width) {
                                // int index=(i*seconds)+(j*pixelpersecond)+(k*samplesPerRow);
                            // print(index);
                            // double freq=((maxFreq-minFreq)/width)*col+minFreq;
                            // double freq = (double) map(col, 0, width, minFreq, maxFreq);
                            double freq = (double) minFreq+col*freqGap;
                            // print(row+","+col+":"    +freq);
                            // int offset = (int) map(l, 0, samplePerPixel, 0, freqGap);
                            double offset = ((double)freqGap/(double)samplePerPixel)*(double)l;
                            freq =freq+offset;
                            // print("("+row+","+col+") :"+l+":"+ freq+"  off: "+offset);
                            double amp = ar[row][col];
                            // double temp=map(amp,0,32700,0,1);
                            // print(amp);
                            // double gap = (((double) (freq) * (double) 360 / (double) samplesPerRow));
                            // System.out.print(temp+",");
                            double gap = (((double) (freq) * (double) 360 / (double) width));
                            // print(gap);
                            double angle = Math.toRadians((gap * k));
                            // print(Math.sin(angle));
                            RawData[index] = (short) (amp * Math.sin((angle)));
                            // print(RawData[index]);
                            // print((short)(amp * Math.sin((angle))));
                            // } else {
                            // RawData[index] = (short) 0;
                            // }
                            index++;

                        }
                        // print("");
                    }
                }
                
            }
            // for (short z : wav.RawData)
            // { print(z);
                
            // }

            print("total samples:: " + index);
            print("expected samples:: " + SampleRate * seconds);
            SubChunk2Size = NumSamples * NumChannels * (BitsPerSample / 8);
            generate();
        } catch (Exception e) {
            print(e);
        }

    }

    // public static void fillaa(double ar[][], int width, int height, int seconds) {
    //     // 1s> pixelpersecond
    //     // per pixel samples > SampleRate / pixelpersecond
    //     SampleRate=width;
    //     seconds=1;
    //     int NumSamples = seconds * SampleRate;
    //     RawData = new short[NumSamples];
    //     SampleCount = NumSamples;
    //     int freqGap = (maxFreq - minFreq) / width;

    //     try {

    //         int index = 0;
    //         int samplesPerRow = SampleRate / pixelpersecond;
    //         int samplePerPixel = samplesPerRow / width;
    //         print("Samples/row: "+samplesPerRow);
    //         print("Samples/pixel: "+samplePerPixel);
    //         print("freq gap: "+freqGap+"Hz");
    //         for (int i = 0; i < seconds; i++) {
    //             for (int j = 0; j < pixelpersecond; j++) {
    //                 for (int k = 0; k < width; k++) {
    //                     // for (int l = 0; l < samplePerPixel; l++) {
    //                         // l++;
    //                         int row = i * pixelpersecond + j;
    //                         int col = k;
    //                         // if (col < width) {
    //                         // int index=(i*seconds)+(j*pixelpersecond)+(k*samplesPerRow);
    //                         // print(index);
    //                         // double freq=((maxFreq-minFreq)/width)*col+minFreq;
    //                         double freq = 5;
    //                         if(k>width/2)
    //                         freq = 15;
                            
    //                         // freq=freq+index%30;
    //                         // int offset = (int) map(l, 0, samplePerPixel, 0, freqGap);
    //                         // double offset = ((double)freqGap/(double)samplePerPixel)*(double)l;
    //                         // freq =freq+offset;
    //                         // print("("+row+","+col+") :"+l+":"+ freq+"  off: "+offset);
    //                         double amp = ar[row][col];
    //                         // double temp=map(amp,0,32700,0,1);
    //                         // print(amp);
    //                         // double gap = (((double) (freq) * (double) 360 / (double) samplesPerRow));
    //                         // System.out.print(temp+",");
    //                         double gap = (((double) (freq) * (double) 360 / (double) width));
    //                         // print(gap);
    //                         double angle = Math.toRadians((gap * k));
    //                         // print(Math.sin(angle));
                            
    //                         RawData[index] = (short) (amp * Math.sin((angle)));
    //                         // if(k>width/2)
    //                         // RawData[index] = (short) (amp * Math.sin((angle+(Math.PI/2))));
                            
    //                         // print(RawData[index]);
    //                         // print((short)(amp * Math.sin((angle))));
    //                         // } else {
    //                         // RawData[index] = (short) 0;
    //                         // }
    //                         index++;

    //                     // }
    //                     // print("");
    //                 }
    //             }

    //         }
    //         // for (short z : wav.RawData)
    //         // { print(z);

    //         // }

    //         print("total samples:: " + index);
    //         print("expected samples:: " + SampleRate * wav.seconds/pixelpersecond);
    //         SubChunk2Size = NumSamples * NumChannels * (BitsPerSample / 8);
    //         generate();
    //     } catch (Exception e) {
    //         print(e);
    //     }

    // }
    // amp ramge 32767 - -32767
    // public static void generateRawData(double amp[][]) {

    // // int frequency = 10000, seconds = 1, amplitude = 32767, i = 0;
    // // double omega = 2 * Math.PI * frequency;
    // int NumSamples = seconds * SampleRate;
    // SampleCount = NumSamples;
    // // double gap = (((double) (frequency) * (double) 360 / (double)
    // SampleRate));
    // // a.print("gap: "+gap);
    // // int cycles = frequency * seconds;
    // RawData = new short[NumSamples];
    // // a.print("Cycle:: " + cycles);
    // // a.print("Frequency:: " + frequency);
    // a.print("Amplitude:: " + amplitude);
    // a.print("Sampling rate:: " + SampleRate + "Hz");
    // a.print("byte/Sample :: " + (BitsPerSample / 8) + "bytes");
    // try {
    // int count = 0;
    // // RawData[0] = (short)32760;
    // // RawData[1] = (short)-32767;
    // // RawData[2] = (short)30000;
    // // RawData[3] = (short)-20000;
    // // RawData[4] = (short)10000;

    // // FileWriter fout=new FileWriter("out.log");
    // for (int i = 0; i < NumSamples; i++) {
    // int row = (int) (i / SampleRate);
    // int col = (int) (i % SampleRate);
    // int frequency = (int) map(col, 0, SampleRate, minFreq, maxFreq);
    // // print(row+","+col+"\t freq:"+frequency);
    // double gap = (((double) (frequency) * (double) 360 / (double) SampleRate));
    // // // double angle=omega*(SampleRate%(i+1));
    // // // double angle =Math.toRadians(90);
    // double angle = Math.toRadians(gap * i);
    // RawData[i] = (short) (amp[row][col] * Math.sin((angle)));
    // // RawData[i] =(int)(amplitude*Math.sin(frequency * 2 * Math.PI ));
    // // // RawData[i] = (short) (((amplitude + (i * 1)) * Math.sin((angle))));

    // if (i < SampleRate)
    // if ((RawData[i] < 0 && RawData[i - 1] > 0) || (RawData[i] > 0 && RawData[i -
    // 1] < 0))
    // count++;

    // // a.print(
    // // "sin("
    // // +Math.toDegrees(angle)+") ="
    // // // +":sin(" +(angle) + ")="
    // // + Math.sin((angle)));

    // System.out.println(i + ": " + RawData[i]);
    // // System.out.println(( amplitude*Math.sin((frequency * 2 * 3.14*i))));
    // // System.out.println((frequency * 2 * Math.PI)%180);
    // // fout.write(i+","+ RawData[i]+"\n");
    // }
    // // fout.close();
    // SubChunk2Size = NumSamples * NumChannels * (BitsPerSample / 8);
    // // a.print("frequncy:::::::::::: " + count / (2));
    // // a.print(SubChunk2Size);
    // // SubChunk2Size=RawData.length*8;
    // generate();
    // } catch (NullPointerException exp) {
    // System.err.println("Raw data generation error : " + exp.getMessage());
    // } catch (Exception exp) {
    // System.err.println(exp.getMessage());
    // }

    // }

   

    public static double map(double n, double start1, double stop1, double start2, double stop2) {
        double val = ((n - start1) / (stop1 - start1)) * (stop2 - start2) + start2;
        return val;
    }

    public static void print(Object o) {
        System.out.println(o);
    }
}
