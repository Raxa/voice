package org.raxa.database;

import java.sql.Timestamp;

/** An object of this class corresponds to the response of the patient
 * to a followup question for a specific date
 * 
 * @author rahulr92
 *
 */
public class FollowupResponse {

	/**
	 * Uniquely identifies response
	 */
	public int frid;
	/**
	 * Identifies followup qstn to which this response belongs
	 */
	public int fid;
	/**
	 * The date in which this response was recorded
	 */
	public Timestamp date;
	/**
	 * The response of the patient
	 * This will be the fcid of one of the followup choices 
	 * assigned for this followup question
	 */
	public int response;
	/**
	 * In case of SMS_TYPE followups, this is set as soon as the followup question is 
	 * send to the patient. The response is set only when a reply is received from the patient
	 * In case of IVR_TYPE, 
	 * this is set as soon as we the question is asked and response is obtained
	 */
	public boolean isExecuted;
	
	public Timestamp lastTry;
	
	public int retryCount;
	/**
	 * Sync information. 
	 * Whether the followup response has been sent back to the Raxa server or not
	 * TODO: add logic to Scheduler to send responses to Raxa server as soon as they are obtained
	 */
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
