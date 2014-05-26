package org.raxa.service.updater;



public class StartUpdater {

	/**
	 * Start the databasePoll thread
	 * @param args
	 */
	public static void main(String[] args) {
		Updater dp=new Updater();
		dp.start();
	}

}