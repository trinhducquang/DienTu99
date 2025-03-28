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
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table quanlybanhang.categories: ~3 rows (approximately)
INSERT INTO `categories` (`id`, `name`, `description`) VALUES
	(1, 'Laptop', 'Các dòng laptop từ phổ thông đến cao cấp'),
	(2, 'Phụ kiện', 'Chuột, bàn phím, tai nghe và các phụ kiện khác'),
	(3, 'Thiết bị văn phòng', 'Màn hình, máy in, bộ phát WiFi và các thiết bị văn phòng khác');

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table quanlybanhang.customers: ~0 rows (approximately)

-- Dumping structure for table quanlybanhang.orders
CREATE TABLE IF NOT EXISTS `orders` (
  `id` int NOT NULL AUTO_INCREMENT,
  `employee_id` int NOT NULL,
  `total_price` decimal(10,2) NOT NULL,
  `order_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `status` enum('pending','completed','cancelled') NOT NULL,
  `customer_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `employee_id` (`employee_id`),
  KEY `fk_orders_customers` (`customer_id`),
  CONSTRAINT `fk_orders_customers` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`id`) ON DELETE CASCADE,
  CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`employee_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table quanlybanhang.orders: ~0 rows (approximately)

-- Dumping structure for table quanlybanhang.order_details
CREATE TABLE IF NOT EXISTS `order_details` (
  `id` int NOT NULL AUTO_INCREMENT,
  `order_id` int NOT NULL,
  `product_id` int NOT NULL,
  `quantity` int NOT NULL,
  `price` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `order_id` (`order_id`),
  KEY `product_id` (`product_id`),
  CONSTRAINT `order_details_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`),
  CONSTRAINT `order_details_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table quanlybanhang.order_details: ~0 rows (approximately)

-- Dumping structure for table quanlybanhang.products
CREATE TABLE IF NOT EXISTS `products` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `category_id` int DEFAULT NULL,
  `description` text,
  `price` decimal(10,2) NOT NULL,
  `stock_quantity` int NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_category` (`category_id`),
  CONSTRAINT `fk_category` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`),
  CONSTRAINT `products_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table quanlybanhang.products: ~12 rows (approximately)
INSERT INTO `products` (`id`, `name`, `category_id`, `description`, `price`, `stock_quantity`, `created_at`, `updated_at`) VALUES
	(1, 'Laptop Dell Inspiron', 1, 'Laptop Dell mạnh mẽ cho doanh nhân', 18000000.00, 10, '2025-03-27 16:08:11', '2025-03-27 16:08:11'),
	(2, 'MacBook Air M1', 1, 'MacBook Air M1 siêu nhẹ, pin trâu', 22000000.00, 8, '2025-03-27 16:08:11', '2025-03-27 16:08:11'),
	(3, 'Bàn phím cơ Logitech G512', 2, 'Bàn phím cơ RGB cao cấp', 2500000.00, 20, '2025-03-27 16:08:11', '2025-03-27 16:08:11'),
	(4, 'Chuột Logitech G Pro', 2, 'Chuột gaming hiệu suất cao', 1500000.00, 15, '2025-03-27 16:08:11', '2025-03-27 16:08:11'),
	(5, 'Màn hình LG 27 inch', 3, 'Màn hình 2K chất lượng cao', 7000000.00, 12, '2025-03-27 16:08:11', '2025-03-27 16:08:11'),
	(6, 'Ổ cứng SSD Samsung 1TB', 3, 'Ổ cứng SSD tốc độ cao', 3200000.00, 30, '2025-03-27 16:08:11', '2025-03-27 16:08:11'),
	(7, 'Tai nghe Sony WH-1000XM4', 2, 'Tai nghe chống ồn cao cấp', 5000000.00, 5, '2025-03-27 16:08:11', '2025-03-27 16:08:11'),
	(8, 'Loa Bluetooth JBL Charge 5', 2, 'Loa JBL chống nước, âm bass mạnh', 3200000.00, 18, '2025-03-27 16:08:11', '2025-03-27 16:08:11'),
	(9, 'Máy in Canon LBP2900', 3, 'Máy in laser đơn sắc', 2500000.00, 10, '2025-03-27 16:08:11', '2025-03-27 16:08:11'),
	(10, 'Bộ phát WiFi TP-Link', 3, 'Router WiFi tốc độ cao', 1200000.00, 25, '2025-03-27 16:08:11', '2025-03-27 16:08:11'),
	(22, 'quang', 1, 'hải annh', 15.00, 15, '2025-03-28 15:14:23', '2025-03-28 15:14:23'),
	(23, 'quang', 2, 'quang', 213.00, 1, '2025-03-28 15:22:35', '2025-03-28 15:22:35');

-- Dumping structure for table quanlybanhang.reports
CREATE TABLE IF NOT EXISTS `reports` (
  `report_id` int NOT NULL AUTO_INCREMENT,
  `report_type` varchar(50) DEFAULT NULL,
  `generated_date` datetime DEFAULT NULL,
  `total_revenue` decimal(15,2) DEFAULT NULL,
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
  `role` enum('admin','nhanvien') NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table quanlybanhang.users: ~2 rows (approximately)
INSERT INTO `users` (`id`, `username`, `password`, `full_name`, `email`, `phone`, `role`) VALUES
	(1, 'quang', '1', NULL, '1', NULL, 'admin'),
	(2, 'quan', '2', NULL, '2', NULL, 'nhanvien');

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
