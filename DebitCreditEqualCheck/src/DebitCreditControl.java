import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseBigDecimal;
import org.supercsv.cellprocessor.ParseLong;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

public class DebitCreditControl {

	public static void main(String[] args) throws Exception {

		ICsvBeanReader beanReader = null;
		String csvFile = "O:\\alper\\mas"
				+ ""
				+ ".csv";
		long kayitSeqNo = 0;
		int insertCount = 0;
		int totalInsertCount = 0;
		boolean eskiVerilerSilindi = false;
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
				jr.setSeqNo(++kayitSeqNo);

				Long offIdNo = jr.getOffIdNo();
				if (jrList.size() > 0) {
					JournalRow previousJR = jrList.get(jrList.size() - 1);
					if (offIdNo.compareTo(previousJR.getOffIdNo()) < 0) {
						throw new Exception("Fiþ Sýra Numaralarý Hatalý! Yevmiye No:" + offIdNo);
					}
				}
				if (!(jr.getDbt().compareTo(BigDecimal.ZERO) > 0 || jr.getCrd().compareTo(BigDecimal.ZERO) > 0)) {
					throw new Exception("Borç/Alacak Tutarý Hatalý! Yevmiye No:" + offIdNo);
				}
				if (jr.getYon() != null
						&& !(jr.getYon().equals("A") || jr.getYon().equals("B") || jr.getYon().equals(" "))) {
					throw new Exception("Borç/Alacak Göstergesi Hatalý! Yevmiye No:" + offIdNo);
				}
				String documentType = jr.getDocumentType();
				if (documentType != null) {
					if (!(documentType.equals("F") || documentType.equals("C") || documentType.equals("S")
							|| documentType.equals("B") || documentType.equals("E") || documentType.equals("N")
							|| documentType.equals("M") || documentType.equals("D"))) {
						throw new Exception("Dosya Türü Deðeri Yanlýþ! Yevmiye No:" + offIdNo);
					}
					String documentOtherDesc = jr.getDocumentOtherDesc();
					if (documentType.equals("D") && (documentOtherDesc == null || documentOtherDesc.isEmpty())) {
						throw new Exception(
								"Dosya Türü D Olduðunda Diðer Dosya Türü Bilgisi Girilmelidir! Yevmiye No:" + offIdNo);
					}
					if (documentType.equals("F") || documentType.equals("C") || documentType.equals("D")) {
						String documentNo = jr.getDocumentNo();
						String documentDate = jr.getDocumentDate();
						if (documentNo == null || documentNo.isEmpty() || documentDate == null
								|| documentDate.isEmpty()) {
							if (documentType.equals("D")) {
								throw new Exception(
										"Diðer Dosya Türü Ýçin Dosya Tarihi ve Dosya No Bilgileri Girilmelidir! Yevmiye No:"
												+ offIdNo);
							} else {
								throw new Exception(
										"Seçilen Dosya Türü Ýçin Dosya Tarihi ve Dosya No Bilgileri Girilmelidir! Yevmiye No:"
												+ offIdNo);
							}
						}
					}
				}
				String paymentType = jr.getPaymentType();
				if (paymentType != null) {
					if (!(paymentType.equals("B") || paymentType.equals("K") || paymentType.equals("C")
							|| paymentType.equals("S") || paymentType.equals("D"))) {
						throw new Exception("Ödeme Þekli Deðeri Yanlýþ! Yevmiye No:" + offIdNo);
					}
					String paymentTypeOtherDesc = jr.getPaymentTypeOtherDesc();
					if (paymentType.equals("D") && (paymentTypeOtherDesc == null || paymentTypeOtherDesc.isEmpty())) {
						throw new Exception(
								"Ödeme Þekli D Olduðunda Diðer Ödeme Þekli Bilgisi ! Yevmiye No:" + offIdNo);
					}
				}

				jrList.add(jr);
				if (!eskiVerilerSilindi && ("A".equals(jr.getYon()) || "B".equals(jr.getYon()))) {
					String voucheDateSubStr = jr.getVoucherDate().substring(2, 10);

				}
			}
			if (jrList.size() > 0) {
				System.out.println("insertListSize.1 :" + jrList.size());
				totalInsertCount += jrList.size();
				checkIfDebtsAndCreditsEqual(jrList);
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

	private static void checkIfDebtsAndCreditsEqual(List<JournalRow> jrList) {

		Map<String, TotalJournalRow> totalJournalMap = new HashMap<String, TotalJournalRow>();

		for (JournalRow journalRow : jrList) {

			if (totalJournalMap.containsKey(journalRow.getVoucherId())) {
				TotalJournalRow totalJournalRow = totalJournalMap.get(journalRow.getVoucherId());
				totalJournalRow.setCrd(journalRow.getCrd().add(totalJournalRow.getCrd()));
				totalJournalRow.setDbt(journalRow.getDbt().add(totalJournalRow.getDbt()));
			} else {
				TotalJournalRow totalJournalRow = new TotalJournalRow();
				totalJournalRow.setVoucherId(journalRow.getVoucherId());
				totalJournalRow.setDbt(journalRow.getDbt());
				totalJournalRow.setCrd(journalRow.getCrd());
				totalJournalMap.put(totalJournalRow.getVoucherId(), totalJournalRow);
			}

		}

		Iterator iter = totalJournalMap.entrySet().iterator();
		while (iter.hasNext()) {
			Entry thisEntry = (Entry) iter.next();
			String voucherId = (String)thisEntry.getKey();
			TotalJournalRow totalJournalRow = (TotalJournalRow)thisEntry.getValue();
			if(totalJournalRow.getCrd().compareTo(totalJournalRow.getDbt()) != 0 ) {
				System.out.println("hatali voucherId = "  + voucherId + " credit =  " + totalJournalRow.getCrd() + " debit = "  + totalJournalRow.getDbt()) ;
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
