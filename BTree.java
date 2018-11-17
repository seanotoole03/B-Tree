
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

import javax.xml.soap.Node;

/**
 * This class
 * 
 * @author angelsanabria
 *
 */
public class BTree<T extends Comparable>
{
	private BTreeNode root;
	private int degree;
	private int BTreeNodeSize;
	private int rootOffset;
	private int insert;
	private int n; // number of key-value pairs in the BTree
	private int height; // height of the BTree 
	

    /**
     * 
     * 
     * @param degree
     * @param fileName
     * @param useCache
     * @param cacheSize
     */
    public BTree(int degree, String fileName, boolean useCache, int cacheSize)
    {
        BTreeNodeSize = 0;
        rootOffset = 0;
        insert = rootOffset + BTreeNodeSize;
        this.degree = degree;        
    }
    
	/**
	 * 
	 * 
	 * @param k
	 */
	public void insert(long k)
	{

	    BTreeNode r = root;
        int i = r.getN();
        if (i == (2 * degree - 1))
        {
            TreeObject obj = new TreeObject(k);
            while (i > 0 && obj.compareTo(r.getKey(i-1)) < 0)
            {
                i--;
            }
     
            if (i > 0 && obj.compareTo(r.getKey(i-1)) == 0)
            {
                r.getKey(i-1).increaseFrequency();
            }
            
            else 
            {
                BTreeNode s = new BTreeNode();
                s.setOffset(r.getOffset());
                root = s;
                r.setOffset(insert);
                r.setParent(s.getOffset());
                s.setIsLeaf(false);
                s.addChild(r.getOffset());
                splitChild(s,r,0);
                insertNonfull(s,k);
            }
        }
        else
            insertNonfull(r,k);
	}
	
	 /**
	  * 
	  * 
	 * @param x
	 * @param k
	 * @return
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
	        
	        if (x.isLeaf())
	        {
	            return null;
	        }
	        
	        else 
	        {
	            int offset = x.getChild(i);
	            return search(x,k);
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
	  
	   }
	
	
	//test a root with two children.  Write  a value into node into memory.
	//Array of key values for the node, create the root, how 
	
	//judge the pointers with the size of node, based on degree
	
	/**
	 * @param x
	 */
	public void splitChild(BTreeNode x, BTreeNode y, int i)// splitting will be the only time where we have pointers
	 {
	    BTreeNode z = new BTreeNode();
        z.setIsLeaf(y.isLeaf());
        z.setParent(y.getParent());
        for (int j = 0; j < degree - 1; j++)
        {
            z.addKey(y.removeKey(degree));
            z.setN(z.getN()+1);
            y.setN(y.getN()-1);

        }
        
        if (!y.isLeaf())
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
	 * 
	 * 
	 * @return
	 */
	public BTreeNode writeNode(BTreeNode x, int i)
	{
		return root;
		
	}
	
	/**
	 * 
	 * 
	 * @return
	 */
	public int size()
	{
		return n;
	}

	/**
	 * 
	 * 
	 */
	public boolean isEmpty()
	{
		return size() == 0;
	}
	
	/**
	 * 
	 * 
	 * @return
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
			    private int n; // number of key-value pairs in the BTree
			    private LinkedList<TreeObject> keys;
			    private LinkedList<Integer> children;
			    private int parent;
			    private int offset;
			    private boolean isLeaf;
			    
			    /**
			     * 
			     */
			    public BTreeNode()
			    {
			        parent = -1;
			        keys = new LinkedList<TreeObject>();
			        children = new LinkedList<Integer>();
			        n = 0;
			    }
			    
			    /**
			     * 
			     * 
			     * @param parent
			     */
			    public void setParent(int parent)
			    {
			        this.parent = parent;
			    }
			    
			    /**
			     * 
			     * 
			     * @return
			     */
			    public int getParent()
			    {
			        return parent;
			    }
			    
			    /**
			     * @param n
			     */
			    public void addChild(int n)
			    {
			        children.add(n);
			    }
			    
			    /**
			     * @param x
			     * @param i
			     */
			    public void addChild(Integer x, int i)
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
			    public LinkedList<Integer> getChildren()
			    {
			        return children;
			    }
			    
			    /**
			     * This method gets the number of key-value pairs in the BTree
			     * 
			     * @return
			     */
			    public int getN()
			    {
			    	return n;
			    }
			    
			    /**
			     * This method sets the number of key-value pairs in the BTree
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
			    public void setIsLeaf(boolean isLeaf)
			    {
			        this.isLeaf = isLeaf;
			    }
			    
			    /**
			     * This method checks if its a leaf
			     * 
			     * @return true or false
			     */
			    public boolean isLeaf()
			    {
			    	return isLeaf();
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
