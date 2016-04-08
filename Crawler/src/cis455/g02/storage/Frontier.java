package cis455.g02.storage;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

@Entity
public class Frontier {
	
	@PrimaryKey
	private String url;
	
	public void setURL (String url) {
		this.url = url;
	}
	
	public String getURL () {
		return url;
	}
	
}
