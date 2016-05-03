import java.util.ArrayList;
/**
 * This is the wrapper class of all the wordFrequency and maxFrequency. 
 * @author fanglinlu
 *
 */
public class HitsWrapper {
	private int wordFrequency;
	private String maxFrequency;
	
	public HitsWrapper (int wordFrequency, String maxFrequency) {
		this.wordFrequency = wordFrequency;
		this.maxFrequency = maxFrequency;
	}
	
	public int getWordFrequency() {
		return wordFrequency;
	}

	public void setWordFrequency(int wordFrequency) {
		this.wordFrequency = wordFrequency;
	}
	
	public void increaseWordFrequency () {
		this.wordFrequency ++; 
	}

	public String getMaxFrequency() {
		return maxFrequency;
	}

	public void setMaxFrequency(String maxFrequency) {
		this.maxFrequency = maxFrequency;
	}
	
	
}
