package org.raxa.database;

/**
 * An object of this class corresponds to one possible choice for a followupQstn 
 * @author rahur
 *
 */
public class FollowupChoice {

	/**
	 * Uniquely identifies a followupChoice
	 */
	public Integer fcid;
	/**
	 * The fid specifies which followupQstn this choice belongs to
	 */
	public int fid;
	/**
	 * The value of the choice. eg. Yes, No etc.
	 */
	public String choice;
	public int getFcid() {
		return fcid;
	}
	public void setFcid(Integer fcid) {
		this.fcid = fcid;
	}
	public int getFid() {
		return fid;
	}
	public void setFid(int fid) {
		this.fid = fid;
	}
	public String getChoice() {
		return choice;
	}
	public void setChoice(String choice) {
		this.choice = choice;
	}
	public FollowupChoice(int fcid, int fid, String choice) {
		super();
		this.fcid = fcid;
		this.fid = fid;
		this.choice = choice;
	}
	public FollowupChoice() {
	
	}
	
}
