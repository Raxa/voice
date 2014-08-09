
package org.raxa.audioplayer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.asteriskjava.fastagi.AgiChannel;
import org.asteriskjava.fastagi.AgiException;
import org.asteriskjava.fastagi.AgiRequest;
import org.asteriskjava.fastagi.BaseAgiScript;
import org.asteriskjava.fastagi.command.RecordFileCommand;
import org.asteriskjava.manager.action.CommandAction;
import org.asteriskjava.manager.response.CommandResponse;
import org.asteriskjava.manager.AuthenticationFailedException;
import org.asteriskjava.manager.ManagerConnection;
import org.asteriskjava.manager.ManagerConnectionFactory;
import org.asteriskjava.manager.TimeoutException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.openmrs.Location;
import org.openmrs.Provider;
import org.openmrs.module.appointmentscheduling.Appointment;
import org.openmrs.module.appointmentscheduling.Appointment.AppointmentStatusType;
import org.openmrs.module.appointmentscheduling.AppointmentType;
import org.openmrs.module.appointmentscheduling.TimeSlot;
import org.raxa.alertmessage.MessageInterface;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.raxa.database.VariableSetter;
import org.raxa.restUtils.AppointmentRestCall;

/**
 * Outgoing Call Context here sets the following channel variable
 * totalItem;item0,item1....,count
 * 
 * CAUTION:Even if the patient has hung up the program is going to execute until it meets an exception or termination.
 * @author rahul
 *
 */
public class AppointmentCallHandler extends CallHandler
{
	private AgiRequest request;
	AgiChannel channel;
	private Logger logger = Logger.getLogger(this.getClass());
	AppointmentRestCall appointmentRestCall;
	List<Location> locations;
	List<Provider> providers;
	List<AppointmentType> appointmentTypes;
	List<TimeSlot> timeSlots;
	List<Appointment> appointments;
	final String LIMIT = "5";
	final int SEARCH_INTERVAL = 14;
	final String DATE_FORMAT = "dd MMMM h a";
	final String REPEAT_FORMAT = "  %d to repeat this menu.";
	SimpleDateFormat sdf;
	
	String pid;
	
	AppointmentCallHandler(AgiRequest request, AgiChannel channel, String language, String pid){
		super(channel,request,language);
		this.request=request;
	    this.channel=channel;
		this.pid = pid;
	    appointmentRestCall = new AppointmentRestCall();
	    sdf = new SimpleDateFormat(DATE_FORMAT);
		try{
    		service(request, channel);
			playAppointmentMenu();
			} catch(Exception ex){
				logger.error("\nCaused by:\n",ex);
			}	
	}

	  /**
	 * checks whether the call is incoming or outgoing.Handles the call accordingly
	 */
    public void service(AgiRequest request, AgiChannel channel) throws AgiException{
    	logger.debug("Entering AppointmentCallHandler"); 
	}
	
    /**
     * This method plays the main appointment related options
     * It recursively calls itself unless user exits by choosing to return to main menu
     */
	public void playAppointmentMenu(){
	try {	
	//String pid = "111";
	String locationOptions,providerOptions,appointmentTypeOptions, timeSlotOptions;
	char locationChoice, providerChoice, appointmentTypeChoice, timeSlotChoice;
	locationOptions = providerOptions = appointmentTypeOptions = timeSlotOptions = "Press ";
	String appointmentMenuText = getValueFromPropertyFile("appointmentMenuText","english");
		char option=getOptionUsingTTS(appointmentMenuText,"1234569","5000",2);
		String keyWord=analyseOption("appointmentMenu",option);
		if(keyWord.equals("book"))
		{
			int i, choice;
			//Last option is always to repeat the menu
			//Get appointment type
			appointmentTypes = appointmentRestCall.getAppointmentTypes(LIMIT);
			for(i=0; i<appointmentTypes.size();i++){
				appointmentTypeOptions += "  "+(i+1)+" for "+appointmentTypes.get(i).getName();
			}
			do{
				playUsingTTS("Please choose your appointment type","en","");
				appointmentTypeChoice = getOptionUsingTTS(appointmentTypeOptions+ String.format(REPEAT_FORMAT, i+1), "123456", "5000", 2);
				choice = Character.getNumericValue(appointmentTypeChoice);
			}while(choice == appointmentTypes.size()+1);
			
			playUsingTTS("You chose "+ appointmentTypes.get(choice-1).getName() ,"en","");

			//Get locations
			locations = appointmentRestCall.getLocations(LIMIT);
			for(i=0; i<locations.size();i++){
				locationOptions += " "+(i+1)+" for "+locations.get(i).getName();
			}
			do{
			playUsingTTS("Please choose your location","en","");
			locationChoice = getOptionUsingTTS(locationOptions+ String.format(REPEAT_FORMAT, i+1), "123456", "5000", 2);
			choice = Character.getNumericValue(locationChoice);
			}while(choice == locations.size()+1);
			playUsingTTS("You chose "+ locations.get(choice-1).getName() ,"en","");
			
			//Get providers
			providers = appointmentRestCall.getProviders(LIMIT);
			for(i=0; i<providers.size();i++){
				providerOptions += " "+(i+1)+" for "+providers.get(i).getName();
			}
			do{
				playUsingTTS("Please choose a doctor","en","");
				providerChoice = getOptionUsingTTS(providerOptions+ String.format(REPEAT_FORMAT, i+1), "123456", "5000", 2);
				choice = Character.getNumericValue(providerChoice);
			}while(choice == providers.size()+1);
			playUsingTTS("You chose "+ providers.get(choice-1).getName() ,"en","");
			
			//Get next 5 timeslots within 2 weeks
			Date fromDate = new Date();
			Date toDate = AppointmentRestCall.addDaysToDate(fromDate, SEARCH_INTERVAL);
			String appointmentTypeUuid = appointmentTypes.get(Character.getNumericValue(appointmentTypeChoice)-1).getUuid();
			String locationUuid = locations.get(Character.getNumericValue(locationChoice)-1).getUuid();
			String providerUuid = providers.get(Character.getNumericValue(providerChoice)-1).getUuid();
			timeSlots = appointmentRestCall.getTimeSlots(fromDate, toDate, appointmentTypeUuid, locationUuid, providerUuid, LIMIT);
			if(timeSlots.size()==0){
				playUsingTTS("Sorry, there are no available timeslots based on your selection. Please try again.","en","");
				return;
			}

			Date date;
			for(i=0; i<locations.size();i++){
				date = timeSlots.get(i).getStartDate();
				timeSlotOptions += " "+(i+1)+" for "+sdf.format(date);
			}
			do{
				playUsingTTS("Please choose from these available timeslots","en","");
				timeSlotChoice = getOptionUsingTTS(timeSlotOptions+ String.format(REPEAT_FORMAT, i+1), "123456", "5000", 2);
				choice = Character.getNumericValue(timeSlotChoice);
			}while(choice == timeSlots.size()+1);
			TimeSlot timeSlot = timeSlots.get(Character.getNumericValue(timeSlotChoice)-1);
			String timeSlotUuid = timeSlot.getUuid();
			char isConfirmed = getOptionUsingTTS("Press 1 to confirm your appointment on "
											+sdf.format(timeSlot.getStartDate())+" Press 2 to retry", "12", "5000", 2);
			
			//No need to check for option 2 since playAppointmentMenu() is called outside the main if condition
			if(isConfirmed=='1'){
				appointmentRestCall.createAppointment(timeSlotUuid, pid, appointmentTypeUuid);
				playUsingTTS("Thanks. Your appointment has been confirmed","en","");
			}

		}
		else if(keyWord.equals("status"))
		{
			appointments = appointmentRestCall.getAppointments(pid,AppointmentStatusType.SCHEDULED, LIMIT);
			if(appointments.size()==0)
			{
				playUsingTTS("Your don't have any appointments scheduled","en","");
			}
			else
			{
				String plural = (appointments.size() == 1) ? " " : "s ";
				playUsingTTS("You have the following appointment"+plural+"scheduled","en","");
				int i;
				Date date;
				String appointmentList = "";
				for(i=0; i<appointments.size();i++){
					date = appointments.get(i).getTimeSlot().getStartDate();
					appointmentList += appointments.get(i).getAppointmentType().getName()+
										" on "+sdf.format(date);
				}
				playUsingTTS(appointmentList,"en","");
				
			}
		}
		else if(keyWord.equals("cancel"))
		{
			appointments = appointmentRestCall.getAppointments(pid, AppointmentStatusType.SCHEDULED, LIMIT);
			if(appointments.size()==0)
			{
				playUsingTTS("Your don't have any appointments to cancel","en","");
			}
			else
			{
				playUsingTTS("Choose appointment to cancel","en","");
				int i;
				Date date;
				String appointmentList = "Press ";
				for(i=0; i<appointments.size();i++){
					date = appointments.get(i).getTimeSlot().getStartDate();
					appointmentList += (i+1)+ " to cancel "+appointments.get(i).getAppointmentType().getName()+
										"on "+sdf.format(date);
				}
				int choice = Character.getNumericValue(getOptionUsingTTS(appointmentList,"12345", "5000", 2));
				if(choice > appointments.size())
				{
					playUsingTTS("Invalid choice. Please try again","en","");
				}
				else
				{
					if(appointmentRestCall.cancelAppointment(appointments.get(choice-1).getUuid()))
					{
						playUsingTTS("An error occured while cancelling. Please try again","en","");
					}
					else
					{
						playUsingTTS("Your appointment has been successfully cancelled","en","");	
					}
				}
			}
		}
		else if(keyWord.equals("mainmenu"))
			{
			return;
			}
		playAppointmentMenu();
	} catch (AgiException e) {
		e.printStackTrace();
	}
			
	}
}