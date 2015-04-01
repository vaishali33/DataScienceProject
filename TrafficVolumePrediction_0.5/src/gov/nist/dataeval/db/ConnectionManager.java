package gov.nist.dataeval.db;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * The Class ConnectionManager.
 *
 * @author Vaishali's
 * This class provides connection to database
 */
public class ConnectionManager {

/** The connection url. */
static String connectionURL;
	
	static{
		String propFile = "prop/config.properties";
		FileInputStream fileStream;
		try {
			
			fileStream = new FileInputStream(propFile);
			Properties prop = new Properties();
			prop.load(fileStream);
			connectionURL = prop.getProperty("connectionUrl");
			
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Gets the connection.
	 *
	 * @return the connection
	 */
	public Connection getConnection(){
		
		Connection conn = null;
			try {
				Class.forName("com.mysql.jdbc.Driver");
				conn = DriverManager.getConnection(connectionURL);
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		return conn;
	}
}
