import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.codec.binary.Base64;

public class StringToByteArray {
	
	public static String readFromFile() {
		

		BufferedReader br = null;
		StringBuffer lineReadFromByteArray = new StringBuffer();
		try {

			String sCurrentLine;

			//br = new BufferedReader(new FileReader("C:\\Users\\akopuz\\Desktop\\test3.txt"));
			//br = new BufferedReader(new FileReader("C:\\Users\\akopuz\\Downloads\\ortakFaturaPreview.jsp.xml"));
			br = new BufferedReader(new FileReader("C:\\Users\\akopuz\\Desktop\\testFaturaWebService02.txt"));
			

			while ((sCurrentLine = br.readLine()) != null) {
				lineReadFromByteArray.append(sCurrentLine);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		
		
		
		
		
		
		return lineReadFromByteArray.toString();

	}
		
	
	public static byte[] convertToByteArray(String strToBeConverted) {
		return strToBeConverted.replaceFirst("^\uFEFF", "").getBytes(StandardCharsets.UTF_8);
	}
	
	
	public static void main(String[] args) {
		
		
		String strToBeConverted = readFromFile();
	//	strToBeConverted = removeUTF8BOM(strToBeConverted);
		//byte[]   bytesEncoded = Base64.encodeBase64(strToBeConverted.replaceAll("\uFFFD", "\"").replaceFirst("^\uFEFF", "").getBytes());
		try {
			String str = new String(Base64.encodeBase64(strToBeConverted.getBytes("UTF-8")), "UTF-8");
			System.out.println(str);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		
		
	

		
	}
	
	
	public static final String UTF8_BOM = "\uFEFF";

	private static String removeUTF8BOM(String s) {
	    if (s.startsWith(UTF8_BOM)) {
	        s = s.substring(1);
	    }
	    return s;
	}

}
