-- MySQL dump 10.13  Distrib 9.1.0, for Win64 (x86_64)
--
-- Host: localhost    Database: online_judge
-- ------------------------------------------------------
-- Server version	9.1.0

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
-- Table structure for table `problem`
--

DROP TABLE IF EXISTS `problem`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `problem` (
  `id` int NOT NULL AUTO_INCREMENT,
  `subject_id` int NOT NULL,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `solution` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci,
  `difficulty` tinyint NOT NULL,
  `last_updated` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_problem_subject` (`subject_id`),
  CONSTRAINT `fk_problem_subject` FOREIGN KEY (`subject_id`) REFERENCES `subject` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `problem`
--

LOCK TABLES `problem` WRITE;
/*!40000 ALTER TABLE `problem` DISABLE KEYS */;
INSERT INTO `problem` VALUES (1,1,'Matrix Multiplication','Given two matrices, compute their product using linear algebra principles.','Use nested loops to multiply matrices',2,'2025-09-27 08:40:12'),(2,1,'Derivative Calculation','Find the derivative of f(x) = 3x^4 - 2x^3 + 5x - 7.','Apply power rule and linearity of differentiation',2,'2025-09-27 08:40:12'),(3,1,'Triangle Area','Calculate the area of a triangle given three vertices in coordinate geometry.','Use cross product formula',1,'2025-09-27 08:40:12'),(4,1,'Probability Distribution','A fair die is rolled twice. Find the probability that the sum is 7.','List all possible outcomes and count favorable cases',2,'2025-09-27 08:40:12'),(5,2,'Binary Search Tree','Implement insertion operation in a binary search tree.','Recursively find correct position based on node values',3,'2025-09-27 08:40:12'),(6,2,'Hash Table Implementation','Create a hash table with collision resolution using chaining.','Use array of linked lists for buckets',3,'2025-09-27 08:40:12'),(7,3,'Newton\'s Law Application','Calculate force given mass and acceleration using F = ma.','Direct application of Newton\'s second law',1,'2025-09-27 08:40:12'),(8,3,'Energy Conservation','A ball is dropped from height h. Find its velocity just before hitting ground.','Use conservation of mechanical energy principle',2,'2025-09-27 08:40:12'),(9,4,'Database Query Optimization','Optimize a SQL query with multiple joins and conditions.','Use indexing and query execution plan analysis',3,'2025-09-27 08:40:12'),(10,4,'Network Protocol Analysis','Explain the difference between TCP and UDP protocols.','Compare reliability, speed, and use cases',2,'2025-09-27 08:40:12'),(11,5,'Graph Traversal','Implement depth-first search algorithm for graph traversal.','Use recursive approach with visited array',2,'2025-09-27 08:40:12'),(12,5,'Dynamic Programming','Solve the classic knapsack problem using dynamic programming.','Build table bottom-up with optimal substructure',3,'2025-09-27 08:40:12'),(13,1,'Eigenvalue Calculation','Find eigenvalues of a 2x2 matrix [[3, 1], [0, 2]].','Solve characteristic polynomial equation',3,'2025-09-27 08:40:12'),(14,1,'Integral Evaluation','Evaluate the definite integral of x^2 from 0 to 3.','Use power rule for integration and fundamental theorem',2,'2025-09-27 08:40:12'),(15,1,'Circle Geometry','Find the equation of a circle passing through three given points.','Use general form and solve system of equations',3,'2025-09-27 08:40:12'),(16,2,'Data Structure Design','Design a stack that supports getMinimum() in O(1) time.','Use auxiliary stack to track minimums',3,'2025-09-27 08:40:12'),(17,2,'Memory Management','Explain garbage collection algorithms in programming languages.','Describe mark-and-sweep and reference counting',2,'2025-09-27 08:40:12'),(18,3,'Wave Equation','A wave has frequency 50 Hz and wavelength 2m. Find wave speed.','Use wave equation v = fλ',1,'2025-09-27 08:40:12'),(19,4,'Operating System Scheduling','Compare preemptive and non-preemptive scheduling algorithms.','Analyze turnaround time and response time',2,'2025-09-27 08:40:12'),(20,5,'Sorting Algorithm Analysis','Compare time complexity of quicksort, mergesort, and heapsort.','Analyze best, average, and worst case scenarios',2,'2025-09-27 08:40:12');
/*!40000 ALTER TABLE `problem` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `problem_tag`
--

DROP TABLE IF EXISTS `problem_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `problem_tag` (
  `problem_id` int NOT NULL,
  `tag_id` int NOT NULL,
  PRIMARY KEY (`problem_id`,`tag_id`),
  KEY `fk_problem_tag_tag` (`tag_id`),
  CONSTRAINT `fk_problem_tag_problem` FOREIGN KEY (`problem_id`) REFERENCES `problem` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_problem_tag_tag` FOREIGN KEY (`tag_id`) REFERENCES `tag` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `problem_tag`
--

LOCK TABLES `problem_tag` WRITE;
/*!40000 ALTER TABLE `problem_tag` DISABLE KEYS */;
INSERT INTO `problem_tag` VALUES (1,1),(13,1),(1,2),(2,2),(8,2),(14,2),(1,3),(2,3),(3,3),(7,3),(15,3),(18,3),(1,4),(2,4),(4,4),(9,5),(5,6),(9,6),(10,6),(12,6),(16,6),(17,6),(19,6),(20,6),(6,9),(11,9);
/*!40000 ALTER TABLE `problem_tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `subject`
--

DROP TABLE IF EXISTS `subject`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `subject` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `subject`
--

LOCK TABLES `subject` WRITE;
/*!40000 ALTER TABLE `subject` DISABLE KEYS */;
INSERT INTO `subject` VALUES (5,'Algorithms'),(2,'Computer Science'),(4,'Informatics'),(1,'Mathematics'),(3,'Physics');
/*!40000 ALTER TABLE `subject` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `submission`
--

DROP TABLE IF EXISTS `submission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `submission` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `problem_id` int NOT NULL,
  `your_solution` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `score` smallint DEFAULT '0',
  `submitted_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `status` enum('Pending','Completed') DEFAULT 'Pending',
  PRIMARY KEY (`id`),
  KEY `fk_submission_user` (`user_id`),
  KEY `fk_submission_problem` (`problem_id`),
  CONSTRAINT `fk_submission_problem` FOREIGN KEY (`problem_id`) REFERENCES `problem` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_submission_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `submission`
--

LOCK TABLES `submission` WRITE;
/*!40000 ALTER TABLE `submission` DISABLE KEYS */;
INSERT INTO `submission` VALUES (1,3,15,'function factorial(n) { return n <= 1 ? 1 : n * factorial(n-1); }',100,'2025-09-27 08:40:12','Completed'),(2,3,15,'function factorial(n) { if (n <= 1) { return 1; } else { return n * factorial(n - 1); } }',100,'2025-09-27 08:40:12','Completed'),(3,7,2,'f\'(x) = cos(x) * e^x + sin(x) * e^x',92,'2025-09-27 08:40:12','Completed'),(4,10,5,'Area of circle = πr², with r = 5, area = 78.54',85,'2025-09-27 08:40:12','Pending'),(5,12,8,'Favorable outcomes = 3, Total outcomes = 8, Probability = 3/8',90,'2025-09-27 08:40:12','Completed'),(6,8,19,'class LinkedList { add(val) { ... } remove(val) { ... } }',87,'2025-09-27 08:40:12','Completed'),(7,14,6,'Hash collision resolved by chaining using linked list',95,'2025-09-27 08:40:12','Completed'),(8,5,18,'Work = F × d = 10N × 2m = 20J',100,'2025-09-27 08:40:12','Completed'),(9,9,13,'Kinetic energy: KE = 1/2 mv², with m=2, v=4 → KE=16J',98,'2025-09-27 08:40:12','Completed'),(10,11,7,'SELECT department, AVG(salary) FROM employees GROUP BY department;',88,'2025-09-27 08:40:12','Completed'),(11,16,1,'HTTP is stateless, HTTPS is HTTP with encryption via SSL/TLS',100,'2025-09-27 08:40:12','Completed'),(12,19,9,'function bfs(graph, start) { let q=[start]; let visited=new Set(); visited.add(start); while(q.length){let node=q.shift(); for(let n of graph[node]) if(!visited.has(n)){visited.add(n); q.push(n);} } return visited; }',93,'2025-09-27 08:40:12','Pending'),(13,6,20,'DP solution: knapsack[i][w] = max(knapsack[i-1][w], value[i] + knapsack[i-1][w-weight[i]])',89,'2025-09-27 08:40:12','Completed'),(14,13,4,'Eigenvalues of [[4,1],[2,3]]: solve det(A-λI)=0 → λ=5,2',95,'2025-09-27 08:40:12','Completed'),(15,2,17,'∫₀² (2x+1) dx = [x² + x]₀² = 4 + 2 = 6',100,'2025-09-27 08:40:12','Completed'),(16,20,11,'Equation of line through (1,2) and (3,6): slope=2, y-2=2(x-1)',90,'2025-09-27 08:40:12','Completed'),(17,4,14,'class Queue { constructor(){this.arr=[];} enqueue(x){this.arr.push(x);} dequeue(){return this.arr.shift();} }',97,'2025-09-27 08:40:12','Completed'),(18,18,12,'Polymorphism allows objects of different classes to be treated through same interface',91,'2025-09-27 08:40:12','Pending'),(19,15,3,'v = d/t = 120m / 10s = 12 m/s',100,'2025-09-27 08:40:12','Completed'),(20,1,16,'Round robin scheduling gives each process equal CPU time in cycles',93,'2025-09-27 08:40:12','Completed'),(21,17,10,'Binary search: O(log n), Linear search: O(n)',96,'2025-09-27 08:40:12','Completed');
/*!40000 ALTER TABLE `submission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tag`
--

DROP TABLE IF EXISTS `tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tag` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tag`
--

LOCK TABLES `tag` WRITE;
/*!40000 ALTER TABLE `tag` DISABLE KEYS */;
INSERT INTO `tag` VALUES (2,'Calculus'),(8,'Combinatorics'),(6,'Discrete Math'),(3,'Geometry'),(9,'Graph Theory'),(1,'Linear Algebra'),(7,'Number Theory'),(4,'Probability'),(5,'Statistics'),(10,'Trigonometry');
/*!40000 ALTER TABLE `tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET ascii COLLATE ascii_general_ci NOT NULL,
  `password` varchar(255) CHARACTER SET ascii COLLATE ascii_general_ci NOT NULL,
  `email` varchar(100) CHARACTER SET ascii COLLATE ascii_general_ci NOT NULL,
  `full_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `display_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `avatar_url` varchar(255) CHARACTER SET ascii COLLATE ascii_general_ci DEFAULT NULL,
  `role` enum('ADMIN','MODERATOR','USER') DEFAULT 'USER',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'admin','$2b$10$examplehash','admin@judge.com','Admin User','Admin','https://example.com/avatar1.jpg','ADMIN','2025-09-27 08:40:12'),(2,'moderator1','$2b$10$examplehash','mod1@judge.com','Moderator One','Mod1','https://example.com/avatar2.jpg','MODERATOR','2025-09-27 08:40:12'),(3,'math_student','$2b$10$examplehash','math1@judge.com','Math Student','MathLover','https://example.com/avatar3.jpg','USER','2025-09-27 08:40:12'),(4,'cs_student','$2b$10$examplehash','cs1@judge.com','CS Student','CodeMaster','https://example.com/avatar4.jpg','USER','2025-09-27 08:40:12'),(5,'physics_student','$2b$10$examplehash','physics1@judge.com','Physics Student','PhysicsFan','https://example.com/avatar5.jpg','USER','2025-09-27 08:40:12'),(6,'informatics_student','$2b$10$examplehash','info1@judge.com','Informatics Student','InfoPro','https://example.com/avatar6.jpg','USER','2025-09-27 08:40:12'),(7,'algo_student','$2b$10$examplehash','algo1@judge.com','Algorithms Student','AlgoExpert','https://example.com/avatar7.jpg','USER','2025-09-27 08:40:12'),(8,'math_pro','$2b$10$examplehash','math2@judge.com','Math Pro','MathGenius','https://example.com/avatar8.jpg','USER','2025-09-27 08:40:12'),(9,'cs_pro','$2b$10$examplehash','cs2@judge.com','CS Pro','CodingNinja','https://example.com/avatar9.jpg','USER','2025-09-27 08:40:12'),(10,'physics_pro','$2b$10$examplehash','physics2@judge.com','Physics Pro','QuantumMaster','https://example.com/avatar10.jpg','USER','2025-09-27 08:40:12'),(11,'info_pro','$2b$10$examplehash','info2@judge.com','Informatics Pro','DataScientist','https://example.com/avatar11.jpg','USER','2025-09-27 08:40:12'),(12,'algo_pro','$2b$10$examplehash','algo2@judge.com','Algorithms Pro','AlgorithmWizard','https://example.com/avatar12.jpg','USER','2025-09-27 08:40:12'),(13,'math_expert','$2b$10$examplehash','math3@judge.com','Math Expert','TheoremProver','https://example.com/avatar13.jpg','USER','2025-09-27 08:40:12'),(14,'cs_expert','$2b$10$examplehash','cs3@judge.com','CS Expert','SystemArchitect','https://example.com/avatar14.jpg','USER','2025-09-27 08:40:12'),(15,'physics_expert','$2b$10$examplehash','physics3@judge.com','Physics Expert','RelativityExpert','https://example.com/avatar15.jpg','USER','2025-09-27 08:40:12'),(16,'info_expert','$2b$10$examplehash','info3@judge.com','Informatics Expert','BigDataAnalyst','https://example.com/avatar16.jpg','USER','2025-09-27 08:40:12'),(17,'algo_expert','$2b$10$examplehash','algo3@judge.com','Algorithms Expert','ComplexityMaster','https://example.com/avatar17.jpg','USER','2025-09-27 08:40:12'),(18,'math_teacher','$2b$10$examplehash','math_teacher@judge.com','Math Teacher','ProfMath','https://example.com/avatar18.jpg','USER','2025-09-27 08:40:12'),(19,'cs_teacher','$2b$10$examplehash','cs_teacher@judge.com','CS Teacher','ProfCode','https://example.com/avatar19.jpg','USER','2025-09-27 08:40:12'),(20,'student_new','$2b$10$examplehash','new_student@judge.com','New Student','Beginner','https://example.com/avatar20.jpg','USER','2025-09-27 08:40:12'),(21,'nguyenvanc','$2a$10$J.S86Kz2owHq0ZpIaeI5Z.qgYUKRxk5PkxhyyEQ04Fn34InMnINlO','duongducanh06@gmail.com',NULL,NULL,NULL,'USER',NULL),(22,'anh2','$2a$10$46N7fN6AweiFKa2mDXYtNuJO7mApH2mPdteViIxqwjGglrIxp1knu','top2chuyentinsupham@gmail.com','Đức Anh','Đức Anh',NULL,'USER',NULL),(23,'anh','$2a$10$T3VlXom2sCO3GCO4njnpyOxxdBH6JoHTkS4RXBaW3brajONNmNGhO','top1chuyentinsupham@gmail.com','Dương Đức Anh','Dương Đức Anh',NULL,'USER',NULL);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-10-06  9:32:25
