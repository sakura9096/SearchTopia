package fileManager;

//record the word occurence
public class WordOccurence {
	private String url; //corresponding url
	private int type; //hit type: 0: for anchor, 1 for title, 2 for metadata, 3 for normal hit
	private boolean isCapital; //if the word is all capitals or not
	private int importance; //get the importance according to the type and whether it is Capitalized
	private int position; //the position of hitting
	private int maxFrequency;
	
	public WordOccurence(String url, int type, boolean isCapital, int position) {
		this.url = url;
		this.type = type;
		this.isCapital = isCapital;
		this.position = position;
		
		switch (type) {
		case 0:
		case 1:
			this.importance = 8;
			break;
		case 2:
			this.importance = 4;
			break;
		case 3:
			this.importance = 2;
			break;
		}
		if (isCapital) this.importance += 2;
	}
	
	public String toString() {
		return this.url + "\t" + this.type + "\t" + this.position + "\t" + this.importance + "\t" + this.maxFrequency;
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

	public boolean isCapital() {
		return isCapital;
	}

	public void setCapital(boolean isCapital) {
		this.isCapital = isCapital;
	}

	public int getImportance() {
		return importance;
	}

	public void setImportance(int importance) {
		this.importance = importance;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}
	
	public int getMaxFrequency() {
		return maxFrequency;
	}
	
	public void setMaxFrequency(int maxFrequency) {
		this.maxFrequency = maxFrequency;
	}
	
	
}
