import java.io.FileInputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class DebitCreditEqualCheckXLSX  {

	public static void main(String[] args) throws Exception {
		

		Row row = null;
		try {
			//String excelFile = "C:\\Users\\akopuz\\Downloads\\181-bpl-01012016-31032016.xlsx";
			String excelFile = excelFile = args[0];
			if(excelFile == null || excelFile.equals("")) {
				System.out.println("Lütfen aktaricaginiz xls dosyasini yaziniz");
			}
			FileInputStream file = new FileInputStream(excelFile);
			Workbook workbook = new XSSFWorkbook(file);
			long kayitSeqNo = 0;
			// Get first sheet from the workbook
			Sheet sheet = workbook.getSheetAt(0);
			// Get iterator to all the rows in current sheet
			Iterator<Row> rowIterator = sheet.iterator();
			List<JournalRow> journalRowList = new ArrayList<JournalRow>();
			int totalInsertCount = 0;
			JournalRow jr = new JournalRow();
			String insertResult;
			String yevmiyeNo = null;
			String tarih = null;
			String referansNo = null;
			
			while (rowIterator.hasNext()) {
				
				jr = new JournalRow();
				row = rowIterator.next();
		
				if(row.getCell(0).getCellType() == Cell.CELL_TYPE_BLANK || (row.getCell(0).getCellType() == Cell.CELL_TYPE_STRING && (!row.getCell(0).getStringCellValue().startsWith("--") && !row.getCell(0).getStringCellValue().contains("-"))))
					continue;
				
				if(row.getCell(0).getCellType() == Cell.CELL_TYPE_STRING  && row.getCell(0).getStringCellValue().startsWith("--")){
					String headLine = row.getCell(0).getStringCellValue();
					String yevmiyeStr = "-----Yevmiye No:";
					String tarihStr = "-----Tarih:";
					String referansStr = "-----Referans :";
					yevmiyeNo = headLine.substring(headLine.indexOf(yevmiyeStr) + yevmiyeStr.length(), headLine.indexOf(tarihStr));
					tarih = headLine.substring(headLine.indexOf(tarihStr) + tarihStr.length() + 1 , headLine.indexOf(referansStr));
					referansNo = headLine.substring(headLine.indexOf(referansStr) + referansStr.length() + 1 , headLine.indexOf("------------------------------------------------------------------------"));
					continue;
				}
				
				jr.setOffIdNo(Long.parseLong(yevmiyeNo));
				jr.setVoucherDate(tarih);
				jr.setVoucherId(referansNo);
				jr.setSeqNo(++kayitSeqNo);
				
				if(row.getCell(0) != null){
					if(row.getCell(0).getCellType() == Cell.CELL_TYPE_STRING) {
						jr.setAccNo(row.getCell(0).getStringCellValue());
					}else {
						jr.setAccNo(String.valueOf(row.getCell(0).getNumericCellValue()).split("\\.")[0]);
					}
					
				}
				
				if(row.getCell(2) != null){
					jr.setAccDesc(row.getCell(2).getStringCellValue());
				}
				
				if(row.getCell(4) != null){
					jr.setVoucherDesc(row.getCell(4).getStringCellValue());
				}
				
				if(row.getCell(6) != null){
					double borc = row.getCell(6).getNumericCellValue();
					if( borc != 0.0 ){
						jr.setYon("B");
					}
					jr.setDbt(new BigDecimal(row.getCell(6).getNumericCellValue()).setScale(2,RoundingMode.HALF_UP));
				}
				
				if(row.getCell(8) != null){
					double alacak = row.getCell(8).getNumericCellValue();
					if( alacak != 0.0 ){
						jr.setYon("A");
					}
					jr.setCrd(new BigDecimal(row.getCell(8).getNumericCellValue()).setScale(2,RoundingMode.HALF_UP));
				}
				journalRowList.add(jr);
			}
			
			if (journalRowList.size() > 0) {
				System.out.println("insertListSize.1 :" + journalRowList.size());
				totalInsertCount += journalRowList.size();
				checkIfDebtsAndCreditsEqual(journalRowList);
			}
			System.out.println("totalinsertCount = " + totalInsertCount);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Kayýtlar atýlýrken hata oluþtu! Hata: " + e.getMessage());
		} finally {
			
		}

	}

	private static void checkIfDebtsAndCreditsEqual(List<JournalRow> jrList) {

		Map<String, TotalJournalRow> totalJournalMap = new HashMap<String, TotalJournalRow>();
		int hataliKayit = 0;

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
				hataliKayit = hataliKayit + 1;
				System.out.println("hatali kayit = " + hataliKayit);
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
