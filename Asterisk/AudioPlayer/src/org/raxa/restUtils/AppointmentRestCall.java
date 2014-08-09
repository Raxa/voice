package org.raxa.restUtils;



import java.io.IOException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.apache.http.entity.StringEntity;
import org.apache.log4j.Logger;
import org.openmrs.Location;
import org.openmrs.Provider;
import org.openmrs.module.appointmentscheduling.Appointment;
import org.openmrs.module.appointmentscheduling.Appointment.AppointmentStatusType;
import org.openmrs.module.appointmentscheduling.AppointmentBlock;
import org.openmrs.module.appointmentscheduling.AppointmentType;
import org.openmrs.module.appointmentscheduling.TimeSlot;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONObject;
import org.json.JSONStringer;
import org.raxa.restUtils.ISO8601DateParser;

/**
 * Assumption:The patient with a valid uuid contains name of patient and phone number.
 * Don't use AlertType as 0;
 * This class supports registration,deregistration of any alertType 
 * @author atul
 *
 */
public class AppointmentRestCall{
	protected Logger logger = Logger.getLogger(this.getClass());
	private Properties prop = new Properties();
	private String contactUUID;
	private String patientQuery;
	private String patientFullQuery;
	private String patientQueryviaQ;
	private String patientFullQueryviaQ;
	public AppointmentRestCall(){
		try {
            prop.load(this.getClass().getClassLoader().getResourceAsStream("appointmentRestCall.properties"));
            String urlbase=prop.getProperty("restBaseUrl","");
            String username=prop.getProperty("username","");
            String password=prop.getProperty("password","");
            contactUUID=prop.getProperty("contactuuid","");
            patientQuery=prop.getProperty("patientQuery","");
            patientFullQuery=prop.getProperty("patientFullQuery","");
            patientQueryviaQ=prop.getProperty("patientQueryviaQ","");
            patientFullQueryviaQ=prop.getProperty("patientFullQueryviaQ","");
			RestCall.setURLBase(urlbase);
	        RestCall.setUsername(username);
	        RestCall.setPassword(password);
		}
		catch(IOException ex){
			logger.error("IMPORTANT:UNABLE TO CONNECT TO THE REST CALL");
		}
	}
	

	
	public List<AppointmentType> getAppointmentTypes(String limit){
		String query="appointmentscheduling/appointmenttype?v=full&limit="+limit;
		ObjectMapper m=new ObjectMapper();
		List<AppointmentType> appointmentTypes = new ArrayList<AppointmentType>();
		try{
			JsonNode rootNode = m.readTree(RestCall.getRequestGet(query));
			JsonNode results = rootNode.get("results");
			System.out.println(results.size());
			for(JsonNode result:results){
				String uuid=result.path("uuid").textValue();
				String name=result.path("display").textValue();
				String desc=result.path("description").textValue();
				Integer duration=result.path("duration").asInt();
				AppointmentType appType = new AppointmentType(name,desc,duration);
				appType.setUuid(uuid);
				appointmentTypes.add(appType);
			}
			return appointmentTypes;
		}
		catch(Exception ex){
			logger.info("Some Error occured.Retruning null for phone number:");
			logger.error("\n ERROR Caused by\n",ex);
			ex.printStackTrace();
			return appointmentTypes;
		}
	}
	
	public List<Appointment> getAppointments(String patient, AppointmentStatusType status, String limit){
		String query="appointmentscheduling/appointment?patient="+patient+
				"&status="+status.toString()+"&limit="+limit+"&v=full";
		ObjectMapper m=new ObjectMapper();
		Appointment appointment;
		TimeSlot timeSlot;
		AppointmentType appointmentType;
		List<Appointment> appointments = new ArrayList<Appointment>();
		try{
			JsonNode rootNode = m.readTree(RestCall.getRequestGet(query));
			JsonNode results = rootNode.get("results");
			System.out.println(results.size());
			for(JsonNode result:results){
				String uuid=result.path("uuid").textValue();
				Date startDate = ISO8601DateParser.parse(result.path("timeSlot").path("startDate").textValue());
				Date endDate = ISO8601DateParser.parse(result.path("timeSlot").path("endDate").textValue());
				appointmentType = new AppointmentType();
				appointmentType.setName( result.path("appointmentType").path("name").textValue());
				timeSlot = new TimeSlot();
				timeSlot.setStartDate(startDate);
				timeSlot.setEndDate(endDate);
				appointment = new Appointment();
				appointment.setUuid(uuid);
				appointment.setTimeSlot(timeSlot);
				appointment.setAppointmentType(appointmentType);
				appointments.add(appointment);
			}
			return appointments;
		}
		catch(Exception ex){
			logger.info("Some Error occured. Returning null appointments for patient "+patient);
			logger.error("\n ERROR Caused by\n",ex);
			ex.printStackTrace();
			return appointments;
		}
	}
	
	public List<Location> getLocations(String limit){
		String query="location?"+"limit="+limit;
		ObjectMapper m=new ObjectMapper();
		List<Location> locations = new ArrayList<Location>();
		Location location;
		try{
			JsonNode rootNode = m.readTree(RestCall.getRequestGet(query));
			JsonNode results = rootNode.get("results");
			for(JsonNode result:results){
				String uuid=result.path("uuid").textValue();
				String name=result.path("display").textValue();
				location = new Location();
				location.setName(name);
				location.setUuid(uuid);
				locations.add(location);
			}
			return locations;
		}
		catch(Exception ex){
			logger.info("Some Error occured while getting locations.");
			logger.error("\n ERROR Caused by\n",ex);
			ex.printStackTrace();
			return locations;
		}
	}
	
	public List<Provider> getProviders(String limit){
		//String query="provider?q=doctor&v=full?limit="+limit;
		String query="provider?v=full&limit="+limit;
		ObjectMapper m=new ObjectMapper();
		List<Provider> providers = new ArrayList<Provider>();
		Provider provider;
		try{
			JsonNode rootNode = m.readTree(RestCall.getRequestGet(query));
			JsonNode results = rootNode.get("results");
			for(JsonNode result:results){
				String uuid=result.path("uuid").textValue();
				String name=result.path("person").path("display").textValue();
				provider = new Provider();
				provider.setName(name);
				provider.setUuid(uuid);
				providers.add(provider);
			}
			return providers;
		}
		catch(Exception ex){
			logger.info("Some Error occured while getting providers");
			logger.error("\n ERROR Caused by\n",ex);
			ex.printStackTrace();
			return providers;
		}
	}

	
	public List<TimeSlot> getTimeSlots(Date fromDate, Date toDate, String appointmentType, String location, String provider, String limit){
		String charset = "UTF-8";
		ObjectMapper m=new ObjectMapper();
		List<TimeSlot> timeSlots = new ArrayList<TimeSlot>();
		TimeSlot timeSlot;
		try{
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
			String query= String.format("appointmentscheduling/timeslot?fromDate=%s"
					+"&toDate=%s&appointmentType=%s"
					+"&location=%s&provider=%s"
					+"&limit=%s&v=full",
					URLEncoder.encode(df.format(fromDate), charset),
					URLEncoder.encode(df.format(toDate), charset),
					URLEncoder.encode(appointmentType, charset),
					URLEncoder.encode(location, charset),
					URLEncoder.encode(provider, charset),
					URLEncoder.encode(limit, charset)
					);
			logger.debug(query);
			JsonNode rootNode = m.readTree(RestCall.getRequestGet(query));
			JsonNode results = rootNode.get("results");
			for(JsonNode result:results){
				String uuid=result.path("uuid").textValue();
				Date startDate = ISO8601DateParser.parse(result.path("startDate").textValue());
				Date endDate = ISO8601DateParser.parse(result.path("endDate").textValue());
				String appBlockUuid=result.path("appointmentBlock").path("uuid").textValue();
				AppointmentBlock appointmentBlock = new AppointmentBlock();
				appointmentBlock.setUuid(appBlockUuid);
				timeSlot = new TimeSlot(appointmentBlock,startDate,endDate);
				timeSlot.setUuid(uuid);
				timeSlots.add(timeSlot);
				System.out.println(startDate.toString()+ " "+endDate.toString());
			}
			return timeSlots;
		}
		catch(Exception ex){
			logger.info("Some Error occured while getting timeSlots");
			logger.error("\n ERROR Caused by\n",ex);
			ex.printStackTrace();
			return timeSlots;
		}
	}
	
	public static Date addDaysToDate(final Date date, int noOfDays) {
	    Date newDate = new Date(date.getTime());

	    GregorianCalendar calendar = new GregorianCalendar();
	    calendar.setTime(newDate);
	    calendar.add(Calendar.DATE, noOfDays);
	    newDate.setTime(calendar.getTime().getTime());

	    return newDate;
	}
	
	public boolean createAppointment(String timeSlot, String patient, String appointmentType){
		String query = "appointmentscheduling/appointment";
		try{
			String input = "{\"patient\": \"" + patient + 
				      "\", \"timeSlot\": \"" + timeSlot + 
				       "\", \"appointmentType\": \"" + appointmentType +
				       "\", \"status\": \"SCHEDULED\"}";
			logger.debug(input);
			System.out.println(input);
			StringEntity se = new StringEntity(input);
			se.setContentType("application/json");
			if(RestCall.getRequestPost(query,se))
				System.out.println("Success");
			else
				System.out.println("Failure");
			return true;
		}catch(Exception ex){
			logger.info("Some Error occured inside createAppointment");
			logger.error("\n ERROR Caused by\n",ex);
			ex.printStackTrace();
			return false;	
		}
	}
	
	public boolean cancelAppointment(String uuid){
		String query = "appointmentscheduling/appointment/"+uuid;
		try{
			String input = "{\"status\": \"CANCELLED\"}";
			logger.debug(input);
			System.out.println(input);
			StringEntity se = new StringEntity(input);
			se.setContentType("application/json");
			if(RestCall.getRequestPost(query,se))
				System.out.println("Success");
			else
				System.out.println("Failure");
			return true;
		}catch(Exception ex){
			logger.info("Some Error occured inside cancelAppointment");
			logger.error("\n ERROR Caused by\n",ex);
			ex.printStackTrace();
			return false;	
		}
	}

}
