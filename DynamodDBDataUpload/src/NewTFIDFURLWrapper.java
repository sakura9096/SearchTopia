/**
 * This is a wrapper class for word, url, tfidf and originalURL
 * @author fanglinlu
 *
 */
public class NewTFIDFURLWrapper {
	String word;
	double tfidf;
	String url;
	String originURL;
	
	public NewTFIDFURLWrapper(String word, double tfidf, String url, String originURL) {
		this.word = word;
		this.tfidf = tfidf;
		this.url = url;
		this.originURL = originURL;
	}
	

}
