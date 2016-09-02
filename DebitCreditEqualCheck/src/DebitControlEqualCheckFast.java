import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseBigDecimal;
import org.supercsv.cellprocessor.ParseLong;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

public class DebitControlEqualCheckFast {
	
	static Map<String, Incomes> incomeMap = new HashMap<String, Incomes>();

	public static void main(String[] args) throws Exception {
		
		ICsvBeanReader beanReader = null;
		String csvFile = "O:\\alper\\mayis2016_yeni"
				+ ""
				+ ".csv";
		int insertCount = 0;
		int totalInsertCount = 0;
		try {
			FileInputStream fis = new FileInputStream(csvFile);
			byte[] possibleBOM = new byte[3];
			fis.read(possibleBOM); // is it?
			beanReader = new CsvBeanReader(new InputStreamReader(fis, "UTF-8"), CsvPreference.STANDARD_PREFERENCE);
			final String[] header = mapHeaderNames(beanReader.getHeader(true));
			final CellProcessor[] processors = new CellProcessor[] {
					new NotNull(new ParseLong()), // offIdNo,
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
			JournalRow jr = null;
		
			List<JournalRow> jrList = new ArrayList<JournalRow>();
			String insertResult;
			while ((jr = beanReader.read(JournalRow.class, header, processors)) != null) {
				if(incomeMap.containsKey(jr.getVoucherId())){
					Incomes incomes = incomeMap.get(jr.getVoucherId());
					incomes.setCrd(incomes.getCrd().add(jr.getCrd()));
					incomes.setDbt(incomes.getDbt().add(jr.getDbt()));
				} else {
					Incomes incomes = new Incomes();
					incomes.setCrd(jr.getCrd());
					incomes.setDbt(jr.getDbt());
					incomeMap.put(jr.getVoucherId(), incomes);
				}
			}
			if (incomeMap.size() > 0) {
				totalInsertCount += jrList.size();
				checkIfDebtsAndCreditsEqual();
			}
			System.out.println("totalinsertCount = " + totalInsertCount);
		} catch (Exception e) {
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

	private static void checkIfDebtsAndCreditsEqual() {

		for(Map.Entry<String, Incomes> entry : incomeMap.entrySet()) {
			Incomes incomes = entry.getValue();
			if(incomes.getCrd().compareTo(incomes.getDbt()) != 0) {
				System.out.println("hatali voucherId = "  + entry.getKey() + " credit =  " + incomes.getCrd() + " debit = "  + incomes.getDbt()) ;	
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