/*
 * 
 */
package gov.nist.dataeval.data;

import gov.nist.dataeval.db.ConnectionManager;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Properties;

/**
 * @author Vaishali's
 * 
 * The Class InsertIntoDatabase.
 */
public class InsertIntoDatabase {

	/** The data path. */
	static String dataPath;
	
	/** The input file. */
	static String inputFile;
	
	/** The connection url. */
	static String connectionURL;

	static{
		String propFile = "prop/config.properties";
		FileInputStream fileStream;
		try {

			fileStream = new FileInputStream(propFile);
			Properties prop = new Properties();
			prop.load(fileStream);
			dataPath = prop.getProperty("dataDir");
			inputFile = prop.getProperty("testData");
			connectionURL = prop.getProperty("connectionUrl");

		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {

		WriteCsvToDatabase();
	}

	/**
	 * Write csv to database.
	 */
	private static void WriteCsvToDatabase() {

		Connection conn = null;
		PreparedStatement pstmt = null;

		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";

		try {
			conn = new ConnectionManager().getConnection();
			
			String sql = "insert into traffic.lanedata(lane_id,measurement_date,speed,volume,occupancy,quality) values(?,?,?,?,?,?)";
			pstmt = conn.prepareStatement(sql);
			
			br = new BufferedReader(new FileReader(dataPath+"\\"+inputFile));
			
			br.readLine();			//skip first header line
			
			while ((line = br.readLine()) != null) {

				String[] row = line.split(cvsSplitBy);

				String laneId = row[0]; 
				String measurement_date = row[1];
				String speed = row[2];
				String volume = row[3];
				String occupancy = row[4];
				String quality = row[5];

				measurement_date = measurement_date.substring(0, measurement_date.length()-3);	//adjust measurement date -04 at end
				Timestamp ts = Timestamp.valueOf(measurement_date);

				pstmt.setInt(1, Integer.parseInt(laneId));
				pstmt.setTimestamp(2, ts);
				pstmt.setDouble(3, Double.parseDouble(speed));
				pstmt.setInt(4, Integer.parseInt(volume));
				pstmt.setDouble(5, Double.parseDouble(occupancy));
				pstmt.setInt(6, Integer.parseInt(quality));

				// execute insert SQL statement
				pstmt .executeUpdate();

			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			//free resources
			try {
				conn.close();
				pstmt.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

}
