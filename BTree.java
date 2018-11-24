import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import javax.xml.soap.Node;

/**
 * This class
 * 
 * @author angelsanabria , seanotoole
 *
 */
public class BTree<T extends Comparable>
{
	private BTreeNode root, parent, current;
	private int degree;			//position 0 of file
	private int BTreeNodeSize, nodeMaxObj;	
	private int rootOffset;		//position 4 of file
	private int insert;
	private int nodeCount; // number of nodes in tree, position 8 of file
	private int height; // height of the BTree, position 12 of file
	//TOTAL BTREE METADATA OFFSET = 16
	private int baseOffset = 16;
	private File file;
    private RandomAccessFile fileRead, fileWrite;
   
    /**
     * Constructor for BTree - includes cache usage.
     * 
     * @param degree - indicates desired size of nodes
     * @param fileName - file to be written to, default will be 'file' for testing and specified filename format 
     * @param useCache - indicates whether the user would like a cache to be used for easier manipulation of frequently used data
     * @param cacheSize - desired size of cache (if used)
     */
    public BTree(int degree, String fileName, boolean useCache)
    {
        BTreeNodeSize = 16 + (24*(degree)) - 4; //bytes
        nodeMaxObj = (2*degree)-1;
        rootOffset = 16;
        insert = 0;
        nodeCount = 1;
        this.degree = degree;  
        
        file = new File(fileName);
        try { 	//Initialize RandcomAccessFile reader and writer
        	fileRead = new RandomAccessFile(file, "r");
        	fileWrite = new RandomAccessFile(file, "rw");
        } catch (FileNotFoundException e) {
        	e.printStackTrace();
        }
        
        try {	//Metadata writing
			fileWrite.writeInt(degree);
			fileWrite.writeInt(rootOffset);
			fileWrite.writeInt(nodeCount);
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        root = new BTreeNode();
        
        //TODO: Implement cache functionality
        
        
        
    }
    
	/**
	 * Inserting a long (binary string) into the B-Tree as a TreeObject or by incrementing an existing TreeObject
	 * 
	 * @param k - long to be inserted into tree, either as a new TreeObject, or by incrementing the frequency of an existing TreeObject.
	 */
	public void insert(long k)
	{
	    BTreeNode r = root;
	    current = r;
        TreeObject obj = new TreeObject(k);
        boolean insertion = false;
        while(insertion == false) {
        	if(current.getN() == nodeMaxObj) { //current node is full
    			//TODO: Split node and continue insertion loop afterwards - maybe directly use continue() command to skip rest of current loop?
        		
        		
    		}
        	int i = current.getN(); //retrieve number of objects in current node
	        while (i > 0 && obj.compareTo(current.getKey(i-1)) > 0) //search backwards to find first smaller object
	        {
	            i--; //should bring us to either 
	        }
	 
	        if (i > 0 && obj.compareTo(current.getKey(i-1)) == 0) //found exact sequence in current node
	        {
	            r.getKey(i-1).increaseFrequency(); //increment frequency of sequence, rather than create duplicates
	            insertion = true;
	        }
	        
	        else 	//either i == 0, indicating this is smaller than anything in the array, or r.getKey(i-1) < obj.g 
	        {		//either way, we will need to either insert the object in a child node, or in this node, if it is a leaf
	        	if(current.isLeaf == 1) { //safe to add object to
	        		//TODO: addToFront of arraylist of TreeObjects
	        		
	        		
	        	} else { //internal node - we need to find child node to check next, and set that to be the current node
	        		parent = current;
	        		current = readNode(current.getChild(i));
	        	}
	        	
	        	//TODO: What purpose does this serve? Is it still necessary?
	        	if(current == r) {
	        		parent = r;
	        	}
	            BTreeNode s = new BTreeNode();
	            s.setOffset(r.getOffset());
	            s = root;
	            s.setOffset(r.getOffset()+BTreeNodeSize);
	            s.setParent(s.getOffset());
	            s.setIsLeaf(0); //false
	            s.addChild(r.getOffset());
	            splitChild(s,r,0);
	            insertNonfull(s,k);
	        }
        }
	}
	
	 /** TODO: review
	  * Search BTree for specific long/DNA sequence
	  * 
	 * @param x - current node being searched
	 * @param k - long/key object being searched for
	 * @return TreeObject representing the found TreeObject
	 */
	public TreeObject search(BTreeNode x, long k)
	 {
	        int i = 0;
	        TreeObject obj = new TreeObject(k);
	        while (i < x.getN() && (obj.compareTo(x.getKey(i)) > 0))
	        {
	            i++;
	        }
	        
	        if (i < x.getN() && obj.compareTo(x.getKey(i)) == 0)
	        {
	            return x.getKey(i);
	        }
	        
	        if (x.isLeaf() == 1)
	        {
	            return null;
	        }
	        
	        else 
	        {
	            int offset = x.getChild(i);
	            BTreeNode next = new BTreeNode();
	            next = readNode(offset);
	            return search(next,k);
	        }
	    }
	
	/**
	 * 
	 * 
	 * @param x
	 * @param k
	 */
	public void insertNonfull(BTreeNode x, long k)
	   {
		 int i = x.getN();
	        TreeObject obj = new TreeObject(k);
	        if (x.isLeaf() == 0)
	        {
	            if (x.getN() != 0) 
	            {
	                while (i > 0 && obj.compareTo(x.getKey(i-1)) < 0){
	                    i--;
	                }
	            }
	            
	            if (i > 0 && obj.compareTo(x.getKey(i-1)) == 0)
	            {
	                x.getKey(i-1).increaseFrequency();
	            }
	            
	            else 
	            {
	                x.addKey(obj,i);
	                x.setN(x.getN()+1);
	            }
	            writeNode(x,x.getOffset());
	        }
	        
	        else 
	        {
	            while (i > 0 && (obj.compareTo(x.getKey(i-1)) < 0))
	            {
	                i--;
	            }
	            
	            if (i > 0 && obj.compareTo(x.getKey(i-1)) == 0)
	            {
	                x.getKey(i-1).increaseFrequency();
	                writeNode(x,x.getOffset());
	                return;
	            }
	            
	            int offset = x.getChild(i);
	            BTreeNode y = readNode(offset);
	            if (y.getN() == 2 * degree - 1)
	            {
	                int j = y.getN();
	                while (j > 0 && obj.compareTo(y.getKey(j-1)) < 0)
	                {
	                    j--;
	                }
	                
	                if (j > 0 && obj.compareTo(y.getKey(j-1)) == 0)
	                {
	                    y.getKey(j-1).increaseFrequency();
	                    writeNode(y,y.getOffset());
	                    return;
	                }
	                
	                else 
	                {
	                    splitChild(x, y, i);
	                        if (obj.compareTo(x.getKey(i)) > 0)
	                        {
	                            i++;
	                        }
	                }
	            }
	            offset = x.getChild(i);
	            BTreeNode child = readNode(offset);
	            insertNonfull(child,k);
	        }
	   }
	
	
	
	//test a root with two children.  Write  a value into node into memory.
	//Array of key values for the node, create the root, how 
	
	//judge the pointers with the size of node, based on degree
	
	/**
	 * @param x, y - 
	 */
	public void splitChild(BTreeNode x, BTreeNode y, int i)// splitting will be the only time where we add pointers
	 {
	    BTreeNode z = new BTreeNode();	//replacement for original node - new parent
        z.setIsLeaf(y.isLeaf());
        z.setParent(y.getParent());
        z.addKey(y.removeKey(degree)); //grab middle object to serve as first key of new parent
        z.setN(1);
        for (int j = 0; j < degree - 1; j++)
        {
          
            //z.setN(z.getN()+1); 
            //y.setN(y.getN()-1); 

        }
        
        if (!(y.isLeaf() == 1)) //we're splitting an internal node
        {
            for (int j = 0; j < degree; j++)
            {
                z.addChild(y.removeChild(degree));
            }
        }
        
        x.addKey(y.removeKey(degree - 1), i);
        x.setN(x.getN()+1);
        y.setN(y.getN()-1);
        if (x == root && x.getN() == 1)
        {
            writeNode(y,insert); //How do I read and write a node from binary
            insert += BTreeNodeSize;
            z.setOffset(insert);
            x.addChild(z.getOffset(),i+1);
            writeNode(z,insert);
            writeNode(x,rootOffset);
            insert += BTreeNodeSize;
        }
        else{
            writeNode(y,y.getOffset());
            z.setOffset(insert);
            writeNode(z,insert);
            x.addChild(z.getOffset(),i+1);
            writeNode(x,x.getOffset());
            insert += BTreeNodeSize;
        }
	 }
	
	/**
	 * Writes node into file - uses parent pointer in node metadata and degree of tree nodes to determine where this node should be written.
	 * 
	 * @return Node written into file - should be the same as node passed in as parameter.
	 */
	public BTreeNode writeNode(BTreeNode x, int t) //node to write and degree of tree
	{
		try {
			int writeLocation = baseOffset + 2*t*(x.getParent() + BTreeNodeSize);
			fileWrite.seek(writeLocation); //should write at (BTree Offset) + 2*t*((parent pointer) + (node size))
			/*
			 * Order of write:
			 * 1. Number of objects (int)
			 * 2. This node's offset (int)
			 * 3. Parent pointer (int)
			 * 4. IsLeaf integer (should be 1 or 0, treated like boolean) (int)
			 * 5. Iterate through key array to N-1 (key - long, frequency - int)
			 * 6. If NOT leaf, iterate through child array to (N+1) (int)
			 */
			fileWrite.writeInt(x.getN());
			fileWrite.writeInt(x.getOffset());
			fileWrite.writeInt(x.getParent());
			fileWrite.writeInt(x.isLeaf());
			for(int i = 0; i < x.getN(); i++) { // only write as many objects as should currently be stored
				fileWrite.writeLong(x.getKey(i).getKey());		//key for object
				fileWrite.writeInt(x.getKey(i).getFrequency());	//frequency of object
			}
			fileWrite.seek(writeLocation + 16 + ((2*t) -1)*12); //move to start location of child pointer array
																//start of node + metadata + size of TreeObject array
			if(x.isLeaf() == 0) { //internal node, has pointers to track
				for(int i = 0; i < (x.getN() + 1); i++) { //could use getChildren.size(), but this should always be accurate, and better reflects desired behavior
					fileWrite.writeInt(x.getChildren().get(i));
				}
			}
			fileWrite.seek(0); //reset fileWrite for next use
		} catch (IOException e) {
			System.out.println("File Writing Error!");
			e.printStackTrace();
		}
		
		return x;
	}
	
	/**
	 * Creates node by reading from location in file and parsing an empty node object with data parsed from that binary.
	 * @param location
	 * @return newly created node, read from binary in file
	 */
	public BTreeNode readNode(int location) { //should we be using a long to read from file? Int will accomodate up to a 2 GB tree...
		BTreeNode node = new BTreeNode();
		try {
			fileRead.seek(location);
			/*
			 * Order of read:
			 * 1. Number of objects (int)
			 * 2. This node's offset (int)
			 * 3. Parent pointer (int)
			 * 4. IsLeaf integer (should be 1 or 0, treated like boolean) (int)
			 * 5. Iterate through key array to N-1 (key - long, frequency - int)
			 * 6. If NOT leaf, iterate through child array to (2*N - 1) (int)
			 */
			node.setN(fileRead.readInt());
			node.setOffset(fileRead.readInt());
			node.setParent(fileRead.readInt());
			node.setIsLeaf(fileRead.readInt()); //should now be 16 bytes in
			TreeObject obj = new TreeObject(0,0);
			for(int i = 0; i < node.getN(); i ++) {
				obj.setKey( fileRead.readLong() );
				obj.setFrequency(fileRead.readInt());
				node.addKey(obj, i);
			}
			fileRead.seek(location + 16 + ((2*degree)-1)*12); //seek end of TreeObject array
			if(node.isLeaf() == 0) { //internal node, should have child pointers
				for(int i = 0; i < (node.getN() + 1); i ++) {
					node.addChild(fileRead.readInt(), i);
				}
			}
			fileRead.seek(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return node;
	}
	
	/**
	 * Size() method for BTree
	 * 
	 * @return current number of nodes
	 */
	public int size()
	{
		return nodeCount;
	}

	/**
	 * isEmpty() check for BTree
	 * @return boolean representing result of statement 'size() == 0;'
	 */
	public boolean isEmpty()
	{
		return size() == 0;
	}
	
	/** TODO: Determine how to increment and accurately track height
	 * Identifies current number of layers in BTree
	 * 
	 * @return current height of BTree
	 */
	public int height()
	{
		return height;
	}
	
	//Inner Class BTreeNode
		/**
		 * This class
		 * 
		 * @author angelsanabria 
		 *
		 */
		public class BTreeNode 
		{
				//Metadata
			    private int n; // number of objects in the BTreeNode
			    private int offset; // pointer to this node in file
			    private int parent; // pointer to parent
			    private int isLeaf; // leaf tracker int (treated as boolean)
			    
			    //Keys and child pointers
			    private ArrayList<TreeObject> keys;
			    private ArrayList<Integer> children;

			    
			    /**
			     * Default constructor - creates empty node with no set parent, empty key and child pointer lists.
			     */
			    public BTreeNode()
			    {
			        parent = -1;
			        keys = new ArrayList<TreeObject>();
			        children = new ArrayList<Integer>();
			        n = 0;
			    }
			    
			    /**
			     * Set pointer to parent node location in memory
			     * @param parent pointer
			     */
			    public void setParent(int parent)
			    {
			        this.parent = parent;
			    }
			    
			    /**
			     * 
			     * 
			     * @return pointer to location of parent node
			     */
			    public int getParent()
			    {
			        return parent;
			    }
			    
			    /**
			     * @param address of a child node to be added to child array
			     */
			    public void addChild(int address)
			    {
			        children.add(address);
			    }
			    
			    /**
			     * @param x
			     * @param i
			     */
			    public void addChild(int x, int i)
			    {
			        children.add(i,x);
			    }
			    
			    /**
			     * @param i
			     * @return
			     */
			    public int removeChild(int i)
			    {
			        return children.remove(i);
			    }
			    
			    /**
			     * @param i
			     * @return
			     */
			    public int getChild(int i)
			    {
			        return children.get(i).intValue();
			    }
			    
			    /**
			     * 
			     * 
			     * @return
			     */
			    public ArrayList<Integer> getChildren()
			    {
			        return children;
			    }
			    
			    /**
			     * This method gets the number of objects in the BTree
			     * 
			     * @return
			     */
			    public int getN()
			    {
			    	return n;
			    }
			    
			    /**
			     * This method sets the number of objects in the BTree
			     * 
			     * @param i
			     */
			    public void setN(int i)
			    {
			    	n = i;
			    }
			    
			    /**
			     * This method gets the key value
			     * 
			     * @param k
			     * @return
			     */
			    public TreeObject getKey(int k)
			    {
			        TreeObject obj = keys.get(k);
			        return obj;
			    }
			    
			    /**
			     * 
			     * 
			     * @param obj
			     */
			    public void addKey(TreeObject obj)
			    {
			        keys.add(obj);
			    }
			    
			    /**
			     * @param obj
			     * @param i
			     */
			    public void addKey(TreeObject obj, int i)
			    {
			        keys.add(i,obj);
			    }
			    
			    /**
			     * This method gets the offset
			     * 
			     * @return
			     */
			    public int getOffset()
			    {
			    	return offset;
			    }
			    
			    /**
			     * This method sets the offset
			     * 
			     * @param offset
			     * @return offset
			     */
			    public int setOffset(int offset)
			    {
			    	return this.offset = offset;
			    }
			    
			    /**
			     * This method sets a leaf from a branch
			     * 
			     * @param isLeaf
			     */
			    public void setIsLeaf(int leaf)
			    {
			        this.isLeaf = leaf;
			    }
			    
			    /**
			     * This method checks if its a leaf
			     * 
			     * @return true or false
			     */
			    public int isLeaf()
			    {
			    	return this.isLeaf;
			    }
			    
			    /**
			     * This method removes the key
			     * 
			     * @param i
			     * @returns the removed key
			     */
			    public TreeObject removeKey (int i)
			    {
			    	return keys.remove(i);
			    }
		}
	
		
		
		
}
