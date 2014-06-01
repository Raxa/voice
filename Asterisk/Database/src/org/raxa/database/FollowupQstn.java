package org.raxa.database;

import java.sql.Time;
import java.sql.Timestamp;

public class FollowupQstn {
	/**
	 * Auto Increment
	 */
	private int fid;
	/**
	 * Patient id
	 */
	private String pid;
	/**
	 * Followup question to ask
	 */
	private String qstn;
	/**
	 * Date to start followup
	 */
	private Timestamp fromDate;
	/**
	 * Date to end followup
	 */
	private Timestamp toDate;
	/**
	 * wether the reply by the system is successfully sent by the SMS Gateway
	 */
	private Time scheduleTime;


	public FollowupQstn(int fid, String pid, String qstn, Timestamp fromDate,
			Timestamp toDate, Time scheduleTime) {
		super();
		this.fid = fid;
		this.pid = pid;
		this.qstn = qstn;
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.scheduleTime = scheduleTime;
	}
	
	public FollowupQstn() {	}

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
