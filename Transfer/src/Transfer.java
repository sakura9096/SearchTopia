import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;

public class Transfer {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			String inputDir = args[0];
			String outputDir = args[1];
			if (!outputDir.endsWith("/")) outputDir = outputDir + "/";
			
			File input = new File(inputDir);
			if (!input.isDirectory()) return;
			File[] files = input.listFiles();
			for (int i = 0; i < files.length; i++) {
				BufferedReader br = new BufferedReader(new FileReader(files[i]));
				String url = br.readLine();
				if (url == null) continue;
				br.close();
				url = url.trim();
				String fileDir = outputDir + hashToString(url);
				File outfile = new File(fileDir);
				if (!outfile.exists()) {
					files[i].renameTo(outfile);
					System.out.println("to the destination: ~~~~~");
					
				}
		}
		
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		
	

	}

	
	private static String hashToString(String s){

		try{
			MessageDigest m = MessageDigest.getInstance("SHA-1");
			m.update(s.getBytes());
			byte byteData[] = m.digest();

			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < byteData.length; i++) {
				sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
			}
			return sb.toString();
		}catch(Exception e){
			return System.currentTimeMillis() + "";
		}
	}
	
}
