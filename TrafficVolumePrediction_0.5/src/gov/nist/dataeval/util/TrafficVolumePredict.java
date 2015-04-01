/*
 * 
 */
package gov.nist.dataeval.util;

import gov.nist.dataeval.db.ConnectionManager;
import gov.nist.dataeval.db.bean.LaneData;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import net.sourceforge.openforecast.DataPoint;
import net.sourceforge.openforecast.DataSet;
import net.sourceforge.openforecast.Forecaster;
import net.sourceforge.openforecast.ForecastingModel;
import net.sourceforge.openforecast.Observation;

/**
 * The Class TrafficVolumePredict  - Main class to run the program
 */
public class TrafficVolumePredict {
	static{
		String propFile = "prop/config.properties";
		FileInputStream fileStream;
		try {
			
			fileStream = new FileInputStream(propFile);
			Properties prop = new Properties();
			prop.load(fileStream);
			String dataPath = prop.getProperty("data");
			
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * Runs a plain vanilla version of PolynomialRegression model for predicting traffic volume
	 */
	public static void main(String[] args) {
		
		List<LaneData> laneDataList = getDataForLane(9873);
		predictVolumeForLaneData(laneDataList);
		System.out.println("-----------------------------------------------");
		laneDataList = getDataForLane(9544);
		predictVolumeForLaneData(laneDataList);
		System.out.println("-----------------------------------------------");
		laneDataList = getDataForLane(9545);
		predictVolumeForLaneData(laneDataList);
		System.out.println("-----------------------------------------------");
		laneDataList = getDataForLane(9546);
		predictVolumeForLaneData(laneDataList);
		System.out.println("-----------------------------------------------");
	}

	/**
	 * Predict volume for lane data.
	 *
	 * @param laneDataList the lane data list
	 */
	private static void predictVolumeForLaneData(List<LaneData> laneDataList) {
				//last instance of lane data
				LaneData lastLaneData = laneDataList.get(laneDataList.size()-1);
				laneDataList.remove(lastLaneData);
				System.out.println(lastLaneData);
				
				DataSet dataSet = new DataSet();
				
				for (LaneData lane : laneDataList) {
					
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(lane.getMeasurement_date());
					
					Observation ob = new Observation(lane.getVolume());
					ob.setIndependentValue("time", calendar.getTimeInMillis());
					ob.setIndependentValue("occupancy", lane.getOccupancy());
					ob.setIndependentValue("speed", lane.getSpeed());
					
					dataSet.add(ob);
				}
					
				//future dataset
				DataSet fcDataSet = new DataSet();
				
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(lastLaneData.getMeasurement_date());
				
				DataPoint dp3 = new Observation(0.0);
				dp3.setIndependentValue("time", calendar.getTimeInMillis()+(1000));
				dp3.setIndependentValue("occupancy", lastLaneData.getOccupancy());
				dp3.setIndependentValue("speed", lastLaneData.getSpeed());
				fcDataSet.add(dp3);
				
				
				
				ForecastingModel forecastingModel = Forecaster.getBestForecast(dataSet);
				System.out.println(forecastingModel.getClass());
				//forecastingModel.init(dataSet);	//used for specific model
				
				forecastingModel.forecast(fcDataSet);
				
				// After calling model.forecast, our fcDataSet now contains
			     //  forecast data points
			     Iterator it = fcDataSet.iterator();
			     while ( it.hasNext() )
			        {
			        DataPoint dp0 = (DataPoint)it.next();
			        double forecastValue = dp0.getDependentValue();

			        // Do something with the forecast value, e.g.
			        System.out.println(forecastValue+"===="+ dp0 );
			        }
	}

	/**
	 * Gets the data for lane.
	 *
	 * @param lane_id the lane_id
	 * @return the data for lane
	 */
	private static List<LaneData> getDataForLane(int lane_id) {
		
		List<LaneData> laneDataList = new ArrayList<LaneData>();
		
		Connection conn = new ConnectionManager().getConnection();
		String sql = "select * from lanedata where lane_id = "+lane_id+" order by measurement_date";
		
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()){
				LaneData lane = new LaneData();
				lane.setId(rs.getLong(1));
				lane.setLane_id(rs.getInt(2));
				lane.setMeasurement_date(rs.getTimestamp(3));
				lane.setSpeed(rs.getDouble(4));
				lane.setVolume(rs.getInt(5));
				lane.setOccupancy(rs.getDouble(6));
				lane.setQuality(rs.getInt(7));
				
				if(lane.getQuality()==0){
					laneDataList.add(lane);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		System.out.println(laneDataList.size());
		return laneDataList;
	}
}