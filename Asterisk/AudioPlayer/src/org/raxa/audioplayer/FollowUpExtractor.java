

package org.raxa.audioplayer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.raxa.alertmessage.FollowUpInformation;
import org.raxa.alertmessage.MedicineInformation;
import org.raxa.alertmessage.MessageTemplate;
import org.raxa.database.Alert;
import org.raxa.database.HibernateUtil;
import org.raxa.database.IvrMsg;
import org.raxa.database.Patient;
import org.raxa.database.VariableSetter;

public class FollowUpExtractor implements VariableSetter {
		private String pid,aid;
		private Logger logger = Logger.getLogger(this.getClass());
		
		public FollowUpExtractor(String pid){
			this.pid=pid;
			
		}
		
		public ReminderExtractor(){}
		 
		protected List<MedicineInformation> getMedicineInfo(Date time){
			return getMedicineInfo(pid,time);
		}
		/**
		 *
		 * 
		 * @param pid
		 * @return all patient medicine info,whose id is pid to be,to be taken from the start of the day.
		 * @return null if any Exception occurs
		 */
		protected List<FollowUpInformation> getFollowUpInfo(String pid,Date time){
			
			List<FollowUpInformation> list=new ArrayList<FollowUpInformation>();
			try{
			
			Session session=HibernateUtil.getSessionFactory().openSession();
			session.beginTransaction();
			String hql="from Alert where scheduleTime>=:time and pid=:pid and alertType=:alertType order by scheduleTime";
			Query query=session.createQuery(hql);
			query.setString("pid", pid);
			query.setTimestamp("time",time);
			query.setInteger("alertType",FOLLOWUP_TYPE);
			List<Alert> content=(List<Alert>)query.list();
			for(Alert alert: content){
				List<String> itemContents=new ArrayList<String>();
		   		String hql2="from IvrMsg where ivrId=:msgId order by itemNumber";
	    		Query query2=session.createQuery(hql2);
	    		query2.setInteger("msgId",alert.getMessageId());
	    		List<IvrMsg> results=(List<IvrMsg>)query2.list();	
	    		for(IvrMsg i:results){
	    			itemContents.add(i.getContent());
	    		}
	    			followUpQuestions f=new FollowUpTemplate().getFollowUpQuestions(itemContents);
	    			f.setScheduleTime(alert.getScheduleTime());
	    			list.add(f);
	    	}
			
			session.getTransaction().commit();
			session.close();
			}
			catch(Exception ex){
				
				logger.info("Some error occured while in incoming-call and fetching information about patient with id "+pid);
				logger.error("\nCaused by:\n",ex);
				list=null;
			}
			return list;
		}
		
	

		
	
}
