import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Scanner;


/**
 * This is a driver Class for searching a GeneBank generated BTree for
 * a list of query sequences.
 * 
 * @author angelsanabria
 *
 */
public class GeneBankSearch 
{
	private static int useCache;
	private static String btreeFile; 
	private static String queryFile;
	private static String subSequences;
	private static int cacheSize; 
	private static int debugLevel = 0;
	private static TreeObject object;
	private static long searchLong;

	public static void main(String[] args) 
	{
		
		//This if statement prints the usage if there are too little 
		//or too many arguments
		if(args.length < 3 || args.length > 5) 
		{
			printUsage();
		}

		// This if statement determines whether to use a cache or not
		if (args[0].equals("1")) 
		{
			useCache = 1; //use BTree with cache
		}
		
		else if (!(args[0].equals("0") || args[0].equals("1"))) 
		{
			printUsage();
		}
		
		
		btreeFile = args[1]; //BTree File
		queryFile = args[2]; //Query File 

		//This if statement checks the size of the cache if there are at least 4 arguments
		if (useCache == 1 && args.length >= 4) 
		{
			cacheSize = Integer.parseInt(args[3]);
		}

		//Set debug level if there are 5 arguments
		if(args.length == 5)
			debugLevel = Integer.parseInt(args[4]);

		//find degree and sequence length
		String seq = "";
		String deg = "";

		//finds the degree of the btree file
		for(int i = btreeFile.length()-1; i >= 0; i--) 
		{
			if(btreeFile.charAt(i) != '.')
				deg += btreeFile.charAt(i);
			else break;
		}
		deg = reverseString(deg);

		//finds the sequence length of the btree file
		for (int i = btreeFile.length()-deg.length()-2; i >= 0; i--) 
		{
			if(btreeFile.charAt(i) != '.')
				seq += btreeFile.charAt(i);
			else break;
		}
		seq = reverseString(seq);

		int degree = Integer.parseInt(deg);
		int sequence = Integer.parseInt(seq);
		//System.out.println("degree: " + degree);
		//System.out.println("sequence length: " + sequence);
		
		subSequences = "";
		//searching in the specified BTree for sequences of given length. The search program
		//assumes that the user specified the proper BTree to use depending upon the query length.
		try {
			File treeFile = new File(btreeFile);
			if(treeFile.createNewFile()) {
				System.out.println("Error, no such file!");
				System.exit(0);
			}
			RandomAccessFile read = new RandomAccessFile(treeFile, "r");
			BTree<TreeObject> tree = new BTree<TreeObject>(degree, btreeFile, queryFile, useCache, cacheSize);
			read.seek(4); //position of rootOffset metadata
			tree.getRoot().setOffset(read.readInt());
			read.seek(tree.getRoot().getOffset());
			tree.setRoot(tree.readNode(tree.getRoot().getOffset()));
			read.seek(0);
			
			
			Scanner scan = new Scanner(new File(queryFile));
			
			while(scan.hasNext()) {
				subSequences = "";
				String query = scan.nextLine(); //sequence to search for binary 
				String currString = "";
				int numValidReads = 0;
				do 
				{
//					String.valueOf(currChar = fileReader.readChar());
//					currChar = fileReader.readChar();
					String currChar = query.substring(numValidReads, numValidReads+1);
					currString += currChar;
					//if good character found convert to binary and save in subsequence string
					if (currChar.equalsIgnoreCase("a"))
					{
						numValidReads++;
						subSequences += "00";
					}
					else if (currChar.equalsIgnoreCase("t"))
					{
						numValidReads++;
						subSequences += "11";
					}
					else if (currChar.equalsIgnoreCase("c"))
					{
						numValidReads++;
						subSequences += "01";						
					}
					else if (currChar.equalsIgnoreCase("g"))
					{
						numValidReads++;
						subSequences += "10";
					}
					//if n found,clear subsequence, find next valid value and return
					else if(currChar.equalsIgnoreCase("n"))
					{
						subSequences = "";

					}

				}while (numValidReads < sequence);
				
				if(!subSequences.isEmpty()) 
					searchLong = Long.parseLong(subSequences, 2);
				
				TreeObject found = tree.search(tree.getRoot(), searchLong);
				
				if(found != null) {
					System.out.println(currString + ": " + found.getFrequency());
				} else {
					System.out.println(currString + ": " + 0);
				}
			}
			
			
			
			scan.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * recursively reverses a string
	 * 
	 * @param s - string to reverse
	 * @return reversed string
	 */
	private static String reverseString(String s) 
	{
		if(s.length() == 1)
			return s;
		return "" + s.charAt(s.length() - 1) + reverseString(s.substring(0, s.length() - 1));
	}

	/**
	 * prints the usage of the program
	 */
	private static void printUsage() 
	{
		System.err.println("Usage: java GeneBankSearch "
				+ "<0/1(no/with Cache)> <btree file> <query file> "
				+ "[<cache size>] [<debug level>]\n");
		System.exit(1); 
	}
}
