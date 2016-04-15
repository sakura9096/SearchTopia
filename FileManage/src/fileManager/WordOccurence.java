package fileManager;

//record the word occurence
public class WordOccurence {
	private String url; //corresponding url
	private int type; //hit type: 0: for fancy hit 1 for normal hit
	//private boolean isCapital; //if the word is all capitals or not
	//private int importance; //get the importance according to the type and whether it is Capitalized
	//private int position; //the position of hitting
	private int maxFrequency;
	
	public WordOccurence(String url, int type) {
		this.url = url;
		this.type = type;
		//this.isCapital = isCapital;
		//this.position = position;
		//if (isCapital) this.importance += 2;
	}
	
	public String toString() {
		return this.url + "\t" + this.maxFrequency;
	}

	//Getters and Setters
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	public int getMaxFrequency() {
		return maxFrequency;
	}
	
	public void setMaxFrequency(int maxFrequency) {
		this.maxFrequency = maxFrequency;
	}
	
	
}
