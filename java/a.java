import java.io.DataOutputStream;
// import java.io.file
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.ByteBuffer;

class a {

	public static void main(String[] args) {
		wav obj = new wav("out.wav");
		obj.generate();
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

	/*size offset endian*/
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
	public int[] RawData;
	

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
		this.SampleRate = 40;
		this.BitsPerSample = 8*2;
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
				ByteBuffer buffer4 = ByteBuffer.allocate(this.SubChunk2Size * 8);
				buffer4.order(ByteOrder.LITTLE_ENDIAN);
				for (i = 0; i < this.SampleCount; i++) {
					// a.print(i*8);
					buffer4.putInt(this.RawData[i]);
					a.print(this.RawData[i]);
					// dos.writeDouble(this.RawData[i]);
				}
					for (i = 0; i < buffer4.capacity(); i++) {
						dos.writeByte(buffer4.get(i));
					
			



			} catch (IOException exp) {
				System.err.println("File output stream error : " + exp.getMessage());
			}

		} catch (IOException exp) {
			System.err.println("Creat new file error : " + exp.getMessage());
		}
	}

	private void generateRawData() {
		int frequency = 10, seconds = 1, amplitude = 10, i = 0;
		int NumSamples = seconds * this.SampleRate;
		this.SampleCount = NumSamples;
		int cycles=frequency*seconds;
		this.RawData = new int[NumSamples];
		a.print("Cycle:: "+cycles);
		a.print("Frequency:: "+frequency);
		a.print("Amplitude:: "+amplitude);
		a.print("Sampling rate:: "+SampleRate+"Hz");
		a.print("byte/Sample :: "+(BitsPerSample/8)+"bytes");
		try {
			// FileWriter fout=new FileWriter("out.log");
			for (i = 0; i < NumSamples; i++) {
				// this.RawData[i]=15.9; //10250
				// if (i%5==0) {
				// 	this.RawData[i]=amplitude*Math.sin(2*3.14*frequency); //10250
				// }else if(i%3==0){
				// 	this.RawData[i]=111.9; //10250
				// }else{
				// 	this.RawData[i]=-11.123123; //10250
				// }
				// this.RawData[i] =(int)(amplitude*Math.sin(frequency * 2 * Math.PI ));
				this.RawData[i] = (int) (amplitude * Math.sin(i));
				// System.out.println(this.RawData[i]);
				// fout.write(i+","+ this.RawData[i]+"\n");
			}
			// fout.close();
			this.SubChunk2Size = NumSamples * this.NumChannels * (this.BitsPerSample / 8);
			// a.print(SubChunk2Size);
			// this.SubChunk2Size=RawData.length*8;
		} catch (NullPointerException exp) {
			System.err.println("Raw data generation error : " + exp.getMessage());
		}
		 catch (Exception exp) {
			System.err.println(exp.getMessage());
		}
	}
}
