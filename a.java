import java.io.DataOutputStream;
// import java.io.file
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.ByteBuffer;

class a {

	public static void main(String[] args) {
		// print("5-255"+wav.map(5,1,10,1,100));
		wav_file obj = new wav_file("out.wav");
		obj.generate();
		print(obj);
	}

	static void print(wav_file o) {
		File f = new File("out.wav");
		print("*******");
		print("p:" + o.ChunkSize);
		print("f:" + f.length());
	}

	static void print(Object o) {
		System.out.println(o);
	}
}

class wav_file {
	public String fileName;
	public int SampleCount;

	/* size offset endian */
	// 4 0 big endian
	public String ChunkID;
	// 4 4 little endian
	public int ChunkSize;
	// 4 8 big endian
	public String Format;
	// 4 12 big endian
	public String SubChunk1ID;
	// 4 16 little endian
	public int SubChunk1Size;
	// 2 20 little endian
	public short AudioFormat;
	// 2 22 little endian
	public short NumChannels;
	// 4 24 little endian
	public int SampleRate;
	// 4 28 little endian
	public int ByteRate;
	// 2 32 little endian
	public short BlockAlign;
	// 2 34 little endian
	public short BitsPerSample;
	// 4 36 big endian
	public String SubChunk2ID;
	// 4 40 little endian
	public int SubChunk2Size;
	// unlimited 44 little endian
	public short[] RawData;

	public wav_file(String a) {
		this.fileName = a;
		
		this.ChunkID = "RIFF";
		this.ChunkSize = 0;
		this.Format = "WAVE";
		this.SubChunk1ID = "fmt ";
		this.SubChunk1Size = 16;
		this.AudioFormat = 1;
		this.NumChannels = 1;
		this.SampleRate = 44100;
		this.BitsPerSample = 8 * 2;
		this.ByteRate = this.SampleRate * this.NumChannels * (this.BitsPerSample / 8);
		this.BlockAlign = (short) (this.NumChannels * this.BitsPerSample / 8);
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

				this.generateRawData();
				this.ChunkSize = 4 + (8 + SubChunk1Size) + (8 + SubChunk2Size);

				// big endian
				dos.writeBytes(this.ChunkID);

				// little endian
				ByteBuffer buffer1 = ByteBuffer.allocate(4);
				buffer1.order(ByteOrder.LITTLE_ENDIAN);
				buffer1.putInt(this.ChunkSize);
				for (int i = 0; i < buffer1.capacity(); i++) {
					dos.writeByte(buffer1.get(i));
				}

				// big endian
				dos.writeBytes(this.Format);
				dos.writeBytes(this.SubChunk1ID);

				// little endian
				ByteBuffer buffer2 = ByteBuffer.allocate(20);
				buffer2.order(ByteOrder.LITTLE_ENDIAN);
				buffer2.putInt(this.SubChunk1Size);
				buffer2.putShort(this.AudioFormat);
				buffer2.putShort(this.NumChannels);
				buffer2.putInt(this.SampleRate);
				buffer2.putInt(this.ByteRate);
				buffer2.putShort(this.BlockAlign);
				buffer2.putShort(this.BitsPerSample);

				for (int i = 0; i < buffer2.capacity(); i++) {
					dos.writeByte(buffer2.get(i));
				}

				// big endian
				dos.writeBytes(this.SubChunk2ID);

				// little endian
				ByteBuffer buffer3 = ByteBuffer.allocate(4);
				buffer3.order(ByteOrder.LITTLE_ENDIAN);
				buffer3.putInt(this.SubChunk2Size);
				for (int i = 0; i < buffer3.capacity(); i++) {
					dos.writeByte(buffer3.get(i));
				}

				// little endian
				int i = 0;
				ByteBuffer buffer4 = ByteBuffer.allocate(RawData.length * 2);
				buffer4.order(ByteOrder.LITTLE_ENDIAN);
				a.print("Bufflen: " + buffer4.capacity());
				for (i = 0; i < this.SampleCount; i++) {
					buffer4.putShort(this.RawData[i]);
					// a.print(this.RawData[i]);
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
	//amp ramge 32767 - -32767
	public void generateRawData() {
		int frequency = 10000, seconds = 1, amplitude = 32767, i = 0;
		// double omega = 2 * Math.PI * frequency;
		int NumSamples = seconds * this.SampleRate;
		this.SampleCount = NumSamples;
		double gap = (((double) (frequency) * (double) 360 / (double) SampleRate));
		// a.print("gap: "+gap);
		int cycles = frequency * seconds;
		RawData = new short[NumSamples];
		a.print("Cycle:: " + cycles);
		a.print("Frequency:: " + frequency);
		a.print("Amplitude:: " + amplitude);
		a.print("Sampling rate:: " + SampleRate + "Hz");
		a.print("byte/Sample :: " + (BitsPerSample / 8) + "bytes");
		try {
			int count = 0;
				// this.RawData[0] = (short)32760;
				// this.RawData[1] = (short)-32767;
				// this.RawData[2] = (short)30000;
				// this.RawData[3] = (short)-20000;
				// this.RawData[4] = (short)10000;
			
			// FileWriter fout=new FileWriter("out.log");
			for (i = 0; i < NumSamples; i++) {
				// gap = (((double) (frequency+i*0.2) * (double) 360 / (double) SampleRate));
				// // double angle=omega*(SampleRate%(i+1));
				// // double angle =Math.toRadians(90);
				double angle = Math.toRadians(gap * i);
				this.RawData[i] = (short) (amplitude * Math.sin((angle)));
				// this.RawData[i] =(int)(amplitude*Math.sin(frequency * 2 * Math.PI ));
				// // this.RawData[i] = (short) (((amplitude + (i * 1)) * Math.sin((angle))));

				if (i < SampleRate)
					if ((RawData[i] < 0 && RawData[i - 1] > 0) || (RawData[i] > 0 && RawData[i - 1] < 0))
						count++;

				// a.print(
				// "sin("
				// +Math.toDegrees(angle)+") ="
				// // +":sin(" +(angle) + ")="
				// + Math.sin((angle)));

				// System.out.println(i+": "+this.RawData[i]);
				// System.out.println(( amplitude*Math.sin((frequency * 2 * 3.14*i))));
				// System.out.println((frequency * 2 * Math.PI)%180);
				// fout.write(i+","+ this.RawData[i]+"\n");
			}
			// fout.close();
			this.SubChunk2Size = NumSamples * this.NumChannels * (this.BitsPerSample / 8);
			// a.print("frequncy:::::::::::: " + count / (2));
			// a.print(SubChunk2Size);
			// this.SubChunk2Size=RawData.length*8;
		} catch (NullPointerException exp) {
			System.err.println("Raw data generation error : " + exp.getMessage());
		} catch (Exception exp) {
			System.err.println(exp.getMessage());
		}
	}

	public static double map(double n,double  start1,double  stop1,double  start2,double  stop2) {
		double val=((n - start1) / (stop1 - start1)) * (stop2 - start2) + start2;
		return val;
	}
}
