
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
	
	//Constructor without page number specified
	WebParser(ArrayList <String> symbol){
		//correcting page for nextag website's page index.
		companySymbol = symbol;
		loadContent();
	}
	
	public void loadContent()
	{
		//Specifying web address to search products
		String webAddress =  "https://www.google.com/finance?q=NASDAQ%3A" + 
		 + "&fstype=ii";
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
			
			while ((inputLine = in.readLine()) != null)
	        {
				//Finding Total Number of Result
				if (inputLine.indexOf("Showing")>0)
				{
					extractNumResult(inputLine);
					pageIndexError = false;
				}
				//Finding Merchant
				if (inputLine.indexOf("See Store")>0 && inputLine.indexOf("opBLink")>0)
				{
					//System.out.println(inputLine);
					extractMerchant(inputLine);
				}
				//Finding product
				if (inputLine.indexOf("\"opILink")>0)
				{
					//System.out.println(inputLine);
					if (inputLine.indexOf(" title=") > 0)
						extractTitle(inputLine);
					else
					{
						//System.out.println(inputLine);
						inputLine = in.readLine();
						while (inputLine.indexOf(" title=") == -1)
							inputLine = in.readLine();
						//System.out.println(inputLine);
						extractTitle(inputLine);
					}
				}
				
				//Finding Description of Product
				if (inputLine.indexOf("sr-subinfo")>0)
				{
					inputLine = in.readLine();
					while (inputLine.indexOf("smallText")== -1 && inputLine.indexOf("sr-info-description")== -1)
						inputLine = in.readLine();
					if (inputLine.indexOf("sr-info-description") > 0 )
						inputLine = in.readLine();
					extractDescription(inputLine);
				}
				
				//Finding no result pages
				if (inputLine.indexOf("Sorry")>0)
				{
					System.out.println("No Result");
					break;
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
        
	}
	
}

