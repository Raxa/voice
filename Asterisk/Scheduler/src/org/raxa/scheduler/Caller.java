
package org.raxa.scheduler;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.raxa.database.Alert;
import org.raxa.database.HibernateUtil;
import org.raxa.database.VariableSetter;

/**
 * Call Manager AMI and place a call and pass neccessary information
 * Udate Alert retry Count
 */

public class Caller implements Runnable,VariableSetter {
	
	private AlertInfo patient;
	
	public Caller(AlertInfo obj){
		patient=obj;
	}
	
	/**
	 * call the patient 
	 */
	public void run(){
		Logger logger = Logger.getLogger(this.getClass());
		OutgoingCallManager o=new OutgoingCallManager();
		if(!(o.callPatient(patient.getPhoneNumber(),String.valueOf(patient.getMsgId()), patient.getAlertId(),patient.getpreferLanguage())))
			logger.error("unable to call Patient with alertId "+patient.getAlertId());
		else{
			logger.info("Placed a call to the patient with aid "+patient.getAlertId());
			updateAlertCount();
		}
	}
	
	/**
	 * Increment retry Count of the patient
	 */
	public void updateAlertCount(){
		
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		String hql="from Alert where aid=:aid and alertType=:alertType";
		Query query=session.createQuery(hql);
		query.setString("aid", patient.getAlertId());
		query.setInteger("alertType", IVR_TYPE);
		Alert alert = (Alert) query.list().get(0);
		int retryCount=alert.getretryCount()+1;
		alert.setretryCount(retryCount);
		session.update(alert);
		session.getTransaction().commit();
		session.close();
	}
}	
