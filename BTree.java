import java.io.*;
import java.nio.ByteBuffer;
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
public class BTree<T>
{
	private BTreeNode root, parent, current;
	private int degree;			//position 0 of file
	private int BTreeNodeSize, nodeMaxObj;	
	private int rootOffset;		//position 4 of file
	private int nodeCount; // number of nodes in tree, position 8 of file
	private int height; // height of the BTree, position 12 of file
	//TOTAL BTREE METADATA OFFSET = 16
	private int baseOffset = 16;
	private File file;
    private RandomAccessFile fileRead, fileWrite;
    
    private BTreeCache cache;
    
    /**
     * Constructor for BTree - includes cache usage.
     * 
     * @param degree - indicates desired size of nodes
     * @param fileName - file to be written to, default will be 'file' for testing and specified filename format 
     * @param useCache - indicates whether the user would like a cache to be used for easier manipulation of frequently used data
     * @param cacheSize - desired size of cache (if used)
     */
    public BTree(int degree, String fileName, int useCache, int cacheSize)
    {
        BTreeNodeSize = 4 + (32*(degree)); //bytes -- 16 + (2t - 1)*12 + (2t)*4
        nodeMaxObj = (2*degree)-1;
        rootOffset = 16;
        height = 0;
        nodeCount = 1;
        this.degree = degree;  
        
        file = new File(fileName);
        
        try { 	//Initialize RandcomAccessFile reader and writer
        	file.delete();
        	file.createNewFile();
        	fileRead = new RandomAccessFile(file, "r");
        	fileWrite = new RandomAccessFile(file, "rw");
        } catch (FileNotFoundException e) {
        	e.printStackTrace();
        	System.exit(0);
        } catch (IOException e) {
        	e.printStackTrace();
        	System.exit(0);
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
        root.setN(0);
        root.setOffset(0);
        root.setParent(0);
        root.setIsLeaf(1); //New tree should have a root that is a leaf.
        
        //TODO: Implement cache functionality
        if(useCache == 1) {
        	cache = new BTreeCache(cacheSize);
        }
        
        
    }
    
    /**
     * Constructor for BTree - includes cache usage.
     * 
     * @param degree - indicates desired size of nodes
     * @param fileName - file to be written to, default will be 'file' for testing and specified filename format 
     * @param useCache - indicates whether the user would like a cache to be used for easier manipulation of frequently used data
     * @param cacheSize - desired size of cache (if used)
     */
    public BTree(int degree, String inFileName, String outFileName, int useCache, int cacheSize)
    {
    	 BTreeNodeSize = 4 + (32*(degree)); //bytes -- 16 + (2t - 1)*12 + (2t)*4
         nodeMaxObj = (2*degree)-1;
         rootOffset = 16;
         height = 0;
         nodeCount = 1;
         this.degree = degree;  
         
         file = new File(inFileName);
         
         try { 	//Initialize RandcomAccessFile reader and writer
         	if(file.createNewFile()) {
         		System.out.println("No such file!");
         		file.delete();
         		System.exit(0);
         	}
         	fileRead = new RandomAccessFile(file, "r");
         	fileWrite = new RandomAccessFile(file, "rw");
         } catch (FileNotFoundException e) {
         	e.printStackTrace();
         	System.exit(0);
         } catch (IOException e) {
         	e.printStackTrace();
         	System.exit(0);
         }
         
         root = new BTreeNode();
         try {
			root.setN(fileRead.readInt());
			root.setOffset(fileRead.readInt());
	        root.setParent(0);
	        
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
         
         root.setIsLeaf(1); //New tree should have a root that is a leaf.
         
         //TODO: Implement cache functionality
         if(useCache == 1) {
         	cache = new BTreeCache(cacheSize);
         }
         
    }
    
    /**
     * Should write root to end of file. Call once rest of BTree has been created.
     */
    public void finalizeBTree() {
    	try {
    		root.setOffset(rootOffset);
			fileWrite.seek(rootOffset);
			root.setOffset(rootOffset);
			writeNode(root, degree);
			root.setOffset(0);
			fileWrite.seek(0);
			fileWrite.writeInt(degree);
			fileWrite.writeInt(rootOffset);
			fileWrite.writeInt(nodeCount);
			fileWrite.writeInt(height);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
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
        		//TODO: How should we move the loop back up to the parent node? We will need to restart search from one level up.
        		// 1 DEC - Added code to splitting child nodes to move pointers up to appropriate locations - test at meeting
        		if(current.equals(root)) { //splitting root
        			splitRoot();
        			
        		} else { //splitting standard node, should split around center (degree)
        			int currIndex = degree-1, i = 0;
        			ArrayList<Integer> childArray = parent.getChildren();
        			while(!childArray.get(i).equals(current.getOffset())) {
        				i++;
        			}
        			currIndex = i;
        			//error check here?
        			splitChild(parent, currIndex, current);
        			
        			//move up to parent to resume search/insert, check first if moving up to root to prevent reading from invalid location
        			if(parent == root) {
        				current = root;
        			} else if(parent.getParent() == 0) { //parent's parent is root
        				current = parent;
        				parent = root;
        			} else { //standard internal or leaf node at least 2 levels deep
        				current = parent;
        				parent = readNode(parent.getParent()); //read parent from file

        			}
        			
        		}
        		
        		continue; //end current loop, resume search/insert 
        		
    		}
        	int i = current.getN(); //retrieve number of objects in current node
	        while (i > 0 && obj.compareTo(current.getKey(i-1)) < 0) //search backwards to find first smaller/equal object
	        {
	            i--; //should bring us to either 
	        }
	 
	        if (i > 0 && obj.compareTo(current.getKey(i-1)) == 0) //found exact sequence in current node
	        {
	            current.getKey(i-1).increaseFrequency(); //increment frequency of sequence, rather than create duplicates
	            if(current != root) {
	            	writeNode(current, degree);
	            }
	            insertion = true;
	        }
	        
	        else 	//either i == 0, indicating this is smaller than anything in the array, or current.getKey(i-1) < obj.g 
	        {		//either way, we will need to either insert the object in a child node, or in this node, if it is a leaf
	        	if(current.isLeaf == 1) { //safe to add object to
	        		//add object to arraylist at current location (i, where i == 0 and/or is correct index to insert at)
	        		current.addKey(obj, i);
	        		current.setN(current.getN()+1);
	        		if(current != root) {
	        			writeNode(current, degree);
	        		}
	        		insertion = true;
	        		
	        	} else { //internal node - we need to find child node to check next, and set that to be the current node
	        		parent = current;
	        		current = readNode(current.getChild(i));
	        		/*try {
						fileRead.seek(0);
						System.out.println(fileRead.readInt());
						fileRead.seek(0);
					} catch (IOException e) {
						e.printStackTrace();
					}*/
	        		/*
	        		tester++;
	        		if(tester == 580) {
	        			getRoot();
	        			
	        			
	        		}
	        		*/
	        	}
	        	
	        	//process should repeat itself until match found or object inserted
	        }
        }
	}
	
	 /** 
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
	        while (i < x.getN() && (obj.compareTo(x.getKey(i)) > 0)) //find first equal or larger object
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
	        
	        else //recursively search next lower node
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
	 * 
	 * @param parent
	 * @param currIndex
	 * @param current
	 */
	public void splitChild(BTreeNode parentNode, int currIndex, BTreeNode currentNode)	// splitting will be the only time where we add pointers
	 {
	    BTreeNode z = new BTreeNode();	//new child
	    z.setOffset(rootOffset);
	    rootOffset += BTreeNodeSize;
        z.setIsLeaf(currentNode.isLeaf());
        z.setParent(currentNode.getParent());
        
        if(currentNode.getParent() == 0 && parent == root && parentNode == root) { 	//parent is root
        	root.addKey(currentNode.removeKey(degree-1), currIndex); 		//grab middle object, push up to parent and adjust subsequent child array items right
        	root.addChild(z.getOffset(), currIndex);
        } else { 
        	parentNode.addKey(currentNode.removeKey(degree-1), currIndex); 		//grab middle object, push up to parent and adjust subsequent child array items right
        	parentNode.addChild(z.getOffset(), currIndex);
        }
        
        z.setN(degree-1);
        for (int j = 0; j < (degree - 1); j++) 	//pass front half of values in array to new node
        {
          z.addKey(currentNode.removeKey(0), j); 
        } 
        
        //remaining values in current should only be second half of array, as middle value and left half have been removed
        currentNode.setN(degree-1);
        if(currentNode.getParent() == 0 && parent == root && parentNode == root) { 	//parent is root
        	root.setN(root.getN()+1);
        } else { 
            parentNode.setN(parentNode.getN()+1);
        }
        
        if (!(currentNode.isLeaf() == 1)) 	//we're splitting an internal node, children pointers should be allocated to new locations/nodes
        {
            for (int j = 0; j < degree; j++)
            {
                z.addChild(currentNode.removeChild(0), j);
                BTreeNode temp = readNode(z.getChild(j));
                temp.setParent(z.getOffset());
                writeNode(temp, degree);
            }
        }
        //update/add nodes in file appropriately
        if(this.parent != root && parentNode != root) {
        	writeNode(parentNode, degree);
        	parent = readNode(parentNode.getOffset());
        }
        
        writeNode(currentNode, degree);
        writeNode(z, degree);
        current = readNode(currentNode.getOffset());
        nodeCount++;
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
		
		TreeObject mid = root.removeKey(degree-1); //remove middle 
		for (int j = 0; j < ((2*degree) -2); j++)
        {
			root.removeKey(0);
        }
		root.addKey(mid);
        
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
        
		//add new children to root
        root.addChild(childLeft.getOffset());
        root.addChild(childRight.getOffset());
        
        //update old children of root to point at new parents
        ArrayList<Integer> childArrays = childLeft.getChildren();
        for(int i = 0; i < childLeft.getChildren().size(); i++) {
        	current = readNode(childArrays.get(i));
        	current.setParent(childLeft.getOffset());
        	writeNode(current, degree);
        }
        childArrays = childRight.getChildren();
        for(int i = 0; i < childRight.getChildren().size(); i++) {
        	current = readNode(childArrays.get(i));
        	current.setParent(childRight.getOffset());
        	writeNode(current, degree);
        }
		
        //reset current to root
        current = parent = root;
        
		if(root.isLeaf() == 1) { //if it was previously a leaf
			root.setIsLeaf(0); 
		}
		
		root.setN(1);
		
		//write root's new children into file at appropriate location
		writeNode(childLeft, degree);
		writeNode(childRight, degree);
		nodeCount+=2;
		
		return;
	}
	
	/**
	 * Writes node into file - uses parent pointer in node metadata and degree of tree nodes to determine where this node should be written.
	 * 
	 * @return Node written into file - should be the same as node passed in as parameter.
	 */
	public BTreeNode writeNode(BTreeNode x, int t) //node to write and degree of tree
	{
		ByteBuffer buff = ByteBuffer.allocate(BTreeNodeSize);
		try {
			buff.putInt(x.getN());
			buff.putInt(x.getOffset());
			buff.putInt(x.getParent());
			buff.putInt(x.isLeaf());
			fileWrite.seek(x.getOffset()); 
			/*
			 * Order of write:
			 * 1. Number of objects (int)
			 * 2. This node's offset (int)
			 * 3. Parent pointer (int)
			 * 4. IsLeaf integer (should be 1 or 0, treated like boolean) (int)
			 * 5. Iterate through key array to N-1 (key - long, frequency - int)
			 * 6. If NOT leaf, iterate through child array to (N+1) (int)
			 */
//			fileWrite.writeInt(x.getN());
//			fileWrite.writeInt(x.getOffset());
//			fileWrite.writeInt(x.getParent());
//			fileWrite.writeInt(x.isLeaf());
			for(int i = 0; i < x.getN(); i++) { // only write as many objects as should currently be stored
//				fileWrite.writeLong(x.getKey(i).getKey());		//key for object
//				fileWrite.writeInt(x.getKey(i).getFrequency());	//frequency of object
				buff.putLong(x.getKey(i).getKey());
				buff.putInt(x.getKey(i).getFrequency());
			}
//			fileWrite.seek(x.getOffset() + 16 + ((2*t) -1)*12); //move to start location of child pointer array
																//start of node + metadata + size of TreeObject array
			int newPos = 16 + ((2*t) -1)*12;
			buff.position(newPos);
			
			if(x.isLeaf() == 0) { //internal node, has pointers to track
				ArrayList<Integer> children = x.getChildren();
				for(int i = 0; i < (x.getN() + 1); i++) { //could use getChildren.size(), but this should always be accurate, and better reflects desired behavior
//					fileWrite.writeInt(x.getChildren().get(i));
					buff.putInt(children.get(i));
				}
			}
			fileWrite.write(buff.array());
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
		byte[] bytes = new byte[BTreeNodeSize];
		ByteBuffer buffer = ByteBuffer.allocate(BTreeNodeSize);
		try {
			fileRead.seek(location);
			fileRead.read(bytes);
			buffer = ByteBuffer.wrap(bytes);
			buffer.position(0);
			/*
			 * Order of read:
			 * 1. Number of objects (int)
			 * 2. This node's offset (int)
			 * 3. Parent pointer (int)
			 * 4. IsLeaf integer (should be 1 or 0, treated like boolean) (int)
			 * 5. Iterate through key array to N-1 (key - long, frequency - int)
			 * 6. If NOT leaf, iterate through child array to (2*N - 1) (int)
			 */
			node.setN(buffer.getInt());
			node.setOffset(buffer.getInt());
			node.setParent(buffer.getInt());
			node.setIsLeaf(buffer.getInt()); //should now be 16 bytes in
			
			for(int i = 0; i < node.getN(); i++) {
				TreeObject obj = new TreeObject(0,0);
				obj.setKey(buffer.getLong()); //long will not be in binary format
				obj.setFrequency(buffer.getInt());
				node.addKey(obj, i);
			}
			//fileRead.seek(location + 16 + ((2*degree)-1)*12); //seek end of TreeObject array
			buffer.position(16 + ((2*degree)-1)*12);
			if(node.isLeaf() == 0) { //internal node, should have child pointers
				for(int i = 0; i < (node.getN() + 1); i ++) {
					node.addChild(buffer.getInt(), i);
				}
			}
			fileRead.seek(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return node;
	}
	
	//Tree traversal - create dump file
	public void treeTraverseDump(File outFile, int seqLength) {
		RandomAccessFile writeDump = null;
		try {
			writeDump = new RandomAccessFile(outFile, "rw");
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		BTreeNode traverseParent = root;
		BTreeNode traverseChild = root;
		inOrder(traverseChild, traverseParent, writeDump, seqLength);
	}
	
	/**
	 * In order traversal of tree, writing all values to dump file.
	 * @param childNode
	 * @param parentNode
	 * @param write
	 * @param seqLength
	 */
	public void inOrder(BTreeNode childNode, BTreeNode parentNode, RandomAccessFile write, int seqLength) {
		try {
			for(int i = 0; i < childNode.getKeys().size(); i++) {
				if(childNode.isLeaf() != 1) {
					inOrder(readNode(childNode.getChild(i)), childNode, write, seqLength);
				}
				
				TreeObject t = childNode.getKey(i);
				write.writeChars(t.toDNAString(seqLength) + ':' + ' ' + t.getFrequency() + '\n'); // sequence, tab, colon, tab, frequency
				
				if(i == (childNode.getKeys().size()-1) && childNode.isLeaf != 1) {
					inOrder(readNode(childNode.getChild(i+1)), childNode, write, seqLength);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
		return;
	}
	
	/**
	 * Returns the root node.
	 * @return root node
	 */
	public BTreeNode getRoot() {
		return root;
	}
	
	/**
	 * Warning: ONLY USE FOR SEARCH INITIALIZATION OF ROOT
	 * @param setNode
	 * @return
	 */
	public BTreeNode setRoot(BTreeNode setNode) {
		root = setNode;
		return root;
	}
	
	/**
	 * Returns current root offset, the current endpoint for the file.
	 * @return current offset.
	 */
	public int getOffset() {
		return rootOffset;
	}
	
	/**
	 * 
	 * @return current node count
	 */
	public int getNodeCount() {
		return nodeCount;
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
		 * @author angelsanabria, seanotoole
		 *
		 */
		public class BTreeNode 
		{
				//Metadata
			    private int n; // number of objects in the BTreeNode
			    private int offset; // pointer to this node in file
			    private int parent; // pointer to parent
			    private int isLeaf; // leaf tracker int (treated as boolean) 0 - false, 1 - true
			    
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
			     * Appends to end of ArrayList
			     * @param address of a child node to be added to child array
			     */
			    public void addChild(int address)
			    {
			        children.add(address);
			    }
			    
			    /**
			     * Inserts at index, shifting all subsequent children right
			     * @param x - address of new child
			     * @param i - index to add at
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
			        return children.get(i);
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
			     * This method gets the number of objects in the node
			     * 
			     * @return
			     */
			    public int getN()
			    {
			    	return n;
			    }
			    
			    /**
			     * This method sets the number of objects in the node
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
			    
			    public ArrayList<TreeObject> getKeys(){
			    	return keys;
			    }
			    
			    /**
			     * Appends new key to end of list
			     * @param obj
			     */
			    public void addKey(TreeObject obj)
			    {
			        keys.add(obj);
			    }
			    
			    /**
			     * Adds key at index i, shifts all subsequent values right
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
	
	//Inner Class BTreeCache	
		//author: Stephen Richardson
		

		public class BTreeCache {
			
			private final Exception Exception = null;
			
			//cache linked lists
			private LinkedList<BTreeNode> cache;
			
			//variables
			int maxSize = 0; //the maximum size that the level 1 cache can be
			
			//statistic variables
			int numRef = 0;//number of references to cache1
			int numHits = 0;//number of hits on cache1
			
			//constructor
			public BTreeCache (int max) {
				cache = new LinkedList<BTreeNode>();
				maxSize = max;
			}	
			/*   Methods   */
			
			private BTreeNode getObject (int index) throws Exception {
				if (index >= cache.size()) {
					throw Exception;
				}
				return cache.get(index);
			}
			
			//add an object to the front of the list. deletes last node if necessary
			public BTreeNode addObject (BTreeNode object, int degree) {
				BTreeNode temp = null;
				
				if (cache.size() >= maxSize) {
					temp = cache.getLast();
					cache.removeLast();
				}
				cache.addFirst(object);
				return temp;
			}
			
			//takes object from node at given index, deletes the node, and moves object to the front
			private void moveToFront (int index) {
				BTreeNode object = cache.get(index);
				cache.remove(index);
				cache.addFirst(object);
			}
			
			//deletes whatever is in front and replaces it with the passed in node
			//meant to be used to update a node with changes made in the BTree class
			public void replaceFront (BTreeNode object) {
				cache.removeFirst();
				cache.addFirst(object);
			}
			
			//clear and reset cache
			public void clearCache () {
				cache.clear();
				return;
			}
			
			//returns cache1 hits
			public int getCacheHits() {
				return numHits;
			}
				
			//returns cache1 refs
			public int getCacheRef() {
				return numRef;
			}
			
			public void setMaxSize (int size) {
				this.maxSize = size;
			}
			
			//returns the hit ratio of cache1
			public double getCacheHitRatio () {
				
				double hitRatio = ((double)numHits) / numRef;
				return hitRatio;
			}
			
			//search for a treeObject in the nodes stored in cache. if found, return the node
			//and move it to the front to be replaced with an updated version.
			//otherwise return null
			/**
			 * This is a BTreeNode return method
			 * @param object
			 * @return
			 * @throws java.lang.Exception
			 */
			public BTreeNode searchForHit (TreeObject object) throws java.lang.Exception {
				int index = 0;
				boolean hit = false;
				
				//record a reference to cache1
				numRef++;
				//search cache1 for object
				while (index < cache.size() && hit == false) 
				{			
					hit = compareObjectToNode(object, index);
					
					if (hit == true)
					{
						//move the matching node to the front and return it
						numHits++;
						this.moveToFront(index);
						return this.getObject(0);
					}
				}//end search cache1 while loop
				
				//if we reach this, no nodes were found and we exit the method.
				return null;
			}//end SearchForHit method
			
			//compares the passed in treeObject with every tree object in the node at the passed
			//in index.
			private boolean compareObjectToNode(TreeObject object, int index)
			{
				BTreeNode temp = null;
				try {
					temp = this.getObject(index);
				} catch (java.lang.Exception e) {
					System.out.println("Cache read error - please switch to cache value of 0 (no cache) in GeneSequencingCreateaBTree arguments and contact the creators, a patch will be released as soon as possible.");
					e.printStackTrace();
					System.exit(0);
				}
				
				for (int i = 0; i < (temp.getKeys().size()); i++)//can't reach getKeys?
				{
					//if keys are the same
					if (object.compareTo(temp.getKey(i)) == 0)
					{
						return true;
					}
				}
				//if nothing was found, return false
				return false;
			}
			
		}
	
		
}