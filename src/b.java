import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

class b {
	public static void main(String[] args) {
		wav obj = new wav("test.wav");
		obj.generate();
	}

}

class wav {
	private String fileName;
	private String ChunkID;
	private int ChunkSize;
	private String Format;
	private String SubChunk1ID;
	private int SubChunk1Size;
	private int AudioFormat;
	private int NumChannels;
	private int SampleRate;
	private int ByteRate;
	private int BlockAlign;
	private int BitesPerSample;
	private String SubChunk2ID;
	private int SubChunk2Size;
	private int SampleCount;
	private double[] RawData;

	public wav(String a) {
		this.fileName = a;
		this.ChunkID = "RIFF";
		this.ChunkSize = 0;
		this.Format = "WAVE";
		this.SubChunk1ID = "fmt ";
		this.SubChunk1Size = 16;
		this.AudioFormat = 1;
		this.NumChannels = 1;
		this.SampleRate = 44100;
		this.BitesPerSample = 16;
		this.ByteRate = this.SampleRate * this.NumChannels * this.BitesPerSample / 8;
		this.BlockAlign = this.NumChannels * this.BitesPerSample / 8;
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
				this.ChunkSize = 36 + this.SubChunk2Size;

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

			} catch (IOException exp) {
				System.err.println("File output stream error : " + exp.getMessage());
			}

		} catch (IOException exp) {
			System.err.println("Creat new file error : " + exp.getMessage());
		}
	}

	private void generateRawData() {
		int frequency = 440, seconds = 1, amplitude = 128, i = 0;
		int dimension = seconds * this.SampleRate;
		this.RawData = new double[dimension];
		try {
			for (i = 0; i < dimension; i++) {
				this.RawData[i] = amplitude * Math.sin(2 * Math.PI * i * frequency / this.SampleRate);
				System.out.println(this.RawData[i]);
			}
			this.SampleCount = i;
			this.SubChunk2Size = i * this.NumChannels * this.BitesPerSample / 8;
		} catch (NullPointerException exp) {
			System.err.println("Raw data generation error : " + exp.getMessage());
		}
	}
}

