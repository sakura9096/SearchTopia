import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Encode {

	public static void main(String[] args) {
		
		List<String> str = new ArrayList<>();
		String res = null;
		try {
			File file = new File("tiny.txt");
			Scanner in = new Scanner(file);
			PrintWriter out = new PrintWriter("dummy.txt");
			while (in.hasNextLine()) {
				str.add(in.nextLine());
			}
			System.out.println(str.size());
			res = Codec.encode(str);
			out.print(res);
			
			in.close();
			out.close();
		} catch (FileNotFoundException e) {		
			e.printStackTrace();
		}
	}

	static class Codec {
		 // Encodes a list of strings to a single string.
	    public static String encode(List<String> strs) {
	        StringBuilder sb = new StringBuilder();
	        for (String str : strs) {
	            sb.append(str.replace("/", "//").replace("*", "/*"));
	            sb.append('*');
	        }
	        return sb.toString();
	    }

	    // Decodes a single string to a list of strings.
	    public static List<String> decode(String s) {
	        StringBuilder sb = new StringBuilder();
	        List<String> res = new ArrayList<String>();
	        for (int i = 0; i < s.length(); i++) {
	            if (s.charAt(i) == '/') {
	                i++;
	                sb.append(s.charAt(i));
	            } else if (s.charAt(i) == '*') {
	                res.add(sb.toString());
	                sb.setLength(0);
	            } else {
	                sb.append(s.charAt(i));
	            }
	        }
	        return res;
	    }
	}
}
