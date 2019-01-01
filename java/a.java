import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteOrder;

import com.sun.corba.se.impl.ior.ByteBuffer;

class a{

    public static void main(String[] args) {
        wav obj=new wav("out.wav");
		obj.generate();
		print(obj);
    }

   	static void print(wav o){
		   File f=new File("out.wav");
		print("*******");
		print("size:"+o.ChunkSize);
		print("f size:"+f.length());
		// print();
		// print();
		// print();
		// print();
    }

    static void print(Object o){
        System.out.println(o);
    }
}

class wav {
	public String fileName;
	public int SampleCount;

	////file
	// 4 0
	public String ChunkID;
	// 4 4
	public int ChunkSize;
	// 4 8
	public String Format;
	// 4 12
	public String SubChunk1ID;
	// 4 16
	public int SubChunk1Size;
	// 2 20
	public short AudioFormat;
	// 2 22
	public short NumChannels;
	// 4 24
	public int SampleRate;
	// 4 28
	public int ByteRate;
	// 2 32
	public short BlockAlign;
	// 2 34
	public short BitesPerSample;
	// 4 36
	public String SubChunk2ID;
	// 4 40
	public int SubChunk2Size;
	//unlimited 44
 	public int[] RawData;

	public wav(String a) {
		this.fileName = a;
		//0
		this.ChunkID = "RIFF";
		//4
		this.ChunkSize = 0000;
		this.Format = "WAVE";
		this.SubChunk1ID = "fmt ";
		this.SubChunk1Size = 16;
		this.AudioFormat = 1;
		this.NumChannels = 1;
		this.SampleRate = 44100;
		this.BitesPerSample = 16;
		this.ByteRate = this.SampleRate * this.NumChannels * (this.BitesPerSample / 8);
		this.BlockAlign = (short)(this.NumChannels * this.BitesPerSample / 8);
		this.SubChunk2ID = "data";
		this.SubChunk2Size = 0;
	}

	public void generate() {
		File f = new File(this.fileName);
		try {
			f.createNewFile();
			try {
				FileOutputStream fos = new FileOutputStream(this.fileName);
				DataOutputStream dos = new DataOutputStream(fos);
				// Little
				// ByteBuffer buffer=ByteBuffer.allocate();
				// buffer.order(ByteOrder.LITTLE_ENDIAN);
				this.generateRawData();
				this.ChunkSize = 4+(8+SubChunk1Size)+(8+SubChunk2Size);
				// this.ChunkSize = 36 + this.SubChunk2Size;
				// dos.writeByte("A");
				dos.writeBytes(this.ChunkID);
				dos.writeInt(this.ChunkSize);
				dos.writeBytes(this.Format);
				dos.writeBytes(this.SubChunk1ID);
				dos.writeInt(this.SubChunk1Size);
				dos.writeShort(this.AudioFormat);
				dos.writeShort(this.NumChannels);
				dos.writeInt(this.SampleRate);
				dos.writeInt(this.ByteRate);
				dos.writeShort(this.BlockAlign);
				dos.writeShort(this.BitesPerSample);
				dos.writeBytes(this.SubChunk2ID);
				dos.writeInt(this.SubChunk2Size);

				int i = 0;
				for (i = 0; i < this.SampleCount; i++) {
					dos.writeDouble(this.RawData[i]);
				}
				// a.print("sizeeeeeeeee:"+this.ChunkSize);

			} catch (IOException exp) {
				System.err.println("File output stream error : " + exp.getMessage());
			}

		} catch (IOException exp) {
			System.err.println("Creat new file error : " + exp.getMessage());
		}
	}

	private void generateRawData() {
		int frequency = 440, seconds = 1, amplitude = 128, i = 0;
		int NumSamples = seconds * this.SampleRate;
		this.RawData = new int[NumSamples];
		try {
			for (i = 0; i < NumSamples; i++) {
				this.RawData[i] =(int)( amplitude * Math.sin(2 * Math.PI * i * frequency / this.SampleRate));
				// System.out.println(this.RawData[i]);
			}
			this.SampleCount = NumSamples;
			this.SubChunk2Size = NumSamples * this.NumChannels * (this.BitesPerSample / 8);
			// this.SubChunk2Size=RawData.length*8;
		} catch (NullPointerException exp) {
			System.err.println("Raw data generation error : " + exp.getMessage());
		}
	}
}
