//author: Stephen Richardson
import java.util.LinkedList;

public class BTreeCache<BTreeNode> {
	
	private static final Exception Exception = null;
	
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
//		BTreeNode temp = this.getObject(index);
//		
//		for (int i = 0; i < temp.getKeys.size(); i++)//can't reach getKeys?
//		{
//			//if keys are the same
//			if (object.compareTo(temp.getKey(i)) == 0)
//			{
//				return true;
//			}
//		}
		//if nothing was found, return false
		return false;
	}
	
}
