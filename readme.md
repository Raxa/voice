=============================================
INSTALLING AND CONFIGURING ASTERISK

=============================================
Tested with Asterisk 1.8 and 11.4.0(http://downloads.asterisk.org/pub/telephony/asterisk/old-releases/asterisk-11.4.0.tar.gz)
     
Install subversion:sudo apt-get install subversion

To install asterisk follow this link http://blogs.digium.com/2012/11/14/how-to-install-asterisk-11-on-ubuntu-12-4-lts/

Do one step at a time i.e make -> make install ..

Ensure to  use make menuselect while installing asterisk 
   
select format_mp3.so from make menuselect:it will load module to play mp3 files.

after 'make menuselect' do  'sudo sh contrib/scrips/get_mp3_source.sh' from asterisk source folder before make

Sample sip.conf,manager.conf,extensions.conf,logger.conf are present in asteriskConf Folder

Second way:

install Dahdi and libpri from the Digium video

do sudo apt-get install asterisk (install asterisk version 1.8)

=============================================

;Configuring /etc/asterisk/sip.conf:Create a SIP user SIP/1000abc that have context=incoming-call
 
 ;For testing purpose it is necessary to create SIP/1000abc as org.raxa.scheduler.OutgoingCallManager redirects all call to sip/1000abc

=============================================

[1000abc]

type=peer

allow=all

udpbindaddr=0.0.0.0 

bindaddr=0.0.0.0

secret=yoursecret

host=dynamic

context=incoming-call
   
=============================================   
;Configuring /etc/asterisk/extensions.conf:add two context outgoing-call and incoming call

=============================================


[outgoing-call]

exten=>100,1,SET(count=0)

exten=>100,2,AGI(agi://127.0.0.1/hello.agi?msgId=${msgId}&language=${preferLanguage}&aid=${aid}&ttsNotation=${ttsNotation})

exten=>100,3,GOTO(outgoing-call,122,1)


exten=>122,1,NoOp(Text:${message})

same=>n,NoOp(Text:${language})

;Here googletranslate goes

;now only support english

same=>n,agi(googletts.agi,${message},en)

same=>n,GOTO(outgoing-call,100,2)



[incoming-call]

exten=>100,1,Answer()

same=>n,AGI(agi://127.0.0.1/hello.agi)


=============================================
;edit /etc/asterisk/manager.conf and add the following lines

;follow http://ofps.oreilly.com/titles/9781449332426/asterisk-AMI.html for further details

=============================================

[general]

enabled = yes

port = 5038

bindaddr = 127.0.0.1

webenabled=yes

allowmultiplelogin=yes


[manager]

secret = squirrel

deny = 0.0.0.0/0.0.0.0

permit = 127.0.0.1/255.0.0.0

read=system,call,log,verbose,agent,command,user,all,call,user

write=system,call,log,verbose,agent,command,user,all


=============================================
;edit /etc/asterisk/logger.conf : This is done to log information about asterisk server.Suppose your project location is 

;/home/user/Project_Voice/logFiles/asteriskLog. add the following line in logger.conf

=============================================

/home/user/Project_Voice/logFiles/asteriskLog => notice,warning,error,dtmf


=============================================
INSTALLING A SIP PHONE(for testing only,need gui to work)

=============================================

install any sip phone.This is a way to install twinkle
 
 sudo apt-get update
 
 sudo apt-get install twinkle
  
 For configuring twinkle:http://www.callcentric.com/support/device/twinkle


=============================================
INSTALLING GOOGLE TTS

=============================================
  follow this Link:https://github.com/zaf/asterisk-googletts
   
  for testing use the example in here :http://zaf.github.io/asterisk-googletts/  
  
  IMPORTANT: copy the googleTTS AGI as present in the code above in agi-bin not the one downloaded from above link
  
  now give the file the write access where googletts.agi is copied. Usually /va/lib/asterisk/agi-bin
  
  do  sudo chmod 777 /var/lib/asterisk/agi-bin/googletts.agi


=============================================
INSTALL ANT

=============================================
  sudo apt-get -u install ant
  
  set environment variable ANT_HOME JAVA_HOME
  
  follow this link:http://ant.apache.org/manual/install.html



=============================================
INSTALLING JDK IN UBUNTU

remove openjdk if exist
 
Follow this:http://www.wikihow.com/Install-Oracle-Java-on-Ubuntu-Linux (manual)
or
follow http://www.webupd8.org/2012/01/install-oracle-java-jdk-7-in-ubuntu-via.html (automatic)

=============================================


=============================================
SOURCE CODE CONFIGURATION AND DEPENDENCY

=============================================

build.xml creates a jar of the module

build1.xml creates a "fat" jar of the module i.e that jar will include all jars used by module.

AlertMessage,AlertRegistration,Database are non-runnable jar(no main function)

  
========================================================================
see AllJarsDependency.txt and put all required jars in projectfolder/lib
========================================================================



=============================================
Steps to run the project(to be followed in the order as described)

=============================================

CREATE Database.jar
1.Database:Edit /resource/hibernate.cfg.xml according to your requirement.Set username,Password and url

2.Copy all the required libraries to lib

3.ant compile jar


CREATE AlertMessage.jar
1.Open AlertMessage

2.open english.properties and other language.properties file and fill in the require fields.

3.Copy all the required libraries to lib

4.ant compile jar


CREATE AlertRegistration.jar
1.Open AlertRegistration

2.fill the properties file

3.Copy all the required libraries to lib

4.ant compile jar


RUN AudioPlayer

1.open AudioPlayer

2.Copy all the required libraries to lib

3 fill the properties file

4.Ensure that the beep.mp3(a 2 sec sound that produces beep,even a silent tone will work) is present in the audioPlayer module.

13.ant compile jar run


CREATE SMS.jar
1.  Open SMSSender

2. Copy all the required libraries to lib

3. ant compile jar


RUN Scheduler
1.open  Scheduler

2.fill the properties file

3.Copy all the required libraries to lib

4.ant compile jar run


give write access to logFiles if required.

=========================================
A Note on Updater
=========================================
Updater updates patient alert for next day everyday
THIS IS ALSO DONE BY SCHEDULER
SO IF SCHEDULER IS RUNNING THERE IS NO NEED OF UPDATER

CAUTION:RUNNING BOTH SCHEDULER AND UPDATER IS A WASTE OF RESOURCE.THOUGH IT WONT AFFECT THE ALERT TABLE

Updater depends on 
->AlertMessage.jar
->Database.jar
->Other common Libraries

===================================================
RUNNING REPLYSMSHANDLER SERVLET
===================================================
INSTALL TOMCAT7

->sudo apt-get install tomcat7

BE SURE TO CHECK TOMCAT IS RUNNING THE SYSTEM JAVA version otherwise it may give UnSupportedClassVersionError
Follow this link:http://askubuntu.com/questions/154953/specify-jdk-for-tomcat7

->set $catallina_home and be sure to put the same in ReplySMSHandler/build.xml

->Bydefault its /usr/share/tomcat7

->go to ReplySMSHandler Module and do ant all in console to build a warfile. Location of the build war file is :dist/sms.war

->stop tomcat sudo /etc/init.d/tomcat7 stop

->copy the war file to /var/lib/tomcat7/webapps

->start tomcat sudo /etc/init.d/tomcat7 start

 The link to the servlet is http://localhost:8080/sms/incomingsms

->The link can be configured by changing web.xml

   

