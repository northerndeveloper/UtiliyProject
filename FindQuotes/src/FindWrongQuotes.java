import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseBigDecimal;
import org.supercsv.cellprocessor.ParseLong;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

public class FindWrongQuotes {

//	public static void main(String[] args) {
//		
//		//String line = " \"RO\"T\"M\";\"RAM\" ";
//		
//		String line = "\"she is devil in the sky\"; \"she is second devil in the sky\"  ";
//		
//		
//		
//		line = line.split("\"")[3];
//		
//		System.out.println(line);
//		
//	
//		
////		int countOfComma = line.length() - line.replace(";", "").length();
////		System.out.println("count of Comma" + countOfComma);
////		
////		int countOfQuote = line.length() - line.replace("\"", "").length();
////		System.out.println("count of Quote" + countOfQuote);
//		
//		
////		Pattern p = Pattern.compile(Pattern.quote("\"") + "(.*?)" + Pattern.quote("\""));
////		Matcher m = p.matcher(line);
////		while (m.find()) {
////		  System.out.println(m.group(1));
////		}
////		
////		
////		String s = "test string \"\"mother\"\"";
////
////		s = s.substring(s.indexOf("\"\"") + 2);
////		s = s.substring(0, s.indexOf("\"\""));
////
////		System.out.println(s);
//		
//		
//	}
//	
	
//	public static void main(String[] args) throws Exception {
//	    Pattern p = Pattern.compile("[“](.*)[”][\\s]+[“](.*)[”][\\s]+[“](.*)[”]");
//	    Matcher m = p.matcher("AddItem rt456 4  12 BOOK “File Structures” “Addison-Wesley” “Michael Folk”");
//
//	    if (m.find()) {
//	        for (int i=1;i<=m.groupCount();i++) {
//	            System.out.println(m.group(i));
//	        }
//	    }
//	}
	
	public static void main(String[] args) throws Exception {

		ICsvBeanReader beanReader = null;
		String csvFile = "C:\\Users\\akopuz\\Desktop\\testHata.csv";
		
	//	String csvFile="C:\\Alper\\1000201602\\1000201602.csv";
		
		long kayitSeqNo = 0;
		int insertCount = 0;
		int totalInsertCount = 0;
		boolean eskiVerilerSilindi = false;
		JournalRow jr = null;
		try {
			FileInputStream fis = new FileInputStream(csvFile);
			byte[] possibleBOM = new byte[3];
			fis.read(possibleBOM); // is it?
			beanReader = new CsvBeanReader(new InputStreamReader(fis, "UTF-8"), CsvPreference.STANDARD_PREFERENCE);
			final String[] header = mapHeaderNames(beanReader.getHeader(true));
			final CellProcessor[] processors = new CellProcessor[] { new NotNull(new ParseLong()), // offIdNo,
					new NotNull(), // voucherDate,
					new NotNull(), // voucherId,
					new Optional(), // accNo,
					new NotNull(), // accDesc,
					new Optional(), // voucherDesc,
					new NotNull(new ParseBigDecimal()), // dbt,
					new NotNull(new ParseBigDecimal()), // crd,
					new Optional(), // yon,
					// new Optional(), // voucherCreationDate,
					new Optional(new ParseLong()), // seqNo,
					new Optional(), // voucherType,
					new Optional(), // documentType,
					new Optional(), // documentDate,
					new Optional(), // documentNo,
					new Optional(), // documentOtherDesc,
					new Optional(), // paymentType,
					new Optional() // paymentTypeOtherDesc
			};
			
			List<JournalRow> jrList = new ArrayList<JournalRow>();
			String insertResult;
			while ((jr = beanReader.read(JournalRow.class, header, processors)) != null) {
				jr.setSeqNo(++kayitSeqNo);
				System.out.println(jr.getVoucherDesc());
				
			}
			if (jrList.size() > 0) {
				System.out.println("insertListSize.1 :" + jrList.size());
				totalInsertCount += jrList.size();
			}
			System.out.println("totalinsertCount = " + totalInsertCount);
		} catch (Exception e) {
			System.out.println("error line = " + jr.getOffIdNo() + " accNo = " + jr.getAccNo() +  " " + jr.getAccDesc() + " "  + jr.getVoucherDesc());
			e.printStackTrace();
			throw new Exception("Kayýtlar atýlýrken hata oluþtu! Hata: " + e.getMessage());
		} finally {
			System.err.println("Toplam dosyadan okunan kayýt sayýsý:" + insertCount
					+ "\nToplam veritabanýna yazýlan kayýt sayýsý:" + totalInsertCount);
			if (beanReader != null) {
				beanReader.close();
			}
		}

	}

	

	private static String mapHeaderName(String header) {
		StringBuilder sb = new StringBuilder(header.length());
		char[] chars = header.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];
			if (c == '"' || Character.toString(c).equals("\"")) {
				continue;
			} else {
				sb.append(Character.toString(c));
			}
		}
		return sb.toString();
	}

	private static String[] mapHeaderNames(String[] headers) {
		for (int i = 0; i < headers.length; i++) {
			headers[i] = mapHeaderName(headers[i]);
		}
		return headers;
	}
	
	
	
}
