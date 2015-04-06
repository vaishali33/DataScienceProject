/* This class runs cross - validation  
 * on polynomial regression model
 */
package gov.nist.dataeval.test;

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
 * The Class TrafficVolumePredictTest.
 *
 * @author Vaishali's
 * 
 * The Class TrafficVolumePredictTest 
 * uses week Day and hour parameter
 */
public class TrafficVolumePredictTest {

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
	 * Runs a PolynomialRegression model for predicting traffic volume
	 */
	public static void main(String[] args) {
		ArrayList<Integer> lanes = new ArrayList<Integer>();
		lanes.add(9873);
		lanes.add(9544);
		lanes.add(9545);
		lanes.add(9546);
		
		for (Integer laneId : lanes) {
			learnAndPredictVolumeForLaneId(laneId);
		}
		
	}

	/**
	 * Learn and predict volume for lane id.
	 *
	 * @param laneId the lane id
	 */
	private static void learnAndPredictVolumeForLaneId(int laneId) {
		
		System.out.println("Lane ID = "+ laneId);
		List<LaneData> laneDataList = getDataForLane(laneId);
		int len = laneDataList.size();
		
		//using 80% data for training the model and 20% data for testing the model
		int split = (int) (len*0.8);
		List<LaneData> trainLaneDataList = laneDataList.subList(0, split);
		List<LaneData> testLaneDataList = laneDataList.subList(split, len);
		
		System.out.println("Training data size = "+trainLaneDataList.size());
		System.out.println("Test data size = "+testLaneDataList.size());
		//laneDataList.removeAll(testLaneDataList);
		
		double mpe = predictVolumeForLaneData(trainLaneDataList, testLaneDataList);
		System.out.println("Mean Percent Error in complete data set = "+mpe);
		System.out.println("-----------------------------------------------");
	}

	/**
	 * Predict volume for lane data.
	 *
	 * @param laneDataList the lane data list
	 * @param testLaneDataList  - Lane data list to predict volume
	 * @return the double
	 */
	public static double predictVolumeForLaneData(List<LaneData> laneDataList, List<LaneData> testLaneDataList) {

		//calculate Mean Percent Error formula = (100/n)*sum(abs(actual-forecast)/actual)
		double mpe = 0;
		//training data set
		DataSet dataSet = new DataSet();
		//testing data set
		DataSet fcDataSet = new DataSet();


		for (LaneData lane : laneDataList) {

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(lane.getMeasurement_date());

			Observation ob = new Observation(lane.getVolume());
			//ob.setIndependentValue("time", calendar.getTimeInMillis());
			ob.setIndependentValue("day", calendar.get(Calendar.DAY_OF_WEEK));
			ob.setIndependentValue("hour", calendar.get(Calendar.HOUR_OF_DAY));
			ob.setIndependentValue("occupancy", lane.getOccupancy());
			ob.setIndependentValue("speed", lane.getSpeed());

			dataSet.add(ob);
		}


		for (LaneData testLaneData : testLaneDataList) {

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(testLaneData.getMeasurement_date());

			DataPoint dp3 = new Observation(0.0);
			//dp3.setIndependentValue("time", calendar.getTimeInMillis()+(1000));
			dp3.setIndependentValue("day", calendar.get(Calendar.DAY_OF_WEEK));
			dp3.setIndependentValue("hour", calendar.get(Calendar.HOUR_OF_DAY));
			dp3.setIndependentValue("occupancy", testLaneData.getOccupancy());
			dp3.setIndependentValue("speed", testLaneData.getSpeed());
			fcDataSet.add(dp3);
		}

		ForecastingModel forecastingModel = Forecaster.getBestForecast(dataSet);
		System.out.println("ForecastingModel used = "+forecastingModel);
		//forecastingModel.init(dataSet);	//used for specific model

		forecastingModel.forecast(fcDataSet);

		// After calling model.forecast, our fcDataSet now contains
		//  forecast data points
		int i = 0;
		Iterator it = fcDataSet.iterator();
		while ( it.hasNext() )
		{
			DataPoint dp0 = (DataPoint)it.next();
			
			//Type-caste into integer as volume is an integer
			int forecastValue = (int) Math.round(dp0.getDependentValue());
			
			//calculate error in prediction
			int actual = testLaneDataList.get(i).getVolume();
			mpe += Math.abs(actual-forecastValue)/actual;

			//System.out.println("Actual value = "+ actual +"Forecast value = " +forecastValue);
			i++;
		}
		mpe /= testLaneDataList.size();
		mpe *= 100;
		return mpe;
	}

	/**
	 * Gets the data for lane.
	 *
	 * @param lane_id the lane_id
	 * @return the data for lane
	 */
	public static List<LaneData> getDataForLane(int lane_id) {
		
		List<LaneData> laneDataList = new ArrayList<LaneData>();
		
		Connection conn = new ConnectionManager().getConnection();
		//Select statement for retrieving lane data for 2013
		//String sql = "select * from lanedata where lane_id = "+lane_id+" and measurement_date < date('2014-01-01')";
		
		//Select statement for retrieving lane data shuffled
		String sql = "select * from lanedata where lane_id = "+lane_id; //order by rand()";//limit 10000
		
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
		
		System.out.println("data size = "+laneDataList.size());
		return laneDataList;
	}

}
