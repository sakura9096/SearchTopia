import java.util.HashSet;
import java.util.Set;

public class SourceURL {

	private static SourceURL instance = null;
	private static Set<String> sourceURL;

	private SourceURL() {
		sourceURL = new HashSet<>();
	}
	
	public static SourceURL getInstance() {
		if (instance == null) {
			synchronized(SourceURL.class) {
				if (instance == null) {
					instance = new SourceURL();
				}
			}
		}
		return instance;
	}
	

	/**
	 * @param sourceURL the sourceURL to add
	 */
	public void addSourceURL(String url) {
		sourceURL.add(url);
	}
		
	/**
	 * @return The set of sourceURLs.
	 */
	public Set<String> getSourceURL() {
		return sourceURL;
	}
	
	public boolean contains(String url) {
		return sourceURL.contains(url);
	}
	
	public int size() {
		return sourceURL.size();
	}
}
