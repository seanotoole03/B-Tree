

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
	
	/**
	 * 
	 */
	public String toDNAString(int seqLength) {
		String str = Long.toBinaryString(key);
		String ret = "";
		while(str.length() < 64) {
			str = "0" + str;
		}
		//should create a string of sequenceLength using binary string created
		for(int i = (64 - 2*seqLength); i < 64; i += 2) {
			String bits = str.substring(i, i+2);
			if(bits.equals("00")) {
				ret += 'a';
			} else if(bits.equals("11")) {
				ret += 't';
			} else if(bits.equals("01")) {
				ret += 't';
			} else { //bits.equals("10")
				ret += 'g';
			}
		}
		return ret;
		
	}
}
