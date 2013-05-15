
/**
 * @author      Calvin Park <jeehunee@umich.edu>
 * @version     1.0                 (the version of the package this class was first added to)                   
 * @since       2011-10-11          (a date or the version number of this program)
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

class WebParser {
	
	public ArrayList <FinancialClass> companyData = new ArrayList<FinancialClass>();


	public void loadContent(String symbol)
	{
		//Specifying web address to search products
		String webAddress =  "https://www.google.com/finance?q=NYSE%3A" + 
				symbol + "&fstype=ii";

		//Output the complete address
		System.out.println(webAddress);

		URL web;
		URLConnection webCon;
		BufferedReader in;
		try {
			web = new URL(webAddress);
			webCon = web.openConnection();

			in = new BufferedReader(
					new InputStreamReader(
							webCon.getInputStream()));

			//Find where the data starts
			if (parseQuarterlyFinancial(in, symbol)){
				this.parseYearlyFinancial(in, symbol);
				this.parseQuarterlyBalanceSheet(in, symbol);
				this.parseYearlyBalanceSheet(in, symbol);
				this.parseQuarterlyCashFlow(in, symbol);
				this.parseYearlyCashFlow(in, symbol);
			}
			
			in.close();


		} catch (MalformedURLException e) {
			System.out.println("Wrong Search String!");
		} catch (IOException e) {
			System.out.println("All circuits are busy, please try again...");
		}
	}

	private boolean parseQuarterlyFinancial(BufferedReader in, String symbol){
		try {
			while (!in.readLine().contains("<thead>"));
			String getLine = in.readLine();
			int mainCounter = 0;
			while (!getLine.contains("<tbody>")){
				if (getLine.contains("week") || getLine.contains("month") || getLine.contains("day")){
					FinancialClass fClass = new FinancialClass();
					fClass.quarterHeader = getLine;
					if (getLine.contains("week")){
						fClass.quarterCounterRange = "week";
						fClass.quarterCount = Integer.parseInt(getLine.substring(0, getLine.indexOf(" "))); 
					} else if (getLine.contains("month")){
						fClass.quarterCounterRange = "month";
						fClass.quarterCount = Integer.parseInt(getLine.substring(0, getLine.indexOf(" ")));
					} else if (getLine.contains("day")){
						fClass.quarterCounterRange = "day";
						fClass.quarterCount = Integer.parseInt(getLine.substring(0, getLine.indexOf(" ")));
					} else {
						fClass.quarterCounterRange = getLine;
						fClass.quarterCount = Integer.parseInt(getLine.substring(0, getLine.indexOf(" ")));
					}
					fClass.eDataType = FinancialClass.DataType.eFinancial;
					String dateString = getLine.substring(getLine.indexOf("20"), getLine.length());
					fClass.dateString = dateString;
					fClass.ticker = symbol;
					fClass.year = Integer.parseInt(dateString.substring(0, 4));
					if (dateString.contains("-03-")){
						fClass.quarter = 1;
					} else if (dateString.contains("-06-")){
						fClass.quarter = 2;
					} else if (dateString.contains("-09-")){
						fClass.quarter = 3;
					} else if (dateString.contains("-12-")){
						fClass.quarter = 4;
					} else {
						fClass.quarter = -1;
					}
					mainCounter++;
					companyData.add(fClass);

				}
				getLine = in.readLine();
			}
			getLine = in.readLine();
			int secondaryCounter = mainCounter;
			while (!getLine.contains("</tbody>")){
				if (getLine.contains("<td class=\"r")){
					String originalGetLine = getLine;
					if (getLine.contains("<span class=chr>")){
						getLine = getLine.replace("<span class=chr>", "");
						getLine = getLine.replace("</span>", "");
					}
					getLine = getLine.substring(getLine.indexOf(">")+1);
					getLine = getLine.substring(0, getLine.indexOf("<"));
					if (getLine.contains("-") && getLine.length() == 1){
						companyData.get(companyData.size() - secondaryCounter).financialData.add(0f);
					} else {
						if (getLine.contains(",")){
							getLine = getLine.replace(",", "");
						}
						companyData.get(companyData.size() - secondaryCounter).financialData.add(Float.parseFloat(getLine));
					}
					if (secondaryCounter == 1){
						secondaryCounter = mainCounter;
					} else {
						secondaryCounter--;
					}
				}
				getLine = in.readLine();
			}
		} catch (IOException e) {
			System.out.println("All circuits are busy, please try again...");
			return false;
		} catch (NullPointerException npe){
			return false;
		}
		return true;
	}

	private void parseYearlyFinancial(BufferedReader in, String symbol){
		try {
			while (!in.readLine().contains("<thead>"));
			String getLine = in.readLine();
			int mainCounter = 0;
			while (!getLine.contains("<tbody>")){
				if (getLine.contains("year") || getLine.contains("week") || getLine.contains("month") || getLine.contains("day")){
					FinancialClass fClass = new FinancialClass();
					fClass.quarterHeader = getLine;
					if (getLine.contains("week")){
						fClass.quarterCounterRange = "week";
						fClass.quarterCount = Integer.parseInt(getLine.substring(0, getLine.indexOf(" "))); 
					} else if (getLine.contains("month")){
						fClass.quarterCounterRange = "month";
						fClass.quarterCount = Integer.parseInt(getLine.substring(0, getLine.indexOf(" ")));
					} else if (getLine.contains("day")){
						fClass.quarterCounterRange = "day";
						fClass.quarterCount = Integer.parseInt(getLine.substring(0, getLine.indexOf(" ")));
					} else if (getLine.contains("year")){
						fClass.quarterCounterRange = "year";
						fClass.quarterCount = Integer.parseInt(getLine.substring(0, getLine.indexOf(" ")));
					} else {
						fClass.quarterCounterRange = getLine;
						fClass.quarterCount = Integer.parseInt(getLine.substring(0, getLine.indexOf(" ")));
					}
					fClass.eDataType = FinancialClass.DataType.eFinancial;
					String dateString = getLine.substring(getLine.indexOf("20"), getLine.length());
					fClass.dateString = dateString;
					fClass.ticker = symbol;
					fClass.year = Integer.parseInt(dateString.substring(0, 4));
					fClass.quarter = 0;
					mainCounter++;
					companyData.add(fClass);
				}
				getLine = in.readLine();
			}
			
			getLine = in.readLine();
			int secondaryCounter = mainCounter;
			while (!getLine.contains("</tbody>")){
				if (getLine.contains("<td class=\"r")){
					if (getLine.contains("<span class=chr>")){
						getLine = getLine.replace("<span class=chr>", "");
						getLine = getLine.replace("</span>", "");
					}
					getLine = getLine.substring(getLine.indexOf(">")+1);
					getLine = getLine.substring(0, getLine.indexOf("<"));
					if (getLine.contains("-") && getLine.length() == 1){
						companyData.get(companyData.size() - secondaryCounter).financialData.add(0f);
					} else {
						if (getLine.contains(",")){
							getLine = getLine.replace(",", "");
						}
						companyData.get(companyData.size() - secondaryCounter).financialData.add(Float.parseFloat(getLine));
					}
					if (secondaryCounter == 1){
						secondaryCounter = mainCounter;
					} else {
						secondaryCounter--;
					}
				}
				getLine = in.readLine();
			}
		} catch (IOException e) {
			System.out.println("All circuits are busy, please try again...");
		}
	}

	private void parseQuarterlyBalanceSheet(BufferedReader in, String symbol){
		try {
			while (!in.readLine().contains("<thead>"));
			String getLine = in.readLine();
			int mainCounter = 0;
			while (!getLine.contains("<tbody>")){
				if (getLine.contains("As")){
					FinancialClass fClass = new FinancialClass();
					fClass.quarterHeader = getLine;
					fClass.eDataType = FinancialClass.DataType.eBalanceSheet;
					String dateString = getLine.substring(getLine.indexOf("20"), getLine.length());
					fClass.dateString = dateString;
					fClass.ticker = symbol;
					fClass.year = Integer.parseInt(dateString.substring(0, 4));
					if (dateString.contains("-03-")){
						fClass.quarter = 1;
					} else if (dateString.contains("-06-")){
						fClass.quarter = 2;
					} else if (dateString.contains("-09-")){
						fClass.quarter = 3;
					} else if (dateString.contains("-12-")){
						fClass.quarter = 4;
					} else{
						fClass.quarter = -1;
					}
					mainCounter++;
					companyData.add(fClass);
				}
				getLine = in.readLine();
			}
			
			getLine = in.readLine();
			int secondaryCounter = mainCounter;
			while (!getLine.contains("</tbody>")){
				if (getLine.contains("<td class=\"r")){
					if (getLine.contains("<span class=chr>")){
						getLine = getLine.replace("<span class=chr>", "");
						getLine = getLine.replace("</span>", "");
					}
					getLine = getLine.substring(getLine.indexOf(">")+1);
					getLine = getLine.substring(0, getLine.indexOf("<"));
					if (getLine.contains("-") && getLine.length() == 1){
						companyData.get(companyData.size() - secondaryCounter).financialData.add(0f);
					} else {
						if (getLine.contains(",")){
							getLine = getLine.replace(",", "");
						}
						companyData.get(companyData.size() - secondaryCounter).financialData.add(Float.parseFloat(getLine));
					}
					if (secondaryCounter == 1){
						secondaryCounter = mainCounter;
					} else {
						secondaryCounter--;
					}
				}
				getLine = in.readLine();
			}
		} catch (IOException e) {
			System.out.println("All circuits are busy, please try again...");
		}
	}

	private void parseYearlyBalanceSheet(BufferedReader in, String symbol){
		try {
			while (!in.readLine().contains("<thead>"));
			String getLine = in.readLine();
			int mainCounter = 0;
			while (!getLine.contains("<tbody>")){
				if (getLine.contains("As")){
					FinancialClass fClass = new FinancialClass();
					fClass.quarterHeader = getLine;
					fClass.eDataType = FinancialClass.DataType.eBalanceSheet;
					String dateString = getLine.substring(getLine.indexOf("20"), getLine.length());
					fClass.dateString = dateString;
					fClass.ticker = symbol;
					fClass.year = Integer.parseInt(dateString.substring(0, 4));
					fClass.quarter = 0;
					mainCounter++;
					companyData.add(fClass);
				}
				getLine = in.readLine();
			}
			
			getLine = in.readLine();
			int secondaryCounter = mainCounter;
			while (!getLine.contains("</tbody>")){
				if (getLine.contains("<td class=\"r")){
					if (getLine.contains("<span class=chr>")){
						getLine = getLine.replace("<span class=chr>", "");
						getLine = getLine.replace("</span>", "");
					}
					getLine = getLine.substring(getLine.indexOf(">")+1);
					getLine = getLine.substring(0, getLine.indexOf("<"));
					if (getLine.contains("-") && getLine.length() == 1){
						companyData.get(companyData.size() - secondaryCounter).financialData.add(0f);
					} else {
						if (getLine.contains(",")){
							getLine = getLine.replace(",", "");
						}
						companyData.get(companyData.size() - secondaryCounter).financialData.add(Float.parseFloat(getLine));
					}
					if (secondaryCounter == 1){
						secondaryCounter = mainCounter;
					} else {
						secondaryCounter--;
					}
				}
				getLine = in.readLine();
			}
		} catch (IOException e) {
			System.out.println("All circuits are busy, please try again...");
		}
	}

	private void parseQuarterlyCashFlow(BufferedReader in, String symbol){
		try {
			while (!in.readLine().contains("<thead>"));
			String getLine = in.readLine();
			int mainCounter = 0;
			while (!getLine.contains("<tbody>")){
				if (getLine.contains("ending")){
					FinancialClass fClass = new FinancialClass();
					fClass.quarterHeader = getLine;
					if (getLine.contains("week")){
						fClass.quarterCounterRange = "week";
						fClass.quarterCount = Integer.parseInt(getLine.substring(0, getLine.indexOf(" "))); 
					} else if (getLine.contains("month")){
						fClass.quarterCounterRange = "month";
						fClass.quarterCount = Integer.parseInt(getLine.substring(0, getLine.indexOf(" ")));
					} else if (getLine.contains("day")){
						fClass.quarterCounterRange = "day";
						fClass.quarterCount = Integer.parseInt(getLine.substring(0, getLine.indexOf(" ")));
					} else {
						fClass.quarterCounterRange = getLine;
						fClass.quarterCount = Integer.parseInt(getLine.substring(0, getLine.indexOf(" ")));
					}
					fClass.eDataType = FinancialClass.DataType.eCashFlow;
					String dateString = getLine.substring(getLine.indexOf("20"), getLine.length());
					fClass.dateString = dateString;
					fClass.ticker = symbol;
					fClass.year = Integer.parseInt(dateString.substring(0, 4));
					if (dateString.contains("-03-")){
						fClass.quarter = 1;
					} else if (dateString.contains("-06-")){
						fClass.quarter = 2;
					} else if (dateString.contains("-09-")){
						fClass.quarter = 3;
					} else if (dateString.contains("-12-")){
						fClass.quarter = 4;
					} else {
						fClass.quarter = -1;
					}
					mainCounter++;
					companyData.add(fClass);
				}
				getLine = in.readLine();
			}
			
			getLine = in.readLine();
			int secondaryCounter = mainCounter;
			while (!getLine.contains("</tbody>")){
				if (getLine.contains("<td class=\"r")){
					if (getLine.contains("<span class=chr>")){
						getLine = getLine.replace("<span class=chr>", "");
						getLine = getLine.replace("</span>", "");
					}
					getLine = getLine.substring(getLine.indexOf(">")+1);
					getLine = getLine.substring(0, getLine.indexOf("<"));
					if (getLine.contains("-") && getLine.length() == 1){
						companyData.get(companyData.size() - secondaryCounter).financialData.add(0f);
					} else {
						if (getLine.contains(",")){
							getLine = getLine.replace(",", "");
						}
						companyData.get(companyData.size() - secondaryCounter).financialData.add(Float.parseFloat(getLine));
					}
					if (secondaryCounter == 1){
						secondaryCounter = mainCounter;
					} else {
						secondaryCounter--;
					}
				}
				getLine = in.readLine();
			}
		} catch (IOException e) {
			System.out.println("All circuits are busy, please try again...");
		}
	}

	private void parseYearlyCashFlow(BufferedReader in, String symbol){
		try {
			while (!in.readLine().contains("<thead>"));
			String getLine = in.readLine();
			int mainCounter = 0;
			while (!getLine.contains("<tbody>")){
				if (getLine.contains("ending")){
					FinancialClass fClass = new FinancialClass();
					fClass.quarterHeader = getLine;
					if (getLine.contains("week")){
						fClass.quarterCounterRange = "week";
						fClass.quarterCount = Integer.parseInt(getLine.substring(0, getLine.indexOf(" "))); 
					} else if (getLine.contains("month")){
						fClass.quarterCounterRange = "month";
						fClass.quarterCount = Integer.parseInt(getLine.substring(0, getLine.indexOf(" ")));
					} else if (getLine.contains("day")){
						fClass.quarterCounterRange = "day";
						fClass.quarterCount = Integer.parseInt(getLine.substring(0, getLine.indexOf(" ")));
					} else if (getLine.contains("year")){
						fClass.quarterCounterRange = "year";
						fClass.quarterCount = Integer.parseInt(getLine.substring(0, getLine.indexOf(" ")));
					} else {
						fClass.quarterCounterRange = getLine;
						fClass.quarterCount = Integer.parseInt(getLine.substring(0, getLine.indexOf(" ")));
					}
					fClass.eDataType = FinancialClass.DataType.eCashFlow;
					String dateString = getLine.substring(getLine.indexOf("20"), getLine.length());
					fClass.dateString = dateString;
					fClass.ticker = symbol;
					fClass.year = Integer.parseInt(dateString.substring(0, 4));					
					fClass.quarter = 0;
					mainCounter++;
					companyData.add(fClass);
				}
				getLine = in.readLine();
			}
			
			getLine = in.readLine();
			int secondaryCounter = mainCounter;
			while (!getLine.contains("</tbody>")){
				if (getLine.contains("<td class=\"r")){
					if (getLine.contains("<span class=chr>")){
						getLine = getLine.replace("<span class=chr>", "");
						getLine = getLine.replace("</span>", "");
					}
					getLine = getLine.substring(getLine.indexOf(">")+1);
					getLine = getLine.substring(0, getLine.indexOf("<"));
					if (getLine.contains("-") && getLine.length() == 1){
						companyData.get(companyData.size() - secondaryCounter).financialData.add(0f);
					} else {
						if (getLine.contains(",")){
							getLine = getLine.replace(",", "");
						}
						companyData.get(companyData.size() - secondaryCounter).financialData.add(Float.parseFloat(getLine));
					}
					if (secondaryCounter == 1){
						secondaryCounter = mainCounter;
					} else {
						secondaryCounter--;
					}
				}
				getLine = in.readLine();
			}
		} catch (IOException e) {
			System.out.println("All circuits are busy, please try again...");
		}
	}
}

