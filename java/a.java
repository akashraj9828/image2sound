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
		wav obj = new wav("out.wav");
		obj.generate();
		// obj.generateRawData();
		print(obj);
	}

	static void print(wav o) {
		File f = new File("out.wav");
		print("*******");
		print("p:" + o.ChunkSize);
		print("f:" + f.length());
		// print();
		// print();
		// print();
		// print();
	}

	static void print(Object o) {
		System.out.println(o);
	}
}

class wav {
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

	public wav(String a) {
		this.fileName = a;
		// 0
		this.ChunkID = "RIFF";
		// 4
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
				// int x=RawData.length;
				// a.print("RawLen: "+x);
				ByteBuffer buffer4 = ByteBuffer.allocate(RawData.length * 2);
				buffer4.order(ByteOrder.LITTLE_ENDIAN);
				a.print("Bufflen: " + buffer4.capacity());
				for (i = 0; i < this.SampleCount; i++) {
					// a.print(i*8);
					// buffer4.putInt(this.RawData[i]);
					buffer4.putShort(this.RawData[i]);
					// a.print(this.RawData[i]);
					// dos.writeByte(buffer4.get(i * 4));
					// a.print(this.RawData[i]+": "+ buffer4.get(i * 8)+" ,"+ buffer4.get(i * 8+1));
					// a.print(this.RawData[i]+": "+ buffer4.get(i * 4)+" ,"+ buffer4.get(i *
					// 4+1)+","+ buffer4.get(i * 4+2)+","+ buffer4.get(i * 4+3)+","+ buffer4.get(i *
					// 4+4));
					// dos.writeDouble(this.RawData[i]);
				}

				FileWriter fout = new FileWriter("out.log");
				// int count = 0;
				for (i = 0; i < buffer4.capacity(); i++) {
					dos.writeByte(buffer4.get(i));
					// if (i % 4 == 0) {
					// count++;
					// fout.write("\n" + count + ": ");
					// } else {
					// fout.write(buffer4.get(i) + ",");

					// }
					// fout.write(i+" "+buffer4.get(i*4) + "\n");

				}
				fout.close();

			} catch (IOException exp) {
				System.err.println("File output stream error : " + exp.getMessage());
			}

		} catch (IOException exp) {
			System.err.println("Creat new file error : " + exp.getMessage());
		}
	}
	//amp ramge 3000-
	public void generateRawData() {
		int frequency = 6000, seconds = 2, amplitude = 4000, i = 0;
		// double omega = 2 * Math.PI * frequency;
		int NumSamples = seconds * this.SampleRate;
		this.SampleCount = NumSamples;
		double gap = (((double) (frequency) * (double) 360 / (double) 44100));
		// a.print("gap: "+gap);
		// a.print("gap: "+ );
		int cycles = frequency * seconds;
		RawData = new short[NumSamples];
		a.print("Cycle:: " + cycles);
		a.print("Frequency:: " + frequency);
		a.print("Amplitude:: " + amplitude);
		a.print("Sampling rate:: " + SampleRate + "Hz");
		a.print("byte/Sample :: " + (BitsPerSample / 8) + "bytes");
		try {
			int count = 0;
			// FileWriter fout=new FileWriter("out.log");
			for (i = 0; i < NumSamples; i++) {
				gap = (((double) (frequency) * (double) 360 / (double) 44100));
				// double angle=omega*(SampleRate%(i+1));
				// double angle =Math.toRadians(90);
				double angle = Math.toRadians(gap * i);
				// this.RawData[i] =(int)(amplitude*Math.sin(frequency * 2 * Math.PI ));
				// this.RawData[i] = (short) (((amplitude + (i * 1)) * Math.sin((angle))));
				this.RawData[i] = (short) (amplitude * Math.sin((angle)));

				if (i < SampleRate)
					if ((RawData[i] < 0 && RawData[i - 1] > 0) || (RawData[i] > 0 && RawData[i - 1] < 0))
						count++;

				// a.print(
				// "sin("
				// +Math.toDegrees(angle)+") ="
				// // +":sin(" +(angle) + ")="
				// + Math.sin((angle)));

				// System.out.println(this.RawData[i]);
				// System.out.println(( amplitude*Math.sin((frequency * 2 * 3.14*i))));
				// System.out.println((frequency * 2 * Math.PI)%180);
				// fout.write(i+","+ this.RawData[i]+"\n");
			}
			// fout.close();
			this.SubChunk2Size = NumSamples * this.NumChannels * (this.BitsPerSample / 8);
			a.print("frequncy:::::::::::: " + count / (2));
			// a.print(SubChunk2Size);
			// this.SubChunk2Size=RawData.length*8;
		} catch (NullPointerException exp) {
			System.err.println("Raw data generation error : " + exp.getMessage());
		} catch (Exception exp) {
			System.err.println(exp.getMessage());
		}
	}
}
