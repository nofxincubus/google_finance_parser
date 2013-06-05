
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

class YahooParser {

	public void loadContent(String symbols)
	{
		//Specifying web address to search products
		String webAddress =  "http://finance.yahoo.com/d/quotes.csv?s=" + 
				symbols + "&f=spf6jka2m3m4r6r7s7";

		//Output the complete address

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
			while (getLine != null){
				System.out.println(getLine);
				getLine = in.readLine();
			}
			
			
			in.close();


		} catch (MalformedURLException e) {
			System.out.println("Wrong Search String!");
		} catch (IOException e) {
			System.out.println("All circuits are busy, please try again...");
		}
	}

}

