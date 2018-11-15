import java.util.LinkedList;

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
