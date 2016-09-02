import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FindErorQuotes {
	
	public static File fileToRead;
	
	public static List<String> linesOfCsvToCheckErrors;

	public static final int QUOTE_NECESSARY_FOR_A_COLUMN = 2;

	public static void main(String[] args) {
		
		fileToRead = new File("O:\\alper\\still arser csv\\1000201603.csv");
		readLinesAndAddThemToList();
		
		for(String lineOfCsvToCheckError : linesOfCsvToCheckErrors) {
			splitLinesByCommaAndCheckIfAnyMistakes(lineOfCsvToCheckError);
		}
		
	}

	public static void readLinesAndAddThemToList()  {
		
		linesOfCsvToCheckErrors = new ArrayList<String>();
		
		try (BufferedReader br = new BufferedReader(new FileReader(fileToRead))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		    	linesOfCsvToCheckErrors.add(line);
		    }
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		removeHeader();
	}
	
	public static void removeHeader(){
		linesOfCsvToCheckErrors.remove(0);
	}
	
	public static void splitLinesByCommaAndCheckIfAnyMistakes(String lineToBeSplittedAndChecked) {
		
		List<String> commaSplittedLine = splitLinesByComma(lineToBeSplittedAndChecked);
		if (checkIfAnyMistake(commaSplittedLine))
			System.out.println(commaSplittedLine);
		
		
	}
	
	
	public static List splitLinesByComma(String lineToBeSplittedAndChecked) {
		return Arrays.asList(lineToBeSplittedAndChecked.split(","));
	}
	
	public static boolean checkIfAnyMistake(List<String> splittedLineToCheckIfAnyMistake) {
		
		for(String columnOfLineToBeChecked : splittedLineToCheckIfAnyMistake) {
			int count = columnOfLineToBeChecked.length() - columnOfLineToBeChecked.replace("\"", "").length();
			if(count > QUOTE_NECESSARY_FOR_A_COLUMN) {
				return true;
			}
		}
		
		return false;
	}
	
	
	
	// okudugun her line i sonrasýnda , leri bol
	
	// sonrasýnda o line lari karsilastir 
	
}
