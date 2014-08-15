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
-- Table structure for table `followupresponse`
--

DROP TABLE IF EXISTS `followupresponse`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `followupresponse` (
  `frid` int(11) NOT NULL AUTO_INCREMENT,
  `fid` int(11) NOT NULL,
  `response` int(11) NOT NULL,
  `isExecuted` char(1) NOT NULL,
  `lastTry` datetime NOT NULL,
  `retryCount` int(11) NOT NULL,
  `syncStatus` char(1) NOT NULL,
  `date` datetime DEFAULT NULL,
  PRIMARY KEY (`frid`)
) ENGINE=InnoDB AUTO_INCREMENT=42 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `followupresponse`
--

LOCK TABLES `followupresponse` WRITE;
/*!40000 ALTER TABLE `followupresponse` DISABLE KEYS */;
INSERT INTO `followupresponse` VALUES (33,1,0,'Y','2014-08-04 03:29:46',0,'N',NULL),(34,8,0,'Y','2014-08-04 03:29:46',0,'N',NULL),(35,5,0,'Y','2014-08-04 03:29:46',0,'N',NULL),(36,6,0,'Y','2014-08-04 03:29:46',0,'N',NULL),(37,7,0,'Y','2014-08-04 03:29:46',0,'N',NULL),(38,2,0,'Y','2014-08-04 03:29:46',0,'N',NULL),(40,12,0,'Y','2014-08-07 17:52:11',0,'N',NULL),(41,1,1,'Y','2014-08-08 21:36:32',0,'N','2014-08-08 21:36:32');
/*!40000 ALTER TABLE `followupresponse` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2014-08-09 23:09:30
