-- MySQL dump 10.13  Distrib 8.0.44, for Linux (x86_64)
--
-- Host: localhost    Database: dormitory_db
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
-- Table structure for table `dorm_access_record`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dorm_access_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `user_name` varchar(50) DEFAULT NULL COMMENT '用户姓名',
  `building_id` bigint NOT NULL COMMENT '楼栋ID',
  `building_name` varchar(50) DEFAULT NULL COMMENT '楼栋名称',
  `access_type` varchar(20) NOT NULL COMMENT '进出类型：ENTER-进入, EXIT-外出',
  `access_time` datetime NOT NULL COMMENT '进出时间',
  `device_location` varchar(100) DEFAULT NULL COMMENT '设备位置',
  `device_id` varchar(50) DEFAULT NULL COMMENT '设备编号',
  `result` varchar(20) DEFAULT 'SUCCESS' COMMENT '结果：SUCCESS-成功, FAILED-失败',
  `remark` text COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_building_id` (`building_id`),
  KEY `idx_access_time` (`access_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='门禁记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dorm_access_record`
--

LOCK TABLES `dorm_access_record` WRITE;
/*!40000 ALTER TABLE `dorm_access_record` DISABLE KEYS */;
INSERT INTO `dorm_access_record` VALUES
(1,1,'张三',1,'1号楼','进入','2026-04-04 08:30:00','1号楼大门','DEV-001','成功','正常刷卡','2026-04-04 06:29:36'),
(2,2,'李四',1,'1号楼','离开','2026-04-04 09:15:00','1号楼大门','DEV-001','成功','正常刷卡','2026-04-04 06:29:36'),
(3,3,'王五',3,'3号楼','进入','2026-04-04 10:00:00','3号楼大门','DEV-003','成功',NULL,'2026-04-04 06:29:36'),
(4,1,'张三',1,'1号楼','离开','2026-04-04 12:00:00','1号楼侧门','DEV-002','成功','正常刷卡','2026-04-04 06:29:36'),
(5,4,'赵六',4,'4号楼','进入','2026-04-04 13:30:00','4号楼大门','DEV-004','失败','卡片过期','2026-04-04 06:29:36');
/*!40000 ALTER TABLE `dorm_access_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dorm_accommodation`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dorm_accommodation` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '住宿记录ID',
  `student_id` bigint NOT NULL COMMENT '学生用户ID',
  `room_id` bigint NOT NULL COMMENT '房间ID',
  `building_id` bigint NOT NULL COMMENT '楼栋ID',
  `bed_number` varchar(20) DEFAULT NULL COMMENT '床位号',
  `check_in_date` date NOT NULL COMMENT '入住日期',
  `expected_check_out_date` date DEFAULT NULL COMMENT '预计退宿日期',
  `actual_check_out_date` date DEFAULT NULL COMMENT '实际退宿日期',
  `status` varchar(20) DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE-在住, TRANSFERRED-已调宿, CHECKED_OUT-已退宿, SUSPENDED-暂停',
  `deposit` decimal(10,2) DEFAULT NULL COMMENT '押金',
  `remark` text COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_room_id` (`room_id`),
  KEY `idx_building_id` (`building_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='学生住宿记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dorm_accommodation`
--

LOCK TABLES `dorm_accommodation` WRITE;
/*!40000 ALTER TABLE `dorm_accommodation` DISABLE KEYS */;
INSERT INTO `dorm_accommodation` (`id`, `student_id`, `room_id`, `building_id`, `bed_number`, `check_in_date`, `expected_check_out_date`, `actual_check_out_date`, `status`, `deposit`, `remark`, `create_time`, `update_time`) VALUES (1,2,1,1,'A','2024-09-01',NULL,NULL,'ACTIVE',500.00,NULL,'2026-03-29 07:42:44','2026-03-29 07:42:44'),(2,3,11,2,'A','2024-09-01',NULL,NULL,'ACTIVE',500.00,NULL,'2026-03-29 07:42:44','2026-03-29 07:42:44'),(3,4,21,3,'A','2024-09-01',NULL,NULL,'ACTIVE',500.00,NULL,'2026-03-29 07:42:44','2026-03-29 07:42:44'),(4,5,21,3,'B','2024-09-01',NULL,NULL,'ACTIVE',500.00,NULL,'2026-03-29 07:42:44','2026-03-29 07:42:44');
/*!40000 ALTER TABLE `dorm_accommodation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dorm_announcement`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dorm_announcement` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `title` varchar(200) NOT NULL COMMENT '标题',
  `content` text COMMENT '内容',
  `type` varchar(20) DEFAULT 'NOTICE' COMMENT '类型：NOTICE-通知, NEWS-新闻, POLICY-政策, ACTIVITY-活动',
  `priority` varchar(20) DEFAULT 'NORMAL' COMMENT '优先级：LOW-低, NORMAL-普通, HIGH-高, URGENT-紧急',
  `status` varchar(20) DEFAULT 'DRAFT' COMMENT '状态：DRAFT-草稿, PUBLISHED-已发布, EXPIRED-已过期',
  `publish_time` datetime DEFAULT NULL COMMENT '发布时间',
  `expire_time` datetime DEFAULT NULL COMMENT '过期时间',
  `target_audience` varchar(50) DEFAULT NULL COMMENT '目标受众',
  `attachments` text COMMENT '附件URL',
  `publisher_id` bigint NOT NULL COMMENT '发布人ID',
  `publisher_name` varchar(50) DEFAULT NULL COMMENT '发布人姓名',
  `view_count` int DEFAULT '0' COMMENT '浏览次数',
  `is_top` tinyint(1) DEFAULT '0' COMMENT '是否置顶',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`),
  KEY `idx_publish_time` (`publish_time`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='公告通知表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dorm_announcement`
--

LOCK TABLES `dorm_announcement` WRITE;
/*!40000 ALTER TABLE `dorm_announcement` DISABLE KEYS */;
INSERT INTO `dorm_announcement` (`id`, `title`, `content`, `type`, `priority`, `status`, `publish_time`, `expire_time`, `target_audience`, `attachments`, `publisher_id`, `publisher_name`, `view_count`, `is_top`, `create_time`, `update_time`) VALUES (1,'关于2024年秋季学期宿舍分配的通知','各位同学，2024年秋季学期宿舍分配工作即将开始，请关注选宿舍时间。','NOTICE','HIGH','PUBLISHED','2026-03-29 07:42:44',NULL,NULL,NULL,1,'系统管理员',0,0,'2026-03-29 07:42:44','2026-03-29 13:23:40'),(2,'宿舍安全管理规定','请同学们注意宿舍安全，禁止使用大功率电器。','POLICY','URGENT','PUBLISHED','2026-03-29 07:42:44',NULL,NULL,NULL,1,'系统管理员',0,0,'2026-03-29 07:42:44','2026-03-29 13:23:40'),(3,'卫生检查通知','本周将进行宿舍卫生大检查，请同学们做好准备。','NOTICE','HIGH','PUBLISHED','2026-03-29 07:42:45',NULL,NULL,NULL,1,'系统管理员',0,0,'2026-03-29 07:42:45','2026-03-29 13:23:40'),(4,'宿舍卫生大检查','本周五将进行宿舍卫生大检查，请同学们提前做好卫生清洁工作。','NOTICE','NORMAL','PUBLISHED','2026-03-29 07:42:45',NULL,NULL,NULL,1,'系统管理员',0,0,'2026-03-29 07:42:45','2026-03-29 13:23:40'),(5,'元旦晚会报名','元旦晚会节目征集正在进行中，有兴趣的同学积极报名参加。','ACTIVITY','NORMAL','PUBLISHED','2026-03-29 07:42:45',NULL,NULL,NULL,1,'系统管理员',0,0,'2026-03-29 07:42:45','2026-03-29 13:23:40');
/*!40000 ALTER TABLE `dorm_announcement` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dorm_building`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dorm_building` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '楼栋ID',
  `building_code` varchar(50) NOT NULL COMMENT '楼栋编号',
  `building_name` varchar(100) NOT NULL COMMENT '楼栋名称',
  `building_type` varchar(20) DEFAULT NULL COMMENT '楼栋类型',
  `gender_type` varchar(20) DEFAULT 'MIXED' COMMENT '性别类型',
  `total_floors` int DEFAULT NULL COMMENT '总楼层数',
  `total_rooms` int DEFAULT NULL COMMENT '总房间数',
  `total_beds` int DEFAULT NULL COMMENT '总床位数',
  `available_beds` int DEFAULT '0' COMMENT '可用床位数',
  `location` varchar(200) DEFAULT NULL COMMENT '位置描述',
  `manager_name` varchar(50) DEFAULT NULL COMMENT '宿管姓名',
  `manager_phone` varchar(20) DEFAULT NULL COMMENT '宿管电话',
  `status` varchar(20) DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE-正常, MAINTENANCE-维修中, CLOSED-已关闭',
  `remark` text COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `building_code` (`building_code`),
  KEY `idx_building_code` (`building_code`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='宿舍楼表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dorm_building`
--

LOCK TABLES `dorm_building` WRITE;
/*!40000 ALTER TABLE `dorm_building` DISABLE KEYS */;
INSERT INTO `dorm_building` (`id`, `building_code`, `building_name`, `building_type`, `gender_type`, `total_floors`, `total_rooms`, `total_beds`, `available_beds`, `location`, `manager_name`, `manager_phone`, `status`, `remark`, `create_time`, `update_time`) VALUES (1,'A01','1号楼','男生宿舍','MIXED',6,60,240,239,'校园东侧','张三','13800138001','ACTIVE',NULL,'2026-03-29 07:42:44','2026-03-29 13:23:58'),(2,'A02','2号楼','男生宿舍','MIXED',6,60,240,239,'校园东侧','李四','13800138002','ACTIVE',NULL,'2026-03-29 07:42:44','2026-03-29 13:23:58'),(3,'B01','3号楼','女生宿舍','MIXED',6,60,240,238,'校园西侧','王五','13800138003','ACTIVE',NULL,'2026-03-29 07:42:44','2026-03-29 13:23:58'),(4,'A03','4号楼','男生宿舍','MIXED',6,60,240,180,'校园北侧','赵六','13800138004','ACTIVE',NULL,'2026-03-29 07:42:44','2026-03-29 13:23:58'),(5,'B02','5号楼','女生宿舍','MIXED',6,60,240,200,'校园北侧','孙七','13800138005','ACTIVE',NULL,'2026-03-29 07:42:44','2026-03-29 13:23:58');
/*!40000 ALTER TABLE `dorm_building` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dorm_disciplinary`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dorm_disciplinary` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '违纪记录ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `student_name` varchar(50) DEFAULT NULL COMMENT '学生姓名',
  `room_id` bigint NOT NULL COMMENT '房间ID',
  `violation_type` varchar(50) NOT NULL COMMENT '违纪类型',
  `description` text COMMENT '违纪描述',
  `violation_time` datetime NOT NULL COMMENT '违纪时间',
  `punishment` varchar(50) DEFAULT NULL COMMENT '处罚措施',
  `fine` decimal(10,2) DEFAULT NULL COMMENT '罚款金额',
  `status` varchar(20) DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE-有效, CANCELLED-已撤销',
  `recorder_id` bigint NOT NULL COMMENT '记录人ID',
  `recorder_name` varchar(50) DEFAULT NULL COMMENT '记录人姓名',
  `remark` text COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_room_id` (`room_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='违纪记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dorm_disciplinary`
--

LOCK TABLES `dorm_disciplinary` WRITE;
/*!40000 ALTER TABLE `dorm_disciplinary` DISABLE KEYS */;
INSERT INTO `dorm_disciplinary` (`id`, `student_id`, `student_name`, `room_id`, `violation_type`, `description`, `violation_time`, `punishment`, `fine`, `status`, `recorder_id`, `recorder_name`, `remark`, `create_time`, `update_time`) VALUES (1,2,'张三',1,'违规用电','使用大功率电器','2026-03-29 07:42:45','警告',0.00,'ACTIVE',1,'系统管理员',NULL,'2026-03-29 07:42:45','2026-03-29 13:22:50'),(2,4,'王芳',21,'晚归','未按时归寝','2026-03-29 07:42:45','通报批评',50.00,'ACTIVE',1,'系统管理员',NULL,'2026-03-29 07:42:45','2026-03-29 13:22:50');
/*!40000 ALTER TABLE `dorm_disciplinary` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dorm_fee`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dorm_fee` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '缴费ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `fee_type` varchar(50) NOT NULL COMMENT '费用类型',
  `amount` decimal(10,2) NOT NULL COMMENT '金额',
  `due_time` datetime NOT NULL COMMENT '应缴时间',
  `paid_time` datetime DEFAULT NULL COMMENT '实际缴费时间',
  `status` varchar(20) DEFAULT 'UNPAID' COMMENT '状态：UNPAID-未缴费, PAID-已缴费, OVERDUE-已逾期, REFUNDED-已退款',
  `payment_method` varchar(50) DEFAULT NULL COMMENT '支付方式',
  `transaction_id` varchar(100) DEFAULT NULL COMMENT '交易流水号',
  `remark` text COMMENT '备注',
  `operator_id` bigint NOT NULL COMMENT '操作人ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='缴费记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dorm_fee`
--

LOCK TABLES `dorm_fee` WRITE;
/*!40000 ALTER TABLE `dorm_fee` DISABLE KEYS */;
INSERT INTO `dorm_fee` (`id`, `student_id`, `fee_type`, `amount`, `due_time`, `paid_time`, `status`, `payment_method`, `transaction_id`, `remark`, `operator_id`, `create_time`, `update_time`) VALUES (1,2,'住宿费',2400.00,'2024-09-01 00:00:00','2024-09-05 00:00:00','PAID','支付宝',NULL,NULL,1,'2026-03-29 07:42:45','2026-03-29 13:22:55'),(2,3,'住宿费',2400.00,'2024-09-01 00:00:00','2024-09-03 00:00:00','PAID','微信',NULL,NULL,1,'2026-03-29 07:42:45','2026-03-29 13:22:55'),(3,4,'住宿费',1800.00,'2024-09-01 00:00:00',NULL,'UNPAID',NULL,NULL,NULL,1,'2026-03-29 07:42:45','2026-03-29 13:22:55'),(4,5,'住宿费',1800.00,'2024-09-01 00:00:00','2024-09-10 00:00:00','PAID','银行卡',NULL,NULL,1,'2026-03-29 07:42:45','2026-03-29 13:22:55'),(5,2,'水电费',150.00,'2024-10-01 00:00:00',NULL,'UNPAID',NULL,NULL,NULL,1,'2026-03-29 07:42:45','2026-03-29 13:22:55');
/*!40000 ALTER TABLE `dorm_fee` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dorm_hygiene`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dorm_hygiene` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '检查记录ID',
  `room_id` bigint NOT NULL COMMENT '房间ID',
  `building_id` bigint NOT NULL COMMENT '楼栋ID',
  `inspection_date` date NOT NULL COMMENT '检查日期',
  `score` int NOT NULL COMMENT '评分(0-100)',
  `grade` varchar(20) DEFAULT NULL COMMENT '评级：EXCELLENT-优秀, GOOD-良好, QUALIFIED-合格, UNQUALIFIED-不合格',
  `items` text COMMENT '检查项目(JSON格式)',
  `problems` text COMMENT '存在问题',
  `images` text COMMENT '图片URL',
  `inspector_id` bigint NOT NULL COMMENT '检查人ID',
  `inspector_name` varchar(50) DEFAULT NULL COMMENT '检查人姓名',
  `remark` text COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_room_id` (`room_id`),
  KEY `idx_building_id` (`building_id`),
  KEY `idx_inspection_date` (`inspection_date`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='卫生检查记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dorm_hygiene`
--

LOCK TABLES `dorm_hygiene` WRITE;
/*!40000 ALTER TABLE `dorm_hygiene` DISABLE KEYS */;
INSERT INTO `dorm_hygiene` (`id`, `room_id`, `building_id`, `inspection_date`, `score`, `grade`, `items`, `problems`, `images`, `inspector_id`, `inspector_name`, `remark`, `create_time`, `update_time`) VALUES (1,1,1,'2026-03-29',85,'GOOD',NULL,'地面清洁',NULL,1,'系统管理员',NULL,'2026-03-29 07:42:45','2026-03-29 13:23:03'),(2,11,2,'2026-03-29',92,'EXCELLENT',NULL,'',NULL,1,'系统管理员',NULL,'2026-03-29 07:42:45','2026-03-29 13:23:03'),(3,21,3,'2026-03-29',78,'QUALIFIED',NULL,'窗户有灰尘',NULL,1,'系统管理员',NULL,'2026-03-29 07:42:45','2026-03-29 13:23:03');
/*!40000 ALTER TABLE `dorm_hygiene` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dorm_repair`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dorm_repair` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '报修ID',
  `student_id` bigint NOT NULL COMMENT '申请人ID',
  `room_id` bigint NOT NULL COMMENT '房间ID',
  `repair_type` varchar(50) NOT NULL COMMENT '报修类型',
  `description` text COMMENT '问题描述',
  `images` text COMMENT '图片URL',
  `priority` varchar(20) DEFAULT 'NORMAL' COMMENT '优先级：LOW-低, NORMAL-普通, HIGH-高, URGENT-紧急',
  `status` varchar(20) DEFAULT 'PENDING' COMMENT '状态：PENDING-待处理, PROCESSING-处理中, COMPLETED-已完成, CANCELLED-已取消',
  `handler_id` bigint DEFAULT NULL COMMENT '处理人ID',
  `handle_result` text COMMENT '处理结果',
  `handle_time` datetime DEFAULT NULL COMMENT '处理时间',
  `repair_cost` decimal(10,2) DEFAULT NULL COMMENT '维修费用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_room_id` (`room_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='报修申请表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dorm_repair`
--

LOCK TABLES `dorm_repair` WRITE;
/*!40000 ALTER TABLE `dorm_repair` DISABLE KEYS */;
INSERT INTO `dorm_repair` (`id`, `student_id`, `room_id`, `repair_type`, `description`, `images`, `priority`, `status`, `handler_id`, `handle_result`, `handle_time`, `repair_cost`, `create_time`, `update_time`) VALUES (1,2,1,'水暖','水龙头漏水',NULL,'HIGH','PENDING',NULL,NULL,NULL,NULL,'2026-03-29 07:42:45','2026-03-29 13:22:46'),(2,3,11,'电路','灯泡不亮',NULL,'NORMAL','PROCESSING',NULL,NULL,NULL,NULL,'2026-03-29 07:42:45','2026-03-29 13:22:46'),(3,4,21,'门窗','门锁损坏',NULL,'URGENT','COMPLETED',NULL,NULL,NULL,NULL,'2026-03-29 07:42:45','2026-03-29 13:22:46'),(4,5,21,'家具','床板松动',NULL,'LOW','PENDING',NULL,NULL,NULL,NULL,'2026-03-29 07:42:45','2026-03-29 13:22:46');
/*!40000 ALTER TABLE `dorm_repair` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dorm_room`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dorm_room` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '房间ID',
  `building_id` bigint NOT NULL COMMENT '所属楼栋ID',
  `room_number` varchar(50) NOT NULL COMMENT '房间号',
  `floor` int DEFAULT NULL COMMENT '楼层',
  `capacity` int DEFAULT NULL COMMENT '容纳人数',
  `occupied_beds` int DEFAULT '0' COMMENT '已占用床位数',
  `room_type` varchar(20) DEFAULT NULL COMMENT '房间类型：FOUR_PERSON-四人间, SIX_PERSON-六人间, EIGHT_PERSON-八人间',
  `status` varchar(20) DEFAULT 'AVAILABLE' COMMENT '状态：AVAILABLE-空闲, PARTIAL-部分占用, FULL-已满, MAINTENANCE-维修中',
  `facilities` varchar(200) DEFAULT NULL COMMENT '设施描述',
  `monthly_fee` decimal(10,2) DEFAULT NULL COMMENT '月租费用',
  `remark` text COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_building_room` (`building_id`,`room_number`),
  KEY `idx_building_id` (`building_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=41 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='房间表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dorm_room`
--

LOCK TABLES `dorm_room` WRITE;
/*!40000 ALTER TABLE `dorm_room` DISABLE KEYS */;
INSERT INTO `dorm_room` (`id`, `building_id`, `room_number`, `floor`, `capacity`, `occupied_beds`, `room_type`, `status`, `facilities`, `monthly_fee`, `remark`, `create_time`, `update_time`) VALUES (1,1,'1001',1,4,1,'FOUR_PERSON','AVAILABLE','空调、独立卫生间、热水器',800.00,NULL,'2026-03-29 07:42:44','2026-03-29 13:37:15'),(2,1,'1002',1,4,0,'FOUR_PERSON','AVAILABLE','空调、独立卫生间、热水器',800.00,NULL,'2026-03-29 07:42:44','2026-03-29 13:37:15'),(3,1,'1003',1,4,0,'FOUR_PERSON','AVAILABLE','空调、独立卫生间、热水器',800.00,NULL,'2026-03-29 07:42:44','2026-03-29 13:37:15'),(4,1,'1004',1,4,0,'FOUR_PERSON','AVAILABLE','空调、独立卫生间、热水器',800.00,NULL,'2026-03-29 07:42:44','2026-03-29 13:37:15'),(5,1,'1005',1,4,0,'FOUR_PERSON','AVAILABLE','空调、独立卫生间、热水器',800.00,NULL,'2026-03-29 07:42:44','2026-03-29 13:37:15'),(6,1,'1006',1,4,0,'FOUR_PERSON','AVAILABLE','空调、独立卫生间、热水器',800.00,NULL,'2026-03-29 07:42:44','2026-03-29 13:37:15'),(7,1,'1007',1,4,0,'FOUR_PERSON','AVAILABLE','空调、独立卫生间、热水器',800.00,NULL,'2026-03-29 07:42:44','2026-03-29 13:37:15'),(8,1,'1008',1,4,0,'FOUR_PERSON','AVAILABLE','空调、独立卫生间、热水器',800.00,NULL,'2026-03-29 07:42:44','2026-03-29 13:37:15'),(9,1,'1009',1,4,0,'FOUR_PERSON','AVAILABLE','空调、独立卫生间、热水器',800.00,NULL,'2026-03-29 07:42:44','2026-03-29 13:37:15'),(10,1,'1010',1,4,0,'FOUR_PERSON','AVAILABLE','空调、独立卫生间、热水器',800.00,NULL,'2026-03-29 07:42:44','2026-03-29 13:37:15'),(16,2,'1001',1,4,2,'FOUR_PERSON','PARTIAL','空调、独立卫生间、热水器',800.00,NULL,'2026-03-29 07:42:44','2026-03-29 13:37:15'),(17,2,'1002',1,4,2,'FOUR_PERSON','PARTIAL','空调、独立卫生间、热水器',800.00,NULL,'2026-03-29 07:42:44','2026-03-29 13:37:15'),(18,2,'1003',1,4,2,'FOUR_PERSON','PARTIAL','空调、独立卫生间、热水器',800.00,NULL,'2026-03-29 07:42:44','2026-03-29 13:37:15'),(19,2,'1004',1,4,2,'FOUR_PERSON','PARTIAL','空调、独立卫生间、热水器',800.00,NULL,'2026-03-29 07:42:44','2026-03-29 13:37:15'),(20,2,'1005',1,4,2,'FOUR_PERSON','PARTIAL','空调、独立卫生间、热水器',800.00,NULL,'2026-03-29 07:42:44','2026-03-29 13:37:15'),(21,2,'1006',1,4,2,'FOUR_PERSON','PARTIAL','空调、独立卫生间、热水器',800.00,NULL,'2026-03-29 07:42:44','2026-03-29 13:37:15'),(22,2,'1007',1,4,2,'FOUR_PERSON','PARTIAL','空调、独立卫生间、热水器',800.00,NULL,'2026-03-29 07:42:44','2026-03-29 13:37:15'),(23,2,'1008',1,4,2,'FOUR_PERSON','PARTIAL','空调、独立卫生间、热水器',800.00,NULL,'2026-03-29 07:42:44','2026-03-29 13:37:15'),(24,2,'1009',1,4,2,'FOUR_PERSON','PARTIAL','空调、独立卫生间、热水器',800.00,NULL,'2026-03-29 07:42:44','2026-03-29 13:37:15'),(25,2,'1010',1,4,2,'FOUR_PERSON','PARTIAL','空调、独立卫生间、热水器',800.00,NULL,'2026-03-29 07:42:44','2026-03-29 13:37:15'),(31,3,'1001',1,6,4,'SIX_PERSON','PARTIAL','空调、独立卫生间、热水器',600.00,NULL,'2026-03-29 07:42:44','2026-03-29 13:37:15'),(32,3,'1002',1,6,4,'SIX_PERSON','PARTIAL','空调、独立卫生间、热水器',600.00,NULL,'2026-03-29 07:42:44','2026-03-29 13:37:15'),(33,3,'1003',1,6,4,'SIX_PERSON','PARTIAL','空调、独立卫生间、热水器',600.00,NULL,'2026-03-29 07:42:44','2026-03-29 13:37:15'),(34,3,'1004',1,6,4,'SIX_PERSON','PARTIAL','空调、独立卫生间、热水器',600.00,NULL,'2026-03-29 07:42:44','2026-03-29 13:37:15'),(35,3,'1005',1,6,4,'SIX_PERSON','PARTIAL','空调、独立卫生间、热水器',600.00,NULL,'2026-03-29 07:42:44','2026-03-29 13:37:15'),(36,3,'1006',1,6,4,'SIX_PERSON','PARTIAL','空调、独立卫生间、热水器',600.00,NULL,'2026-03-29 07:42:44','2026-03-29 13:37:15'),(37,3,'1007',1,6,4,'SIX_PERSON','PARTIAL','空调、独立卫生间、热水器',600.00,NULL,'2026-03-29 07:42:44','2026-03-29 13:37:15'),(38,3,'1008',1,6,4,'SIX_PERSON','PARTIAL','空调、独立卫生间、热水器',600.00,NULL,'2026-03-29 07:42:44','2026-03-29 13:37:15'),(39,3,'1009',1,6,4,'SIX_PERSON','PARTIAL','空调、独立卫生间、热水器',600.00,NULL,'2026-03-29 07:42:44','2026-03-29 13:37:15'),(40,3,'1010',1,6,4,'SIX_PERSON','PARTIAL','空调、独立卫生间、热水器',600.00,NULL,'2026-03-29 07:42:44','2026-03-29 13:37:15');
/*!40000 ALTER TABLE `dorm_room` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dorm_room_transfer`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dorm_room_transfer` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_id` bigint NOT NULL,
  `student_name` varchar(50) DEFAULT NULL,
  `old_room_id` bigint DEFAULT NULL,
  `old_room_number` varchar(50) DEFAULT NULL,
  `old_building_id` bigint DEFAULT NULL,
  `old_building_name` varchar(100) DEFAULT NULL,
  `old_bed_number` varchar(20) DEFAULT NULL,
  `new_room_id` bigint DEFAULT NULL,
  `new_room_number` varchar(50) DEFAULT NULL,
  `new_building_id` bigint DEFAULT NULL,
  `new_building_name` varchar(100) DEFAULT NULL,
  `new_bed_number` varchar(20) DEFAULT NULL,
  `reason` text,
  `status` varchar(20) DEFAULT 'PENDING',
  `apply_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `approve_time` datetime DEFAULT NULL,
  `approver_id` bigint DEFAULT NULL,
  `approver_name` varchar(50) DEFAULT NULL,
  `reject_reason` text,
  `remark` text,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='换宿舍申请表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dorm_room_transfer`
--

LOCK TABLES `dorm_room_transfer` WRITE;
/*!40000 ALTER TABLE `dorm_room_transfer` DISABLE KEYS */;
/*!40000 ALTER TABLE `dorm_room_transfer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dorm_selection_config`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dorm_selection_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `config_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '配置名称',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `academic_year` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '学年',
  `semester` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '学期',
  `gender_restriction` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'ALL' COMMENT '性别限制',
  `allowed_colleges` text COLLATE utf8mb4_unicode_ci COMMENT '允许的学院',
  `allowed_grades` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '允许的年级',
  `target_audience` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '目标人群',
  `building_ids` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '可选楼栋ID，逗号分隔',
  `max_select_count` int DEFAULT '3' COMMENT '最大可选志愿数',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE-进行中, INACTIVE-未开始, ENDED-已结束',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '描述说明',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `version` int DEFAULT '0' COMMENT '乐观锁版本号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='选宿舍配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dorm_selection_config`
--

LOCK TABLES `dorm_selection_config` WRITE;
/*!40000 ALTER TABLE `dorm_selection_config` DISABLE KEYS */;
INSERT INTO `dorm_selection_config` (`id`, `config_name`, `start_time`, `end_time`, `academic_year`, `semester`, `gender_restriction`, `allowed_colleges`, `allowed_grades`, `target_audience`, `building_ids`, `max_select_count`, `status`, `description`, `create_time`, `update_time`, `version`) VALUES (1,'2024年秋季新生选宿舍','2026-02-27 07:41:58','2026-04-28 07:41:58','2024-2025','第一学期','ALL',NULL,NULL,'2024级新生','1,2,3,4,5',3,'ACTIVE','2024年秋季入学新生宿舍选择，请仔细阅读选宿舍须知后进行选择。','2026-03-29 07:41:58','2026-03-29 13:23:31',0);
/*!40000 ALTER TABLE `dorm_selection_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dorm_selection_record`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dorm_selection_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `config_id` bigint NOT NULL COMMENT '配置ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `student_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '学生姓名',
  `building_id` bigint DEFAULT NULL COMMENT '楼栋ID',
  `building_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '楼栋名称',
  `room_id` bigint DEFAULT NULL COMMENT '房间ID',
  `room_number` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '房间号',
  `bed_number` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '床位号',
  `priority` int DEFAULT '0' COMMENT '志愿优先级',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'SELECTED' COMMENT '状态：SELECTED-已选, CANCELLED-已取消',
  `remark` text COLLATE utf8mb4_unicode_ci COMMENT '备注',
  `select_time` datetime DEFAULT NULL COMMENT '选择时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_student` (`config_id`,`student_id`),
  KEY `idx_config_id` (`config_id`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_room_id` (`room_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='选宿舍记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dorm_selection_record`
--

LOCK TABLES `dorm_selection_record` WRITE;
/*!40000 ALTER TABLE `dorm_selection_record` DISABLE KEYS */;
/*!40000 ALTER TABLE `dorm_selection_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dorm_visitor`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dorm_visitor` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '访客ID',
  `visitor_name` varchar(50) NOT NULL COMMENT '访客姓名',
  `visitor_phone` varchar(20) DEFAULT NULL COMMENT '访客电话',
  `id_card` varchar(50) DEFAULT NULL COMMENT '身份证号',
  `visitor_type` varchar(50) DEFAULT NULL COMMENT '访客类型',
  `visit_room_id` bigint NOT NULL COMMENT '被访房间ID',
  `visit_student_id` bigint NOT NULL COMMENT '被访学生ID',
  `visit_student_name` varchar(50) DEFAULT NULL COMMENT '被访学生姓名',
  `visit_reason` varchar(100) NOT NULL COMMENT '来访事由',
  `visit_time` datetime NOT NULL COMMENT '来访时间',
  `leave_time` datetime DEFAULT NULL COMMENT '离开时间',
  `status` varchar(20) DEFAULT 'VISITING' COMMENT '状态：VISITING-访问中, LEFT-已离开',
  `remark` text COMMENT '备注',
  `register` varchar(50) DEFAULT NULL COMMENT '登记人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_visit_room_id` (`visit_room_id`),
  KEY `idx_visit_student_id` (`visit_student_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='访客登记表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dorm_visitor`
--

LOCK TABLES `dorm_visitor` WRITE;
/*!40000 ALTER TABLE `dorm_visitor` DISABLE KEYS */;
INSERT INTO `dorm_visitor` (`id`, `visitor_name`, `visitor_phone`, `id_card`, `visitor_type`, `visit_room_id`, `visit_student_id`, `visit_student_name`, `visit_reason`, `visit_time`, `leave_time`, `status`, `remark`, `register`, `create_time`, `update_time`) VALUES (1,'家长王','13888888888',NULL,'家长',1,2,'张三','探望学生','2026-03-29 07:42:45',NULL,'VISITING',NULL,NULL,'2026-03-29 07:42:45','2026-03-29 13:23:00'),(2,'快递员李','13999999999',NULL,'快递',11,3,'李四','送快递','2026-03-29 07:42:45',NULL,'LEFT',NULL,NULL,'2026-03-29 07:42:45','2026-03-29 13:23:00');
/*!40000 ALTER TABLE `dorm_visitor` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_permission`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_permission` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '权限ID',
  `name` varchar(100) NOT NULL COMMENT '权限名称',
  `code` varchar(100) DEFAULT NULL COMMENT '权限编码',
  `type` varchar(20) NOT NULL DEFAULT 'MENU' COMMENT '类型: MENU/BUTTON',
  `parent_id` bigint DEFAULT '0' COMMENT '父权限ID',
  `path` varchar(200) DEFAULT NULL COMMENT '路由路径',
  `component` varchar(200) DEFAULT NULL COMMENT '前端组件路径',
  `icon` varchar(100) DEFAULT NULL COMMENT '图标',
  `sort` int DEFAULT '0' COMMENT '排序',
  `status` varchar(20) DEFAULT 'ACTIVE' COMMENT '状态',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='权限表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_permission`
--

LOCK TABLES `sys_permission` WRITE;
/*!40000 ALTER TABLE `sys_permission` DISABLE KEYS */;
INSERT INTO `sys_permission` (`id`, `name`, `code`, `type`, `parent_id`, `path`, `component`, `icon`, `sort`, `status`, `create_time`, `update_time`) VALUES (1,'仪表盘','dashboard','MENU',0,'/dashboard',NULL,'Odometer',1,'ACTIVE','2026-03-29 13:15:13','2026-03-29 13:15:13'),(2,'楼栋管理','building','MENU',0,'/building',NULL,'OfficeBuilding',2,'ACTIVE','2026-03-29 13:15:13','2026-03-29 13:15:13'),(3,'房间管理','room','MENU',0,'/room',NULL,'House',3,'ACTIVE','2026-03-29 13:15:13','2026-03-29 13:15:13'),(4,'住宿管理','accommodation','MENU',0,'/accommodation',NULL,'User',4,'ACTIVE','2026-03-29 13:15:13','2026-03-29 13:15:13'),(5,'选宿舍配置','selection','MENU',0,'/selection',NULL,'List',5,'ACTIVE','2026-03-29 13:15:13','2026-03-29 13:15:13'),(6,'换宿舍审批','room-transfer','MENU',0,'/room-transfer',NULL,'Switch',6,'ACTIVE','2026-03-29 13:15:13','2026-03-29 13:15:13'),(7,'报修管理','repair','MENU',0,'/repair',NULL,'Tools',7,'ACTIVE','2026-03-29 13:15:13','2026-03-29 13:15:13'),(8,'卫生检查','hygiene','MENU',0,'/hygiene',NULL,'Brush',8,'ACTIVE','2026-03-29 13:15:13','2026-03-29 13:15:13'),(9,'访客管理','visitor','MENU',0,'/visitor',NULL,'UserFilled',9,'ACTIVE','2026-03-29 13:15:13','2026-03-29 13:15:13'),(10,'门禁记录','access-record','MENU',0,'/access-record',NULL,'Key',10,'ACTIVE','2026-03-29 13:15:13','2026-03-29 13:15:13'),(11,'缴费管理','fee','MENU',0,'/fee',NULL,'Money',11,'ACTIVE','2026-03-29 13:15:13','2026-03-29 13:15:13'),(12,'违纪管理','disciplinary','MENU',0,'/disciplinary',NULL,'Warning',12,'ACTIVE','2026-03-29 13:15:13','2026-03-29 13:15:13'),(13,'公告管理','announcement','MENU',0,'/announcement',NULL,'Bell',13,'ACTIVE','2026-03-29 13:15:13','2026-03-29 13:15:13'),(14,'统计报表','statistics','MENU',0,'/statistics',NULL,'DataAnalysis',14,'ACTIVE','2026-03-29 13:15:13','2026-03-29 13:15:13'),(15,'用户管理','user','MENU',0,'/user',NULL,'UserFilled',15,'ACTIVE','2026-03-29 13:15:13','2026-03-29 13:15:13'),(16,'角色管理','role','MENU',0,'/role',NULL,'Stamp',16,'ACTIVE','2026-03-29 13:15:13','2026-03-29 13:15:13'),(17,'权限管理','permission','MENU',0,'/permission',NULL,'Lock',17,'ACTIVE','2026-03-29 13:15:13','2026-03-29 13:15:13');
/*!40000 ALTER TABLE `sys_permission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(50) NOT NULL COMMENT '角色名称',
  `role_code` varchar(50) DEFAULT NULL COMMENT '角色编码',
  `description` text COMMENT '角色描述',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `role_name` (`role_name`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role`
--

LOCK TABLES `sys_role` WRITE;
/*!40000 ALTER TABLE `sys_role` DISABLE KEYS */;
INSERT INTO `sys_role` (`id`, `role_name`, `role_code`, `description`, `create_time`, `update_time`) VALUES (1,'系统管理员','ADMIN','系统管理员，拥有所有权限','2026-03-29 07:42:44','2026-03-29 13:16:40'),(2,'宿管','DORM_MANAGER','宿舍管理员，管理楼栋和房间','2026-03-29 07:42:44','2026-03-29 13:16:40'),(3,'学生','STUDENT','学生用户，查看个人信息','2026-03-29 07:42:44','2026-03-29 13:16:40');
/*!40000 ALTER TABLE `sys_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role_permission`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role_permission` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `permission_id` bigint NOT NULL COMMENT '权限ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_permission` (`role_id`,`permission_id`)
) ENGINE=InnoDB AUTO_INCREMENT=85 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色权限关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role_permission`
--

LOCK TABLES `sys_role_permission` WRITE;
/*!40000 ALTER TABLE `sys_role_permission` DISABLE KEYS */;
INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`) VALUES (43,1,1),(44,1,2),(45,1,3),(46,1,4),(47,1,5),(48,1,6),(49,1,7),(50,1,8),(51,1,9),(52,1,10),(53,1,11),(54,1,12),(55,1,13),(56,1,14),(57,1,15),(58,1,16),(59,1,17),(74,2,1),(75,2,2),(76,2,3),(77,2,4),(78,2,5),(79,2,6),(80,2,7),(81,2,8),(82,2,9),(83,2,10),(84,2,13);
/*!40000 ALTER TABLE `sys_role_permission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(255) NOT NULL COMMENT '密码',
  `real_name` varchar(50) DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `gender` varchar(10) DEFAULT NULL COMMENT '性别',
  `dept_id` bigint DEFAULT NULL COMMENT '部门ID',
  `student_id` varchar(50) DEFAULT NULL COMMENT '学号',
  `department` varchar(50) DEFAULT NULL COMMENT '院系',
  `class_name` varchar(50) DEFAULT NULL COMMENT '班级',
  `user_type` varchar(20) NOT NULL COMMENT '用户类型：STUDENT-学生, DORM_MANAGER-宿管, ADMIN-管理员',
  `status` varchar(20) DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE-正常, INACTIVE-未激活, LOCKED-锁定',
  `avatar` text COMMENT '头像URL',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  KEY `idx_username` (`username`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_user_type` (`user_type`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user`
--

LOCK TABLES `sys_user` WRITE;
/*!40000 ALTER TABLE `sys_user` DISABLE KEYS */;
INSERT INTO `sys_user` (`id`, `username`, `password`, `real_name`, `phone`, `email`, `gender`, `dept_id`, `student_id`, `department`, `class_name`, `user_type`, `status`, `avatar`, `create_time`, `update_time`) VALUES (1,'admin','$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG','系统管理员',NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ADMIN','ACTIVE',NULL,'2026-03-29 07:42:44','2026-03-29 13:15:47'),(2,'stu001','$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG','张三','13900001001',NULL,'MALE',NULL,'2024001','计算机学院','软件工程1班','STUDENT','ACTIVE',NULL,'2026-03-29 07:42:44','2026-03-29 13:37:11'),(3,'stu002','$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG','李四','13900001002',NULL,'MALE',NULL,'2024002','计算机学院','软件工程1班','STUDENT','ACTIVE',NULL,'2026-03-29 07:42:44','2026-03-29 13:37:11'),(4,'stu003','$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG','王五','13900001003',NULL,'FEMALE',NULL,'2024003','外国语学院','英语1班','STUDENT','ACTIVE',NULL,'2026-03-29 07:42:44','2026-03-29 13:37:11'),(5,'stu004','$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG','赵六','13900001004',NULL,'FEMALE',NULL,'2024004','外国语学院','英语1班','STUDENT','ACTIVE',NULL,'2026-03-29 07:42:44','2026-03-29 13:37:11');
/*!40000 ALTER TABLE `sys_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user_role`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`,`role_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户角色关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user_role`
--

LOCK TABLES `sys_user_role` WRITE;
/*!40000 ALTER TABLE `sys_user_role` DISABLE KEYS */;
INSERT INTO `sys_user_role` (`id`, `user_id`, `role_id`, `create_time`) VALUES (6,1,1,'2026-03-29 13:15:26'),(7,2,3,'2026-03-29 13:15:26'),(8,3,3,'2026-03-29 13:15:26'),(9,4,3,'2026-03-29 13:15:26'),(10,5,3,'2026-03-29 13:15:26');
/*!40000 ALTER TABLE `sys_user_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary view structure for view `v_accommodation_info`
--

SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `v_accommodation_info` AS SELECT 
 1 AS `id`,
 1 AS `student_id`,
 1 AS `student_name`,
 1 AS `student_id_number`,
 1 AS `department`,
 1 AS `class_name`,
 1 AS `room_id`,
 1 AS `room_number`,
 1 AS `building_id`,
 1 AS `building_name`,
 1 AS `bed_number`,
 1 AS `check_in_date`,
 1 AS `expected_check_out_date`,
 1 AS `actual_check_out_date`,
 1 AS `status`,
 1 AS `deposit`*/;
SET character_set_client = @saved_cs_client;

--
-- Final view structure for view `v_accommodation_info`
--

/*!50001 DROP VIEW IF EXISTS `v_accommodation_info`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_accommodation_info` AS select `a`.`id` AS `id`,`a`.`student_id` AS `student_id`,`u`.`real_name` AS `student_name`,`u`.`student_id` AS `student_id_number`,`u`.`department` AS `department`,`u`.`class_name` AS `class_name`,`a`.`room_id` AS `room_id`,`r`.`room_number` AS `room_number`,`a`.`building_id` AS `building_id`,`b`.`building_name` AS `building_name`,`a`.`bed_number` AS `bed_number`,`a`.`check_in_date` AS `check_in_date`,`a`.`expected_check_out_date` AS `expected_check_out_date`,`a`.`actual_check_out_date` AS `actual_check_out_date`,`a`.`status` AS `status`,`a`.`deposit` AS `deposit` from (((`dorm_accommodation` `a` left join `sys_user` `u` on((`a`.`student_id` = `u`.`id`))) left join `dorm_room` `r` on((`a`.`room_id` = `r`.`id`))) left join `dorm_building` `b` on((`a`.`building_id` = `b`.`id`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-03-29 13:45:57
