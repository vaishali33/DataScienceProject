/*
 * 
 */
package gov.nist.dataeval.data;

import net.sourceforge.openforecast.DataPoint;


/**
 * The Class TrafficDataPoint.
 * Future purpose custom DataPoint
 */
public class TrafficDataPoint implements DataPoint{

	/* (non-Javadoc)
	 * @see net.sourceforge.openforecast.DataPoint#setDependentValue(double)
	 */
	@Override
	public void setDependentValue(double value) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.openforecast.DataPoint#getDependentValue()
	 */
	@Override
	public double getDependentValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.openforecast.DataPoint#setIndependentValue(java.lang.String, double)
	 */
	@Override
	public void setIndependentValue(String name, double value) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.openforecast.DataPoint#getIndependentValue(java.lang.String)
	 */
	@Override
	public double getIndependentValue(String name) {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.openforecast.DataPoint#getIndependentVariableNames()
	 */
	@Override
	public String[] getIndependentVariableNames() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.openforecast.DataPoint#equals(net.sourceforge.openforecast.DataPoint)
	 */
	@Override
	public boolean equals(DataPoint dp) {
		// TODO Auto-generated method stub
		return false;
	}



}
