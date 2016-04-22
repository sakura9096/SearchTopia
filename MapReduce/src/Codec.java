

import java.util.ArrayList;
import java.util.List;

public class Codec {
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
