import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

import java.nio.ByteBuffer;

class c{

    public static void main(String[] args) {
        try {

            byte[] buffer = "Heloooooooooooooooooooooooooooooooooo!!!!!! \n".getBytes();
            int number_of_lines = 999999;
            for(int k=0;k<1000;k++){
                // FileChannel
                // RandomAccessFile
                // ByteBuffer
                // java.nio.ByteBuffer
                
                FileChannel rwChannel = new RandomAccessFile("out/textfile"+k+".txt", "rw").getChannel();
                ByteBuffer wrBuf = rwChannel.map(FileChannel.MapMode.READ_WRITE, 0, buffer.length * number_of_lines);
                for (int i = 0; i < number_of_lines; i++)
                {
                    wrBuf.put(buffer);
                }
                rwChannel.close();

            }
            
        } catch (Exception e) {
            System.out.println(e);
        }
        
    }
}