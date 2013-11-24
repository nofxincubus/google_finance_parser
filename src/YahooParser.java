
/**
 * @author      Calvin Park <jeehunee@umich.edu>
 * @version     1.0                 (the version of the package this class was first added to)                   
 * @since       2011-10-11          (a date or the version number of this program)
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

class YahooParser {

	public ArrayList<String> loadContent(String symbols, BufferedWriter w, ArrayList<String> exchange)
	{
		ArrayList<String> array = new ArrayList<String>();
		//Specifying web address to search products
		String webAddress =  "http://finance.yahoo.com/d/quotes.csv?s=" + 
				symbols + "&f=f6spjka2m3m4r6r7s7";
		//Output the complete address
		try {
		
			URL web;
			URLConnection webCon;
			BufferedReader in;
		
			web = new URL(webAddress);
			webCon = web.openConnection();
			
	        
	        
			in = new BufferedReader(
					new InputStreamReader(
							webCon.getInputStream()));
			String getLine = in.readLine();
			int i = 0;
			while (getLine != null){
				System.out.println(getLine);
				
				String fshare = getLine.substring(0, getLine.indexOf("\"")-1);
				
				String rest = getLine.substring(getLine.indexOf("\""),getLine.length());
				String ticker = rest.substring(1,rest.indexOf(",")-1);
				rest = rest.substring(rest.indexOf(","),rest.length());
				ticker = ticker + "_" + exchange.get(i);
				
				fshare = fshare.replace(",","");
				String finalString = fshare + "," + ticker +  rest;
				finalString = finalString.replace("N/A", "");
				w.write(finalString);
				w.newLine();
				getLine = in.readLine();
				i++;
			}
			
			
			in.close();

		} catch (MalformedURLException e) {
			System.out.println("Wrong Search String!");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("All circuits are busy, please try again...");
		}
		return array;
	}

}

