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
-- Table structure for table `followupqstn`
--

DROP TABLE IF EXISTS `followupqstn`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `followupqstn` (
  `fid` int(11) NOT NULL,
  `pid` varchar(255) NOT NULL,
  `qstn` varchar(1000) NOT NULL,
  `fromDate` datetime NOT NULL,
  `toDate` datetime NOT NULL,
  `scheduleTime` time NOT NULL,
  `followupType` int(11) DEFAULT '1',
  PRIMARY KEY (`fid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `followupqstn`
--

LOCK TABLES `followupqstn` WRITE;
/*!40000 ALTER TABLE `followupqstn` DISABLE KEYS */;
INSERT INTO `followupqstn` VALUES (1,'f529a343-27fa-d5d1-8657-11aaa2c2a153','Did you take your morning medication?','2014-06-01 00:00:00','2014-06-10 00:00:00','18:32:35',1),(2,'1000abc','What is your exercise level?','2014-06-21 21:11:09','2014-06-21 21:11:09','21:11:09',1),(5,'1000abc','What is your exercise level?','2014-06-21 21:11:09','2014-06-21 21:11:09','21:11:09',1),(6,'1000abc','What is your exercise level?','2014-08-03 02:22:25','2014-08-03 02:22:25','14:22:25',1),(7,'1000abc','What is your exercise level?','2014-06-21 21:11:09','2014-06-21 21:11:09','21:11:09',1),(8,'1000abc','What is your exercise level?','2014-06-21 21:11:09','2014-06-21 21:11:09','21:11:09',1),(12,'1000abc','Did you take your morning medication?','2014-08-03 02:24:09','2014-08-03 02:24:09','14:24:09',2);
/*!40000 ALTER TABLE `followupqstn` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2014-08-09 23:08:17
