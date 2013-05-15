
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class main_test {

	static String COMPANY_TABLE_NAME = "company";
	static String COMPANY_NAME_COLUMN = "name";
	static String COMPANY_SYMBOL_COLUMN = "symbol";
	static String COMPANY_MARKETCAP_COLUMN = "cap";

	static String COMPANY_TABLE_QUERY_STRING = "CREATE TABLE IF NOT EXISTS " + 
			COMPANY_TABLE_NAME + " (Id LONG PRIMARY KEY AUTO_INCREMENT, "
			+ COMPANY_NAME_COLUMN + " VARCHAR(25),"
			+ COMPANY_SYMBOL_COLUMN + " VARCHAR(20),"
			+ COMPANY_MARKETCAP_COLUMN + " FLOAT) ENGINE=InnoDB;";
	static WebParser webParser;

	public static void main(String[] args) {

		ArrayList<CompanyClass> companyList = new ArrayList<CompanyClass>();

		companyList = CompanyClass.parseCSV("nasdaqlist.csv");
		webParser = new WebParser();
		/*
		for (CompanyClass cClass:companyList){
			webParser.loadContent(cClass.companySymbol);
		}
		*/
		webParser.loadContent("INTC");
		writeCSV();



	}


	public static void parseNasDaq(String fileName){
		CSVWriter incomeWriter;
		CSVWriter balanceWriter;
		CSVWriter cashflowWriter;
		CSVReader reader;
		CSVReader incomeHeaderReader;
		CSVReader balanceHeaderReader;
		CSVReader cashflowHeaderReader;
		try {
			reader = new CSVReader(new FileReader("./" + fileName));
			incomeHeaderReader = new CSVReader(new FileReader("./BalanceSheetHeadings.csv"));
			balanceHeaderReader = new CSVReader(new FileReader("./CashFlowHeadings.csv" + fileName));
			cashflowHeaderReader = new CSVReader(new FileReader("./IncomeStatementHeadings.csv" + fileName));
			incomeWriter = new CSVWriter(new FileWriter("./income_nasdaq.csv"));
			balanceWriter = new CSVWriter(new FileWriter("./balance_nasdaq.csv"));
			cashflowWriter = new CSVWriter(new FileWriter("./cash_nasdaq.csv"));
			String [] nextLine = incomeHeaderReader.readNext();
			String [] newHeading = addIndex(nextLine);
			
			
			ArrayList<String> stringArray = new ArrayList<String>();
			while ((nextLine = reader.readNext()) != null) {
				// nextLine[] is an array of values from the line
				if (nextLine[3].contains("Financial")){
					incomeWriter.writeNext(nextLine);
				} else if (nextLine[3].contains("Balance")){
					incomeWriter.writeNext(nextLine);
				} else if (nextLine[3].contains("Financial")){
					incomeWriter.writeNext(nextLine);
				}
			}
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public static String[] addIndex (String [] oldString){
		String []newString = new String[oldString.length + 1];
		int counter = 1;
		newString[0] = "index";
		for (String str:oldString){
			newString[counter] = str;
		}		
		return newString;
	}


	public static void writeCSV(){
		CSVWriter incomeWriter;
		CSVWriter balanceWriter;
		CSVWriter cashflowWriter;
		CSVReader incomeHeaderReader;
		CSVReader balanceHeaderReader;
		CSVReader cashflowHeaderReader;
		
		try {
			int countFinancial= 0;
			int countBalance= 0;
			int countCashflow= 0;
			balanceHeaderReader = new CSVReader(new FileReader("./BalanceSheetHeadings.csv"));
			cashflowHeaderReader = new CSVReader(new FileReader("./CashFlowHeadings.csv"));
			incomeHeaderReader = new CSVReader(new FileReader("./IncomeStatementHeadings.csv"));
			incomeWriter = new CSVWriter(new FileWriter("./income_nasdaq.csv"));
			balanceWriter = new CSVWriter(new FileWriter("./balance_nasdaq.csv"));
			cashflowWriter = new CSVWriter(new FileWriter("./cash_nasdaq.csv"));
			String[] incomeHeaderSans = incomeHeaderReader.readNext();
			String[] balanceHeaderSans = balanceHeaderReader.readNext();
			String[] cashflowHeaderSans = cashflowHeaderReader.readNext();
			incomeHeaderReader.close();
			balanceHeaderReader.close();
			cashflowHeaderReader.close();
			String[] header = new String [incomeHeaderSans.length + 9];
			String[] header1 = new String [balanceHeaderSans.length + 9];
			String[] header2 = new String [cashflowHeaderSans.length + 9];
			header[0] = header1[0] = header2[0] = "index";
			header[1] = header1[1] = header2[1] = "ticker";
			header[2] = header1[2] = header2[2] = "year";
			header[3] = header1[3] = header2[3] = "quarter";
			header[4] = header1[4] = header2[4] = "QuarterHeading";
			header[5] = header1[5] = header2[5] = "QuarterRange";
			header[6] = header1[6] = header2[6] = "QuarterCountperRange";
			header[7] = header1[7] = header2[7] = "DateString";
			int hCounter = 8;
			for (String num:incomeHeaderSans){
				header[hCounter] = num;
				hCounter++;
			}
			hCounter = 8;
			for (String num:balanceHeaderSans){
				header1[hCounter] = num;
				hCounter++;
			}
			hCounter = 8;
			for (String num:cashflowHeaderSans){
				header2[hCounter] = num;
				hCounter++;
			}
			incomeWriter.writeNext(header);
			balanceWriter.writeNext(header1);
			cashflowWriter.writeNext(header2);
			for (FinancialClass fClass:webParser.companyData){
				String[] data = new String [fClass.financialData.size() + 9];
				data[1] = fClass.ticker;
				data[2] = fClass.year + "";
				data[3] = fClass.quarter + "";
				data[4] = fClass.quarterHeader;
				data[5] = fClass.quarterCounterRange + "s";
				data[6] = fClass.quarterCount + "";
				data[7] = fClass.dateString;
				int counter = 8;
				for (float num:fClass.financialData){
					data[counter] = num + "";
					counter++;
				}
				if (fClass.eDataType == FinancialClass.DataType.eFinancial){
					data[0] = countFinancial + "";
					countFinancial++;
					incomeWriter.writeNext(data);
				} else if (fClass.eDataType == FinancialClass.DataType.eBalanceSheet){
					data[0] = countBalance + "";
					countBalance++;
					balanceWriter.writeNext(data);
				} else if (fClass.eDataType == FinancialClass.DataType.eCashFlow){
					data[0] = countCashflow + "";
					countCashflow++;
					cashflowWriter.writeNext(data);
				}
				
			}
			incomeWriter.close();
			balanceWriter.close();
			cashflowWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void sqlStuff(){
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;


		String url = "jdbc:mysql://localhost:3306/financedb";
		String user = "calvin";
		String password = "wlgnsl";

		try {
			con = DriverManager.getConnection(url, user, password);
			st = con.createStatement();
			rs = st.executeQuery(CompanyClass.COMPANY_TABLE_QUERY_STRING);

			if (rs.next()) {
				System.out.println(rs.getString(1));
			}

			for (int i=1; i<=1000; i++) {
				String query = "INSERT INTO Testing(Id) VALUES(" + 2*i + ")";
				st.executeUpdate(query);
			}

		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(main_test.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);

		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (st != null) {
					st.close();
				}
				if (con != null) {
					con.close();
				}

			} catch (SQLException ex) {
				Logger lgr = Logger.getLogger(main_test.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}

	}

}
