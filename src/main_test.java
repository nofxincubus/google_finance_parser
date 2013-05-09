
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class main_test {

    public static void main(String[] args) {
    	
    	ArrayList<CompanyClass> companyList = new ArrayList<CompanyClass>();
    	companyList = CompanyClass.parseCSV("companylist.csv");
    	for (CompanyClass cClass:companyList){
    		System.out.println(cClass.companyName + " " + cClass.companySymbol + " " + cClass.marketCap);
    	}
        
    	FinancialClass fClass = WebParser.loadContent("INTC");
    	
        
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
