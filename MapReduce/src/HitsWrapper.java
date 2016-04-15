import java.util.ArrayList;

public class HitsWrapper {
	private int wordFrequency;
	private ArrayList<HitInfo> hitsInfo;
	private String maxFrequency;
	
	public HitsWrapper (int wordFrequency, ArrayList<HitInfo> hitsInfo, String maxFrequency) {
		this.wordFrequency = wordFrequency;
		this.hitsInfo = hitsInfo;
		this.maxFrequency = maxFrequency;
	}
	
	public void addHitsInfo (HitInfo hitInfo) {
		hitsInfo.add(hitInfo);
	}

	public int getWordFrequency() {
		return wordFrequency;
	}

	public void setWordFrequency(int wordFrequency) {
		this.wordFrequency = wordFrequency;
	}

	public ArrayList<HitInfo> getHitsInfo() {
		return hitsInfo;
	}

	public void setHitsInfo(ArrayList<HitInfo> hitsInfo) {
		this.hitsInfo = hitsInfo;
	}

	public String getMaxFrequency() {
		return maxFrequency;
	}

	public void setMaxFrequency(String maxFrequency) {
		this.maxFrequency = maxFrequency;
	}
	
	
}
