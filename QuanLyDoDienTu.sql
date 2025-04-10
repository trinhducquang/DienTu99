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
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table quanlybanhang.categories: ~4 rows (approximately)
INSERT INTO `categories` (`id`, `name`, `description`, `parent_id`) VALUES
	(1, 'Tivi', '8k', 3),
	(2, 'Tivi samsung', 'siêu đẹp', NULL),
	(3, 'điện thoại', 'màn hình đỉnh', NULL),
	(4, 'iphone', 'cc', NULL);

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
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table quanlybanhang.customers: ~2 rows (approximately)
INSERT INTO `customers` (`id`, `name`, `phone`, `email`, `address`, `created_at`) VALUES
	(1, 'haianh', '09344311316', 'ádasdasd213', 'áddsasadasd', '2025-04-09 18:34:58'),
	(2, 'haianh', '09344313316', 'dsadasdsa', 'adsasdadsasd', '2025-04-09 19:33:06');

-- Dumping structure for table quanlybanhang.orders
CREATE TABLE IF NOT EXISTS `orders` (
  `id` int NOT NULL AUTO_INCREMENT,
  `employee_id` int NOT NULL,
  `total_price` decimal(18,2) NOT NULL,
  `order_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `status` enum('Đang xử lý','Đang Giao Hàng','Hoàn thành','Đã hủy') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `customer_id` int DEFAULT NULL,
  `shipping_fee` decimal(10,2) NOT NULL DEFAULT '0.00',
  `note` text,
  PRIMARY KEY (`id`),
  KEY `employee_id` (`employee_id`),
  KEY `fk_orders_customers` (`customer_id`),
  CONSTRAINT `fk_orders_customers` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`id`) ON DELETE CASCADE,
  CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`employee_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table quanlybanhang.orders: ~3 rows (approximately)
INSERT INTO `orders` (`id`, `employee_id`, `total_price`, `order_date`, `status`, `customer_id`, `shipping_fee`, `note`) VALUES
	(1, 2, 958400000.00, '2025-04-09 17:00:00', 'Hoàn thành', 1, 0.00, '0'),
	(2, 2, 479200000.00, '2025-04-09 17:00:00', 'Đang xử lý', 2, 0.00, 'saddas'),
	(3, 2, 479200000.00, '2025-04-09 17:00:00', 'Hoàn thành', 1, 0.00, 'h');

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
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table quanlybanhang.order_details: ~4 rows (approximately)
INSERT INTO `order_details` (`id`, `order_id`, `product_id`, `quantity`, `price`) VALUES
	(1, 1, 1, 4, 119800000.00),
	(2, 1, 2, 4, 119800000.00),
	(3, 2, 4, 4, 119800000.00),
	(4, 3, 4, 4, 119800000.00);

-- Dumping structure for view quanlybanhang.order_summary
-- Creating temporary table to overcome VIEW dependency errors
CREATE TABLE `order_summary` (
	`order_id` INT(10) NOT NULL,
	`order_date` TIMESTAMP NULL,
	`status` ENUM('Đang xử lý','Đang Giao Hàng','Hoàn thành','Đã hủy') NOT NULL COLLATE 'utf8mb4_0900_ai_ci',
	`total_price` DECIMAL(18,2) NOT NULL,
	`shipping_fee` DECIMAL(10,2) NOT NULL,
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
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table quanlybanhang.products: ~11 rows (approximately)
INSERT INTO `products` (`id`, `name`, `category_id`, `description`, `price`, `stock_quantity`, `created_at`, `updated_at`, `status`, `image_url`, `specifications`) VALUES
	(1, 'Smart Tivi Neo QLED Samsung 4K 98 inch QA98QN90D [ 98QN90D ]', 2, 'Smart Tivi Neo QLED 4K chân thực, sống động.Mãn nhãn với chất lượng hình ảnh 4K từ bộ xử lý AI NQ4 thế hệ 2.Kích thước màn hình lớn 98 inch như màn hình rạp chiếu phim thu nhỏ trong nhà.Tối ưu hình ảnh cho màn hình 98 inch siêu lớn bằng AI nhờ công nghệ Supersize Picture Enhancer.Kiểm soát độ sáng và tối hoàn hảo nhờ công nghệ Quantum Matrix.Công nghệ Neo Quantum HDR+ khung hình bừng sáng, màu sắc rực rỡ 4K.Công nghệ âm thanh theo chuyển động OTS+ với giả lập âm vòm 3D Dolby Atmos (60W; 4.2.2 CH).Hệ điều hành Tizen giao diện cải tiến, kho ứng dụng phong phú.', 119800000.00, 4, '2025-04-09 18:00:12', '2025-04-09 18:19:14', 'Còn hàng', 'https://images.samsung.com/is/image/samsung/p6pim/vn/qa98q80cakxxv/gallery/vn-qled-q80c-451227-qa98q80cakxxv-536734797?$684_547_PNG$', '{"camera": "", "battery": "", "features": "", "configMemory": "", "connectivity": "", "designMaterials": ""}'),
	(2, 'Smart Tivi Neo QLED Samsung 4K 98 inch QA98QN90D [ 98QN90D ]', 2, 'Smart Tivi Neo QLED 4K chân thực, sống động.Mãn nhãn với chất lượng hình ảnh 4K từ bộ xử lý AI NQ4 thế hệ 2.Kích thước màn hình lớn 98 inch như màn hình rạp chiếu phim thu nhỏ trong nhà.Tối ưu hình ảnh cho màn hình 98 inch siêu lớn bằng AI nhờ công nghệ Supersize Picture Enhancer.Kiểm soát độ sáng và tối hoàn hảo nhờ công nghệ Quantum Matrix.Công nghệ Neo Quantum HDR+ khung hình bừng sáng, màu sắc rực rỡ 4K.Công nghệ âm thanh theo chuyển động OTS+ với giả lập âm vòm 3D Dolby Atmos (60W; 4.2.2 CH).Hệ điều hành Tizen giao diện cải tiến, kho ứng dụng phong phú.', 119800000.00, 4, '2025-04-09 18:00:12', '2025-04-09 18:19:11', 'Còn hàng', 'https://images.samsung.com/is/image/samsung/p6pim/vn/qa98q80cakxxv/gallery/vn-qled-q80c-451227-qa98q80cakxxv-536734797?$684_547_PNG$', '{"camera": "", "battery": "", "features": "", "configMemory": "", "connectivity": "", "designMaterials": ""}'),
	(3, 'Smart Tivi Neo QLED Samsung 4K 98 inch QA98QN90D [ 98QN90D ]', 2, 'Smart Tivi Neo QLED 4K chân thực, sống động.Mãn nhãn với chất lượng hình ảnh 4K từ bộ xử lý AI NQ4 thế hệ 2.Kích thước màn hình lớn 98 inch như màn hình rạp chiếu phim thu nhỏ trong nhà.Tối ưu hình ảnh cho màn hình 98 inch siêu lớn bằng AI nhờ công nghệ Supersize Picture Enhancer.Kiểm soát độ sáng và tối hoàn hảo nhờ công nghệ Quantum Matrix.Công nghệ Neo Quantum HDR+ khung hình bừng sáng, màu sắc rực rỡ 4K.Công nghệ âm thanh theo chuyển động OTS+ với giả lập âm vòm 3D Dolby Atmos (60W; 4.2.2 CH).Hệ điều hành Tizen giao diện cải tiến, kho ứng dụng phong phú.', 119800000.00, 4, '2025-04-09 18:00:12', '2025-04-09 18:04:26', 'Còn hàng', 'https://images.samsung.com/is/image/samsung/p6pim/vn/qa98q80cakxxv/gallery/vn-qled-q80c-451227-qa98q80cakxxv-536734797?$684_547_PNG$', '{"camera": "", "battery": "", "features": "", "configMemory": "", "connectivity": "", "designMaterials": ""}'),
	(4, 'Smart Tivi Neo QLED Samsung 4K 98 inch QA98QN90D [ 98QN90D ]', 2, 'Smart Tivi Neo QLED 4K chân thực, sống động.Mãn nhãn với chất lượng hình ảnh 4K từ bộ xử lý AI NQ4 thế hệ 2.Kích thước màn hình lớn 98 inch như màn hình rạp chiếu phim thu nhỏ trong nhà.Tối ưu hình ảnh cho màn hình 98 inch siêu lớn bằng AI nhờ công nghệ Supersize Picture Enhancer.Kiểm soát độ sáng và tối hoàn hảo nhờ công nghệ Quantum Matrix.Công nghệ Neo Quantum HDR+ khung hình bừng sáng, màu sắc rực rỡ 4K.Công nghệ âm thanh theo chuyển động OTS+ với giả lập âm vòm 3D Dolby Atmos (60W; 4.2.2 CH).Hệ điều hành Tizen giao diện cải tiến, kho ứng dụng phong phú.', 119800000.00, 4, '2025-04-09 18:00:12', '2025-04-09 18:19:05', 'Còn hàng', 'https://images.samsung.com/is/image/samsung/p6pim/vn/qa98q80cakxxv/gallery/vn-qled-q80c-451227-qa98q80cakxxv-536734797?$684_547_PNG$https://images.samsung.com/is/image/samsung/p6pim/vn/qa98q80cakxxv/gallery/vn-qled-q80c-451227-qa98q80cakxxv-536734797?$684_547_PNG$', '{"camera": "", "battery": "", "features": "", "configMemory": "", "connectivity": "", "designMaterials": ""}'),
	(5, 'Smart Tivi Neo QLED Samsung 4K 98 inch QA98QN90D [ 98QN90D ]', 2, 'Smart Tivi Neo QLED 4K chân thực, sống động.Mãn nhãn với chất lượng hình ảnh 4K từ bộ xử lý AI NQ4 thế hệ 2.Kích thước màn hình lớn 98 inch như màn hình rạp chiếu phim thu nhỏ trong nhà.Tối ưu hình ảnh cho màn hình 98 inch siêu lớn bằng AI nhờ công nghệ Supersize Picture Enhancer.Kiểm soát độ sáng và tối hoàn hảo nhờ công nghệ Quantum Matrix.Công nghệ Neo Quantum HDR+ khung hình bừng sáng, màu sắc rực rỡ 4K.Công nghệ âm thanh theo chuyển động OTS+ với giả lập âm vòm 3D Dolby Atmos (60W; 4.2.2 CH).Hệ điều hành Tizen giao diện cải tiến, kho ứng dụng phong phú.', 119800000.00, 4, '2025-04-09 18:00:12', '2025-04-09 18:19:03', 'Còn hàng', 'https://images.samsung.com/is/image/samsung/p6pim/vn/qa98q80cakxxv/gallery/vn-qled-q80c-451227-qa98q80cakxxv-536734797?$684_547_PNG$https://images.samsung.com/is/image/samsung/p6pim/vn/qa98q80cakxxv/gallery/vn-qled-q80c-451227-qa98q80cakxxv-536734797?$684_547_PNG$', '{"camera": "", "battery": "", "features": "", "configMemory": "", "connectivity": "", "designMaterials": ""}'),
	(6, 'Smart Tivi Neo QLED Samsung 4K 98 inch QA98QN90D [ 98QN90D ]', 2, 'Smart Tivi Neo QLED 4K chân thực, sống động.Mãn nhãn với chất lượng hình ảnh 4K từ bộ xử lý AI NQ4 thế hệ 2.Kích thước màn hình lớn 98 inch như màn hình rạp chiếu phim thu nhỏ trong nhà.Tối ưu hình ảnh cho màn hình 98 inch siêu lớn bằng AI nhờ công nghệ Supersize Picture Enhancer.Kiểm soát độ sáng và tối hoàn hảo nhờ công nghệ Quantum Matrix.Công nghệ Neo Quantum HDR+ khung hình bừng sáng, màu sắc rực rỡ 4K.Công nghệ âm thanh theo chuyển động OTS+ với giả lập âm vòm 3D Dolby Atmos (60W; 4.2.2 CH).Hệ điều hành Tizen giao diện cải tiến, kho ứng dụng phong phú.', 119800000.00, 4, '2025-04-09 18:00:12', '2025-04-09 18:19:01', 'Còn hàng', 'https://images.samsung.com/is/image/samsung/p6pim/vn/qa98q80cakxxv/gallery/vn-qled-q80c-451227-qa98q80cakxxv-536734797?$684_547_PNG$https://images.samsung.com/is/image/samsung/p6pim/vn/qa98q80cakxxv/gallery/vn-qled-q80c-451227-qa98q80cakxxv-536734797?$684_547_PNG$', '{"camera": "", "battery": "", "features": "", "configMemory": "", "connectivity": "", "designMaterials": ""}'),
	(7, 'Smart Tivi Neo QLED Samsung 4K 98 inch QA98QN90D [ 98QN90D ]', 2, 'Smart Tivi Neo QLED 4K chân thực, sống động.Mãn nhãn với chất lượng hình ảnh 4K từ bộ xử lý AI NQ4 thế hệ 2.Kích thước màn hình lớn 98 inch như màn hình rạp chiếu phim thu nhỏ trong nhà.Tối ưu hình ảnh cho màn hình 98 inch siêu lớn bằng AI nhờ công nghệ Supersize Picture Enhancer.Kiểm soát độ sáng và tối hoàn hảo nhờ công nghệ Quantum Matrix.Công nghệ Neo Quantum HDR+ khung hình bừng sáng, màu sắc rực rỡ 4K.Công nghệ âm thanh theo chuyển động OTS+ với giả lập âm vòm 3D Dolby Atmos (60W; 4.2.2 CH).Hệ điều hành Tizen giao diện cải tiến, kho ứng dụng phong phú.', 119800000.00, 4, '2025-04-09 18:00:12', '2025-04-09 18:18:57', 'Còn hàng', 'https://images.samsung.com/is/image/samsung/p6pim/vn/qa98q80cakxxv/gallery/vn-qled-q80c-451227-qa98q80cakxxv-536734797?$684_547_PNG$https://images.samsung.com/is/image/samsung/p6pim/vn/qa98q80cakxxv/gallery/vn-qled-q80c-451227-qa98q80cakxxv-536734797?$684_547_PNG$', '{"camera": "", "battery": "", "features": "", "configMemory": "", "connectivity": "", "designMaterials": ""}'),
	(8, 'Smart Tivi Neo QLED Samsung 4K 98 inch QA98QN90D [ 98QN90D ]', 2, 'Smart Tivi Neo QLED 4K chân thực, sống động.Mãn nhãn với chất lượng hình ảnh 4K từ bộ xử lý AI NQ4 thế hệ 2.Kích thước màn hình lớn 98 inch như màn hình rạp chiếu phim thu nhỏ trong nhà.Tối ưu hình ảnh cho màn hình 98 inch siêu lớn bằng AI nhờ công nghệ Supersize Picture Enhancer.Kiểm soát độ sáng và tối hoàn hảo nhờ công nghệ Quantum Matrix.Công nghệ Neo Quantum HDR+ khung hình bừng sáng, màu sắc rực rỡ 4K.Công nghệ âm thanh theo chuyển động OTS+ với giả lập âm vòm 3D Dolby Atmos (60W; 4.2.2 CH).Hệ điều hành Tizen giao diện cải tiến, kho ứng dụng phong phú.', 119800000.00, 4, '2025-04-09 18:00:12', '2025-04-09 18:04:26', 'Còn hàng', 'https://images.samsung.com/is/image/samsung/p6pim/vn/qa98q80cakxxv/gallery/vn-qled-q80c-451227-qa98q80cakxxv-536734797?$684_547_PNG$', '{"camera": "", "battery": "", "features": "", "configMemory": "", "connectivity": "", "designMaterials": ""}'),
	(9, 'Smart Tivi Neo QLED Samsung 4K 98 inch QA98QN90D [ 98QN90D ]', 2, 'Smart Tivi Neo QLED 4K chân thực, sống động.Mãn nhãn với chất lượng hình ảnh 4K từ bộ xử lý AI NQ4 thế hệ 2.Kích thước màn hình lớn 98 inch như màn hình rạp chiếu phim thu nhỏ trong nhà.Tối ưu hình ảnh cho màn hình 98 inch siêu lớn bằng AI nhờ công nghệ Supersize Picture Enhancer.Kiểm soát độ sáng và tối hoàn hảo nhờ công nghệ Quantum Matrix.Công nghệ Neo Quantum HDR+ khung hình bừng sáng, màu sắc rực rỡ 4K.Công nghệ âm thanh theo chuyển động OTS+ với giả lập âm vòm 3D Dolby Atmos (60W; 4.2.2 CH).Hệ điều hành Tizen giao diện cải tiến, kho ứng dụng phong phú.', 119800000.00, 4, '2025-04-09 18:00:12', '2025-04-09 18:34:02', 'Còn hàng', 'https://nvs.tn-cdn.net/2023/12/ban-phim-co-e-dra-ek375-pro-beta.jpg', '{"camera": "", "battery": "", "features": "", "configMemory": "", "connectivity": "", "designMaterials": ""}'),
	(10, 'Smart Tivi Neo QLED Samsung 4K 98 inch QA98QN90D [ 98QN90D ]', 2, 'Smart Tivi Neo QLED 4K chân thực, sống động.Mãn nhãn với chất lượng hình ảnh 4K từ bộ xử lý AI NQ4 thế hệ 2.Kích thước màn hình lớn 98 inch như màn hình rạp chiếu phim thu nhỏ trong nhà.Tối ưu hình ảnh cho màn hình 98 inch siêu lớn bằng AI nhờ công nghệ Supersize Picture Enhancer.Kiểm soát độ sáng và tối hoàn hảo nhờ công nghệ Quantum Matrix.Công nghệ Neo Quantum HDR+ khung hình bừng sáng, màu sắc rực rỡ 4K.Công nghệ âm thanh theo chuyển động OTS+ với giả lập âm vòm 3D Dolby Atmos (60W; 4.2.2 CH).Hệ điều hành Tizen giao diện cải tiến, kho ứng dụng phong phú.', 119800000.00, 4, '2025-04-09 18:00:12', '2025-04-09 20:29:24', 'Còn hàng', 'https://cdn.vjshop.vn/tin-tuc/do-phan-giai-8k-la-gi/8k-la-gi-7.png', '{"camera": "a", "battery": "a", "features": "a", "configMemory": "a", "connectivity": "a", "designMaterials": "a"}'),
	(11, 'b', 2, 'b', 1.00, 1, '2025-04-09 20:37:20', '2025-04-09 20:37:20', 'Còn hàng', 'b', '{"camera": "b", "battery": "b", "features": "b", "configMemory": "b", "connectivity": "b", "designMaterials": "b"}');

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
  `role` enum('Admin','Nhân Viên','Nhân viên kho','Nhân viên thu ngân','Nhân viên bán hàng','Lock') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table quanlybanhang.users: ~2 rows (approximately)
INSERT INTO `users` (`id`, `username`, `password`, `full_name`, `email`, `phone`, `role`) VALUES
	(1, 'admin', '$argon2id$v=19$m=65536,t=3,p=2$jIdw8PVlE9UbszsLtomCHQ$OmK/fzWKjxzSV/PgH95rPKndBAuEUPUsgbrMveR3+ag', 'trinhducquang', '312123321', '312321321321', 'Admin'),
	(2, 'haianh', '$argon2id$v=19$m=65536,t=3,p=2$j0Eeq9QGITBtLg+94q+cFw$itcx9ONO/B2mVxs3um2iJpVWFqrB+POo3k3MKm6r2iM', 'phamhaianh', 'haianh', '123321123', 'Nhân viên kho');

-- Dumping structure for table quanlybanhang.warehouse_transactions
CREATE TABLE IF NOT EXISTS `warehouse_transactions` (
  `id` int NOT NULL AUTO_INCREMENT,
  `product_id` int NOT NULL,
  `sku` varchar(50) DEFAULT NULL,
  `quantity` int NOT NULL,
  `unit_price` decimal(18,2) NOT NULL,
  `type` enum('Nhập Kho','Xuất Kho','Kiểm Kho') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `note` text,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `transaction_code` varchar(20) DEFAULT NULL,
  `created_by` int DEFAULT NULL,
  `excess_quantity` int DEFAULT '0',
  `deficient_quantity` int DEFAULT '0',
  `stock_quantity` int DEFAULT '0',
  `missing` int DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `product_id` (`product_id`),
  KEY `fk_created_by` (`created_by`),
  CONSTRAINT `fk_created_by` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `warehouse_transactions_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table quanlybanhang.warehouse_transactions: ~1 rows (approximately)
INSERT INTO `warehouse_transactions` (`id`, `product_id`, `sku`, `quantity`, `unit_price`, `type`, `note`, `created_at`, `updated_at`, `transaction_code`, `created_by`, `excess_quantity`, `deficient_quantity`, `stock_quantity`, `missing`) VALUES
	(1, 1, 'SP001', 50, 1200000.00, 'Nhập Kho', 'Nhập hàng đợt 1', '2025-04-09 18:41:06', '2025-04-09 18:41:06', 'IMP20240401', 1, 0, 0, 50, 0);

-- Dumping structure for view quanlybanhang.warehouse_transaction_details
-- Creating temporary table to overcome VIEW dependency errors
CREATE TABLE `warehouse_transaction_details` (
	`id` INT(10) NOT NULL,
	`product_id` INT(10) NOT NULL,
	`sku` VARCHAR(50) NULL COLLATE 'utf8mb4_0900_ai_ci',
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
CREATE ALGORITHM=UNDEFINED SQL SECURITY DEFINER VIEW `warehouse_transaction_details` AS select `wt`.`id` AS `id`,`wt`.`product_id` AS `product_id`,`wt`.`sku` AS `sku`,`wt`.`quantity` AS `quantity`,`wt`.`unit_price` AS `unit_price`,`wt`.`type` AS `type`,`wt`.`note` AS `note`,`wt`.`created_at` AS `created_at`,`wt`.`updated_at` AS `updated_at`,`wt`.`transaction_code` AS `transaction_code`,`wt`.`created_by` AS `created_by`,`wt`.`excess_quantity` AS `excess_quantity`,`wt`.`deficient_quantity` AS `deficient_quantity`,`wt`.`missing` AS `missing`,`p`.`name` AS `product_name`,`p`.`price` AS `sell_price`,`c`.`name` AS `category_name`,`u`.`full_name` AS `created_by_name` from (((`warehouse_transactions` `wt` join `products` `p` on((`wt`.`product_id` = `p`.`id`))) join `categories` `c` on((`p`.`category_id` = `c`.`id`))) join `users` `u` on((`wt`.`created_by` = `u`.`id`)));

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
