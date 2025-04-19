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

-- Dumping data for table quanlybanhang.categories: ~19 rows (approximately)
INSERT INTO `categories` (`id`, `name`, `description`, `parent_id`) VALUES
	(1, 'Tivi', '8k', 3),
	(2, 'Tivi samsung', 'siêu đẹp', NULL),
	(3, 'điện thoại', 'màn hình đỉnh', NULL),
	(4, 'Iphone', 'cc', 3),
	(5, 'quang', 'saddasdasasd', NULL),
	(6, '1', '1', 2),
	(8, '2', '2', NULL),
	(9, 'haiem', '1', 3),
	(10, 'asss', '', NULL),
	(11, 'ưqeewq', 'ưeqweqwqe', NULL),
	(12, '231', '213', 2),
	(13, '23', '123', 3),
	(14, '312', '213', NULL),
	(15, 'dâsd', 'adsdas', NULL),
	(16, 'kk', '21321', NULL),
	(17, '213321', '213231', NULL),
	(18, 'toiyeuem', '231', NULL),
	(19, 'yeutinhyeu don gian', '123', NULL),
	(20, 'hahahahavichinh', '321321213', 2);

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

-- Dumping data for table quanlybanhang.customers: ~4 rows (approximately)
INSERT INTO `customers` (`id`, `name`, `phone`, `email`, `address`, `created_at`) VALUES
	(30, 'abc', '0934431316', 'quang.td.2430@aptechlearning.edu.vn', 'hà trung, hà nội', '2025-04-16 20:22:01'),
	(31, 'haianh', '21321', '213321', '231321213', '2025-04-18 03:22:46'),
	(32, '123321', '213321', '231213', '231321', '2025-04-18 03:22:53'),
	(33, 'áddas', '21323', 'đấ', 'áddasdas', '2025-04-18 03:24:32');

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

-- Dumping data for table quanlybanhang.orders: ~5 rows (approximately)
INSERT INTO `orders` (`id`, `employee_id`, `total_price`, `order_date`, `status`, `customer_id`, `shipping_fee`, `note`) VALUES
	(301, 44, 1212.00, '2025-04-17 17:00:00', 'Đang xử lý', 31, 22222.00, 'abc'),
	(302, 44, 22.00, '2025-04-17 17:00:00', 'Đang xử lý', 31, 0.00, 'aaaa'),
	(303, 44, 1212.00, '2025-04-17 17:00:00', 'Hoàn thành', 31, 0.00, 'ádasdasd'),
	(304, 44, 65656.00, '2025-04-18 17:00:00', 'Đang xử lý', 31, 222222.00, 'ấdasad2'),
	(305, 44, 23132.00, '2025-04-18 17:00:00', 'Hoàn thành', 30, 0.00, 'saddasdas');

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

-- Dumping data for table quanlybanhang.order_details: ~0 rows (approximately)

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

-- Dumping data for table quanlybanhang.products: ~1 rows (approximately)
INSERT INTO `products` (`id`, `name`, `category_id`, `description`, `price`, `stock_quantity`, `created_at`, `updated_at`, `status`, `image_url`, `specifications`) VALUES
	(1350, 'a', 1, 'a', 2000000.00, 161, '2025-04-19 07:35:42', '2025-04-19 12:28:02', 'Còn hàng', 'a', '{"camera": "ád", "battery": "ád", "features": "ád", "configMemory": "a", "connectivity": "ád", "designMaterials": "sad"}');

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

-- Dumping data for table quanlybanhang.reports: ~0 rows (approximately)

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

-- Dumping data for table quanlybanhang.users: ~31 rows (approximately)
INSERT INTO `users` (`id`, `username`, `password`, `full_name`, `email`, `phone`, `role`, `status`) VALUES
	(1, 'quang', '$argon2id$v=19$m=65536,t=2,p=1$wJSr5Kr/QtzznbQZtjlJfQ$/HvdHMAdftWIucNLEESbz0q4PxiBieVqLMBsVCS9zGM', 'quang', 'haianh@', '123456789', 'Admin', 'Unlock'),
	(42, 'haianh', '$argon2id$v=19$m=65536,t=3,p=2$4ATl4ZKZsKiIyRXXnrTv8Q$0m/XoqZnTbvio7O9/5SSgTdLQ5SOTxEtXKbUdK+q2vY', 'haianh', 'abc@', '123456789', 'Nhân viên kho', 'Unlock'),
	(43, 'quang1', '$argon2id$v=19$m=65536,t=3,p=2$uoG6Vfzjmp02oyMGki7D2w$80hikT928hOMYCQ7fxcth++oa+gIoPXHqPCcM+DbgKQ', 'quang1', 'quang1@', '12345678', 'Nhân viên thu ngân', 'Unlock'),
	(44, 'lon1', '$argon2id$v=19$m=65536,t=3,p=2$MdXAS1sq74j9fHfkbB3pdw$fhzU/X2Ds8mbjnrRkdyDlb4l6/ZVnFBmmy4cGB4QWeM', 'huhu', '1@', '123123312', 'Nhân viên bán hàng', 'Unlock'),
	(45, '213', '$argon2id$v=19$m=65536,t=3,p=2$OKty2fubAtFmgq4EtcfCsQ$Jd5neaMe5x3/dEilOhVqugF21GhTk59OdGQbi0aNMWY', '213', '231@', '231231231', 'Nhân Viên', 'Unlock'),
	(46, '231', '$argon2id$v=19$m=65536,t=3,p=2$tUEruPsfQxTDVrslY//Qyw$Cj/0ouq1gHRx5ILreJIlvec1cqK31QdO/xMrKtXlwb8', '2312', '231@', '231231231', 'Nhân Viên', 'Unlock'),
	(50, 'quang2', '$argon2id$v=19$m=65536,t=3,p=2$uoG6Vfzjmp02oyMGki7D2w$80hikT928hOMYCQ7fxcth++oa+gIoPXHqPCcM+DbgKQ', 'quang1s', 'quang1@', '12345678', 'Nhân viên thu ngân', 'Unlock'),
	(51, 'quang3', '$argon2id$v=19$m=65536,t=3,p=2$uoG6Vfzjmp02oyMGki7D2w$80hikT928hOMYCQ7fxcth++oa+gIoPXHqPCcM+DbgKQ', 'quang1a', 'quang1@', '12345678', 'Nhân viên thu ngân', 'Unlock'),
	(52, 'quang4', '$argon2id$v=19$m=65536,t=3,p=2$uoG6Vfzjmp02oyMGki7D2w$80hikT928hOMYCQ7fxcth++oa+gIoPXHqPCcM+DbgKQ', 'quang1a', 'quang1@', '12345678', 'Nhân viên thu ngân', 'Unlock'),
	(53, 'quang5', '$argon2id$v=19$m=65536,t=3,p=2$uoG6Vfzjmp02oyMGki7D2w$80hikT928hOMYCQ7fxcth++oa+gIoPXHqPCcM+DbgKQ', 'quang1b', 'quang1@', '12345678', 'Nhân viên thu ngân', 'Unlock'),
	(54, 'quang6', '$argon2id$v=19$m=65536,t=3,p=2$uoG6Vfzjmp02oyMGki7D2w$80hikT928hOMYCQ7fxcth++oa+gIoPXHqPCcM+DbgKQ', 'quang1c', 'quang1@', '12345678', 'Nhân viên thu ngân', 'Unlock'),
	(55, 'quang7', '$argon2id$v=19$m=65536,t=3,p=2$uoG6Vfzjmp02oyMGki7D2w$80hikT928hOMYCQ7fxcth++oa+gIoPXHqPCcM+DbgKQ', 'quang1b', 'quang1@', '12345678', 'Nhân viên thu ngân', 'Unlock'),
	(56, 'quang8', '$argon2id$v=19$m=65536,t=3,p=2$uoG6Vfzjmp02oyMGki7D2w$80hikT928hOMYCQ7fxcth++oa+gIoPXHqPCcM+DbgKQ', 'quang1n', 'quang1@', '12345678', 'Nhân viên thu ngân', 'Unlock'),
	(57, 'quang9', '$argon2id$v=19$m=65536,t=3,p=2$uoG6Vfzjmp02oyMGki7D2w$80hikT928hOMYCQ7fxcth++oa+gIoPXHqPCcM+DbgKQ', 'quang1qư', 'quang1@', '12345678', 'Nhân viên thu ngân', 'Unlock'),
	(58, 'quang11', '$argon2id$v=19$m=65536,t=3,p=2$uoG6Vfzjmp02oyMGki7D2w$80hikT928hOMYCQ7fxcth++oa+gIoPXHqPCcM+DbgKQ', 'quang1ewq', 'quang1@', '12345678', 'Nhân viên thu ngân', 'Unlock'),
	(59, 'quang1s', '$argon2id$v=19$m=65536,t=3,p=2$uoG6Vfzjmp02oyMGki7D2w$80hikT928hOMYCQ7fxcth++oa+gIoPXHqPCcM+DbgKQ', 'quang1ưqe', 'quang1@', '12345678', 'Nhân viên thu ngân', 'Unlock'),
	(62, 'quang56', '$argon2id$v=19$m=65536,t=3,p=2$uoG6Vfzjmp02oyMGki7D2w$80hikT928hOMYCQ7fxcth++oa+gIoPXHqPCcM+DbgKQ', 'quang1s', 'quang1@', '12345678', 'Nhân viên thu ngân', 'Unlock'),
	(64, 'quang565', '$argon2id$v=19$m=65536,t=3,p=2$uoG6Vfzjmp02oyMGki7D2w$80hikT928hOMYCQ7fxcth++oa+gIoPXHqPCcM+DbgKQ', 'quang1s', 'quang1@', '12345678', 'Nhân viên thu ngân', 'Unlock'),
	(65, 'quang53', '$argon2id$v=19$m=65536,t=3,p=2$uoG6Vfzjmp02oyMGki7D2w$80hikT928hOMYCQ7fxcth++oa+gIoPXHqPCcM+DbgKQ', 'quang1a', 'quang1@', '12345678', 'Nhân viên thu ngân', 'Unlock'),
	(66, 'quang45', '$argon2id$v=19$m=65536,t=3,p=2$uoG6Vfzjmp02oyMGki7D2w$80hikT928hOMYCQ7fxcth++oa+gIoPXHqPCcM+DbgKQ', 'quang1a', 'quang1@', '12345678', 'Nhân viên thu ngân', 'Unlock'),
	(67, 'quang5312', '$argon2id$v=19$m=65536,t=3,p=2$uoG6Vfzjmp02oyMGki7D2w$80hikT928hOMYCQ7fxcth++oa+gIoPXHqPCcM+DbgKQ', 'quang1b', 'quang1@', '12345678', 'Nhân viên thu ngân', 'Unlock'),
	(68, '2134', '$argon2id$v=19$m=65536,t=3,p=2$uoG6Vfzjmp02oyMGki7D2w$80hikT928hOMYCQ7fxcth++oa+gIoPXHqPCcM+DbgKQ', 'quang1c', 'quang1@', '12345678', 'Nhân viên thu ngân', 'Unlock'),
	(69, 'qu435ang7', '$argon2id$v=19$m=65536,t=3,p=2$uoG6Vfzjmp02oyMGki7D2w$80hikT928hOMYCQ7fxcth++oa+gIoPXHqPCcM+DbgKQ', 'quang1b', 'quang1@', '12345678', 'Nhân viên thu ngân', 'Unlock'),
	(70, '345', '$argon2id$v=19$m=65536,t=3,p=2$uoG6Vfzjmp02oyMGki7D2w$80hikT928hOMYCQ7fxcth++oa+gIoPXHqPCcM+DbgKQ', 'quang1n', 'quang1@', '12345678', 'Nhân viên thu ngân', 'Unlock'),
	(71, 'quaretng9', '$argon2id$v=19$m=65536,t=3,p=2$uoG6Vfzjmp02oyMGki7D2w$80hikT928hOMYCQ7fxcth++oa+gIoPXHqPCcM+DbgKQ', 'quang1qư', 'quang1@', '12345678', 'Nhân viên thu ngân', 'Unlock'),
	(72, 'ret', '$argon2id$v=19$m=65536,t=3,p=2$uoG6Vfzjmp02oyMGki7D2w$80hikT928hOMYCQ7fxcth++oa+gIoPXHqPCcM+DbgKQ', 'quang1ewq', 'quang1@', '12345678', 'Nhân viên thu ngân', 'Unlock'),
	(73, 'quẻtang1sg', '$argon2id$v=19$m=65536,t=3,p=2$uoG6Vfzjmp02oyMGki7D2w$80hikT928hOMYCQ7fxcth++oa+gIoPXHqPCcM+DbgKQ', 'quang1ưqe', 'quang1@', '12345678', 'Nhân viên thu ngân', 'Unlock'),
	(74, 'quareng1sf', '$argon2id$v=19$m=65536,t=3,p=2$uoG6Vfzjmp02oyMGki7D2w$80hikT928hOMYCQ7fxcth++oa+gIoPXHqPCcM+DbgKQ', 'quang1ewsq', 'quang1@', '12345678', 'Nhân viên thu ngân', 'Unlock'),
	(75, 'quaẻtng1sh', '$argon2id$v=19$m=65536,t=3,p=2$uoG6Vfzjmp02oyMGki7D2w$80hikT928hOMYCQ7fxcth++oa+gIoPXHqPCcM+DbgKQ', 'quang1ưsqe', 'quang1@', '12345678', 'Nhân viên thu ngân', 'Unlock'),
	(76, 'quaẻtng1sghj', '$argon2id$v=19$m=65536,t=3,p=2$uoG6Vfzjmp02oyMGki7D2w$80hikT928hOMYCQ7fxcth++oa+gIoPXHqPCcM+DbgKQ', 'quang1ưecq', 'quang1@', '12345678', 'Nhân viên thu ngân', 'Unlock'),
	(77, 'quaẻtng1shjg', '$argon2id$v=19$m=65536,t=3,p=2$uoG6Vfzjmp02oyMGki7D2w$80hikT928hOMYCQ7fxcth++oa+gIoPXHqPCcM+DbgKQ', 'quang1ewr', 'quang1@', '12345678', 'Nhân viên thu ngân', 'Unlock');

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
  PRIMARY KEY (`id`),
  KEY `product_id` (`product_id`),
  KEY `fk_created_by` (`created_by`),
  CONSTRAINT `fk_created_by` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `warehouse_transactions_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=284 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table quanlybanhang.warehouse_transactions: ~20 rows (approximately)
INSERT INTO `warehouse_transactions` (`id`, `product_id`, `quantity`, `unit_price`, `type`, `note`, `created_at`, `updated_at`, `inventory_check_date`, `inventory_note`, `transaction_code`, `created_by`, `excess_quantity`, `deficient_quantity`, `missing`, `inventory_status`) VALUES
	(263, 1350, 50, 500000.00, 'Nhập Kho', 'test1', '2025-04-19 07:35:59', '2025-04-19 07:35:59', NULL, NULL, 'NK-20250419-E093A', 42, 0, 0, 0, NULL),
	(264, 1350, 50, 1000000.00, 'Nhập Kho', '', '2025-04-19 07:36:20', '2025-04-19 07:36:20', NULL, NULL, 'NK-20250419-6D8E2', 42, 0, 0, 0, NULL),
	(265, 1350, 50, 0.00, 'Xuất Kho', '', '2025-04-19 07:39:04', '2025-04-19 07:39:04', NULL, NULL, 'XK-20250419-D607D', 42, 0, 0, 0, NULL),
	(266, 1350, 20, 0.00, 'Xuất Kho', '', '2025-04-19 07:39:31', '2025-04-19 07:39:31', NULL, NULL, 'XK-20250419-BB8DE', 42, 0, 0, 0, NULL),
	(267, 1350, 10, 2000000.00, 'Nhập Kho', 'abc', '2025-04-19 07:39:50', '2025-04-19 07:39:49', NULL, NULL, 'NK-20250419-4BBCA', 42, 0, 0, 0, NULL),
	(268, 1350, 10, 0.00, 'Xuất Kho', '', '2025-04-19 07:40:13', '2025-04-19 07:40:12', NULL, NULL, 'XK-20250419-10611', 42, 0, 0, 0, NULL),
	(269, 1350, 30, 0.00, 'Xuất Kho', '', '2025-04-19 07:40:13', '2025-04-19 07:40:12', NULL, NULL, 'XK-20250419-10611', 42, 0, 0, 0, NULL),
	(270, 1350, 10, 5000000.00, 'Nhập Kho', '', '2025-04-19 07:40:37', '2025-04-19 07:40:37', NULL, NULL, 'NK-20250419-C9EA0', 42, 0, 0, 0, NULL),
	(271, 1350, 10, 0.00, 'Xuất Kho', 'áddsadaad', '2025-04-19 07:41:05', '2025-04-19 07:41:04', NULL, NULL, 'XK-20250419-141FC', 42, 0, 0, 0, NULL),
	(272, 1350, 50, 2000000.00, 'Nhập Kho', '', '2025-04-19 07:41:16', '2025-04-19 07:41:16', NULL, NULL, 'NK-20250419-AAE3C', 42, 0, 0, 0, NULL),
	(273, 1350, 10, 0.00, 'Xuất Kho', '', '2025-04-19 07:41:32', '2025-04-19 07:41:32', NULL, NULL, 'XK-20250419-3F1F0', 42, 0, 0, 0, NULL),
	(274, 1350, 5, 0.00, 'Xuất Kho', '', '2025-04-19 07:41:32', '2025-04-19 07:41:32', NULL, NULL, 'XK-20250419-3F1F0', 42, 0, 0, 0, NULL),
	(275, 1350, 50, 21321321.00, 'Nhập Kho', '', '2025-04-19 08:32:16', '2025-04-19 08:32:16', NULL, NULL, 'NK-20250419-C211D', 42, 0, 0, 0, NULL),
	(276, 1350, 1, 0.00, 'Xuất Kho', 'abc', '2025-04-19 10:58:13', '2025-04-19 10:58:12', NULL, NULL, 'XK-20250419-2F6F1', 42, 0, 0, 0, NULL),
	(277, 1350, 1, 0.00, 'Xuất Kho', '1', '2025-04-19 10:58:34', '2025-04-19 10:58:34', NULL, NULL, 'XK-20250419-4455A', 42, 0, 0, 0, NULL),
	(278, 1350, 1, 0.00, 'Xuất Kho', 'abc', '2025-04-19 11:19:29', '2025-04-19 11:19:29', NULL, NULL, 'XK-20250419-DF86D', 42, 0, 0, 0, NULL),
	(280, 1350, 1, 0.00, 'Xuất Kho', 'â', '2025-04-19 11:51:19', '2025-04-19 11:51:18', NULL, NULL, 'XK-20250419-AD5E6', 42, 0, 0, 0, NULL),
	(281, 1350, 10, 0.00, 'Xuất Kho', 'saddasdasdas', '2025-04-19 11:51:45', '2025-04-19 11:51:45', NULL, NULL, 'XK-20250419-594D0', 42, 0, 0, 0, NULL),
	(282, 1350, 10, 0.00, 'Xuất Kho', 'saddasdasdas', '2025-04-19 11:51:45', '2025-04-19 11:51:45', NULL, NULL, 'XK-20250419-594D0', 42, 0, 0, 0, NULL),
	(283, 1350, 100, 3000000.00, 'Nhập Kho', 'abc', '2025-04-19 12:28:02', '2025-04-19 12:28:02', NULL, NULL, 'NK-20250419-D0CFE', 42, 0, 0, 0, NULL);

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
