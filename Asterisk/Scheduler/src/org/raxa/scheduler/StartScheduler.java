package org.raxa.scheduler;

public class StartScheduler {

	/**
	 * Start the databasePoll thread
	 * @param args
	 */
	public static void main(String[] args) {
		DatabasePoller dp=new DatabasePoller();
		dp.start();
	}

}
