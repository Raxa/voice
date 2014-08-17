package org.raxa.module.handlesms;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.openmrs.Location;
import org.openmrs.Provider;
import org.openmrs.module.appointmentscheduling.Appointment;
import org.openmrs.module.appointmentscheduling.AppointmentType;
import org.openmrs.module.appointmentscheduling.TimeSlot;
import org.openmrs.module.appointmentscheduling.Appointment.AppointmentStatusType;
import org.raxa.database.Patient;
import org.raxa.database.VariableSetter;
import org.raxa.restUtils.AppointmentRestCall;

/**
 * All classes extending Options function as state machines
 * Based on user response, the state of the instance changes
 * Makes use of AppointmentRESTCall class
 *  
 * @author rahulr92
 *
 */
//TODO: Change raw numbers for states to named constants
public class AppointmentSMS extends Options implements VariableSetter {
	List<Location> locations;
	List<Provider> providers;
	List<AppointmentType> appointmentTypes;
	List<TimeSlot> timeSlots;
	List<Appointment> appointments;
	AppointmentType appointmentType;
	Location location;
	Provider provider;
	TimeSlot timeSlot;
	Appointment appointment;
	private static Logger logger = Logger.getLogger(AppointmentSMS.class);
	private static String LIMIT = "5";
	//move to properties file
	private static String HEADER = "Choose your %s:";
	private static String FOOTER = "%s\n%s";
	final int SEARCH_INTERVAL = 14;
	final String DATE_FORMAT = "dd MMMM h a";
	SimpleDateFormat sdf;
	private final String APPOINTMENT_CONFIRMATION = "Thanks. Your appointment has been confirmed. Details:\n"
			+ "Appointment type:%s\n"
			+ "Location:%s\n"
			+ "Doctor:%s\n"
			+ "Time:%s";
	private final String APPOINTMENT_MENU = "Choose your option:\n"
			+ "1. Book appointment\n"
			+ "2. View appointments\n"
			+ "3. Cancel appointment";
	private final String NO_APPOINTMENTS = "Your don't have any appointments scheduled";
	/**
	 * The system is initially in state 0. It responds with the menu in state 0 & sets processMenu as true.
	 * When processMenu is true, the user response is used to determine the next state (eg. 1,6,7 for booking, viewing & canceling )
	 */
	boolean processMenu;
	
	AppointmentRestCall appointmentRestCall;
	public AppointmentSMS(String userID, String pnumber) {
		super(userID, pnumber);
		appointmentRestCall = new AppointmentRestCall();
	    sdf = new SimpleDateFormat(DATE_FORMAT);
	    processMenu = false;
		List<Patient> patients = DatabaseService.getAllPatients(pnumber);
		if(patients != null && patients.size() > 0)
			pid = patients.get(0).getPatientId();
	}

	@Override
	public String getKeyWord() {
		return Keyword.APPOINTMENT.getKeyword();
	}

	@Override
	protected String getDescription() {
		return Keyword.APPOINTMENT.getDescription();
	}

	/**
	 * Manages state of the instance and creates reply messages for each state
	 * Returns empty string when there is no reply to be sent
	 */
	@Override
	public String getReply(String message, String pnumber) {

		String reply = "";
		String [] messageParts = message.split(" ");
		String choice;
		int choiceIndex = 0;
		
		//Get choice
		if(messageParts.length > 1)
		{
			choice = messageParts[1];
			choiceIndex = Integer.parseInt(choice)-1;
			logger.info("User has chosen "+choice);
		}
		
		//Update state
		if(processMenu){
			logger.info("processing menu");
			processMenu = false;
			switch(choiceIndex+1){
			case 1:
				//book appointment
				state++;
				break;
			case 2:
				//view appointments
				state=6;
				break;
			case 3:
				//cancel appointment
				state=7;
			}
		} else {
			state++;
		}
		logger.info("User is in state "+state);
		
		switch(state){
		case 0:
		{	//provide menu
			processMenu = true;
			return String.format(FOOTER, APPOINTMENT_MENU, getLastLine());
		}	
		case 1:
		{
			//get appointment type
			appointmentTypes = appointmentRestCall.getAppointmentTypes(LIMIT);
			reply = String.format(HEADER,"appointment type");
			for(int i=0; i<appointmentTypes.size();i++){
				reply += "\n"+(i+1)+". "+appointmentTypes.get(i).getName();
			}
			
			return String.format(FOOTER, reply, getLastLine());
		}
		case 2:
		{
			//identify choice of previous state
			appointmentType = appointmentTypes.get(choiceIndex);
			//get location
			locations = appointmentRestCall.getLocations(LIMIT);
			reply = String.format(HEADER,"location");
			for(int i=0; i < locations.size(); i++){
				reply += "\n"+(i+1)+". "+locations.get(i).getName();
			}
			return String.format(FOOTER, reply, getLastLine());
		}
		case 3:
		{
			location = locations.get(choiceIndex);
			//get provider
			providers = appointmentRestCall.getProviders(LIMIT);
			reply = String.format(HEADER,"doctor");
			for(int i=0; i < providers.size(); i++){
				reply += "\n"+(i+1)+". "+providers.get(i).getName();
			}
			return String.format(FOOTER, reply, getLastLine());
		}
		case 4:
		{
			provider = providers.get(choiceIndex);
			//Get next 5 timeslots within 2 weeks
			Date fromDate = new Date();
			Date toDate = AppointmentRestCall.addDaysToDate(fromDate, SEARCH_INTERVAL);
			String appointmentTypeUuid = appointmentType.getUuid();
			String locationUuid = location.getUuid();
			String providerUuid = provider.getUuid();
			reply = String.format(HEADER,"appointment time");
			//get timeslot
			timeSlots = appointmentRestCall.getTimeSlots(fromDate, toDate, appointmentTypeUuid, locationUuid, providerUuid, LIMIT);
			Date date;
			for(int i=0; i<timeSlots.size();i++){
				date = timeSlots.get(i).getStartDate();
				reply += "\n"+(i+1)+". "+sdf.format(date);
			}
			return String.format(FOOTER, reply, getLastLine());
		}
		case 5:
		{
			timeSlot = timeSlots.get(choiceIndex);
			String appointmentTypeUuid = appointmentType.getUuid();
			String timeSlotUuid = timeSlot.getUuid();
			//provide booking confirmation
			if(appointmentRestCall.createAppointment(timeSlotUuid, pid, appointmentTypeUuid)){
				reply = String.format(APPOINTMENT_CONFIRMATION, appointmentType.getName(),
										location.getName(),provider.getName(),sdf.format(timeSlot.getStartDate()));
			}else{
				reply = RMessage.ERROR.getMessage();
			}
			return reply;
			
		}
		case 6:
		{
			//provide appointment info
			appointments = appointmentRestCall.getAppointments(pid,AppointmentStatusType.SCHEDULED, LIMIT);
			if(appointments.size()==0)
			{
				return NO_APPOINTMENTS;
			}
			String plural = (appointments.size() == 1) ? " " : "s ";
			reply = "You have the following appointment"+plural+"scheduled";
			int i;
			Date date;
			for(i=0; i<appointments.size();i++){
				date = appointments.get(i).getTimeSlot().getStartDate();
				reply += "\n"+(i+1)+". "+appointments.get(i).getAppointmentType().getName()+
									" on "+sdf.format(date);
			}
			return reply;		
		}
		case 7:
		{
			//provider booked appointment for cancellation
			//TODO: possibly use state 6 with modified header and footer
			appointments = appointmentRestCall.getAppointments(pid,AppointmentStatusType.SCHEDULED, LIMIT);
			if(appointments.size()==0)
			{
				return NO_APPOINTMENTS;
			}
			String plural = (appointments.size() == 1) ? " " : "s ";
			reply = "Choose an appointment to cancel:";
			int i;
			Date date;
			for(i=0; i<appointments.size();i++){
				date = appointments.get(i).getTimeSlot().getStartDate();
				reply += "\n"+(i+1)+". "+appointments.get(i).getAppointmentType().getName()+
									" on "+sdf.format(date);
			}
			return reply;		
		}
		case 8:
		{
			appointment = appointments.get(choiceIndex);
			//provide cancel confirmation
			if(appointmentRestCall.cancelAppointment(appointment.getUuid()))
			{
				return "Your appointment has been successfully cancelled";
			}
			else
			{
				return "An error occured while cancelling. Please try again.";
			}
		}
		default: 
			//incorrect state message
		}
		return reply;
	}

}