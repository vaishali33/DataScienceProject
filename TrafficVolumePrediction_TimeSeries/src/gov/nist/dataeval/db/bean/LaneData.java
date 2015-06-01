/*
 * 
 */
package gov.nist.dataeval.db.bean;

import java.sql.Timestamp;

/**
 * @author Vaishali's
 * 
 * The Class LaneData is Bean.
 */
public class LaneData {
	
	/** The id. */
	private long id;
	
	/** The lane_id. */
	private int lane_id;
	
	/** The measurement_date. */
	private Timestamp measurement_date;
	
	/** The speed. */
	private double speed;
	
	/** The volume. */
	private int volume;
	
	/** The occupancy. */
	private double occupancy;
	
	/** The quality. */
	private int quality;
	
	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public long getId() {
		return id;
	}
	
	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(long id) {
		this.id = id;
	}
	
	/**
	 * Gets the lane_id.
	 *
	 * @return the lane_id
	 */
	public int getLane_id() {
		return lane_id;
	}
	
	/**
	 * Sets the lane_id.
	 *
	 * @param lane_id the new lane_id
	 */
	public void setLane_id(int lane_id) {
		this.lane_id = lane_id;
	}
	
	/**
	 * Gets the measurement_date.
	 *
	 * @return the measurement_date
	 */
	public Timestamp getMeasurement_date() {
		return measurement_date;
	}
	
	/**
	 * Sets the measurement_date.
	 *
	 * @param measurement_date the new measurement_date
	 */
	public void setMeasurement_date(Timestamp measurement_date) {
		this.measurement_date = measurement_date;
	}
	
	/**
	 * Gets the speed.
	 *
	 * @return the speed
	 */
	public double getSpeed() {
		return speed;
	}
	
	/**
	 * Sets the speed.
	 *
	 * @param speed the new speed
	 */
	public void setSpeed(double speed) {
		this.speed = speed;
	}
	
	/**
	 * Gets the volume.
	 *
	 * @return the volume
	 */
	public int getVolume() {
		return volume;
	}
	
	/**
	 * Sets the volume.
	 *
	 * @param volume the new volume
	 */
	public void setVolume(int volume) {
		this.volume = volume;
	}
	
	/**
	 * Gets the occupancy.
	 *
	 * @return the occupancy
	 */
	public double getOccupancy() {
		return occupancy;
	}
	
	/**
	 * Sets the occupancy.
	 *
	 * @param occupancy the new occupancy
	 */
	public void setOccupancy(double occupancy) {
		this.occupancy = occupancy;
	}
	
	/**
	 * Gets the quality.
	 *
	 * @return the quality
	 */
	public int getQuality() {
		return quality;
	}
	
	/**
	 * Sets the quality.
	 *
	 * @param quality the new quality
	 */
	public void setQuality(int quality) {
		this.quality = quality;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "LaneData [id=" + id + ", lane_id=" + lane_id
				+ ", measurement_date=" + measurement_date + ", speed=" + speed
				+ ", volume=" + volume + ", occupancy=" + occupancy
				+ ", quality=" + quality + "]";
	}
	
	
}
