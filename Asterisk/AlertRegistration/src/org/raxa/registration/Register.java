
package org.raxa.registration;
import org.raxa.database.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Assumption:The patient with a valid uuid contains name of patient and phone number.
 * Don't use AlertType as 0;
 * This class supports registration,deregistration of any alertType 
 * @author atul
 *
 */
public class Register implements VariableSetter,registrationInterface {
	protected Logger logger = Logger.getLogger(this.getClass());
	private Properties prop = new Properties();
	private String contactUUID;
	private String patientQuery;
	private String patientFullQuery;
	private String patientQueryviaQ;
	private String patientFullQueryviaQ;
	public Register(){
		try {
            prop.load(this.getClass().getClassLoader().getResourceAsStream("restCall.properties"));
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

	/**
	 * Check if patient is register for the alert.
	 * If not,register patient for the Alert
	 * Now check if the patient information is available(patient table)
	 * if no,addPatient information;
	 * 
	 * Patient will be able to listen the alert from next day onwards (after midnight when the reset reminder will update the alert Table)
	 */
	public boolean addReminder(String pid,String preferLanguage,int alertType){
		
		if(checkIfPatientExist(pid,alertType)){
			logger.info("Patient with id:"+pid+" already exist for the alertType "+alertType);
			return false;
		}
		else{
			if(!addPateintToAlert(pid,alertType))
				return false;
		}
		
		if((getPatient(pid)!=null) || addPatient(pid,preferLanguage)){
			logger.info("Patient with id:"+pid+"successfully added");
			return true;
		}
		
		return false;
	}
	
	/**
	 * Returns true if the (patient Id,alertType) exist in table PAlert
	 * @param pid
	 * @param alertType
	 * @return boolean
	 */
	private boolean checkIfPatientExist(String pid,int alertType){
		List<PAlert> list=getpatientAlert(pid,alertType);
		if(list!=null && list.size()>0)
			return true;
		else return false;
	}
	
	/**
	 * Insert into table PAlert(pid,alertType).
	 * @param pid
	 * @param alertType
	 * @return boolean
	 */
	private boolean addPateintToAlert(String pid,int alertType){
		try{
			PAlert patientAlert=new PAlert(pid,alertType);
			Session session = HibernateUtil.getSessionFactory().openSession();
			session.beginTransaction();
			int id=(Integer)session.save(patientAlert);
			patientAlert.setPatientAlertId(id);
			session.getTransaction().commit();
			session.close();
			return true;
		}
		catch(Exception ex){
			logger.error("Unable to add patient in the alert");
			logger.error("\n ERROR Caused by\n",ex);
		}
		return false;
	}
	
	/**
	 * Add patient information patient Id,name,primary number,secondary number to table patient
	 * @param pid:patientId
	 * @param preferLanguage
	 * @return boolean
	 */
	private boolean addPatient(String pid,String preferLanguage){   
		List<String> info=getPatientNameAndNumberFromRest(pid);  
		Patient patient=null;
		if(!(info.equals(null)&& info.size()>1)){
			try{
				if(info.size()==2)							//if we don't have secondary number
					patient=new Patient(pid,info.get(0),info.get(1),preferLanguage);
				else patient=new Patient(pid,info.get(0),info.get(1),info.get(2),preferLanguage);  //if we have secondary number information of the patient
				Session session = HibernateUtil.getSessionFactory().openSession();
				session.beginTransaction();
				session.save(patient);
				session.getTransaction().commit();
				session.close();
				return true;
			}
			catch(Exception ex){
				logger.error("Unable to add Patient with id "+ pid);
				logger.error("\n ERROR Caused by\n",ex);
				return false;
			}
		}
		logger.error("Unable to add Patient with id "+ pid);
		return false;
	}
	
	
	/**
	 * remove patient from alert of alertType
	 * 
	 *<p>Checks if the patient is registered only for the alertType.If yes,remove patient from palert table as well as patient info table
	 *If patient is also registerd for other alerts remove patient from pAlert only
	 * @param pid
	 * @param alertType	
	 * @return boolean	
	 */
	public boolean deleteReminder(String pid,int alertType){
		logger.error("Deleting the patient with patient id:"+pid+" for alert:"+alertType);
		boolean deletePatient=false;PAlert patientAlert=null;
		Patient patient=getPatient(pid);
		List<PAlert> list=getpatientAlert(pid,alertType);
		if(list!=null && list.size()>0)
			patientAlert=(PAlert) list.get(0);
		else{
			logger.info("Patient with pid:"+pid+"\t is not register for this alert");
			return false;
		}
		
		List<PAlert> allList=getpatientAllAlert(pid);
		
		if(allList.size()==1 && patient!=null)
			deletePatient=true;
		try{
			Session session = HibernateUtil.getSessionFactory().openSession();
			session.beginTransaction();
			if(patientAlert!=null){
				session.delete(patientAlert);
				if(deletePatient)
					session.delete(patient);
			}
			session.getTransaction().commit();
			session.close();
			return true;
		}
		catch(Exception ex){
			logger.error("unable to delete patient");
			logger.error("\n ERROR Caused by\n",ex);
			return false;
		}
	}
	

	
	/**
	 * Make a Raxa REST call to get all patient bearing phone number pnumber
	 * @param pnumner:patient phone number
	 * @param prefereLanguage: patient Langaugae
	 * @return All patient who bears phone number "pnumber" as their primary or secondary number
	 */
	public List<Patient> getpatientFromNumber(String pnumber,String preferLanguage){
		logger.info("Trying to get patient info from patient where pnumber:"+pnumber);
		List<Patient> patients=new ArrayList<Patient>();
		String query=patientQueryviaQ+pnumber+patientFullQueryviaQ;
		ObjectMapper m=new ObjectMapper();
		try{
			JsonNode rootNode = m.readTree(RestCall.getRequestGet(query));
			if(rootNode==null)
				return null;
			JsonNode results = rootNode.get("results");
			for(JsonNode result:results){
				String uuid=result.path("uuid").textValue();
				String name=result.path("person").path("display").textValue();
				patients.add(new Patient(uuid,name,pnumber,null,preferLanguage));
			}
		}
		catch(Exception ex){
			logger.info("Some Error occured.Retruning null for phone number:"+pnumber);
			logger.error("\n ERROR Caused by\n",ex);
			patients=null;
		}
		return patients;
	}
	
	
	
	/**
	 * return patient information in form of a list
	 * list.get(0) is patient name,list.get(1) is patient primaty number,further are patient secondaty numbers.
	 * returns 
	 * @param pid
	 * @return List<String> 
	 * 
	 */
	private List<String> getPatientNameAndNumberFromRest(String pid){
		try{
			logger.info("Trying to get patient name and number where id:"+pid);
			List<String> a=new ArrayList<String>();
			String query=patientQuery+pid+patientFullQuery;
			ObjectMapper m=new ObjectMapper();
			JsonNode rootNode = m.readTree(RestCall.getRequestGet(query));
			
				
			try{
				a.add(rootNode.path("person").path("display").textValue());
				
			}
			catch(Exception ex){
				logger.warn("name not found for patient with uuid "+pid);
				logger.error("\n ERROR Caused by\n",ex);
				a.add(null);
			}
			
			JsonNode attribute=rootNode.path("person").get("attributes");
			for(int i=0;i<attribute.size();i++)
				if((attribute.get(i).path("attributeType").path("uuid").textValue()).equals(contactUUID))
					a.add(attribute.get(i).path("value").textValue());
			
			return a;
		}
		
		catch(Exception ex){
			logger.error("Some error while making rest call on patient with id "+pid);
			logger.error("\n ERROR Caused by\n",ex);
			return null;
		}
		
	}
	
	/**
	 * return Patietn object containing patient information given a patient id
	 * @param pid
	 * @return Patient
	 */
	private Patient getPatient(String pid){
		logger.info("Extracting patient information with id:"+pid);
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		Patient patient = (Patient) session.get(Patient.class,pid);
		session.getTransaction().commit();
		session.close();
		return patient;
	}
	
	/**
	 * return Objects of Palert containing patient id and alertType
	 * @param pid
	 * @param alertType
	 * @return List<(Object) PAlert>
	 */
	
	private List<PAlert> getpatientAlert(String pid,int alertType){
		logger.info("Extracting patient information with id:"+pid+"alertType:"+alertType);
		Session session = HibernateUtil.getSessionFactory().openSession();
		String hql="from PAlert where pid=:pid and alertType=:alertType";
		session.beginTransaction();
		Query query = session.createQuery(hql);
		query.setString("pid", pid);
		query.setInteger("alertType", alertType);
		List<PAlert> list=(List<PAlert>) query.list();
		session.getTransaction().commit();
		session.close();
		return list;
	}
	
	/**
	 * return all alerts(patient Id and alertType for which patient is registered where patient ID is pid
	 * @param pid
	 * @return List<(Object) PAlert>
	 */
	
	private List<PAlert> getpatientAllAlert(String pid){
		logger.info("Getting patient alert of pid:"+pid);
		Session session = HibernateUtil.getSessionFactory().openSession();
		String hql="from PAlert where pid=:pid";
		session.beginTransaction();
		Query query = session.createQuery(hql);
		query.setString("pid", pid);
		List<PAlert> list=(List<PAlert>) query.list();
		session.getTransaction().commit();
		session.close();
		return list;
	}
	
	
	
	
}