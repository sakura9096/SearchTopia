package fileWriter;

public class WordHit {
	String word;
	String url;
	double tf_idf;
	
	//takes in a string and parse it to wordhit
	public WordHit(String string) {
		try {
			String[] array = string.split("\\s+", 2);
			this.word = array[1];
			String val = array[2];
			int index = array[2].lastIndexOf(' ');
			this.url = array[2].substring(0, index);
			this.tf_idf = Double.parseDouble(array[2].substring(index + 1));
		} catch (Exception e) {
			return;
		}
	}
	
	@Override
	public String toString() {
		return this.word + " " + this.url + " " + this.tf_idf;
	}
	
	
}
