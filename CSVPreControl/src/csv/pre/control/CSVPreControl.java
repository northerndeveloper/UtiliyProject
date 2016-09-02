package csv.pre.control;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseBigDecimal;
import org.supercsv.cellprocessor.ParseLong;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

public class CSVPreControl {

	final String selectedDocumentPath = null;

	static StringBuffer errorLines = new StringBuffer();
	
	static boolean hataliKayitBulundu = false;

	static Text checkDoubleQuoteErrorControlTextArea = null;

	public static final String TITLE = "CSV Pre Validation";

	public static final String CHECK_DOUBLE_QUOTE_TITLE = "Çift Týrnaklarý Kontrol Et";
	
	public static final String CHECK_DEBIT_CREDIT = "Borç Alacak Kontrol Et";

	public static List<String> linesOfCsvToCheckErrors;

	public static final int QUOTE_NECESSARY_FOR_A_COLUMN = 2;
	
	public static int debitCreditErrorLineCount = 0;

	static Map<String, Incomes> incomeMap = new HashMap<String, Incomes>();
	
	public static void main(String[] args) {
		Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setSize(300, 200);
		shell.setText(TITLE);

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		shell.setLayout(gridLayout);

		final Button fileUploadButton = new Button(shell, SWT.PUSH);
		fileUploadButton.setText("Dosya Seç");

		final Text text = new Text(shell, 0);
		text.setBounds(200, 100, 200, 50);
		text.setTextLimit(200);

		fileUploadButton.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent event) {

				FileDialog fd = new FileDialog(shell, SWT.OPEN);
				fd.setText("Open");
				fd.setFilterPath("C:/");
				String[] filterExt = { "*.csv" };
				fd.setFilterExtensions(filterExt);
				String selectedDocumentPath = fd.open();
				text.setText(selectedDocumentPath);
			}

			public void widgetDefaultSelected(SelectionEvent event) {
				text.setText("No worries!");
			}
		});

		final Button dosyaKontrolEt = new Button(shell, SWT.PUSH);
		dosyaKontrolEt.setText(CHECK_DOUBLE_QUOTE_TITLE);

		dosyaKontrolEt.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent event) {
				controlDoubleQuotes(text.getText(), shell);

			}

			public void widgetDefaultSelected(SelectionEvent event) {
				text.setText("No worries!");
			}
		});
		
		final Button debitCreditControlBtn = new Button(shell, SWT.PUSH);
		debitCreditControlBtn.setText(CHECK_DEBIT_CREDIT);

		debitCreditControlBtn.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent event) {
				try {
					checkDebitCredit(text.getText(), shell);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
			public void widgetDefaultSelected(SelectionEvent event) {
				text.setText("No worries!");
			}
		});

		checkDoubleQuoteErrorControlTextArea = new Text(shell, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.WRAP);
		checkDoubleQuoteErrorControlTextArea
				.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL));

		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();

	}

	public static void controlDoubleQuotes(String filePathToControl, Shell shell) {
		
		cleanTextArea();
		
		checkFilePath(filePathToControl, shell);
		
		readLinesAndAddThemToList(filePathToControl);

		for (String lineOfCsvToCheckError : linesOfCsvToCheckErrors) {
			splitLinesByCommaAndCheckIfAnyMistakes(lineOfCsvToCheckError);
		}
		
		checkDoubleQuoteErrorControlTextArea.setText(errorLines.toString());

	}
	
	public static void cleanTextArea() {
		
		debitCreditErrorLineCount = 0;
		errorLines = new StringBuffer();
		checkDoubleQuoteErrorControlTextArea.setText("");
		
	}

	public static void readLinesAndAddThemToList(String filePathToControl) {

		linesOfCsvToCheckErrors = new ArrayList<String>();

		try (BufferedReader br = new BufferedReader(new FileReader(filePathToControl))) {
			String line;
			while ((line = br.readLine()) != null) {
				linesOfCsvToCheckErrors.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		removeHeader();
	}

	public static void removeHeader() {
		linesOfCsvToCheckErrors.remove(0);
	}

	public static void splitLinesByCommaAndCheckIfAnyMistakes(String lineToBeSplittedAndChecked) {

		List<String> commaSplittedLine = splitLinesByComma(lineToBeSplittedAndChecked);
		if (checkIfAnyMistake(commaSplittedLine))
			errorLines.append(commaSplittedLine + "\n");
		
	}

	public static List splitLinesByComma(String lineToBeSplittedAndChecked) {
		return Arrays.asList(lineToBeSplittedAndChecked.split(","));
	}

	public static boolean checkIfAnyMistake(List<String> splittedLineToCheckIfAnyMistake) {

		for (String columnOfLineToBeChecked : splittedLineToCheckIfAnyMistake) {
			int count = columnOfLineToBeChecked.length() - columnOfLineToBeChecked.replace("\"", "").length();
			if (count > QUOTE_NECESSARY_FOR_A_COLUMN) {
				return true;
			}
		}

		return false;
	}

	
	public static void checkDebitCredit(String filePathToControl, Shell shell) throws Exception {
		
		cleanTextArea();
		
		checkFilePath(filePathToControl, shell);
		
		ICsvBeanReader beanReader = null;
		
		long kayitSeqNo = 0;
		int insertCount = 0;
		int totalInsertCount = 0;
		boolean eskiVerilerSilindi = false;
		try {
			FileInputStream fis = new FileInputStream(filePathToControl);
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

	private static void checkFilePath(String filePathToControl, Shell shell) {
		if(filePathToControl == null || filePathToControl.equals("")) {
			MessageBox dialog =  new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
					dialog.setText("UYARI");
					dialog.setMessage("Dosya Seçmeden Hata Kontrolü yapamazsýnýz");
					dialog.open();
					return;
		}
	}

	private static void checkIfDebtsAndCreditsEqual() {

		for(Map.Entry<String, Incomes> entry : incomeMap.entrySet()) {
			Incomes incomes = entry.getValue();
			if(incomes.getCrd().compareTo(incomes.getDbt()) != 0) {
				errorLines.append("hatali voucherId = "  + entry.getKey() + " credit =  " + incomes.getCrd() + " debit = "  + incomes.getDbt() + "\n");
				hataliKayitBulundu = true;
			}
		}
		
		if(!hataliKayitBulundu)  {
			errorLines.append("Hatali kayit bulunmamistir. CSV hatali degildir.");
		}
			
		checkDoubleQuoteErrorControlTextArea.setText(errorLines.toString());
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
