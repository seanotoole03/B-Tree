import java.io.File;
import java.io.RandomAccessFile;
import java.util.Scanner;

/**
 * This class searches in a specified BTree
 * for sequences for given length
 * 
 * @author angelsanabria
 *
 */
public class GeneBankSearch 
{
	private static boolean useCache = false;
	private static String btreeFile; 
	private static String queryFile;
	private static int cacheSize; 
	private static int debugLevel = 0;

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
			useCache = true; //use BTree with cache
		}
		
		else if (!(args[0].equals("0") || args[0].equals("1"))) 
		{
			printUsage();
		}

		btreeFile = args[1]; //BTree File
		queryFile = args[2]; //Query File

		//This if statement checks the size of the cache if there are at least 4 arguments
		if (useCache && args.length >= 4) 
		{
			cacheSize = Integer.parseInt(args[3]);
		}

		//Set debug level if there are 5 arguments
		if(args.length == 5)
			debugLevel = Integer.parseInt(args[4]);

		//find degree and sequence length
		String seq = "", deg = "";

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
		
		try {
			SequenceReader gbc = new SequenceReader();
			BTree tree = new BTree(degree, btreeFile, useCache, cacheSize);
			Scanner scan = new Scanner(new File(queryFile));
			
			while(scan.hasNext()) {
				String query = scan.nextLine(); //sequence to search for
				
				long q = gbc.readSubSequence((RandomAccessFile)query); // trying to read the sequence of the frequency of the object 
				TreeObject result = tree.search(tree.readNode(sequence), q); //result should have key and frequency
				
				if(result != null) 
					System.out.println(Integer.parseInt(seq)+": "+ result.getFrequency());
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
