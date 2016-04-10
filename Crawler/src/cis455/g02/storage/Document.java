package cis455.g02.storage;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

@Entity
public class Document {
	
	@PrimaryKey
	private String hasedContent;
	private String url;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getHasedContent() {
		return hasedContent;
	}

	public void setHasedContent(String hasedContent) {
		this.hasedContent = hasedContent;
	}
}
