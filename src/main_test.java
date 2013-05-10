
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

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
	
    public static void main(String[] args) {
    	
    	ArrayList<CompanyClass> companyList = new ArrayList<CompanyClass>();
    	/*
    	companyList = CompanyClass.parseCSV("companylist.csv");
    	for (CompanyClass cClass:companyList){
    		System.out.println(cClass.companyName + " " + cClass.companySymbol + " " + cClass.marketCap);
    	}*/
        
    	ArrayList<String> tickerList = new ArrayList<String>();
    	tickerList.add("INTC");
    	WebParser webParser = new WebParser(tickerList);
    	
    	webParser.loadContent("INTC");
    	for (FinancialClass fClass:webParser.companyData){
    		System.out.println(fClass.year + " " + fClass.quarter + " " + fClass.eDataType);
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
