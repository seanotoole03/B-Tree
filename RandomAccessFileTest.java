import java.io.*;

public class RandomAccessFileTest {
	
	private int k = 0;
	private File testFile;
	private long longLong = Long.parseLong("0010110001011011110010010011010101101010101010101000101010111101", 2), 
			shortLong = Long.parseLong("001000", 2);

	public RandomAccessFileTest() {
		
		testFile = new File("testFile");
		try {
			
			/*
			 * Setting up file reader and writer, placing data in file.
			 */
			RandomAccessFile fileWrite = new RandomAccessFile(testFile, "rw");
			RandomAccessFile fileRead = new RandomAccessFile(testFile, "rw");
			
			fileWrite.writeLong(longLong);
			fileWrite.writeLong(shortLong);
			
			
			/*
			 * Base Usage - RandomAccessFile.readLong(): returns single long literal as decimal string
			 */
			System.out.println("Base usage of readLong()");
			System.out.println(fileRead.readLong() + "\t" + fileRead.readLong() + "\n\n");
			fileRead.seek(0);
			
			/*
			 * Combined Usage - RandomAccessFile.readLong() & Long.toBinaryString: returns single long literal as binary string
			 */
			System.out.println("Usage of readLong() combined with Long.toBinaryString()");
			String longLongString = Long.toBinaryString(fileRead.readLong());
			String shortLongString = Long.toBinaryString(fileRead.readLong());
			System.out.println(longLongString + "\t" + shortLongString + "\n\n");
			fileRead.seek(0);
			
			/*
			 * Modified Combined Usage - RandomAccessFile.readLong() & Long.toBinaryString, with 0-append loop: returns single long literal as binary string, with 0's appended to 64 bits
			 */
			System.out.println("Usage of readLong() combined with Long.toBinaryString(), and a loop to append 0's until 64-bit size.");
			String longLongStringBinary = Long.toBinaryString(fileRead.readLong());
			if(longLongStringBinary.length() < 64) {
				while(longLongStringBinary.length() < 64) {
					longLongStringBinary = "0" + longLongStringBinary;
				}
			}
			String shortLongStringBinary = Long.toBinaryString(fileRead.readLong());
			if(shortLongStringBinary.length() < 64) {
				while(shortLongStringBinary.length() < 64) {
					shortLongStringBinary = "0" + shortLongStringBinary;
				}
			}
			System.out.println(longLongStringBinary + "\t" + shortLongStringBinary + "\n\n");
			fileRead.seek(0);
			
			/*
			 * Base Usage - RandomAccessFile.readBytes(): unpredictable and unusual behavior, though perhaps my implementation is flawed
			 */
			System.out.println("Usage of readByte() with loop for 8 bytes, and single readByte() for shorter val. Confusing and incorrect output.");
			String val = "";
			for(int i = 0; i < 8; i++) {
				val += Byte.toString(fileRead.readByte());
			}
			String longLongStringFromBytes = val;
			String shortLongStringFromBytes = Byte.toString(fileRead.readByte());
			System.out.println(longLongStringFromBytes + "\t" + shortLongStringFromBytes);
			fileRead.seek(0);
			
			
			fileWrite.close();
			fileRead.close();
			
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
