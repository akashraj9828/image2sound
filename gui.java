
//Usually you will require both swing and awt packages
// even if you are working with just swings.
// FOR GUI
import javax.swing.*;
import javax.swing.text.DefaultCaret;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*;

import java.io.File;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.net.URL;
//

// FOR IMAGE2AUDIO

import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.ByteBuffer;
import java.awt.image.BufferedImage;
import java.io.*;

import javax.imageio.ImageIO;

import java.lang.Math;

import java.util.*;

// 
public class gui {
    public static JFrame frame;
    public static JMenuBar mb;
    public static JMenu m1;
    public static JMenuItem m11;
    public static JMenuItem m12;
    public static JPanel control_panel;
    public static JPanel output_panel;
    public static JLabel file_l;
    public static JLabel duration_l;
    // public static JLabel max_freq_l;
    public static JLabel density_l;
    public static JTextField file_tf;
    public static JTextField duration_tf;
    public static JTextField density_tf;
    public static JButton choose;
    public static JButton start;
    public static JFileChooser fileChooser;
    public static JTextArea ta;

    public static JProgressBar progressbar;

    public static void main(String args[]) {
        // Creating the Frame
        frame = new JFrame("Image2Sound");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // frame.pack();
        frame.setSize(600, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        // frame.setLocationByPlatform(true);
        // frame.setLocation(100, 10);

        // Creating the MenuBar and adding components
        mb = new JMenuBar();

        m1 = new JMenu("Credits");

        mb.add(m1);

        m11 = new JMenuItem("Website");
        m11.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                gui.openURL("https://github.com/akashraj9828/image2sound/tree/GUI");
            }
        });

        m12 = new JMenuItem("Github");
        m12.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                gui.openURL("https://github.com/akashraj9828");
            }
        });

        m1.add(m11);
        m1.add(m12);

        // Creating the panel at bottom and adding components
        control_panel = new JPanel(); // the panel is not visible in output

        file_l = new JLabel("Image:");
        duration_l = new JLabel("Duration:");
        density_l = new JLabel("Density:");

        file_tf = new JTextField(10); // accepts upto 10 characters
        duration_tf = new JTextField(3); // accepts upto 10 characters
        density_tf = new JTextField(2); // accepts upto 10 characters

        duration_tf.setText("10");
        density_tf.setText("2");
        choose = new JButton("Choose image");
        choose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                fileChooser = new JFileChooser();
                // fileChooser.setCurrentDirectory(new File(System.getProperty("user.path")));
                fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
                // fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Image
                // files"));
                fileChooser.setFileFilter(new FileNameExtensionFilter("Images", "jpg", "png", "gif", "bmp"));

                int result = fileChooser.showOpenDialog(control_panel);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    // System.out.println("Selected file: " + selectedFile.getAbsolutePath());
                    file_tf.setText(selectedFile.getAbsolutePath());
                    print("Selected file: " + selectedFile.getAbsolutePath());
                }
            }
        });
        start = new JButton("Start");
        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                String fname = file_tf.getText();
                String duration = duration_tf.getText();
                String density = density_tf.getText();
                String[] args = { fname, duration, density };
                // doing it in thread because UI doesnt update when during action listner
                Thread queryThread = new Thread() {
                    public void run() {
                        new img2sound(args);
                    }
                };
                queryThread.start();

                // new img2sound(args);
            }
        });

        control_panel.add(file_l); // Components Added using Flow Layout
        control_panel.add(file_tf);
        control_panel.add(choose);
        control_panel.add(duration_l);
        control_panel.add(duration_tf);
        control_panel.add(density_l);
        control_panel.add(density_tf);
        control_panel.add(start);

        // Text Area at the Center
        output_panel = new JPanel(); // the panel is not visible in output

        ta = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(ta);

        DefaultCaret caret = (DefaultCaret) ta.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);


        frame.getContentPane().add(BorderLayout.NORTH, mb);
        frame.getContentPane().add(BorderLayout.SOUTH, control_panel);
        frame.getContentPane().add(BorderLayout.CENTER, scrollPane);

        frame.setVisible(true);

    }

    public static void openURL(String domain) {
        String url = domain;
        try {
            Desktop.getDesktop().browse(new URL(url).toURI());
        } catch (Exception e) {
        }
    }

    public static void print(Object o) {
        ta.append("\t" + (String) o + "\n");
    }

}

class img2sound {

    public static String img_name;
    public static String audio_name;
    public static int duration = 5;
    public static int factor = 4;

    public img2sound(String[] args) {
        // img_name = img_name_p;
        // audio_name = img_name_p.substring(0, img_name_p.lastIndexOf('.')) + " " +
        // duration + "s density-" + factor
        // + ".wav";
        parseArgs(args);

        wav obj = new wav(img_name, audio_name, duration, factor);
        obj.init();
    }

    public static void main(String[] args) {
        parseArgs(args);
        // print(args[1]);
        // wav obj = new wav("temp.png", "out.wav");
        wav obj = new wav(img_name, audio_name, duration, factor);
        obj.init();
    }

    public static void parseArgs(String[] args) {
        int len = args.length;
        if (len == 0) {
            showHelp();
        }
        if (len == 1) {
            if (args[0].equals("-h") || args[0].equals("--h") || args[0].equals("?") || args[0].equals("-help")
                    || args[0].equals("help")) {
                showHelp();
            }
        }
        try {
            if (len > 2) {
                factor = Integer.parseInt(args[2]);
            }
            if (len > 1) {
                duration = Integer.parseInt(args[1]);
            }
            if (len > 0) {
                img_name = args[0];
                audio_name = args[0].substring(0, args[0].lastIndexOf('.')) + "_" + duration + "s_density_" + factor
                        + ".wav";
            }
        } catch (Exception e) {
            showHelp();
        }
    }

    public static void showHelp() {

        print("\n\n \t Usage: java  img2sound [image name] [seconds] [Density]");
        print("\t");
        print("\t [image name] png and jpeg supported");
        print("\t [seconds] Default 5, Output audio file length in seconds");
        print("\t [Density] Default 4, Small numbers make image pixel narrower and sharpen. But, processing takes a long time.");
        print("\t [?,-h,--h,-help,help] To display this help screen");

        // System.exit(0);
    }

    static void print(Object o) {
        System.out.println(o);
        gui.print(o + "");
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

    // user parmaeters
    public static int durationSeconds = 1;
    public static int Factor = 1;

    public wav(String imgfile, String wavfile, int seconds, int factor) {
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

        durationSeconds = seconds;
        Factor = factor;
    }

    public void init() {
        print("Input: " + imgFileName);
        print("Output: " + fileName);
        processImage();

    }

    public static short[] addElement(short[] org, short added) {
        short[] result = Arrays.copyOf(org, org.length + 1);
        result[org.length] = added;
        return result;
    }

    public static void processImage() {
        int x, y;

        try {

            File f = new File(imgFileName); // image file path
            BufferedImage img = ImageIO.read(f);

            int width = img.getWidth(); // width of the image
            int height = img.getHeight(); // height of the image

            // int durationSeconds = 1;
            double maxSpecFreq = 20000;
            // int Factor = 1;

            double maxFreq = 0;
            int sampleRate = 44100;
            int channels = 1;
            int numSamples = (int) (sampleRate * durationSeconds);
            int samplesPerPixel = (int) Math.ceil(numSamples / width) + 1;

            int C = (int) (maxSpecFreq / height) + 1; // freqncy per row
            float yFactor = Factor;

            short tmpData[] = new short[numSamples];
            short data2[] = new short[numSamples];
            print("---------------------------\n\n");

            print("width: " + width);
            print("height: " + height);
            print("durationSeconds: " + durationSeconds);
            print("maxSpecFreq: " + maxSpecFreq);
            print("Density: " + Factor);
            // print("maxFreq: " + maxFreq);
            print("sampleRate: " + sampleRate);
            print("channels: " + channels);
            print("numSamples: " + numSamples);
            print("samplesPerPixel: " + samplesPerPixel);
            print("\n\n\n");
            // print("C: " + C);
            // print("yFactor: " + yFactor);

            // loop stats
            // print("out loop x: " + numSamples);
            // print("out loop pixel_x: " + (int) Math.floor(numSamples / samplesPerPixel));
            // print("in loop y: " + height);

            // int x,y;
            for (x = 0; x < numSamples; x++) {
                double rez = 0;
                int pixel_x = (int) Math.floor(x / samplesPerPixel);
                // String s="*";
                if (x % sampleRate == 0) {
                    String prog = new String(new char[(x / sampleRate + 1)]).replace("\0", "# ");
                    String proginv = new String(new char[durationSeconds - (x / sampleRate + 1)]).replace("\0", "- ");
                    print("Progress " + prog + proginv + (x / sampleRate + 1) + '/' + durationSeconds);
                    // print(pixel_x);
                }

                for (y = 0; y < height; y += yFactor) {
                    int color = img.getRGB(pixel_x, y);
                    double b = color & 0xff;
                    double g = (color & 0xff00) >> 8;
                    double r = (color & 0xff0000) >> 16;

                    double s = r + b + g;
                    double volume = s / 765 * 100;

                    double freq = Math.round(C * (height - y + 1));
                    rez += Math.floor(volume * Math.cos(freq * 6.28 * x / sampleRate));
                }

                // tmpData = addElement(tmpData, (short)rez);
                tmpData[x] = (short) rez;

                if (Math.abs(rez) > maxFreq) {
                    maxFreq = Math.abs(rez);
                }
                // print(rez+"\t"+maxFreq);

            }

            // print(" : " + tmpData.length);
            for (int i = 0; i < tmpData.length; i++) {
                data2[i] = (short) (32700 * tmpData[i] / maxFreq); // 32767
                // print(i + "\t"+tmpData[i]+"\t" + data2[i]);
            }

            // print("total samples taken:: " + data2.length);
            // print("expected samples:: " + SampleRate * seconds+"\n");
            SubChunk2Size = numSamples * channels * (BitsPerSample / 8);
            RawData = data2;
            generateAudio();

        } catch (Exception e) {
            print("Error: " + e);
            e.printStackTrace();

            // print(x+"\t::::::::::\t"+y);

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
                // for (int i = 0; i < SampleCount; i++) {
                for (int i = 0; i < RawData.length; i++) {
                    // print("---------------------------------------------");
                    // print(RawData[i]);
                    buff.putShort(RawData[i]);
                }

                byte[] buffer = buff.array();

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

                print("\n\n");
                print("FINISHED!");
                print("\n\n");
                // print("Raw data size: " + RawData.length * 2);
                // print("buff size: " + buff.limit());
                // print("Chunk size: " + ChunkSize);
                print("Output file size: " + (double) (ChunkSize + 4) / 1024 / 1024 + " mb");
                print("Output file : " + fileName);

            } catch (IOException exp) {
                System.err.println("File output stream error : " + exp.getMessage());
            }

        } catch (IOException exp) {
            System.err.println("Creat new file error : " + exp.getMessage());
        }
    }

    public static void print(Object o) {
        System.out.println(o);
        gui.print(o + "");
    }
}
