//written by Stephen Richardson

import java.io.*;
import java.util.ArrayList;

public class SequenceReader {
	
	private static File fileRead = new File("test1.gbk");
	public static ArrayList<Long> subsequences = new ArrayList<Long>();
	private static int k = 10;
	private static String subsequence = "";
	
	public static void sequenceReader(RandomAccessFile fileReader) throws IOException
	{
		boolean originFound = false;
		
		//look for the ORIGIN line
		while (fileReader.getFilePointer() < fileReader.length() && originFound == false) 
		{
			String fileLine = fileReader.readLine();
			System.out.println(fileLine);
			if (fileLine.contains("ORIGIN"))
			{
				originFound = true;
			}
		}
		//if ORIGIN not found, exit the class
		if (fileReader.getFilePointer() == fileReader.length())
		{
			System.out.println("Origin not found");
			return;
		} 
		String nextChar = "";
		do {
			nextChar = readSubSequence(fileReader);
		} while (!nextChar.equals("/"));
		
		//if the file isn't empty, recursively call itself
		if (fileReader.getFilePointer() < fileReader.length())
		{
			sequenceReader(fileReader);
		}
		
	}
	
	//reads chars to create subsequences, then adds valid subsequences to arraylist. if n or / are hit before a subsequence
	//is complete then the subsequence is cleared without being added to the arraylist
	private static String readSubSequence(RandomAccessFile fileReader) throws IOException 
	{
		int numValidReads = 0;
		String currChar = "";
		long longSub = 0;
		
		//if subsequence String is not empty, lop off the first two chars and find how values are in there
		if (!subsequence.equals("") && !subsequence.equals(" "))
		{
			subsequence = subsequence.substring(2);
			numValidReads = subsequence.length() / 2;
		}
		
		do 
		{
//			String.valueOf(currChar = fileReader.readChar());
//			currChar = fileReader.readChar();
			currChar = Character.toString((char) fileReader.readByte());
			//if good character found convert to binary and save in subsequence string
			if (currChar.equals("a"))
			{
				numValidReads++;
				subsequence += "00";
			}
			else if (currChar.equals("t"))
			{
				numValidReads++;
				subsequence += "11";
			}
			else if (currChar.equals("c"))
			{
				numValidReads++;
				subsequence += "01";						
			}
			else if (currChar.equals("g"))
			{
				numValidReads++;
				subsequence += "10";
			}
			//if n found,clear subsequence, find next valid value and return
			else if(currChar.equals("n"))
			{
				subsequence = "";
				
				while (currChar.equals("n"))
				{
					currChar = Byte.toString(fileReader.readByte());;
				}
				
				//pointer should now be one byte past the next valid character, so back the pointer up one character
				fileReader.seek(fileReader.getFilePointer() - 1);
				return currChar;
			}
			//the only other possible character is /, so if that is found, clear subsequence and exit
			else if (currChar.equals("/"))
			{
				subsequence = "";
				return currChar;
			}
			
		}while (numValidReads < k);
		
		//subsequence should have 2k characters, all ones and zeros. convert into long and add to arrayList
		longSub = Long.parseLong(subsequence,2);
		subsequences.add(longSub);
		System.out.println(subsequence);
		
		return currChar;
	}

	public static void main(String[] args) 
	{
		try 
		{
			RandomAccessFile fileReader = new RandomAccessFile(fileRead, "r");
			sequenceReader(fileReader);
			
		} catch (FileNotFoundException e) {
			System.out.println("File not found.");
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("IO Error.");
			System.exit(0);
		}
		
		for(int i = 0;i < subsequences.size();i++)
		{
			System.out.println(subsequences.get(i));
		}
	}
	

}
