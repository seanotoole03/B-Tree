package lab4;

/**
 * 
 * 
 * @author angel
 *
 */
public class TreeObject implements Comparable<TreeObject>
{
	private int frequency;
	private long key;
	
	/**
	 * 
	 * 
	 * @param key
	 * @param frequency
	 */
	public TreeObject(long key, int frequency)
	{
		this.key = key;
		this.frequency = frequency;
	}
	
	/**
	 * 
	 * 
	 * @param key
	 */
	public TreeObject(long key)
	{
		this.key = key;
		this.frequency = 1;
	}
	
	/**
	 * 
	 * 
	 * @return
	 */
	public long getKey()
	{
		return key;
	}
	
	/**
	 * 
	 * 
	 * @param key
	 * @return
	 */
	public long setKey(long key)
	{
		return this.key = key;
	}
	
	/**
	 * 
	 * 
	 * @return
	 */
	public int getFrequency()
	{
		return frequency;
	}
	
	/**
	 * 
	 * @param freq
	 */
	public void setFrequency(int freq)
	{
		this.frequency = freq;
	}
	
	/**
	 * 
	 */
	public void increaseFrequency()
	{
		frequency++;
	}
	
	/**
	 * 
	 */
	@Override
	public int compareTo(TreeObject arg0) 
	{
		  if (this.key < arg0.key)
	            return -1;
	        else if (this.key > arg0.key)
	            return 1;
	        else
		return 0;
	}
	
}
