//written by Stephen Richardson

import java.io.*;
import java.util.ArrayList;

public class SequenceReader {
	
	private static File fileRead = new File("test5.gbk");
	private static ArrayList<Long> subsequences = new ArrayList<Long>();
	private static int k = 10;
	private static String subsequence = "";
	
	
	public SequenceReader(String fileName, int seqLength) {
		fileRead = new File(fileName);
		subsequences = new ArrayList<Long>();
		k = seqLength;
		subsequence = "";
	}
	
	public ArrayList<Long> getSubsequences(){
		return subsequences;
	}
	
	public void sequenceReader(RandomAccessFile fileReader) throws IOException
	{
		boolean originFound = false;
		
		//look for the ORIGIN line
		while (fileReader.getFilePointer() < fileReader.length() && originFound == false) 
		{
			String fileLine = fileReader.readLine();
			//System.out.println(fileLine);
			if (fileLine.contains("ORIGIN"))
			{
				System.out.println("Origin found");
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
			if (currChar.equalsIgnoreCase("a"))
			{
				numValidReads++;
				subsequence += "00";
			}
			else if (currChar.equalsIgnoreCase("t"))
			{
				numValidReads++;
				subsequence += "11";
			}
			else if (currChar.equalsIgnoreCase("c"))
			{
				numValidReads++;
				subsequence += "01";						
			}
			else if (currChar.equalsIgnoreCase("g"))
			{
				numValidReads++;
				subsequence += "10";
			}
			//if n found,clear subsequence, find next valid value and return
			else if(currChar.equalsIgnoreCase("n"))
			{
				subsequence = "";
				
				while (currChar.equalsIgnoreCase("n"))
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
		//System.out.println(subsequence);
		
		return currChar;
	}

	public static void main(String[] args) 
	{
		SequenceReader seqR = new SequenceReader("test5.gbk", 10);
		//USE IN CreateBTree
		try 
		{
			RandomAccessFile fileReader = new RandomAccessFile(fileRead, "r");
			seqR.sequenceReader(fileReader);
			
		} catch (FileNotFoundException e) {
			System.out.println("File not found.");
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("IO Error.");
			System.exit(0);
		}
		
		/**
		for(int i = 0;i < subsequences.size();i++)
		{
			String lbs = Long.toBinaryString(subsequences.get(i));
			for(int j = lbs.length(); j < 20; j++) {
				lbs = "0" + lbs;
			}
			System.out.println(lbs);
		}
		*/
	}
	

}