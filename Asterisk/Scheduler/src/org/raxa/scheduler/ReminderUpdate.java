package org.raxa.scheduler;

import java.sql.Timestamp;
import java.util.List;
import org.raxa.alertmessage.GetJson;
import org.raxa.alertmessage.MessageTemplate;
import org.raxa.alertmessage.Reminder;
import org.raxa.database.Alert;
import org.raxa.database.HibernateUtil;
import org.raxa.database.IvrMsg;
import org.raxa.database.VariableSetter;
import org.raxa.database.SmsMsg;
import org.apache.log4j.Logger;
import org.hibernate.Session;

/**
 * Set alerts for today
 * @author Atul Agrawal
 *
 */
public class ReminderUpdate implements VariableSetter {
	protected Logger logger = Logger.getLogger(this.getClass());
	/**
	 * search for all reminder for patient ID for today and create a message out of each reminder uisng message Template
	 * @param pid patient ID
	 * @param name patient Name : use while formating a message
	 * @param preferLanguage :language for the alert
	 * @param alertType : alert patietnt want
	 */
	public void resetReminder(String pid,String name,String preferLanguage,int alertType){
		try{
			GetJson json=new GetJson();
			List<Reminder> reminder=json.getAlert(pid);
			MessageTemplate m=new MessageTemplate();
			if((!(reminder==null)) && reminder.size()>=1){
				for(Reminder r:reminder){
					List<String> template=m.templatize(r.getrawmessage(), preferLanguage, name, pid,alertType);  //IMPORTANT have not implemented the feature to join all alert that occur between 30 minutes interval.Must incl}
					if(!(template==null)){
						r.setTemplatizeMessage(template);
						addReminderToDatabase(pid,r,alertType);
					}
				}
			}
		}
		catch(Exception ex){
			
			logger.error("Some error occured while entering Reminder of patient with patient ID:"+pid);
			logger.error("\nCaused by\n",ex);
		}
		
	}
	/**
	 * put information in Reminder Object to database		
	 * @param pid patient ID
	 * @param r Reminder Object
	 * @param alertType
	 */
	public void addReminderToDatabase(String pid,Reminder r,int alertType){
		int msgId=getMsgId(alertType);int count=0;			//return the max+1 msgID
		Session session = HibernateUtil.getSessionFactory().openSession();
		Timestamp time=new Timestamp(r.getTime().getTime());
		Alert a=new Alert(r.getAlertId(),pid,alertType,msgId,time,null);a.setretryCount(0);a.setIsExecuted(false);a.setServiceInfo(null);
		session.beginTransaction();
		for(String content:r.getTemplatizeMessage()){
			if(alertType==IVR_TYPE){
				IvrMsg ivrmsg=new IvrMsg(msgId,++count,content);
				int id = (Integer) session.save(ivrmsg);
				ivrmsg.setId(id);
				session.persist(ivrmsg);
			}
			else if(alertType==SMS_TYPE){
				SmsMsg smsmsg=new SmsMsg(msgId,++count,content);
				int sid = (Integer) session.save(smsmsg);
				smsmsg.setId(sid);
				session.persist(smsmsg);
			}
			else return;
		}
		session.save(a);
		session.getTransaction().commit();
		session.close();
		logger.info("Added reminder for alert of patient:"+pid);
	}
	/**
	 * @return max(msgId) + 1 for alertType 
	 *
	 */
	
	public int getMsgId(int alertType){
		int maxID;String hql;
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		if(alertType==IVR_TYPE)
			hql="select max(a.ivrId) from IvrMsg a";
		else if(alertType==SMS_TYPE)
			hql="select max(a.smsId) from SmsMsg a";
		else return 0;
		List list = session.createQuery(hql).list(); 
		if((Integer)list.get(0)==null)
			maxID=0;
		else 
			maxID = ( (Integer)list.get(0) ).intValue();
		session.getTransaction().commit();
		session.close();
		return maxID+1;
	}
	
	
}
