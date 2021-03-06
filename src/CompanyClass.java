import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import au.com.bytecode.opencsv.CSVReader;


public class CompanyClass {
	static String COMPANY_TABLE_NAME = "company";
	static String COMPANY_NAME_COLUMN = "name";
	static String COMPANY_SYMBOL_COLUMN = "symbol";
	static String COMPANY_MARKETCAP_COLUMN = "cap";
	
	static String COMPANY_TABLE_QUERY_STRING = "CREATE TABLE IF NOT EXISTS " + 
            CompanyClass.COMPANY_TABLE_NAME + " (Id LONG PRIMARY KEY AUTO_INCREMENT, "
    		+ CompanyClass.COMPANY_NAME_COLUMN + " VARCHAR(25),"
    		+ CompanyClass.COMPANY_SYMBOL_COLUMN + " VARCHAR(20),"
    		+ CompanyClass.COMPANY_MARKETCAP_COLUMN + " FLOAT) ENGINE=InnoDB;";
	
	
	long id;
	String companySymbol;
	String companyName;
	String industry;
	String sector;
	float marketCap;
	
	public static ArrayList<CompanyClass> parseCSV(String fileName){
		ArrayList<CompanyClass> companyList = new ArrayList<CompanyClass>();
		CSVReader reader;
		try {
			reader = new CSVReader(new FileReader("./" + fileName));
			String [] nextLine;
			CompanyClass newCompany;
			reader.readNext();
		    while ((nextLine = reader.readNext()) != null) {
		        // nextLine[] is an array of values from the line
		    	newCompany = new CompanyClass();
		    	newCompany.companySymbol = nextLine[0];
		    	newCompany.companyName = nextLine[1];
		    	newCompany.sector = nextLine[6];
		    	newCompany.industry = nextLine[7];
		    	try {
		    	newCompany.marketCap = Float.parseFloat(nextLine[3]);
		    	} catch (NumberFormatException nfe){
		    		newCompany.marketCap = 0;
		    	}
		    	companyList.add(newCompany);
		    }
		    reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
		
		return companyList;
	}
	
	public static ArrayList<String> parseSymbols(String fileName){
		ArrayList<String> symbolList = new ArrayList<String>();
		CSVReader reader;
		try {
			reader = new CSVReader(new FileReader("./" + fileName));
			String [] nextLine;
			reader.readNext();
		    while ((nextLine = reader.readNext()) != null) {
		        // nextLine[] is an array of values from the line
		    	String name = nextLine[0].substring(0, nextLine[0].indexOf("_"));
	    		name = name.replace("^", ".");
	    		name = name.replace("/", ".");
		    	symbolList.add(nextLine[0]);
		    }
		    reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
		
		return symbolList;
	}
	
	public static ArrayList<String> parseSymbolsOnly(String fileName){
		ArrayList<String> symbolList = new ArrayList<String>();
		CSVReader reader;
		try {
			reader = new CSVReader(new FileReader("./" + fileName));
			String [] nextLine;
			reader.readNext();
		    while ((nextLine = reader.readNext()) != null) {
		        // nextLine[] is an array of values from the line
		    	String name = nextLine[0].substring(0, nextLine[0].indexOf("_"));
	    		name = name.replace("^", "-");
	    		name = name.replace("/", ".");
		    	symbolList.add(name);
		    }
		    reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
		
		return symbolList;
	}
	
	public static ArrayList<String> parseExchangeOnly(String fileName){
		ArrayList<String> symbolList = new ArrayList<String>();
		CSVReader reader;
		try {
			reader = new CSVReader(new FileReader("./" + fileName));
			String [] nextLine;
			reader.readNext();
		    while ((nextLine = reader.readNext()) != null) {
		        // nextLine[] is an array of values from the line
		    	String name = nextLine[0].substring(nextLine[0].indexOf("_") + 1, nextLine[0].length());
		    	symbolList.add(name);
		    }
		    reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
		
		return symbolList;
	}

}
