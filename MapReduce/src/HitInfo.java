
public class HitInfo {
	String position;
	String importance;
//	int maxFrequency;
//	String maxFrequency;
	
	public HitInfo (String position, String importance) {
		this.position = position;
		this.importance = importance;
//		this.maxFrequency = maxFrequency;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getImportance() {
		return importance;
	}

	public void setImportance(String importance) {
		this.importance = importance;
	}

//	public String getMaxFrequency() {
//		return maxFrequency;
//	}
//
//	public void setMaxFrequency(String maxFrequency) {
//		this.maxFrequency = maxFrequency;
//	}
	
	
	
	
	
}
