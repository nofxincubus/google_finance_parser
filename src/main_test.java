
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
		
		ArrayList<String> symbols = CompanyClass.parseSymbols("industry.csv");
		YahooParser yahooParser = new YahooParser();
		int i = 0;
		int tempCounter = 0;
		String getSymbolString = "";
		while (i < symbols.size()){
			if (tempCounter < 200){
				getSymbolString = getSymbolString + symbols.get(i) + "+";
				tempCounter++;
				if (tempCounter == 200){
					getSymbolString = getSymbolString.substring(0, getSymbolString.length()-1);
					System.out.println(getSymbolString);
					getSymbolString = "";
					tempCounter = 0;
				}
			}
			i++;
		}
		
		//combineData("income_nasdaq.csv","income_nyse_1.csv","income_nyse_2.csv","income.csv");
		//combineData("balance_nasdaq.csv","balance_nyse_1.csv","balance_nyse_2.csv","balance.csv");
		//combineData("cash_nasdaq.csv","cash_nyse_1.csv","cash_nyse_2.csv","cash.csv");
		//combineIndustryData("nasdaqlist.csv","nyselist.csv","industry.csv");
		//parseTrailingTwelve("income.csv", "income_ttm.csv");
		//parseTrailingTwelve("cash.csv", "cash_ttm.csv");
	}
	
	public static void combineData(String fileNameOne,String fileNameTwo,String fileNameThree,String outputName){
		CSVReader readerOne;
		CSVReader readerTwo;
		CSVReader readerThree;
		CSVWriter writer;
		try {
			readerOne = new CSVReader(new FileReader("./" + fileNameOne));
			String oneExchange = "";
			if (fileNameOne.contains("nyse")){
				oneExchange = "_nyse";
			} else if (fileNameOne.contains("nasdaq")){
				oneExchange = "_nasdaq";
			}
			readerTwo = new CSVReader(new FileReader("./" + fileNameTwo));
			String twoExchange = "";
			if (fileNameTwo.contains("nyse")){
				twoExchange = "_nyse";
			} else if (fileNameTwo.contains("nasdaq")){
				twoExchange = "_nasdaq";
			}
			readerThree = new CSVReader(new FileReader("./" + fileNameThree));
			String threeExchange = "";
			if (fileNameThree.contains("nyse")){
				threeExchange = "_nyse";
			} else if (fileNameThree.contains("nasdaq")){
				threeExchange = "_nasdaq";
			}
			writer = new CSVWriter(new FileWriter("./" + outputName));
			String [] header = readerOne.readNext();
			readerTwo.readNext();
			readerThree.readNext();
			writer.writeNext(header);
			String [] nextLine;
			int lastIndex = 0;
			while ((nextLine = readerOne.readNext()) != null){
				nextLine[1].replace(" ", "");
				nextLine[1] = nextLine[1].trim() + oneExchange;
				lastIndex = Integer.parseInt(nextLine[0]);
				writer.writeNext(nextLine);
			}
			lastIndex++;
			while ((nextLine = readerTwo.readNext()) != null){
				nextLine[1].replace(" ", "");
				nextLine[1] = nextLine[1].trim() + twoExchange;
				nextLine[0] = lastIndex + "";
				writer.writeNext(nextLine);
				lastIndex++;
			}
			lastIndex++;
			while ((nextLine = readerThree.readNext()) != null){
				nextLine[1].replace(" ", "");
				nextLine[1] = nextLine[1].trim() + threeExchange;
				nextLine[0] = lastIndex + "";
				writer.writeNext(nextLine);
				lastIndex++;
			}
			readerOne.close();
			readerTwo.close();
			readerThree.close();
			writer.close();
		}  catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void combineIndustryData(String fileNameOne,String fileNameTwo,String outputName){
		CSVReader readerOne;
		CSVReader readerTwo;
		CSVWriter writer;
		try {
			readerOne = new CSVReader(new FileReader("./" + fileNameOne));
			readerTwo = new CSVReader(new FileReader("./" + fileNameTwo));
			writer = new CSVWriter(new FileWriter("./" + outputName));
			String [] header = readerOne.readNext();
			readerTwo.readNext();
			writer.writeNext(header);
			String [] nextLine;
			while ((nextLine = readerOne.readNext()) != null){
				nextLine[0].replace(" ", "");
				nextLine[0] = nextLine[0].trim() + "_nasdaq";
				writer.writeNext(nextLine);
			}
			while ((nextLine = readerTwo.readNext()) != null){
				nextLine[0].replace(" ", "");
				nextLine[0] = nextLine[0].trim() + "_nyse";
				writer.writeNext(nextLine);
			}
			readerOne.close();
			readerTwo.close();
			writer.close();
		}  catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void parseTrailingTwelve(String fileName, String outputName){
		CSVReader reader;
		CSVWriter writer;
		try {
			reader = new CSVReader(new FileReader("./" + fileName));
			writer = new CSVWriter(new FileWriter("./" + outputName));
			String [] nextLine;
			String [] heading = reader.readNext();
			writer.writeNext(heading);
			boolean nextTickerFlag = false;
			String [] trailing = null;
			//String [] current;
			String [] next;
			while ((nextLine = reader.readNext()) != null) {
				if (trailing != null){
					System.out.print(nextLine[1] + " " + trailing[1] + "\n");
					if (!nextLine[1].equalsIgnoreCase(trailing[1])){
						trailing = null;
						nextTickerFlag = false;
					}
				}
				
				//Quarterly Only and if moving onto nextTickerFlag
				if (!nextLine[3].contains("0") && !nextTickerFlag){
					next = nextLine;
					if (trailing == null){
						trailing = next;
						if (trailing[5].contains("week")){
							trailing[6] = Float.parseFloat(trailing[6])*7 + "";
							trailing[5] = "days";
						} else if (trailing[5].contains("month")){
							trailing[6] = Float.parseFloat(trailing[6])*30.4375 + "";
							trailing[5] = "days";
						}
						continue;
					}
					Calendar currentEndDate = Calendar.getInstance();
					Calendar nextEndDate = Calendar.getInstance();
					SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
					SimpleDateFormat formatterWTF = new SimpleDateFormat("yyyy-MM-dd");
					try {
						if (trailing[7].contains("/")){
							currentEndDate.setTime(formatter.parse(trailing[7]));
							nextEndDate.setTime(formatter.parse(next[7]));
						} else if (trailing[7].contains("-")){
							currentEndDate.setTime(formatterWTF.parse(trailing[7]));
							nextEndDate.setTime(formatterWTF.parse(next[7]));
						}
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					 if (trailing != null) {
						if (next[5].contains("week")){
							next[6] = Float.parseFloat(next[6])*7 + "";
							next[5] = "days";
						} else if (next[5].contains("month")){
							next[6] = Float.parseFloat(next[6])*30.4375 + "";
							next[5] = "days";
						}
						Calendar currentStartDate = getStartDate(currentEndDate,trailing);
						Calendar nextStartDate = getStartDate(nextEndDate, next);
						//currentEndDate.set(Calendar.MILLISECOND, 0);
						System.out.print(currentEndDate.get(Calendar.YEAR) + " " + currentEndDate.get(Calendar.MONTH) + " " + 
								currentEndDate.get(Calendar.DAY_OF_MONTH) + "\n");
						System.out.print(currentStartDate.get(Calendar.YEAR) + " " + currentStartDate.get(Calendar.MONTH) + " " + 
								currentStartDate.get(Calendar.DAY_OF_MONTH) + "\n");
						float periodInDays = timeBetweenTwoDates(currentEndDate, currentStartDate);
						if (currentStartDate.before(nextEndDate)){
							if (nextStartDate.before(currentStartDate)){
								
								trailing = subtractData(trailing,
										multiplyConstant(trailing, timeBetweenTwoDates(nextEndDate,currentStartDate)/periodInDays));
								trailing = addData(trailing,next);
							}
						} else {
							if (nextEndDate.before(currentStartDate) ||
									nextEndDate.equals(currentStartDate)){
								trailing = addData(trailing,next);
							}
						}
						if (Float.parseFloat(trailing[6]) >= 360){
							Calendar trailingEndDate = Calendar.getInstance();
							try {
								if (trailing[7].contains("/")){
									trailingEndDate.setTime(formatter.parse(trailing[7]));
								} else if (trailing[7].contains("-")){
									trailingEndDate.setTime(formatterWTF.parse(trailing[7]));
								}
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							trailingEndDate.add(Calendar.DAY_OF_YEAR, -366);
							float diffPeriodInDays = timeBetweenTwoDates(trailingEndDate, nextStartDate);
							float nextPeriodInDays = timeBetweenTwoDates(nextEndDate, nextStartDate);
							trailing = subtractData(trailing,multiplyConstant(next,diffPeriodInDays/nextPeriodInDays));
							writer.writeNext(trailing);
							nextTickerFlag = true;
						}
						
					}
				}
			}
			reader.close();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String [] multiplyConstant(String []first, float constant){
		String [] result = first.clone();
		result[6] = (Float.parseFloat(result[6]) * constant) + "";
		for (int i = 8;i < first.length;i++){
			if (first[i].contains(" ") || first[i].isEmpty())
				break;
			result[i] = Float.parseFloat(first[i])*constant + "";
			
		}
		return result;
	}

	public static String [] subtractData(String []first, String []second){
		String [] result = first;
		result[6] = (Float.parseFloat(result[6]) - Float.parseFloat(second[6])) + "";
		for (int i = 8;i < first.length;i++){
			if (first[i].contains(" ") || first[i].isEmpty())
				break;
			result[i] = Float.parseFloat(first[i]) - Float.parseFloat(second[i]) + "";
		}
		return result;
	}

	public static String [] addData(String []first, String []second){
		String [] result = first;
		result[6] = (Float.parseFloat(result[6]) + Float.parseFloat(second[6])) + "";
		for (int i = 8;i < first.length;i++){
			if (first[i].contains(" ") || first[i].isEmpty())
				break;
			result[i] = Float.parseFloat(first[i]) + Float.parseFloat(second[i]) + "";
		}
		return result;
	}

	public static Calendar getStartDate(Calendar endDate, String [] lineString){
		long currentQuarterCount =  Math.round(Float.parseFloat(lineString[6]));
		//String currentQuarterRange = lineString[5];
		
		Calendar currentStartDate = (Calendar) endDate.clone();
		currentStartDate.setTimeInMillis((long) (endDate.getTimeInMillis() - currentQuarterCount * 86400000));   
		System.out.print("period day : " + lineString[6] + "\n");
		System.out.print(endDate.get(Calendar.YEAR) + "/" + endDate.get(Calendar.MONTH) + "/" +
		endDate.get(Calendar.DAY_OF_MONTH)  + "\n");
		System.out.print(currentStartDate.get(Calendar.YEAR) + "/" + currentStartDate.get(Calendar.MONTH) + "/" +
				currentStartDate.get(Calendar.DAY_OF_MONTH)  + "\n\n\n");
		return currentStartDate;
	}


	public static int monthToWeek(int month, String range) {
		float week = month;
		if (range.contains("months")){
			week = month * 4.34812f;
		} else {
			int boobies = 1;
			System.out.print("" + boobies);
		}

		return Math.round(week);
	}
	
	public static float timeBetweenTwoDates(Calendar calendar1, Calendar calendar2){
	    return (calendar1.getTimeInMillis() - calendar2.getTimeInMillis())/(24 * 60 * 60 * 1000);
	}

	//Aggregate all data
	public static void runAggregate(){
		ArrayList<CompanyClass> companyList = new ArrayList<CompanyClass>();

		companyList = CompanyClass.parseCSV("leftovernyse.csv");
		webParser = new WebParser();

		for (CompanyClass cClass:companyList){
			webParser.loadContent(cClass.companySymbol);
		}

		//webParser.loadContent("INTC");
		writeCSV();
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
			incomeWriter = new CSVWriter(new FileWriter("./income_nyse.csv"));
			balanceWriter = new CSVWriter(new FileWriter("./balance_nyse.csv"));
			cashflowWriter = new CSVWriter(new FileWriter("./cash_nyse.csv"));
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
