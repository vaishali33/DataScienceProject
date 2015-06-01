/**
 * 
 */
package gov.nist.dataeval.util;

import gov.nist.dataeval.db.ConnectionManager;
import gov.nist.dataeval.db.bean.LaneData;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Vaishali's
 *
 */
public class WriteToArffFile {

	private static final Object CSV_SEPARATOR = ",";
	private static final String ARFF_FILE = "G:\\Research\\NIST\\data\\CATTLab\\lane\\trainGroupByHour.arff";
	
	public static void main(String[] args) {
		List<LaneData> laneDataList = getDataForLane(9873);
		laneDataList = groupHourlyData(laneDataList);
		writeToARFF(laneDataList, ARFF_FILE);
	}

	public static void writeToARFF(List<LaneData> laneDataList, String filename)
	{
		try
		{
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename, false), "UTF-8"));
			String header1 = "@relation traffic-volume\n\n@attribute mdate date \"yyyy-MM-dd HH:mm:ss\"\n@attribute speed numeric\n"+
							"@attribute occupancy numeric\n@attribute volume numeric\n@DATA\n";		
			String header = "@relation traffic-volume\n\n@attribute mdate numeric\n@attribute speed numeric\n"+
					"@attribute occupancy numeric\n@attribute volume numeric\n@DATA\n";		
			bw.write(header1);
			for (LaneData laneData : laneDataList)
			{
					StringBuffer oneLine = new StringBuffer();
					
					String mdate = laneData.getMeasurement_date().toString();
					oneLine.append("\""+mdate.substring(0, mdate.length()-2)+"\"");
					//oneLine.append(laneData.getMeasurement_date().getTime());			//use different header
					oneLine.append(CSV_SEPARATOR);
					oneLine.append(laneData.getSpeed());
					oneLine.append(CSV_SEPARATOR);
					oneLine.append(laneData.getOccupancy());
					oneLine.append(CSV_SEPARATOR);
					oneLine.append(laneData.getVolume());
					bw.write(oneLine.toString());
					bw.newLine();
			}
			bw.flush();
			bw.close();
			
		}
		catch (UnsupportedEncodingException e) {}
		catch (FileNotFoundException e){}
		catch (IOException e){}
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
		String sql = "select * from lanedata where lane_id = "+lane_id+" and measurement_date < date('2014-01-01') order by measurement_date";
		
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

	/**
	 * @param laneDataList
	 * @return
	 */
	public static List<LaneData> groupHourlyData(List<LaneData> laneDataList) {
		List<LaneData> groupedList = new ArrayList<LaneData>();
		int vol = 0;
		double speed = 0;
		double occ = 0;
		int count = 0;
		Timestamp start = null;
		
		long diff = 0;
		long min = TimeUnit.MILLISECONDS.toMinutes(diff);
		
		for (LaneData laneData : laneDataList) {
			if(start == null){
				start = laneData.getMeasurement_date();
				vol = laneData.getVolume();
				speed = laneData.getSpeed();
				occ = laneData.getOccupancy();
				count = 1;
			}
			else if((laneData.getMeasurement_date().getTime() - start.getTime())< 60*60*1000){
				vol += laneData.getVolume();
				speed += laneData.getSpeed();
				occ += laneData.getOccupancy();
				count++;
			}
			else{
				LaneData groupedLaneData = new LaneData();
				groupedLaneData.setLane_id(laneData.getLane_id());
				groupedLaneData.setMeasurement_date(start);
				groupedLaneData.setVolume(vol);
				groupedLaneData.setSpeed(Math.round( (speed/count) * 10.0 ) / 10.0);
				groupedLaneData.setOccupancy(Math.round( (occ/count) * 10.0 ) / 10.0);
				groupedList.add(groupedLaneData);
				
				start = laneData.getMeasurement_date();
				vol = laneData.getVolume();
				speed = laneData.getSpeed();
				occ = laneData.getOccupancy();
				count = 1;
			}
			
		}
		System.out.println("GROUP LIST size - " + groupedList.size());
		return groupedList;
	}

}
