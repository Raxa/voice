package org.raxa.scheduler;

import java.io.Serializable;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class FollowupData implements Serializable {
    @SerializedName("fid")
	private int fid;
    
    @SerializedName("pid")
	private String pid;
    
    @SerializedName("qstn")
	private String qstn;
    
    @SerializedName("from_date")
	private Timestamp fromDate;
    
    @SerializedName("to_date")
	private Timestamp toDate;
    
    @SerializedName("schedule_time")
	private Time scheduleTime;
    
    @SerializedName("options")
	public List<String> options;
	
	public List<String> getOptions() {
		return options;
	}
	public void setOptions(List<String> options) {
		this.options = options;
	}
	
		public int getFid() {
		return fid;
	}

	public void setFid(int fid) {
		this.fid = fid;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getQstn() {
		return qstn;
	}

	public void setQstn(String qstn) {
		this.qstn = qstn;
	}

	public Timestamp getFromDate() {
		return fromDate;
	}

	public void setFromDate(Timestamp fromDate) {
		this.fromDate = fromDate;
	}

	public Timestamp getToDate() {
		return toDate;
	}

	public void setToDate(Timestamp toDate) {
		this.toDate = toDate;
	}

	public Time getScheduleTime() {
		return scheduleTime;
	}

	public void setScheduleTime(Time scheduleTime) {
		this.scheduleTime = scheduleTime;
	}
}
