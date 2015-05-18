/**
 * 
 */
package gov.nist.dataeval.util;

import gov.nist.dataeval.db.bean.LaneData;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * @author Vaishali's
 *
 */
public class WriteToArffFile {

	private static final Object CSV_SEPARATOR = ",";
	private static final String ARFF_FILE = "G:\\Research\\NIST\\data\\CATTLab\\lane\\trainGroupByHour.arff";
	
	public static void main(String[] args) {
		List<LaneData> laneDataList = TrafficVolumePredict.getDataForLane(9873);
		laneDataList = TrafficVolumePredict.groupHourlyData(laneDataList);
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
}
