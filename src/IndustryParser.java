
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

class IndustryParser {
	
	public ArrayList <String> industryData = new ArrayList<String>();


	public void loadContent(String symbol)
	{
		String name = symbol.substring(0, symbol.indexOf("_"));
		String exchange = symbol.substring(symbol.indexOf("_")+1,symbol.length());
		name = name.replace("^", ".");
		name = name.replace("/", ".");		
		//Specifying web address to search products
		String webAddress = "";
		if (exchange.contains("nyse")){
			webAddress =  "https://www.google.com/finance?q=NYSE%3A" + 
					name;
		} else if (exchange.contains("nasdaq")){
			webAddress = "https://www.google.com/finance?q=NASDAQ%3A" + 
					name;
		} else {
			return;
		}

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
				String getLine = in.readLine();
				while (!getLine.contains("Industry:"))
					getLine = in.readLine();
				getLine = getLine.substring(getLine.indexOf("Industry"),getLine.length());
				getLine = getLine.substring(getLine.indexOf(">") + 1,getLine.indexOf("</"));
				industryData.add(getLine);

		} catch (MalformedURLException e) {
			System.out.println("Wrong Search String!");
		} catch (IOException e) {
			System.out.println("All circuits are busy, please try again...");
		}
	}

}

