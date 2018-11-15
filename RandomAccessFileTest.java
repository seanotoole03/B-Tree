import java.io.*;

public class RandomAccessFileTest {
	
	private File testFile;
	private long longLong = Long.parseLong("0010110001011011110010010011010101101010101010101000101010111101", 2), 
			shortLong = Long.parseLong("001000", 2);

	public RandomAccessFileTest() {
		testFile = new File("testFile");
		try {
			RandomAccessFile fileWrite = new RandomAccessFile(testFile, "rw");
			RandomAccessFile fileRead = new RandomAccessFile(testFile, "rw");
			
			fileWrite.writeLong(longLong);
			fileWrite.writeLong(shortLong);
			
			System.out.println(fileRead.readLong() + "\t" + fileRead.readLong());
			fileRead.seek(0);
			
			String longLongString = Long.toBinaryString(fileRead.readLong());
			String shortLongString = Long.toBinaryString(fileRead.readLong());
			System.out.println(longLongString + "\t" + shortLongString);
			fileRead.seek(0);
			
			String val = "";
			for(int i = 0; i < 8; i++) {
				val += Byte.toString(fileRead.readByte());
			}
			String longLongStringFromBytes = val;
			String shortLongStringFromBytes = Byte.toString(fileRead.readByte());
			System.out.println(longLongStringFromBytes + "\t" + shortLongStringFromBytes);
			fileRead.seek(0);
			
		} catch (FileNotFoundException e) {
			System.out.println("File not found.");
			System.exit(0);
		} catch (IOException e) {
			System.out.println("IO Error.");
			System.exit(0);
		}
	}
	
	public static void main(String[] args) {
		RandomAccessFileTest test = new RandomAccessFileTest();
	}

}
