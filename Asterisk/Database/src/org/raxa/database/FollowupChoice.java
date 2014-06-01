package org.raxa.database;

public class FollowupChoice {

	public int fcid;
	public int fid;
	public String option;
	public int getFcid() {
		return fcid;
	}
	public void setFcid(int fcid) {
		this.fcid = fcid;
	}
	public int getFid() {
		return fid;
	}
	public void setFid(int fid) {
		this.fid = fid;
	}
	public String getOption() {
		return option;
	}
	public void setOption(String option) {
		this.option = option;
	}
	public FollowupChoice(int fcid, int fid, String option) {
		super();
		this.fcid = fcid;
		this.fid = fid;
		this.option = option;
	}
	public FollowupChoice() {
	
	}
	
}
