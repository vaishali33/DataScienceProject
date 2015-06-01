/**
 * 
 */
package gov.nist.dataeval.util;

/**
 * @author Vaishali's
 *
 */
import java.io.*;
import java.util.List;

import weka.core.Instances;
import weka.classifiers.functions.GaussianProcesses;
import weka.classifiers.functions.SMOreg;
import weka.classifiers.evaluation.NumericPrediction;
import weka.classifiers.timeseries.WekaForecaster;
import weka.classifiers.timeseries.core.TSLagMaker;

/**
 * Traffic Volume Prediction using the time series forecasting API Weka. To compile and
 * run the CLASSPATH will need to contain:
 *
 * weka.jar (from your weka distribution)
 * pdm-timeseriesforecasting-ce-TRUNK-SNAPSHOT.jar (from the time series package)
 * jcommon-1.0.14.jar (from the time series package lib directory)
 * jfreechart-1.0.13.jar (from the time series package lib directory)
 * 
 */
public class TimeSeriesPredictionWeka {

  public static void main(String[] args) {
    try {
    	String inputFilePath = "G:\\Research\\NIST\\data\\CATTLab\\lane\\trainGroupByHour.arff";

      // load the traffic volume historical data
      Instances trafficData = new Instances(new BufferedReader(new FileReader(inputFilePath)));

      // new forecaster
      WekaForecaster forecaster = new WekaForecaster();

      // set the targets we want to forecast. This method calls
      // setFieldsToLag() on the lag maker object for us
      forecaster.setFieldsToForecast("volume,speed,occupancy");

      // default underlying classifier is SMOreg (SVM) - we'll use
      // gaussian processes for regression instead
      forecaster.setBaseForecaster(new GaussianProcesses());
      forecaster.setBaseForecaster(new SMOreg());

      forecaster.getTSLagMaker().setTimeStampField("mdate"); // date time stamp
      forecaster.getTSLagMaker().setMinLag(1);
      forecaster.getTSLagMaker().setMaxLag(24); // hourly data

      // add a month of the year indicator field
       forecaster.getTSLagMaker().setAddMonthOfYear(true);

      // add a quarter of the year indicator field
       forecaster.getTSLagMaker().setAddQuarterOfYear(true);

      // build the model
      forecaster.buildForecaster(trafficData, System.out);

      // prime the forecaster with enough recent historical data
      // to cover up to the maximum lag. In our case, we could just supply
      // the 24 most recent historical instances, as this covers our maximum
      // lag period
      forecaster.primeForecaster(trafficData);

      // forecast for 5 units (hours) beyond the end of the
      // training data
      List<List<NumericPrediction>> forecast = forecaster.forecast(5, System.out);

      // output the predictions. Outer list is over the steps; inner list is over
      // the targets
      System.out.println("Volume\tSpeed\tOccupancy\n");
      for (int i = 0; i < 5; i++) {
        List<NumericPrediction> predsAtStep = forecast.get(i);
        //loop over predicted values volume, speed and occupancy
        for (int j = 0; j < 3; j++) {
          NumericPrediction predForTarget = predsAtStep.get(j);
          System.out.print("" + predForTarget.predicted() + "\t");
        }
        System.out.println();
      }

      // we can continue to use the trained forecaster for further forecasting
      // by priming with the most recent historical data (as it becomes available).
      // At some stage it becomes prudent to re-build the model using current
      // historical data.

    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
}
