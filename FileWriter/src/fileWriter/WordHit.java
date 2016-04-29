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
			
		}
	}
	
	
	
}
