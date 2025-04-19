-- --------------------------------------------------------
-- Máy chủ:                      127.0.0.1
-- Server version:               8.0.30 - MySQL Community Server - GPL
-- Server OS:                    Win64
-- HeidiSQL Phiên bản:           12.1.0.6537
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Dumping database structure for quanlybanhang
CREATE DATABASE IF NOT EXISTS `quanlybanhang` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `quanlybanhang`;

-- Dumping structure for table quanlybanhang.categories
CREATE TABLE IF NOT EXISTS `categories` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `description` text,
  `parent_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  KEY `parent_id` (`parent_id`),
  CONSTRAINT `categories_ibfk_1` FOREIGN KEY (`parent_id`) REFERENCES `categories` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Data exporting was unselected.

-- Dumping structure for table quanlybanhang.customers
CREATE TABLE IF NOT EXISTS `customers` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `phone` varchar(20) NOT NULL,
  `email` varchar(255) DEFAULT NULL,
  `address` text,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `phone` (`phone`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Data exporting was unselected.

-- Dumping structure for table quanlybanhang.orders
CREATE TABLE IF NOT EXISTS `orders` (
  `id` int NOT NULL AUTO_INCREMENT,
  `employee_id` int NOT NULL,
  `total_price` decimal(18,2) NOT NULL,
  `order_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `status` enum('Đang xử lý','Đang Giao Hàng','Hoàn thành','Đã hủy') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `customer_id` int DEFAULT NULL,
  `shipping_fee` decimal(18,2) NOT NULL DEFAULT '0.00',
  `note` text,
  PRIMARY KEY (`id`),
  KEY `employee_id` (`employee_id`),
  KEY `fk_orders_customers` (`customer_id`),
  CONSTRAINT `fk_orders_customers` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`id`) ON DELETE CASCADE,
  CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`employee_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=306 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Data exporting was unselected.

-- Dumping structure for table quanlybanhang.order_details
CREATE TABLE IF NOT EXISTS `order_details` (
  `id` int NOT NULL AUTO_INCREMENT,
  `order_id` int NOT NULL,
  `product_id` int NOT NULL,
  `quantity` int NOT NULL,
  `price` decimal(18,2) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `order_id` (`order_id`),
  KEY `product_id` (`product_id`),
  CONSTRAINT `order_details_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`),
  CONSTRAINT `order_details_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=179 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Data exporting was unselected.

-- Dumping structure for view quanlybanhang.order_summary
-- Creating temporary table to overcome VIEW dependency errors
CREATE TABLE `order_summary` (
	`order_id` INT(10) NOT NULL,
	`order_date` TIMESTAMP NULL,
	`status` ENUM('Đang xử lý','Đang Giao Hàng','Hoàn thành','Đã hủy') NOT NULL COLLATE 'utf8mb4_0900_ai_ci',
	`total_price` DECIMAL(18,2) NOT NULL,
	`shipping_fee` DECIMAL(18,2) NOT NULL,
	`employee_id` INT(10) NOT NULL,
	`note` TEXT NULL COLLATE 'utf8mb4_0900_ai_ci',
	`customer_id` INT(10) NOT NULL,
	`customer_name` VARCHAR(255) NOT NULL COLLATE 'utf8mb4_0900_ai_ci',
	`product_ids` TEXT NULL COLLATE 'utf8mb4_0900_ai_ci',
	`product_names` TEXT NULL COLLATE 'utf8mb4_0900_ai_ci',
	`product_images` TEXT NULL COLLATE 'utf8mb4_0900_ai_ci',
	`product_quantities` TEXT NULL COLLATE 'utf8mb4_0900_ai_ci',
	`product_prices` TEXT NULL COLLATE 'utf8mb4_0900_ai_ci'
) ENGINE=MyISAM;

-- Dumping structure for table quanlybanhang.products
CREATE TABLE IF NOT EXISTS `products` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `category_id` int DEFAULT NULL,
  `description` text,
  `price` decimal(18,2) NOT NULL,
  `stock_quantity` int NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `status` enum('Còn hàng','Hết hàng','Ngừng Bán') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'Còn hàng',
  `image_url` text,
  `specifications` json DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_category` (`category_id`),
  CONSTRAINT `fk_category` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`),
  CONSTRAINT `products_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=1351 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Data exporting was unselected.

-- Dumping structure for table quanlybanhang.reports
CREATE TABLE IF NOT EXISTS `reports` (
  `report_id` int NOT NULL AUTO_INCREMENT,
  `report_type` varchar(50) DEFAULT NULL,
  `generated_date` datetime DEFAULT NULL,
  `total_revenue` decimal(18,2) DEFAULT NULL,
  `total_orders` int DEFAULT NULL,
  `best_selling_product_id` int DEFAULT NULL,
  PRIMARY KEY (`report_id`),
  KEY `best_selling_product_id` (`best_selling_product_id`),
  CONSTRAINT `reports_ibfk_1` FOREIGN KEY (`best_selling_product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Data exporting was unselected.

-- Dumping structure for table quanlybanhang.users
CREATE TABLE IF NOT EXISTS `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `full_name` varchar(100) DEFAULT NULL,
  `email` varchar(100) NOT NULL,
  `phone` varchar(15) DEFAULT NULL,
  `role` enum('Admin','Nhân Viên','Nhân viên kho','Nhân viên thu ngân','Nhân viên bán hàng') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `status` enum('Lock','Unlock') NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=78 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Data exporting was unselected.

-- Dumping structure for table quanlybanhang.warehouse_transactions
CREATE TABLE IF NOT EXISTS `warehouse_transactions` (
  `id` int NOT NULL AUTO_INCREMENT,
  `product_id` int NOT NULL,
  `quantity` int NOT NULL,
  `unit_price` decimal(18,2) NOT NULL,
  `type` enum('Nhập Kho','Xuất Kho','Kiểm Kho') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `note` text,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `inventory_check_date` timestamp NULL DEFAULT NULL,
  `inventory_note` text,
  `transaction_code` varchar(20) DEFAULT NULL,
  `created_by` int DEFAULT NULL,
  `excess_quantity` int DEFAULT '0',
  `deficient_quantity` int DEFAULT '0',
  `missing` int DEFAULT '0',
  `inventory_status` enum('Đã xác nhận','Có chênh lệch','Chờ xác nhận') DEFAULT NULL,
  `source_transaction_code` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `product_id` (`product_id`),
  KEY `fk_created_by` (`created_by`),
  CONSTRAINT `fk_created_by` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `warehouse_transactions_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=290 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Data exporting was unselected.

-- Dumping structure for view quanlybanhang.warehouse_transaction_details
-- Creating temporary table to overcome VIEW dependency errors
CREATE TABLE `warehouse_transaction_details` (
	`id` INT(10) NOT NULL,
	`product_id` INT(10) NOT NULL,
	`quantity` INT(10) NOT NULL,
	`unit_price` DECIMAL(18,2) NOT NULL,
	`type` ENUM('Nhập Kho','Xuất Kho','Kiểm Kho') NOT NULL COLLATE 'utf8mb4_0900_ai_ci',
	`note` TEXT NULL COLLATE 'utf8mb4_0900_ai_ci',
	`created_at` TIMESTAMP NULL,
	`updated_at` TIMESTAMP NULL,
	`transaction_code` VARCHAR(20) NULL COLLATE 'utf8mb4_0900_ai_ci',
	`created_by` INT(10) NULL,
	`excess_quantity` INT(10) NULL,
	`deficient_quantity` INT(10) NULL,
	`missing` INT(10) NULL,
	`inventory_check_date` TIMESTAMP NULL,
	`inventory_note` TEXT NULL COLLATE 'utf8mb4_0900_ai_ci',
	`inventory_status` ENUM('Đã xác nhận','Có chênh lệch','Chờ xác nhận') NULL COLLATE 'utf8mb4_0900_ai_ci',
	`product_name` VARCHAR(255) NOT NULL COLLATE 'utf8mb4_0900_ai_ci',
	`sell_price` DECIMAL(18,2) NOT NULL,
	`category_name` VARCHAR(100) NOT NULL COLLATE 'utf8mb4_0900_ai_ci',
	`created_by_name` VARCHAR(100) NULL COLLATE 'utf8mb4_0900_ai_ci'
) ENGINE=MyISAM;

-- Dumping structure for view quanlybanhang.order_summary
-- Removing temporary table and create final VIEW structure
DROP TABLE IF EXISTS `order_summary`;
CREATE ALGORITHM=UNDEFINED SQL SECURITY DEFINER VIEW `order_summary` AS select `o`.`id` AS `order_id`,`o`.`order_date` AS `order_date`,`o`.`status` AS `status`,`o`.`total_price` AS `total_price`,`o`.`shipping_fee` AS `shipping_fee`,`o`.`employee_id` AS `employee_id`,`o`.`note` AS `note`,`c`.`id` AS `customer_id`,`c`.`name` AS `customer_name`,group_concat(`p`.`id` order by `od`.`id` ASC separator ',') AS `product_ids`,group_concat(replace(`p`.`name`,',',' ') order by `od`.`id` ASC separator ', ') AS `product_names`,group_concat(`p`.`image_url` order by `od`.`id` ASC separator ', ') AS `product_images`,group_concat(`od`.`quantity` order by `od`.`id` ASC separator ',') AS `product_quantities`,group_concat(`od`.`price` order by `od`.`id` ASC separator ',') AS `product_prices` from (((`orders` `o` join `customers` `c` on((`o`.`customer_id` = `c`.`id`))) join `order_details` `od` on((`o`.`id` = `od`.`order_id`))) join `products` `p` on((`od`.`product_id` = `p`.`id`))) group by `o`.`id`;

-- Dumping structure for view quanlybanhang.warehouse_transaction_details
-- Removing temporary table and create final VIEW structure
DROP TABLE IF EXISTS `warehouse_transaction_details`;
CREATE ALGORITHM=UNDEFINED SQL SECURITY DEFINER VIEW `warehouse_transaction_details` AS select `wt`.`id` AS `id`,`wt`.`product_id` AS `product_id`,`wt`.`quantity` AS `quantity`,`wt`.`unit_price` AS `unit_price`,`wt`.`type` AS `type`,`wt`.`note` AS `note`,`wt`.`created_at` AS `created_at`,`wt`.`updated_at` AS `updated_at`,`wt`.`transaction_code` AS `transaction_code`,`wt`.`created_by` AS `created_by`,`wt`.`excess_quantity` AS `excess_quantity`,`wt`.`deficient_quantity` AS `deficient_quantity`,`wt`.`missing` AS `missing`,`wt`.`inventory_check_date` AS `inventory_check_date`,`wt`.`inventory_note` AS `inventory_note`,`wt`.`inventory_status` AS `inventory_status`,`p`.`name` AS `product_name`,`p`.`price` AS `sell_price`,`c`.`name` AS `category_name`,`u`.`full_name` AS `created_by_name` from (((`warehouse_transactions` `wt` join `products` `p` on((`wt`.`product_id` = `p`.`id`))) join `categories` `c` on((`p`.`category_id` = `c`.`id`))) join `users` `u` on((`wt`.`created_by` = `u`.`id`)));

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
