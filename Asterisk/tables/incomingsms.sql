-- MySQL dump 10.13  Distrib 5.5.37, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: atul
-- ------------------------------------------------------
-- Server version	5.5.37-0ubuntu0.12.04.1

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
-- Table structure for table `incomingsms`
--

DROP TABLE IF EXISTS `incomingsms`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `incomingsms` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `sys_number` varchar(15) DEFAULT NULL,
  `user_number` varchar(15) DEFAULT NULL,
  `dtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `msgtxt` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `incomingsms`
--

LOCK TABLES `incomingsms` WRITE;
/*!40000 ALTER TABLE `incomingsms` DISABLE KEYS */;
INSERT INTO `incomingsms` VALUES (1,'111','22','2014-08-07 14:23:38','hai'),(2,'11','11','2014-08-07 16:43:48','hai'),(3,'11','11','2014-08-07 19:40:08','FWP 10 12'),(4,'11','11','2014-08-07 19:41:20','FWP 1 12'),(5,'11','sip/1000abc','2014-08-07 19:42:57','FWP 1 12'),(6,'11','sip/1000abc','2014-08-07 19:45:53','FWP 1 12'),(7,'11','sip/1000abc','2014-08-07 19:52:32','FWP 1 12'),(8,'11','sip/1000abc','2014-08-07 20:00:11','FWP 1 12'),(9,'16','sip/1000abc','2014-08-08 15:04:40','FWP 1 12'),(10,'16','sip/1000abc','2014-08-08 15:25:13','FWP 1 15'),(11,'88','sip/1000abc','2014-08-08 15:46:56','FWP 1 15'),(12,'88','sip/1000abc','2014-08-08 15:48:50','FWP 1 15'),(13,'88','sip/1000abc','2014-08-08 15:50:05','FWP 1 1'),(14,'88','sip/1000abc','2014-08-08 15:55:38','FWP 5 5'),(15,'88','sip/1000abc','2014-08-08 16:06:32','FWP 1 1'),(16,'33','222','2014-08-09 14:51:42','test sms'),(17,'33','222','2014-08-09 14:52:06','test sms'),(18,'33','222','2014-08-09 14:53:12','test sms');
/*!40000 ALTER TABLE `incomingsms` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2014-08-10 10:20:19
