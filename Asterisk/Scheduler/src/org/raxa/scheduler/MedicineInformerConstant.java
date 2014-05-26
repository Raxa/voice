package org.raxa.scheduler;
/**
 * SQL QUERIES
 * @author atul
 *
 */
public interface MedicineInformerConstant {
		
	  public static final String IVR_MEDICINE_QUERY_DATE="select p1.pnumber,p1.preferLanguage,a1.msgId,a1.aid from Patient p1,Alert a1 where p1.pid=a1.pid and a1.alertType=:alertType and a1.scheduleTime<=:systemTime and a1.isExecuted=:isExecuted and a1.retryCount<:retryCount";
		
	  public static final String SMS_MEDICINE_QUERY_DATE="select p1.pnumber,p1.preferLanguage,a1.msgId,a1.aid from Patient p1,Alert a1 where p1.pid=a1.pid and a1.alertType=:alertType and a1.scheduleTime<=:systemTime and a1.isExecuted=:isExecuted and a1.retryCount<:retryCount";
	
	  public static final String IVR_MEDICINE_QUERY_UUID="select p1.pnumber,p1.preferLanguage,i1.ivrId,a1.aid,a1.scheduleTime from Patient p1,Alert a1,IvrMsg i1 where p1.pid=:pid and p1.pid=a1.pid and a1.alertType=:alertType and a1.msgId=i1.ivrId order by a1.scheduleTime";
		
	  public static final String SMS_MEDICINE_QUERY_UUID="select p1.pnumber,s1.content,s1.ivrId,p1.pid,a1.aid from Patient p1,Alert a1,SmsMsg s1 where p1.pid=:pid and p1.pid=a1.pid and a1.alertType=:alertType and a1.msgId=s1.smsId order by s1.id,s1.itemNumber";
		
	  public static final String IVR_MEDICINE_QUERY_NUMBER="select p1.pnumber,p1.preferLanguage,i1.ivrId,a1.aid,a1.scheduleTime from Patient p1,Alert a1,IvrMsg i1 where p1.pid=:pid and (p1.pnumber=:pnumber or p1.snumber=:pnumber) and a1.alertType=:alertType and a1.msgId=i1.ivrId order by a1.scheduleTime";
		
	  public static final String SMS_MEDICINE_QUERY_NUMBER="select p1.pnumber,s1.content,s1.ivrId,p1.pid,a1.aid from Patient p1,Alert a1,SmsMsg s1 where p1.pnumber=:pnumber and p1.pid=a1.pid and a1.alertType=1 and a1.msgId=s1.smsId order by s1.id,s1.itemNumber";
		
}
