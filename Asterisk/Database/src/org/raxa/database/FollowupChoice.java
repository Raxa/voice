package org.raxa.database;

public class FollowupChoice {

	public Integer fcid;
	public int fid;
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
