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
        BTreeNodeSize = 8 + (24*(degree)); //bytes -- 16 + (2t - 1)*8 + (2t)*4
        nodeMaxObj = (2*degree)-1;
        rootOffset = 16;
        insert = 0;
        height = 0;
        nodeCount = 1;
        this.degree = degree;  
        
        file = new File(fileName);
        try { 	//Initialize RandcomAccessFile reader and writer
        	fileRead = new RandomAccessFile(file, "r");
        	fileWrite = new RandomAccessFile(file, "rw");
        } catch (FileNotFoundException e) {
        	e.printStackTrace();
        }
        
        try {	//Metadata writing - 16 bytes in total
			fileWrite.writeInt(degree);
			fileWrite.writeInt(rootOffset);
			fileWrite.writeInt(nodeCount);
			fileWrite.writeInt(height);
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        root = new BTreeNode();
        root.setOffset(0);
        
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
	
	
	
	//test a root with two children.  Write  a value into node into memory.
	//Array of key values for the node, create the root, how 
	
	//judge the pointers with the size of node, based on degree
	
	/**
	 * @param x, y - 
	 */
	public void splitChild(BTreeNode parent, int currIndex, BTreeNode current)// splitting will be the only time where we add pointers
	 {
	    BTreeNode z = new BTreeNode();	//replacement for original node - new child
	    z.setOffset(rootOffset);
	    rootOffset += BTreeNodeSize;
        z.setIsLeaf(current.isLeaf());
        z.setParent(current.getParent());
        if(current.getParent() == 0) { //parent is root
        	root.addKey(current.removeKey(degree), currIndex); //grab middle object, push up to parent
        	root.addChild(z.getOffset(), currIndex);
        } else {
        	parent.addKey(current.removeKey(degree), currIndex); //grab middle object, push up to parent
        	parent.addChild(z.getOffset(), currIndex);
        }
        z.setN(degree-1);
        for (int j = 0; j < degree - 1; j++)
        {
          z.addKey(current.removeKey(0), j);
        }
        
        if (!(current.isLeaf() == 1)) //we're splitting an internal node, 
        {
            for (int j = 0; j < degree; j++)
            {
                z.addChild(current.removeChild(0), j);
            }
        }
        
        return;
	 }
	
	/**
	 * SplitRoot - splits current root and allocates values within key and child pointer arrays to two new child nodes.
	 */
	public void splitRoot() {
		BTreeNode childLeft = new BTreeNode();
		childLeft.setN(degree-1);
		childLeft.setOffset(rootOffset);
		rootOffset += BTreeNodeSize;
		childLeft.setParent(0);
		childLeft.setIsLeaf(root.isLeaf());
		
		BTreeNode childRight = new BTreeNode();
		childRight.setN(degree-1);
		childRight.setOffset(rootOffset);
		rootOffset += BTreeNodeSize;
		childRight.setParent(0);
		childRight.setIsLeaf(root.isLeaf());
		
		for (int j = 0; j < degree - 1; j++)
        {
          childLeft.addKey(root.getKey(j), j);
          childRight.addKey(root.getKey(j + (degree)), j);
        }
		
		for (int j = 0; j < ((2*degree) -1); j++)
        {
			root.removeKey(0);
        }
        
        if (!(current.isLeaf() == 1)) //we're splitting an internal root 
        {
            for (int j = 0; j < degree; j++)
            {
            	childLeft.addChild(root.getChild(j), j);
                childRight.addChild(root.getChild(j + (degree)), j);
            }
            for(int j = 0; j < 2*degree; j++) {
            	root.removeChild(0);
            }
        }
		
        root.addChild(childLeft.getOffset());
        root.addChild(childRight.getOffset());
		
		if(root.isLeaf() == 1) {
			root.setIsLeaf(0); 
		} 
		
		return;
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
			     * Removes child pointer from child array, returns that value
			     * @param i
			     * @return
			     */
			    public int removeChild(int i)
			    {
			        return children.remove(i);
			    }
			    
			    /**
			     * Gets value of child pointer from child array, returns that value
			     * @param i
			     * @return
			     */
			    public int getChild(int i)
			    {
			        return children.get(i).intValue();
			    }
			    
			    /**
			     * This method allows controlled access to the child pointer array
			     * 
			     * @return child pointer array
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
			     * This method checks if this a leaf
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
			    
			    /**
			     * This method sets (overwrites) the current key at a location in the keys ArrayList
			     * @param index - index in keys array to overwrite to
			     * @param newKey - new TreeObject to replace existing
			     */
			    public void SetKey(int index, TreeObject newKey) {
			    	keys.set(index, newKey);
			    }
		}	
		
}
