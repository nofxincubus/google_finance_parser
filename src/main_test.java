
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

		companyList = CompanyClass.parseCSV("leftover.csv");
		webParser = new WebParser();
		for (CompanyClass cClass:companyList){
			webParser.loadContent(cClass.companySymbol);
		}

		writeCSV("yay.csv");



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


	public static void writeCSV(String fileName){
		CSVWriter writer;
		try {
			writer = new CSVWriter(new FileWriter("./" + fileName));
			for (FinancialClass fClass:webParser.companyData){
				String[] data = new String [fClass.financialData.size() + 5];
				data[0] = fClass.ticker;
				data[1] = fClass.year + "";
				data[2] = fClass.quarter + "";
				data[3] = fClass.eDataType + "";
				int counter = 4;
				for (float num:fClass.financialData){
					data[counter] = num + "";
					counter++;
				}
				writer.writeNext(data);
			}
			writer.close();
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
