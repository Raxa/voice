-- MySQL dump 10.13  Distrib 5.5.31, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: atul
-- ------------------------------------------------------
-- Server version	5.5.31-0ubuntu0.12.04.2

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `smsRecord`
--

DROP TABLE IF EXISTS `smsRecord`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `smsRecord` (
  `id` bigint(10) NOT NULL AUTO_INCREMENT,
  `pnumber` varchar(15) NOT NULL,
  `inTime` datetime NOT NULL,
  `msg` varchar(255) DEFAULT NULL,
  `reply` varchar(255) DEFAULT NULL,
  `outTime` datetime NOT NULL,
  `isExecuted` char(1) DEFAULT NULL,
  `transactionId` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `smsRecord`
--

LOCK TABLES `smsRecord` WRITE;
/*!40000 ALTER TABLE `smsRecord` DISABLE KEYS */;
INSERT INTO `smsRecord` VALUES (1,'8149195049','2013-09-07 14:44:29','wassup dude?','what bitch?','2013-09-07 14:44:29','N','sadas45644'),(2,'8149195049','2013-09-07 14:45:47','wassup dude?','what bitch?','2013-09-07 14:45:47','N','sadas45644'),(3,'8149195049','2013-09-07 14:45:47','wassup dude?','what bitch?','2013-09-07 14:45:47','Y','sadas45644'),(4,'8149195049','2013-09-07 14:50:58','wassup dude?','what bitch?','2013-09-07 14:50:58','N','sadas45644'),(5,'8149195049','2013-09-07 14:50:58','wassup dude?','what bitch?','2013-09-07 14:50:58','Y','sadas45644'),(8,'8149195049','2013-09-07 15:03:28','wassup dude?','what bitch?','2013-09-07 15:03:28','N','sadas45644'),(9,'8149195049','2013-09-07 15:03:28','wassup dude?','what bitch?','2013-09-07 15:03:28','Y','sadas45644'),(10,'8149195049','2013-09-07 15:04:20','wassup dude?','what bitch?','2013-09-07 15:04:20','N','sadas45644'),(11,'8149195049','2013-09-07 15:04:20','wassup dude?','what bitch?','2013-09-07 15:04:20','Y','sadas45644'),(12,'8093672342','2013-09-06 00:10:00','get menu','Main Menu :\n1. Get your Medicine Reminder\n2. Register yourself for the service\n3. Unregister yourself from the service\n\nType DPQ (space)(your option)to send your option.','2013-09-07 16:26:33','N','Message contains spam '),(13,'8093672342','2013-09-07 16:27:11','DPQ 1','Type DPQ your option \n1. Get Which Medicine To take Now.Short:GET REM NOW\n2. Get Which Medicine To take today.Short:GET REM TODAY\n3. Get Which Medicine to take afterwards.Short:GET REM NOWON\n\nType DPQ (space)(your option)to send your option.','2013-09-07 16:27:11','N','Message contains spam '),(14,'8093672342','2013-09-07 16:27:20','DPQ 2','Type DPQ (your aoption)\n1 for Ishan Mehta\n2 for Piyush Madan2\n3 for Akshay Sohal\n','2013-09-07 16:27:20','Y','ins37_13785514418086'),(15,'8093672342','2013-09-07 16:27:31','DPQ 3','Hello Akshay Sohal Sorry The system has no answer to your query.Start Again or try later','2013-09-07 16:27:32','Y','ins37_13785514528117'),(16,'8093672342','2013-09-06 00:10:00','get menu','Main Menu :\n1. Get your Medicine Reminder\n2. Register yourself for the service\n3. Unregister yourself from the service\n\nType QWI (space)(your option)to send your option.','2013-09-07 16:27:38','N','Message contains spam '),(17,'8093672342','2013-09-07 16:27:43','qwi 1','Type QWI your option \n1. Get Which Medicine To take Now.Short:GET REM NOW\n2. Get Which Medicine To take today.Short:GET REM TODAY\n3. Get Which Medicine to take afterwards.Short:GET REM NOWON\n\nType QWI (space)(your option)to send your option.','2013-09-07 16:27:43','N','Message contains spam '),(18,'8093672342','2013-09-07 16:27:56','qwi 2','Type QWI (your aoption)\n1 for Ishan Mehta\n2 for Piyush Madan2\n3 for Akshay Sohal\n','2013-09-07 16:27:56','Y','ins37_13785514768261'),(19,'8093672342','2013-09-07 16:27:59','qwi 1','Hello Ishan Mehta Sorry The system has no answer to your query.Start Again or try later','2013-09-07 16:27:59','Y','ins37_13785514798283'),(20,'8093672342','2013-09-06 00:10:00','get rem today','Type KHY (your aoption)\n1 for Ishan Mehta\n2 for Piyush Madan2\n3 for Akshay Sohal\n','2013-09-07 16:28:25','Y','ins37_13785515058588'),(21,'8093672342','2013-09-06 00:10:00','get reg','Choose Your preferLangauge\n1 .English2 .Hindi\nType WVJ (space)(your option)to send your option.','2013-09-07 16:29:28','N','Message contains spam '),(22,'8093672342','2013-09-07 16:29:37','wvj 2','Type WVJ (your aoption)\n1 for Akshay Sohal\n2 for Ishan Mehta\n3 for Piyush Madan2\n','2013-09-07 16:29:40','Y','ins37_13785515808884'),(23,'8093672342','2013-09-07 16:29:49','wvj 3','Hello Piyush Madan2 Either you are already Registered or Not exist in the system.\n Please consult the Hospital Authority if you are not registered','2013-09-07 16:29:49','Y','ins37_13785515898938'),(24,'8093672342','2013-09-06 00:10:00','get menu','Main Menu : 1. Get your Medicine Reminder 2. Register yourself for the service 3. Unregister yourself from the service  Type RNE (space)(your option)to send your option.','2013-09-07 16:42:44','N','Message contains spam '),(25,'8093672342','2013-09-06 00:10:00','get menu','Main Menu : 1. Get your Medicine Reminder 2. Register yourself for the service 3. Unregister yourself from the service  Type XKZ (space)(your option)to send your option.','2013-09-07 16:43:12','N','Message contains spam '),(26,'8093672342','2013-09-06 00:10:00','get menu','Main Menu : 1. Get your Medicine Reminder 2. Register yourself for the service 3. Unregister yourself from the service  Type YXM (space)(your option)to send your option.','2013-09-07 16:43:15','N','Message contains spam '),(27,'8093672342','2013-09-06 00:10:00','get menu','Main Menu : 1. Get your Medicine Reminder 2. Register yourself for the service 3. Unregister yourself from the service  Type VQC (space)(your option)to send your option.','2013-09-07 16:44:19','N','Message contains spam '),(28,'8093672342','2013-09-07 16:44:36','VQC 1','Type VQC your option  1. Get Which Medicine To take Now.Short:GET REM NOW 2. Get Which Medicine To take today.Short:GET REM TODAY 3. Get Which Medicine to take afterwards.Short:GET REM NOWON  Type VQC (space)(your option)to send your option.','2013-09-07 16:44:36','N','Message contains spam '),(29,'8093672342','2013-09-07 16:44:39','VQC 2','Type VQC (your aoption) 1 for Ishan Mehta 2 for Piyush Madan2 3 for Akshay Sohal ','2013-09-07 16:44:40','Y','ins37_13785524807513'),(30,'8093672342','2013-09-07 16:44:43','VQC 3','Hello Akshay Sohal Sorry The system has no answer to your query.Start Again or try later','2013-09-07 16:44:43','Y','ins37_13785524837517'),(31,'136113113131','2013-09-06 00:10:00','get menu','Main Menu : 1. Get your Medicine Reminder 2. Register yourself for the service 3. Unregister yourself from the service  Type BAY (space)(your option)to send your option.','2013-09-07 16:45:03','N','Message contains spam '),(32,'136113113131','2013-09-07 16:45:10','BAY 2','Choose Your preferLangauge 1 .English2 .Hindi Type BAY (space)(your option)to send your option. Type BAY (space)(your option)to send your option.','2013-09-07 16:45:10','N','Message contains spam '),(33,'136113113131','2013-09-07 16:45:16','BAY 2','Sorry your Phone number is not recognised by the system.You may want to register yourself first','2013-09-07 16:45:23','N','Message contains spam '),(34,'136113113131','2013-09-10 22:26:52','BAY 2','Sorry your session has expired or not created yet.Use GET MENU to fetch Menu','2013-09-10 22:26:52','N',' API Time Expired'),(35,'8093672342','2013-09-06 00:10:00','get reg','Choose Your preferLangauge 1 .English 2 .Hindi  Type ZXI (space)(your option)to send your option.','2013-09-11 01:22:40','N',' API Time Expired'),(36,'8093672342','2013-09-06 00:10:00','get reg','Choose Your preferLangauge 1 .English 2 .Hindi  Type JZY (space)(your option)to send your option.','2013-09-11 01:24:29','N',' API Time Expired');
/*!40000 ALTER TABLE `smsRecord` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2013-09-11 16:47:32
