
//Usually you will require both swing and awt packages
// even if you are working with just swings.
import javax.swing.*;

// import akash.img2sound;

import akash.*;

import java.awt.*;
import java.io.File;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
class gui {

public static JFrame frame;
public static JMenuBar mb;
public static JMenu m1;
public static JMenuItem m11;
public static JMenuItem m12;
public static JMenuItem m13;
public static JPanel panel;
public static JLabel label;
public static JTextField tf;
public static JButton choose;
public static JButton start;
public static JFileChooser fileChooser;
public static JTextArea ta ;
    public static void main(String args[]) {
        // Creating the Frame
        frame = new JFrame("Image2Text");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);

        // Creating the MenuBar and adding components
        mb = new JMenuBar();
        m1 = new JMenu("Credits");
        mb.add(m1);
        m11 = new JMenuItem("Website");
        m12 = new JMenuItem("Code");
        m13 = new JMenuItem("Github");
        m1.add(m11);
        m1.add(m12);
        m1.add(m13);

        // Creating the panel at bottom and adding components
        panel = new JPanel(); // the panel is not visible in output
        // label = new JLabel("");
        tf = new JTextField(10); // accepts upto 10 characters
        choose = new JButton("Choose image");
        choose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
                int result = fileChooser.showOpenDialog(panel);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    System.out.println("Selected file: " + selectedFile.getAbsolutePath());
                    tf.setText(selectedFile.getAbsolutePath());
                    // print("Selected file: " + selectedFile.getAbsolutePath());
                }
            }
        });
        start = new JButton("Start");
        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                String fname=tf.getText();
                new img2sound(fname);
            }
        });

        // panel.add(label); // Components Added using Flow Layout
        panel.add(tf);
        panel.add(choose);
        panel.add(start);

       

        // Text Area at the Center
        ta = new JTextArea();

        // Adding Components to the frame.
        frame.getContentPane().add(BorderLayout.SOUTH, panel);
        frame.getContentPane().add(BorderLayout.NORTH, mb);
        frame.getContentPane().add(BorderLayout.CENTER, ta);
        frame.setVisible(true);

        print("aaaaaaaaaaaaaa");
        print("bbbbbbbbbbbbbbb");
        print("xxxxxxxxxxxxxxxxxx");
    }

    public static void print(Object obj){
        ta.append((String)obj+"\n");
    }
}