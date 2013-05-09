
/**
 * @author      Calvin Park <jeehunee@umich.edu>
 * @version     1.0                 (the version of the package this class was first added to)                   
 * @since       2011-10-11          (a date or the version number of this program)
 */

import java.net.*;
import java.util.ArrayList;
import java.io.*;

class WebParser {
	
	// Integer is preferred for numResult but for demo show purpose String will be used
	//	int numResult;
	String numResult = new String();
	String pageNum = new String();
	String searchString = new String();
	
	ArrayList <String> companySymbol = new ArrayList<String>();
	ArrayList <FinancialClass> companyData = new ArrayList<FinancialClass>();
	
	//Constructor without page number specified
	WebParser(ArrayList <String> symbol){
		//correcting page for nextag website's page index.
		companySymbol = symbol;
		for (String sym:companySymbol){
			companyData.addAll(loadContent(sym));
		}
	}
	
	public static ArrayList<FinancialClass> loadContent(String symbol)
	{
		ArrayList<FinancialClass> fcList = new ArrayList<FinancialClass>();
		//Specifying web address to search products
		String webAddress =  "https://www.google.com/finance?q=NASDAQ%3A" + 
				symbol + "&fstype=ii";
		boolean pageIndexError = true;
		
		//Output the complete address
		System.out.println(webAddress);
		
		URL web;
		URLConnection webCon;
		BufferedReader in;
		String inputLine;
		try {
			web = new URL(webAddress);
			webCon = web.openConnection();
			
			in = new BufferedReader(
                    new InputStreamReader(
                    		webCon.getInputStream()));
			
			//Find where the data starts
			while (!in.readLine().contains("<thead><tr><th class=\"lm lft nwp\">"));
			//Skip Three lines
			in.readLine();
			in.readLine();
			in.readLine();
			String getLine = in.readLine();
			while (!getLine.contains("<tbody>")){
				if (getLine.contains("week")){
					FinancialClass fClass = new FinancialClass();
					String dateString = getLine.substring(getLine.indexOf("20"), getLine.length());
					fClass.ticker = symbol;
					fClass.year = Integer.parseInt(dateString.substring(0, 3));
					if (dateString.contains("-03-")){
						fClass.quarter = 1;
					} else if (dateString.contains("-06-")){
						fClass.quarter = 2;
					} else if (dateString.contains("-09-")){
						fClass.quarter = 3;
					} else if (dateString.contains("-12-")){
						fClass.quarter = 4;
					}
					fcList.add(fClass);
				}
				getLine = in.readLine();
	        }
			getLine = in.readLine();
			int mainCounter = 5;
			while (!getLine.contains("</tbody>")){
				if (getLine.contains("<td class=\"r")){
					getLine = getLine.substring(getLine.indexOf(">")+1);
					getLine = getLine.substring(0, getLine.indexOf("<"));
					if (getLine.contains("-") && getLine.length() == 1){
						fcList.get(fcList.size() - mainCounter).financialData.add(0f);
					} else {
						fcList.get(fcList.size() - mainCounter).financialData.add(Float.parseFloat(getLine));
					}
					mainCounter--;
				}
			}
			in.close();
			
			//If wrong page number is given
			if (pageIndexError)
				System.out.println("Wrong page Number!");
				
			
		} catch (MalformedURLException e) {
			System.out.println("Wrong Search String!");
		}catch (IOException e) {
			System.out.println("All circuits are busy, please try again...");
		}
        return fcList;
	}
	
}

