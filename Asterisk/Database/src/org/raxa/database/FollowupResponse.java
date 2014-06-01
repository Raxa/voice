package org.raxa.database;

import java.sql.Timestamp;

public class FollowupResponse {

	public int frid;
	
	public int fid;
	
	public Timestamp date;
	
	public int response;
	
	public boolean isExecuted;
	
	public Timestamp lastTry;
	
	public int retryCount;
	
	public boolean syncStatus;

	public int getFrid() {
		return frid;
	}

	public void setFrid(int frid) {
		this.frid = frid;
	}

	public int getFid() {
		return fid;
	}

	public void setFid(int fid) {
		this.fid = fid;
	}

	public Timestamp getDate() {
		return date;
	}

	public void setDate(Timestamp date) {
		this.date = date;
	}

	public int getResponse() {
		return response;
	}

	public void setResponse(int response) {
		this.response = response;
	}

	public boolean isExecuted() {
		return isExecuted;
	}

	public void setExecuted(boolean isExecuted) {
		this.isExecuted = isExecuted;
	}

	public Timestamp getLastTry() {
		return lastTry;
	}

	public void setLastTry(Timestamp lastTry) {
		this.lastTry = lastTry;
	}

	public int getRetryCount() {
		return retryCount;
	}

	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}

	public boolean isSyncStatus() {
		return syncStatus;
	}

	public void setSyncStatus(boolean syncStatus) {
		this.syncStatus = syncStatus;
	}

	public FollowupResponse(int frid, int fid, Timestamp date, int response,
			boolean isExecuted, Timestamp lastTry, int retryCount,
			boolean syncStatus) {
		super();
		this.frid = frid;
		this.fid = fid;
		this.date = date;
		this.response = response;
		this.isExecuted = isExecuted;
		this.lastTry = lastTry;
		this.retryCount = retryCount;
		this.syncStatus = syncStatus;
	}

	public FollowupResponse() {
	}
	
}
