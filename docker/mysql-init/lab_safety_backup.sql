-- MySQL dump 10.13  Distrib 8.0.44, for Linux (aarch64)
--
-- Host: localhost    Database: lab_safety
-- ------------------------------------------------------
-- Server version	8.0.44

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `lab_safety`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `lab_safety` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `lab_safety`;

--
-- Table structure for table `equipment`
--

DROP TABLE IF EXISTS `equipment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `equipment` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `lab_id` bigint NOT NULL,
  `category` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `model` varchar(150) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `specification` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `quantity` int NOT NULL DEFAULT '1',
  `purchase_date` date DEFAULT NULL,
  `storage_location` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'AVAILABLE',
  `description` text COLLATE utf8mb4_unicode_ci,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_equipment_lab_name_model` (`lab_id`,`name`,`model`),
  KEY `idx_equipment_lab_id` (`lab_id`),
  KEY `idx_equipment_category` (`category`),
  KEY `idx_equipment_status` (`status`),
  CONSTRAINT `fk_equipment_lab` FOREIGN KEY (`lab_id`) REFERENCES `labs` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `chk_equipment_status` CHECK ((`status` in (_utf8mb4'AVAILABLE',_utf8mb4'MAINTENANCE',_utf8mb4'BROKEN',_utf8mb4'RETIRED')))
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `equipment`
--

LOCK TABLES `equipment` WRITE;
/*!40000 ALTER TABLE `equipment` DISABLE KEYS */;
INSERT INTO `equipment` VALUES (1,4,'显微镜','光学显微镜','T100','通用',5,'2023-01-01','储存柜 A','AVAILABLE','用途： 观察细胞、组织切片等微小结构\n特点： 使用可见光成像，操作简单，适合教学与基础实验','2026-02-14 11:23:05','2026-02-19 10:41:58'),(3,4,'显微镜','电子显微镜','T200','通用',5,'2023-01-01','储存柜 A','AVAILABLE','用途： 观察纳米级结构，如病毒、材料微观结构\n特点： 分辨率极高，设备昂贵，需专业操作人员','2026-02-14 14:56:55','2026-02-19 10:42:04'),(6,4,'玻璃器皿','250ml烧杯','250ml','通用',10,'2026-02-05','储存柜B','AVAILABLE','用途： 溶液配制、加热、混合液体\n特点： 耐高温玻璃材质，带刻度线，可重复使用','2026-02-19 10:41:41','2026-02-19 10:42:10'),(7,4,'安全器材','二氧化碳灭火器','通用型','CQT',2,'2023-02-08','储存柜C','AVAILABLE','用途： 扑灭电器及化学火灾\n特点： 不导电，无残留物，适用于实验室环境','2026-02-19 10:43:40','2026-02-19 10:43:40'),(8,4,'化学试剂','氢化钠','通用','5ml',4,'2026-02-05','保险柜A','AVAILABLE','用途： 配制溶液、基础化学实验\n特点： 白色晶体，常温稳定，需密封保存','2026-02-19 10:45:02','2026-02-19 11:01:44');
/*!40000 ALTER TABLE `equipment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `experiment_project_reservations`
--

DROP TABLE IF EXISTS `experiment_project_reservations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `experiment_project_reservations` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `reservation_id` bigint NOT NULL,
  `experiment_project_id` bigint NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_epr_reservation` (`reservation_id`),
  KEY `idx_epr_experiment_project_id` (`experiment_project_id`),
  CONSTRAINT `fk_epr_experiment_project` FOREIGN KEY (`experiment_project_id`) REFERENCES `experiment_projects` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_epr_reservation` FOREIGN KEY (`reservation_id`) REFERENCES `reservations` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `experiment_project_reservations`
--

LOCK TABLES `experiment_project_reservations` WRITE;
/*!40000 ALTER TABLE `experiment_project_reservations` DISABLE KEYS */;
INSERT INTO `experiment_project_reservations` VALUES (1,4,1,'2026-02-14 16:49:56'),(2,13,1,'2026-02-19 10:09:20'),(3,14,2,'2026-02-19 12:12:57');
/*!40000 ALTER TABLE `experiment_project_reservations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `experiment_projects`
--

DROP TABLE IF EXISTS `experiment_projects`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `experiment_projects` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `lab_id` bigint NOT NULL,
  `name` varchar(150) NOT NULL,
  `objective` text NOT NULL,
  `content` text NOT NULL,
  `requirements` text NOT NULL,
  `start_time` datetime NOT NULL,
  `end_time` datetime NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_experiment_projects_lab_id` (`lab_id`),
  KEY `idx_experiment_projects_start_time` (`start_time`),
  KEY `idx_experiment_projects_name` (`name`),
  CONSTRAINT `fk_experiment_projects_lab` FOREIGN KEY (`lab_id`) REFERENCES `labs` (`id`),
  CONSTRAINT `ck_experiment_projects_time` CHECK ((`end_time` > `start_time`))
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `experiment_projects`
--

LOCK TABLES `experiment_projects` WRITE;
/*!40000 ALTER TABLE `experiment_projects` DISABLE KEYS */;
INSERT INTO `experiment_projects` VALUES (1,4,'电路基础与信号测试实验','掌握基本电路搭建方法，理解电压、电流测量原理。','搭建简单串并联电路，使用万用表测量电压、电流并记录数据。','正确连接电路，数据记录完整，实验结束后整理设备。','2026-02-21 01:00:00','2026-02-21 04:00:00','2026-02-14 16:43:46'),(2,4,'化学溶液浓度测定实验','学习标准溶液配制方法，掌握滴定操作流程。','配制一定浓度溶液，进行酸碱滴定并计算浓度。','操作规范，佩戴防护用品，数据计算准确。','2025-01-10 02:00:00','2025-01-10 04:00:00','2026-02-15 18:28:54'),(3,4,'计算机网络组网实验','理解局域网结构及IP地址配置方法。','搭建小型局域网，配置IP地址并测试连通性。','独立完成配置，确保网络正常通信，提交实验截图。','2025-01-10 02:00:00','2025-01-10 04:00:00','2026-02-18 14:14:42');
/*!40000 ALTER TABLE `experiment_projects` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `flyway_schema_history`
--

DROP TABLE IF EXISTS `flyway_schema_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `flyway_schema_history` (
  `installed_rank` int NOT NULL,
  `version` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `description` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `script` varchar(1000) COLLATE utf8mb4_unicode_ci NOT NULL,
  `checksum` int DEFAULT NULL,
  `installed_by` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `installed_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `execution_time` int NOT NULL,
  `success` tinyint(1) NOT NULL,
  PRIMARY KEY (`installed_rank`),
  KEY `flyway_schema_history_s_idx` (`success`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `flyway_schema_history`
--

LOCK TABLES `flyway_schema_history` WRITE;
/*!40000 ALTER TABLE `flyway_schema_history` DISABLE KEYS */;
INSERT INTO `flyway_schema_history` VALUES (1,'1','create users and roles','SQL','V1__create_users_and_roles.sql',-906161780,'lab','2026-02-11 12:23:44',23,1),(2,'2','create labs equipment reservations','SQL','V2__create_labs_equipment_reservations.sql',741811642,'lab','2026-02-11 12:23:44',118,1),(3,'3','add manager email to labs','SQL','V3__add_manager_email_to_labs.sql',-1992571028,'lab','2026-02-12 14:50:52',98,1),(4,'5','add cancelled status','SQL','V5__add_cancelled_status.sql',-1207377141,'lab','2026-02-14 16:10:06',121,1),(5,'6','create experiment projects','SQL','V6__create_experiment_projects.sql',-1313457413,'lab','2026-02-14 16:40:47',124,1),(6,'7','create experiment project reservations','SQL','V7__create_experiment_project_reservations.sql',1881032205,'lab','2026-02-14 16:40:47',44,1),(7,'8','create safety training tables','SQL','V8__create_safety_training_tables.sql',-1404562049,'lab','2026-02-14 16:57:03',284,1),(8,'10','create inspection tables','SQL','V10__create_inspection_tables.sql',1252592182,'lab','2026-02-14 17:18:22',234,1),(9,'12','seed training questions','SQL','V12__seed_training_questions.sql',1069793063,'lab','2026-02-19 06:19:42',29,1);
/*!40000 ALTER TABLE `flyway_schema_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `inspection_plans`
--

DROP TABLE IF EXISTS `inspection_plans`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `inspection_plans` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `lab_id` bigint NOT NULL,
  `inspector_id` bigint NOT NULL,
  `inspection_time` datetime NOT NULL,
  `content` text NOT NULL,
  `status` varchar(20) NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_inspection_plans_lab` (`lab_id`),
  KEY `idx_inspection_plans_inspector` (`inspector_id`),
  KEY `idx_inspection_plans_time` (`inspection_time`),
  CONSTRAINT `fk_inspection_plans_inspector` FOREIGN KEY (`inspector_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_inspection_plans_lab` FOREIGN KEY (`lab_id`) REFERENCES `labs` (`id`) ON DELETE CASCADE,
  CONSTRAINT `ck_inspection_plans_status` CHECK ((`status` in (_utf8mb4'SCHEDULED',_utf8mb4'COMPLETED')))
) ENGINE=InnoDB AUTO_INCREMENT=46 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `inspection_plans`
--

LOCK TABLES `inspection_plans` WRITE;
/*!40000 ALTER TABLE `inspection_plans` DISABLE KEYS */;
INSERT INTO `inspection_plans` VALUES (43,4,41,'2026-03-10 16:00:00','检查消防器材是否在有效期内，压力表是否正常，摆放位置是否合理。','SCHEDULED','2026-02-20 09:19:57'),(44,4,41,'2026-02-18 16:00:00','检查疏散通道是否畅通，出口标识是否清晰，紧急照明是否可用。','COMPLETED','2026-02-20 09:20:21'),(45,4,41,'2026-02-02 16:00:00','检查实验室用电安全，是否存在私拉电线或插排超负荷使用情况。','COMPLETED','2026-02-20 09:20:39');
/*!40000 ALTER TABLE `inspection_plans` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `inspection_records`
--

DROP TABLE IF EXISTS `inspection_records`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `inspection_records` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `plan_id` bigint NOT NULL,
  `is_safe` tinyint(1) NOT NULL,
  `problem_description` text,
  `suggestion` text,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_inspection_records_plan` (`plan_id`),
  CONSTRAINT `fk_inspection_records_plan` FOREIGN KEY (`plan_id`) REFERENCES `inspection_plans` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `inspection_records`
--

LOCK TABLES `inspection_records` WRITE;
/*!40000 ALTER TABLE `inspection_records` DISABLE KEYS */;
INSERT INTO `inspection_records` VALUES (4,45,1,'一切正常','继续保持','2026-02-20 09:21:14'),(5,44,0,'安全出口不通畅','清理安全出口区域杂物','2026-02-20 09:22:57');
/*!40000 ALTER TABLE `inspection_records` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `labs`
--

DROP TABLE IF EXISTS `labs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `labs` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `type` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `location` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `capacity` int NOT NULL,
  `open_time` time NOT NULL,
  `close_time` time NOT NULL,
  `manager_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `manager_phone` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `details` text COLLATE utf8mb4_unicode_ci,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `manager_email` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  KEY `idx_labs_type` (`type`),
  KEY `idx_labs_location` (`location`),
  CONSTRAINT `chk_labs_time` CHECK ((`close_time` > `open_time`))
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `labs`
--

LOCK TABLES `labs` WRITE;
/*!40000 ALTER TABLE `labs` DISABLE KEYS */;
INSERT INTO `labs` VALUES (4,'物理实验室A','Physics','一号教学楼302',50,'09:00:00','17:00:00','王教授','13900000000','物理实验','2026-02-14 11:21:37','2026-02-19 09:49:09','wang@example.com'),(5,'化学实验室A','化学','2号教学楼301',40,'08:00:00','18:00:00','张教授','13800000000','通用化学实验室','2026-02-15 17:35:01','2026-02-19 11:08:45','zhang@example.com');
/*!40000 ALTER TABLE `labs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reservations`
--

DROP TABLE IF EXISTS `reservations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reservations` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `lab_id` bigint NOT NULL,
  `equipment_id` bigint NOT NULL,
  `student_id` bigint NOT NULL,
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDING',
  `start_time` datetime NOT NULL,
  `end_time` datetime NOT NULL,
  `purpose` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `approver_id` bigint DEFAULT NULL,
  `decided_at` datetime DEFAULT NULL,
  `decision_note` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_reservations_lab_status_time` (`lab_id`,`status`,`start_time`,`end_time`),
  KEY `idx_reservations_equipment_status_time` (`equipment_id`,`status`,`start_time`,`end_time`),
  KEY `idx_reservations_student_time` (`student_id`,`start_time`,`end_time`),
  KEY `idx_reservations_approver` (`approver_id`),
  CONSTRAINT `fk_reservations_approver` FOREIGN KEY (`approver_id`) REFERENCES `users` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_reservations_equipment` FOREIGN KEY (`equipment_id`) REFERENCES `equipment` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_reservations_lab` FOREIGN KEY (`lab_id`) REFERENCES `labs` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_reservations_student` FOREIGN KEY (`student_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `chk_reservations_status` CHECK ((`status` in (_utf8mb4'PENDING',_utf8mb4'APPROVED',_utf8mb4'REJECTED',_utf8mb4'CANCELLED'))),
  CONSTRAINT `chk_reservations_time` CHECK ((`end_time` > `start_time`))
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reservations`
--

LOCK TABLES `reservations` WRITE;
/*!40000 ALTER TABLE `reservations` DISABLE KEYS */;
INSERT INTO `reservations` VALUES (1,4,1,42,'APPROVED','2026-02-15 02:00:00','2026-02-15 04:00:00','Microscope practice',NULL,41,'2026-02-14 15:24:30','Approved','2026-02-14 15:11:01','2026-02-14 15:24:29'),(2,4,1,42,'CANCELLED','2026-02-20 02:00:00','2026-02-20 04:00:00','Test cancel flow',NULL,42,'2026-02-14 16:10:24','Cannot attend','2026-02-14 16:04:44','2026-02-14 16:10:23'),(3,4,1,42,'REJECTED','2026-02-20 02:00:00','2026-02-20 04:00:00','Same slot after cancel',NULL,41,'2026-02-17 16:33:44','','2026-02-14 16:12:48','2026-02-17 16:33:43'),(4,4,1,42,'REJECTED','2025-01-10 02:00:00','2025-01-10 03:00:00','Course experiment','test',41,'2026-02-17 16:33:48','','2026-02-14 16:49:56','2026-02-17 16:33:47'),(5,4,1,42,'CANCELLED','2026-02-23 02:00:00','2026-02-24 04:00:00','Microscope experiment','Testing reservation flow',42,'2026-02-15 16:37:15','Cancel for personal reason','2026-02-15 16:33:26','2026-02-15 16:37:14'),(6,4,1,42,'REJECTED','2026-02-23 02:00:00','2026-02-24 04:00:00','Microscope experiment','Testing reservation flow',41,'2026-02-15 16:40:44','Equipment maintenance conflict','2026-02-15 16:40:34','2026-02-15 16:40:44'),(7,4,1,42,'CANCELLED','2026-02-23 02:00:00','2026-02-24 04:00:00','Microscope experiment','Testing reservation flow',46,'2026-02-15 16:42:58','Administrative cancellation','2026-02-15 16:42:30','2026-02-15 16:42:57'),(8,4,1,42,'APPROVED','2026-02-23 02:00:00','2026-02-24 04:00:00','Microscope experiment','Testing reservation flow',41,'2026-02-17 16:33:49','','2026-02-15 16:43:40','2026-02-17 16:33:49'),(9,4,1,42,'REJECTED','2026-02-26 02:00:00','2026-02-26 04:00:00','Microscope experiment','Testing reservation flow',41,'2026-02-17 16:33:50','','2026-02-17 15:50:30','2026-02-17 16:33:50'),(10,4,1,42,'REJECTED','2026-02-27 02:00:00','2026-02-27 04:00:00','Microscope experiment','Testing reservation flow',41,'2026-02-17 16:33:51','','2026-02-17 16:15:58','2026-02-17 16:33:51'),(11,4,1,42,'REJECTED','2026-02-28 02:00:00','2026-02-28 04:00:00','Microscope experiment','Testing reservation flow',41,'2026-02-17 16:33:52','','2026-02-17 16:25:07','2026-02-17 16:33:52'),(12,4,1,42,'REJECTED','2026-03-29 02:00:00','2026-03-29 04:00:00','Microscope experiment','Testing reservation flow',41,'2026-02-19 12:13:09','','2026-02-18 14:23:34','2026-02-19 12:13:08'),(13,4,1,42,'REJECTED','2026-02-21 01:00:00','2026-02-21 04:00:00','Course experiment','',41,'2026-02-19 12:13:11','','2026-02-19 10:09:20','2026-02-19 12:13:10'),(14,4,6,42,'APPROVED','2025-01-10 02:00:00','2025-01-10 04:00:00','Course experiment','',41,'2026-02-19 12:13:10','','2026-02-19 12:12:57','2026-02-19 12:13:09');
/*!40000 ALTER TABLE `reservations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES (1,'ADMIN'),(4,'INSPECTOR'),(3,'STUDENT'),(2,'TEACHER');
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `training_attempt_answers`
--

DROP TABLE IF EXISTS `training_attempt_answers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `training_attempt_answers` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `attempt_id` bigint NOT NULL,
  `question_id` bigint NOT NULL,
  `selected_option` char(1) NOT NULL,
  `is_correct` tinyint(1) NOT NULL DEFAULT '0',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_training_attempt_answers_attempt_question` (`attempt_id`,`question_id`),
  KEY `idx_training_attempt_answers_attempt` (`attempt_id`),
  KEY `idx_training_attempt_answers_question` (`question_id`),
  CONSTRAINT `fk_training_attempt_answers_attempt` FOREIGN KEY (`attempt_id`) REFERENCES `training_attempts` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_training_attempt_answers_question` FOREIGN KEY (`question_id`) REFERENCES `training_questions` (`id`) ON DELETE CASCADE,
  CONSTRAINT `ck_training_attempt_answers_selected_option` CHECK ((`selected_option` in (_utf8mb4'A',_utf8mb4'B',_utf8mb4'C',_utf8mb4'D')))
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `training_attempt_answers`
--

LOCK TABLES `training_attempt_answers` WRITE;
/*!40000 ALTER TABLE `training_attempt_answers` DISABLE KEYS */;
INSERT INTO `training_attempt_answers` VALUES (1,12,1,'A',1,'2026-02-19 09:13:33'),(2,12,2,'C',1,'2026-02-19 09:13:33'),(3,12,3,'B',1,'2026-02-19 09:13:33'),(4,12,4,'B',1,'2026-02-19 09:13:33'),(5,12,5,'C',1,'2026-02-19 09:13:33'),(6,13,21,'B',1,'2026-02-19 09:31:31'),(7,13,22,'B',1,'2026-02-19 09:31:31'),(8,13,23,'B',1,'2026-02-19 09:31:31'),(9,13,24,'B',1,'2026-02-19 09:31:31'),(10,13,25,'B',1,'2026-02-19 09:31:31'),(11,14,16,'B',1,'2026-02-19 09:59:28'),(12,14,17,'B',1,'2026-02-19 09:59:28'),(13,14,18,'B',1,'2026-02-19 09:59:28'),(14,14,19,'B',1,'2026-02-19 09:59:28'),(15,14,20,'B',1,'2026-02-19 09:59:28'),(16,16,21,'B',1,'2026-02-20 09:29:05'),(17,16,22,'B',1,'2026-02-20 09:29:05'),(18,16,23,'B',1,'2026-02-20 09:29:05'),(19,16,24,'B',1,'2026-02-20 09:29:05'),(20,16,25,'B',1,'2026-02-20 09:29:05');
/*!40000 ALTER TABLE `training_attempt_answers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `training_attempts`
--

DROP TABLE IF EXISTS `training_attempts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `training_attempts` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `course_id` bigint NOT NULL,
  `student_id` bigint NOT NULL,
  `started_at` datetime NOT NULL,
  `submitted_at` datetime DEFAULT NULL,
  `score` int DEFAULT NULL,
  `passed` tinyint(1) DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_training_attempts_course` (`course_id`),
  KEY `idx_training_attempts_student` (`student_id`),
  KEY `idx_training_attempts_course_student` (`course_id`,`student_id`),
  CONSTRAINT `fk_training_attempts_course` FOREIGN KEY (`course_id`) REFERENCES `training_courses` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_training_attempts_student` FOREIGN KEY (`student_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `ck_training_attempts_score` CHECK (((`score` is null) or ((`score` >= 0) and (`score` <= 100))))
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `training_attempts`
--

LOCK TABLES `training_attempts` WRITE;
/*!40000 ALTER TABLE `training_attempts` DISABLE KEYS */;
INSERT INTO `training_attempts` VALUES (1,1,42,'2026-02-15 18:48:51','2026-02-15 18:50:51',0,0,'2026-02-15 18:48:51'),(2,1,42,'2026-02-18 17:01:18','2026-02-18 17:03:25',0,0,'2026-02-18 17:01:18'),(3,7,42,'2026-02-18 18:00:06','2026-02-19 07:16:30',0,0,'2026-02-18 18:00:06'),(4,7,42,'2026-02-18 18:00:23','2026-02-19 08:35:10',0,0,'2026-02-18 18:00:23'),(5,7,42,'2026-02-19 06:33:08',NULL,NULL,NULL,'2026-02-19 06:33:08'),(6,7,42,'2026-02-19 06:33:20','2026-02-19 07:15:25',0,0,'2026-02-19 06:33:20'),(7,6,42,'2026-02-19 06:33:25',NULL,NULL,NULL,'2026-02-19 06:33:25'),(8,7,42,'2026-02-19 06:39:18','2026-02-19 06:39:29',0,0,'2026-02-19 06:39:18'),(9,7,42,'2026-02-19 06:39:37','2026-02-19 06:40:02',0,0,'2026-02-19 06:39:37'),(10,7,42,'2026-02-19 06:50:35','2026-02-19 06:50:44',0,0,'2026-02-19 06:50:35'),(11,7,42,'2026-02-19 06:50:56','2026-02-19 06:51:06',0,0,'2026-02-19 06:50:56'),(12,3,42,'2026-02-19 09:13:12','2026-02-19 09:13:33',100,1,'2026-02-19 09:13:12'),(13,7,42,'2026-02-19 09:31:23','2026-02-19 09:31:31',100,1,'2026-02-19 09:31:23'),(14,6,42,'2026-02-19 09:59:01','2026-02-19 09:59:28',100,1,'2026-02-19 09:59:01'),(15,7,42,'2026-02-19 10:09:32',NULL,NULL,NULL,'2026-02-19 10:09:32'),(16,7,42,'2026-02-20 09:28:39','2026-02-20 09:29:05',100,1,'2026-02-20 09:28:39');
/*!40000 ALTER TABLE `training_attempts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `training_courses`
--

DROP TABLE IF EXISTS `training_courses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `training_courses` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(150) NOT NULL,
  `content` text NOT NULL,
  `start_time` datetime NOT NULL,
  `end_time` datetime NOT NULL,
  `mode` varchar(20) NOT NULL,
  `passing_score` int NOT NULL DEFAULT '60',
  `duration_minutes` int NOT NULL DEFAULT '30',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_training_courses_start_time` (`start_time`),
  KEY `idx_training_courses_title` (`title`),
  CONSTRAINT `ck_training_courses_duration` CHECK ((`duration_minutes` > 0)),
  CONSTRAINT `ck_training_courses_passing_score` CHECK (((`passing_score` >= 0) and (`passing_score` <= 100))),
  CONSTRAINT `ck_training_courses_time` CHECK ((`end_time` > `start_time`))
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `training_courses`
--

LOCK TABLES `training_courses` WRITE;
/*!40000 ALTER TABLE `training_courses` DISABLE KEYS */;
INSERT INTO `training_courses` VALUES (1,'Lab Safety 101','Basic laboratory safety training.','2026-02-20 01:00:00','2026-02-28 15:59:59','ONLINE',60,30,'2026-02-15 18:44:00'),(2,'Lab Safety 101','Basic laboratory safety training.','2026-02-20 01:00:00','2026-02-28 15:59:59','ONLINE',60,30,'2026-02-18 16:59:20'),(3,'实验室安全基础培训','本课程介绍实验室基本安全规范，包括个人防护装备的正确使用、危险品存储规范、紧急事故处理流程等内容。','2026-03-01 01:00:00','2026-03-15 15:59:59','ONLINE',60,30,'2026-02-18 17:43:16'),(4,'化学实验安全专项培训','本课程重点讲解化学品分类、MSDS使用方法、易燃易爆物品管理规范以及化学废弃物处理流程。','2026-03-05 01:00:00','2026-03-25 15:59:59','ONLINE',70,45,'2026-02-18 17:43:30'),(5,'实验室电气安全培训','讲解实验室电气线路检查规范、电器设备接地要求、常见电气安全隐患识别方法以及应急处理措施。','2026-03-10 01:00:00','2026-03-30 15:59:59','ONLINE',65,40,'2026-02-18 17:43:40'),(6,'实验室消防安全培训','介绍灭火器的分类与使用方法、消防通道管理规范、火灾报警流程以及紧急疏散演练要求。','2026-03-12 01:00:00','2026-04-01 15:59:59','ONLINE',60,35,'2026-02-18 17:43:49'),(7,'实验室管理进阶培训','本课程面向实验室管理员与教师，讲解安全责任划分、检查记录规范填写、风险评估方法及整改流程。','2026-03-15 01:00:00','2026-04-10 15:59:59','OFFLINE',75,60,'2026-02-18 17:44:06');
/*!40000 ALTER TABLE `training_courses` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `training_enrollments`
--

DROP TABLE IF EXISTS `training_enrollments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `training_enrollments` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `course_id` bigint NOT NULL,
  `student_id` bigint NOT NULL,
  `status` varchar(20) NOT NULL,
  `completed_at` datetime DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_training_enrollments_course_student` (`course_id`,`student_id`),
  KEY `idx_training_enrollments_student` (`student_id`),
  KEY `idx_training_enrollments_course` (`course_id`),
  CONSTRAINT `fk_training_enrollments_course` FOREIGN KEY (`course_id`) REFERENCES `training_courses` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_training_enrollments_student` FOREIGN KEY (`student_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `training_enrollments`
--

LOCK TABLES `training_enrollments` WRITE;
/*!40000 ALTER TABLE `training_enrollments` DISABLE KEYS */;
INSERT INTO `training_enrollments` VALUES (1,1,42,'ENROLLED',NULL,'2026-02-15 18:47:40'),(2,7,42,'ENROLLED',NULL,'2026-02-19 09:31:21');
/*!40000 ALTER TABLE `training_enrollments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `training_questions`
--

DROP TABLE IF EXISTS `training_questions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `training_questions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `course_id` bigint NOT NULL,
  `question_text` text NOT NULL,
  `option_a` varchar(500) NOT NULL,
  `option_b` varchar(500) NOT NULL,
  `option_c` varchar(500) NOT NULL,
  `option_d` varchar(500) NOT NULL,
  `correct_option` char(1) NOT NULL,
  `explanation` text,
  `sort_order` int NOT NULL DEFAULT '0',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_training_questions_course_id` (`course_id`),
  KEY `idx_training_questions_sort` (`course_id`,`sort_order`),
  CONSTRAINT `fk_training_questions_course` FOREIGN KEY (`course_id`) REFERENCES `training_courses` (`id`) ON DELETE CASCADE,
  CONSTRAINT `ck_training_questions_correct_option` CHECK ((`correct_option` in (_utf8mb4'A',_utf8mb4'B',_utf8mb4'C',_utf8mb4'D')))
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `training_questions`
--

LOCK TABLES `training_questions` WRITE;
/*!40000 ALTER TABLE `training_questions` DISABLE KEYS */;
INSERT INTO `training_questions` VALUES (1,3,'进入实验室前，最基本需要做到的是？','穿戴规定的个人防护用品（如实验服、护目镜）','先把实验器材搬到工位','先打开所有电源设备','先把实验室门窗全部锁上','A','基础培训强调：进入实验室先满足PPE与基本规范。',1,'2026-02-19 06:19:42'),(2,3,'发现实验台面有不明液体泄漏，正确做法是？','立即用手擦拭干净','先拍照发朋友圈提醒同学','先报告老师/管理员并按流程处理','不管它，等自然挥发','C','不明泄漏可能是腐蚀/易燃/有毒，必须按流程处置。',2,'2026-02-19 06:19:42'),(3,3,'实验过程中最重要的原则之一是？','追求速度，尽快完成','严格按实验规程操作','尽量减少记录','遇到问题自己硬扛','B','规范操作是安全的底线。',3,'2026-02-19 06:19:42'),(4,3,'实验结束后，最正确的收尾步骤是？','直接离开，让下一组收拾','关闭设备、清理台面、分类处置废弃物','只关灯，其他不管','把废液倒入下水道省事','B','关停+清理+废弃物分类是标准收尾。',4,'2026-02-19 06:19:42'),(5,3,'关于实验室饮食行为，正确的是？','可以在实验台边喝饮料','只要不影响实验就能吃零食','实验室内禁止饮食','戴着手套吃东西更安全','C','避免交叉污染与误食风险。',5,'2026-02-19 06:19:42'),(6,4,'配制稀硫酸时正确的操作顺序是？','把水倒进浓硫酸','把浓硫酸慢慢倒入水中并搅拌','两者同时快速混合','随便都可以','B','“酸入水”是经典原则，避免飞溅与剧烈放热。',1,'2026-02-19 06:19:42'),(7,4,'化学品标签中最关键的信息之一是？','颜色好不好看','危险性/防护/应急信息','生产厂家logo','瓶子大小','B','标签与SDS用于识别风险与应急处理。',2,'2026-02-19 06:19:42'),(8,4,'有机溶剂大量泄漏的第一反应应优先考虑？','立刻点火把它烧掉','通风、远离火源并按应急流程处置','用水大量冲洗到地漏','用纸巾随便擦掉','B','溶剂通常易燃，需远离火源并通风、隔离。',3,'2026-02-19 06:19:42'),(9,4,'化学废液正确处理方式是？','倒入洗手池','倒入马桶冲走','按类别收集到指定废液桶','混在一起省空间','C','必须分类收集，防止反应/污染。',4,'2026-02-19 06:19:42'),(10,4,'闻到刺激性气味时，正确做法是？','把鼻子凑近确认是什么味道','继续实验，习惯就好','立即停止相关操作并检查通风与泄漏来源','用香水盖住味道','C','刺激性气味可能是挥发性有害物，应立即排查。',5,'2026-02-19 06:19:42'),(11,5,'发现电源线外皮破损，正确处理是？','用胶带随便缠几圈继续用','立即停止使用并更换/报修','把破损处放到桌下看不见就行','用水冲一下看看是否漏电','B','破损电源线有触电/短路风险，必须停用更换。',1,'2026-02-19 06:19:42'),(12,5,'多插排串联使用的主要风险是？','更省电','电流过载导致发热甚至起火','网络更快','电压自动升高','B','串联易过载发热，是常见电气火灾原因。',2,'2026-02-19 06:19:42'),(13,5,'带电设备附近发生液体洒落，优先做法是？','立即用湿抹布擦','先切断电源再处理液体','先把插头拔掉但不关总电源','继续运行设备避免数据丢失','B','先断电避免触电与短路。',3,'2026-02-19 06:19:42'),(14,5,'关于接地（地线），正确的是？','地线可有可无','地线能降低漏电触电风险','地线主要是装饰','地线接不接都一样','B','接地是基本保护措施。',4,'2026-02-19 06:19:42'),(15,5,'电气设备冒烟时，正确做法是？','立刻泼水降温','断电并使用合适的灭火器材/报警','用手摸摸看温度','打开设备外壳继续维修','B','先断电，电气火灾不能直接泼水。',5,'2026-02-19 06:19:42'),(16,6,'发生火情时，第一步通常应做的是？','先把实验做完','立即报警并启动应急预案','先拍视频记录','先去找灭火器但不通知任何人','B','优先报警与组织应急处置。',1,'2026-02-19 06:19:42'),(17,6,'火灾逃生时，正确的是？','乘坐电梯快速下楼','沿安全通道疏散，低姿态避烟','躲在实验台下等待','返回宿舍拿贵重物品','B','电梯风险高，烟气上升快，低姿态更安全。',2,'2026-02-19 06:19:42'),(18,6,'灭火器使用的基本要领常用口诀是？','推拉转按','提、拔、握、压（或提拔握压/对准根部）','看闻摸尝','转圈跳跃','B','核心是对准火焰根部并按压喷射。',3,'2026-02-19 06:19:42'),(19,6,'关于消防通道，正确的是？','可以临时堆放杂物','保持畅通，严禁占用','人少时可以堵一会儿','只要不锁门就行','B','通道被占用会延误疏散与救援。',4,'2026-02-19 06:19:42'),(20,6,'实验室内易燃物管理正确的是？','随意放在电器旁边方便取用','分类存放，远离火源热源并贴标识','统一倒在一个大桶里','放在窗台晒太阳','B','易燃物要远离火源并规范存储。',5,'2026-02-19 06:19:42'),(21,7,'实验室预约制度的核心目标是？','让管理员更忙','提高资源利用率并减少冲突','限制学生使用','让设备闲置更久','B','预约管理的核心是资源可控、可追溯、少冲突。',1,'2026-02-19 06:19:42'),(22,7,'发现设备长期处于“不可用”状态，合理做法是？','忽略不管','登记报修/维护并更新设备状态','把状态改成可用以方便预约','直接丢弃不记录','B','设备状态要真实可追溯，维护流程要闭环。',2,'2026-02-19 06:19:42'),(23,7,'关于实验室安全培训记录，合理做法是？','不需要记录','记录应可追溯：谁、何时、是否通过','只要口头说通过就行','记录越少越安全','B','培训记录是审核与责任追踪的重要依据。',3,'2026-02-19 06:19:42'),(24,7,'实验室管理中最重要的数据质量原则是？','数据越多越好','数据及时更新且真实准确','只要写得漂亮','允许随意修改历史记录','B','真实与及时是管理系统的生命线。',4,'2026-02-19 06:19:42'),(25,7,'当预约冲突频繁出现，优先应该检查？','把审批全部取消','冲突检测规则与预约时间填写是否规范','让学生自己协商','把系统关掉','B','应先从规则与输入规范排查，保证系统一致性。',5,'2026-02-19 06:19:42');
/*!40000 ALTER TABLE `training_questions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `email` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `role` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`),
  CONSTRAINT `chk_users_role` CHECK ((`role` in (_utf8mb4'ADMIN',_utf8mb4'TEACHER',_utf8mb4'STUDENT')))
) ENGINE=InnoDB AUTO_INCREMENT=50 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (41,'teacher1','teacher1@example.com','$2a$10$kIaVZGndyt3m47Sk7M6A/e7T8EcYnzfZOyHpl3x.qu8867Xot/vkS','TEACHER','2026-02-14 11:06:59'),(42,'student1','student1@example.com','$2a$10$kIaVZGndyt3m47Sk7M6A/e7T8EcYnzfZOyHpl3x.qu8867Xot/vkS','STUDENT','2026-02-14 11:06:59'),(43,'teacher2','teacher2@example.com','$2a$10$KNQbQe8LptIeO.vQJoWVH.Tas5VeI1MLdvAh5pBKAJBgv6gv7wHf.','TEACHER','2026-02-15 05:57:46'),(45,'teacher4','teacher4@example.com','$2a$10$GFjpb75S9G0ekSf.SVa98OjQYi1uVuVbsLl06BhTHDwLI7n.AtpKa','STUDENT','2026-02-15 07:33:56'),(46,'admin2','admin@example.com','$2a$10$JUMHipDnc5Q1aVlSNqySS.AN7t2otyafOao6LKAjsIB0gyhQVcAiq','ADMIN','2026-02-15 11:32:12'),(48,'学生2','asfaef@qq.com','$2a$10$jwM0D.aikVPtyah3Irx9GuPjVy4KeQpHhTXF3u8gv/8q5pFlD/a7O','STUDENT','2026-02-19 11:11:46');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-02-20 11:00:14
