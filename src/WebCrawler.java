/**
 * @author      Calvin Park <jeehunee@umich.edu>
 * @version     1.0                 (the version of the package this class was first added to)                   
 * @since       2011-10-11          (a date or the version number of this program)
 */

import java.net.*;
import java.util.ArrayList;
import java.io.*;

class WebCrawler {
	
	// Integer is preferred for numResult but for demo show purpose String will be used
	//	int numResult;
	String numResult = new String();
	String pageNum = new String();
	String searchString = new String();
	
	ArrayList <String> productTitle = new ArrayList<String>();
	ArrayList <String> productMerchant = new ArrayList<String>();
	ArrayList <String> productDescription = new ArrayList<String>();
	
	//Constructor without page number specified
	WebCrawler(String searchStr){
		//correcting page for nextag website's page index.
		pageNum = String.valueOf(Integer.decode("1")-1);
		searchString = searchStr; 
		loadContent();
		printResults();
	}
	
	//Constructor with page number specified
	WebCrawler(String searchStr, String pNum){
		//correcting page for nextag website's page index.
		pageNum = String.valueOf(Integer.decode(pNum)-1);
		searchString = searchStr;
		loadContent();
		printResults();
	}
	
	public void loadContent()
	{
		//Specifying web address to search products
		String webAddress =  "http://www.nextag.com/All--zz" + 
		replaceSpaceWithPlus(searchString) + "z" + pageNum + "zBiz5---html";
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
	
	//Replaces spaces in the string with dash
	private String replaceSpaceWithPlus(String inputString)
	{
		if (inputString.contains(" "))
		{
			inputString = inputString.replace(" ", "+");
		}
		return inputString;
	}
	
	//Extracting total number of result from the given String
	private void extractNumResult(String inputString)
	{
		inputString = inputString.substring(inputString.indexOf("of")+3);
		inputString = inputString.substring(0, inputString.indexOf(" "));
		inputString = inputString.replace(",", "");
		
		//Do not need number version so will keep String
		//numResult = Integer.parseInt(inputString);
		numResult = inputString;
	}
	
	//Extracting title
	private void extractTitle(String inputString)
	{
		inputString = inputString.substring(inputString.indexOf(" title=")+8);

		// Removing category heading
		if (inputString.indexOf(" - ") > 0)
			inputString = inputString.substring(inputString.indexOf("-")+2,inputString.indexOf("\""));
		else
			inputString = inputString.substring(0,inputString.indexOf("\""));
		
		//Removing special characters
		while (inputString.indexOf("&") > 0)
		{	
			inputString = inputString.substring(0,inputString.indexOf("&")) + 
			inputString.substring(inputString.indexOf(";")+1,inputString.length());
		}
		//Add the Title of the product
		productTitle.add(inputString);
		
		//Add the Merchant to match the number of titles
		productMerchant.add("");
	}
	
	//Extracting Merchant name
	private void extractMerchant(String inputString)
	{
		inputString = inputString.substring(inputString.indexOf("opBLink")+8);
		//indexNum is used since some product does not have a store specified.
		String indexNum = inputString.substring(0,inputString.indexOf("\""));
		inputString = inputString.substring(inputString.indexOf("See Store")+12);
		inputString = inputString.substring(0,inputString.indexOf("\""));
		productMerchant.set(Integer.decode(indexNum),inputString);
	}
	
	//Extracting Description
	private void extractDescription(String inputString)
	{
		if (inputString.indexOf("smallText") > 0)
		{
			inputString = inputString.substring(inputString.indexOf(">")+1);
			//Possible empty description handler
			if (inputString.indexOf("<") == 0)
				inputString = " ";
		}
		else
		{
			//Getting rid of Space in the beginning
			inputString = inputString.substring(12);
		}
		
		//Getting rid of Special Characters
		while (inputString.indexOf("&") > -1)
		{	
			inputString = inputString.substring(0,inputString.indexOf("&")) + 
			inputString.substring(inputString.indexOf(";")+1,inputString.length());
		}
		
		//Getting rid of HTML tags
		while (inputString.indexOf("<") > -1)
		{	
			inputString = inputString.substring(0,inputString.indexOf("<")) + 
			inputString.substring(inputString.indexOf(">")+1,inputString.length());
		}
		
		productDescription.add(inputString);

	}
	
	//Display the result of the crawling on the screen
	private void printResults()
	{
		System.out.print("Search String : ");
		System.out.println(searchString);
		System.out.print("Number of Result : ");
		System.out.println(numResult);

		for (int i = 0; i < productTitle.size();i++)
		{
			System.out.println(Integer.toString(i+1));
			System.out.print("Product Title : ");
			System.out.println(productTitle.get(i));
			System.out.print("Product Description : ");
			System.out.println(productDescription.get(i));
			System.out.print("Product Merchant : ");
			System.out.println(productMerchant.get(i));
		}
	}
}
