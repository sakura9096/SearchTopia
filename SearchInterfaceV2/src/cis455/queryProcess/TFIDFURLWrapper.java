package cis455.queryProcess;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class TFIDFURLWrapper {
	String url;
	double tfidf;
	
	
	public TFIDFURLWrapper (double tfidf, String url) {
		this.url = url;
		this.tfidf = tfidf;
	}
	

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
            // if deriving: appendSuper(super.hashCode()).
            append(url).
            append(tfidf).
            toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
       if (!(obj instanceof TFIDFURLWrapper))
            return false;
        if (obj == this)
            return true;

        TFIDFURLWrapper rhs = (TFIDFURLWrapper) obj;
        return new EqualsBuilder().
            // if deriving: appendSuper(super.equals(obj)).
            append(url, rhs.url).
            isEquals();
    }
}
