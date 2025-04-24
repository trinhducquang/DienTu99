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
) ENGINE=InnoDB AUTO_INCREMENT=1001 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table quanlybanhang.categories: ~50 rows (approximately)
INSERT INTO `categories` (`id`, `name`, `description`, `parent_id`) VALUES
	(1, 'Điện tử', 'Các sản phẩm điện tử và công nghệ', NULL),
	(2, 'Điện thoại', 'Điện thoại di động và phụ kiện', 1),
	(3, 'Máy tính', 'Máy tính và phụ kiện', 1),
	(4, 'Thiết bị âm thanh', 'Thiết bị âm thanh và giải trí', 1),
	(5, 'TV & Màn hình', 'TV và màn hình hiển thị', 1),
	(6, 'Thiết bị gia dụng', 'Thiết bị điện tử gia dụng', 1),
	(7, 'Thiết bị đeo thông minh', 'Đồng hồ thông minh và thiết bị đeo', 1),
	(8, 'Thiết bị mạng', 'Thiết bị mạng và kết nối', 1),
	(9, 'Máy ảnh', 'Máy ảnh và thiết bị quay phim', 1),
	(10, 'Linh kiện điện tử', 'Linh kiện và phụ tùng điện tử', 1),
	(11, 'Laptop', 'Laptop và phụ kiện', 3),
	(12, 'Máy tính bảng', 'Máy tính bảng và phụ kiện', 3),
	(13, 'Máy chủ', 'Máy chủ và thiết bị mạng', 3),
	(14, 'Pin và Sạc', 'Pin và sạc cho các thiết bị', 2),
	(15, 'Phụ kiện điện thoại', 'Phụ kiện cho điện thoại di động', 2),
	(16, 'Tai nghe', 'Tai nghe và phụ kiện âm thanh', 4),
	(17, 'Loa', 'Loa và dàn âm thanh', 4),
	(18, 'Dàn máy', 'Dàn máy và âm thanh gia đình', 4),
	(19, 'Máy in', 'Máy in và thiết bị văn phòng', 5),
	(20, 'Máy chiếu', 'Máy chiếu và phụ kiện', 5),
	(21, 'Tivi thông minh', 'Tivi thông minh và màn hình độ nét cao', 5),
	(22, 'Thiết bị nhà bếp', 'Thiết bị nhà bếp thông minh', 6),
	(23, 'Máy hút bụi', 'Máy hút bụi và thiết bị làm sạch', 6),
	(24, 'Máy lạnh', 'Máy lạnh và thiết bị làm mát', 6),
	(25, 'Điều hòa', 'Điều hòa không khí và thiết bị điều nhiệt', 6),
	(26, 'Bộ phát Wifi', 'Bộ phát Wifi và thiết bị kết nối mạng', 8),
	(27, 'Router', 'Router và thiết bị mạng gia đình', 8),
	(28, 'Thiết bị bảo mật', 'Thiết bị bảo mật và camera giám sát', 8),
	(29, 'Ổ cứng', 'Ổ cứng và bộ nhớ ngoài', 9),
	(30, 'Thẻ nhớ', 'Thẻ nhớ và bộ lưu trữ', 9),
	(31, 'Máy quay', 'Máy quay phim và thiết bị ghi hình', 9),
	(32, 'Máy ảnh kỹ thuật số', 'Máy ảnh kỹ thuật số và ống kính', 9),
	(33, 'Linh kiện máy tính', 'Linh kiện máy tính và phụ kiện', 3),
	(34, 'Card đồ họa', 'Card đồ họa và phụ kiện', 3),
	(35, 'Bộ vi xử lý', 'Bộ vi xử lý và linh kiện máy tính', 3),
	(36, 'Bộ nhớ RAM', 'Bộ nhớ RAM và thiết bị lưu trữ', 3),
	(37, 'Bàn phím và chuột', 'Bàn phím và chuột máy tính', 3),
	(38, 'Màn hình máy tính', 'Màn hình máy tính và thiết bị hiển thị', 3),
	(39, 'Thiết bị chơi game', 'Thiết bị chơi game và phụ kiện', 1),
	(40, 'Console', 'Console chơi game và phụ kiện', 39),
	(41, 'Thiết bị VR', 'Thiết bị thực tế ảo và phụ kiện', 39),
	(42, 'Phụ kiện máy ảnh', 'Phụ kiện cho máy ảnh và máy quay', 9),
	(43, 'Tủ lạnh', 'Tủ lạnh và thiết bị lưu trữ thực phẩm', 6),
	(44, 'Máy giặt', 'Máy giặt và thiết bị giặt tẩy', 6),
	(45, 'Quạt và thiết bị làm mát', 'Quạt và thiết bị làm mát không khí', 6),
	(46, 'Bộ nồi', 'Bộ nồi và dụng cụ nhà bếp', 6),
	(47, 'Đèn LED', 'Đèn LED chiếu sáng và thiết bị chiếu sáng', 6),
	(48, 'Đồng hồ', 'Đồng hồ đeo tay và thiết bị đeo', 7),
	(49, 'Dây sạc', 'Dây sạc và phụ kiện điện thoại', 2),
	(50, 'Máy phát điện', 'Máy phát điện và thiết bị phát điện', 1);

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
) ENGINE=InnoDB AUTO_INCREMENT=5002 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table quanlybanhang.customers: ~0 rows (approximately)

-- Dumping structure for table quanlybanhang.orders
CREATE TABLE IF NOT EXISTS `orders` (
  `id` int NOT NULL AUTO_INCREMENT,
  `employee_id` int NOT NULL,
  `total_price` decimal(18,2) NOT NULL,
  `order_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `export_status` varchar(255) DEFAULT NULL,
  `status` enum('Đang xử lý','Đang Giao Hàng','Hoàn thành','Đã hủy') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `customer_id` int DEFAULT NULL,
  `shipping_fee` decimal(18,2) NOT NULL DEFAULT '0.00',
  `note` text,
  PRIMARY KEY (`id`),
  KEY `employee_id` (`employee_id`),
  KEY `fk_orders_customers` (`customer_id`),
  CONSTRAINT `fk_orders_customers` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`id`) ON DELETE CASCADE,
  CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`employee_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10003 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table quanlybanhang.orders: ~0 rows (approximately)

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
) ENGINE=InnoDB AUTO_INCREMENT=150009 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

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
	`product_prices` TEXT NULL COLLATE 'utf8mb4_0900_ai_ci',
	`export_status` VARCHAR(255) NULL COLLATE 'utf8mb4_0900_ai_ci'
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
  CONSTRAINT `products_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`) ON DELETE SET NULL,
  CONSTRAINT `chk_stock_quantity_non_negative` CHECK ((`stock_quantity` >= 0))
) ENGINE=InnoDB AUTO_INCREMENT=1001 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table quanlybanhang.products: ~85 rows (approximately)
INSERT INTO `products` (`id`, `name`, `category_id`, `description`, `price`, `stock_quantity`, `created_at`, `updated_at`, `status`, `image_url`, `specifications`) VALUES
	(1, 'Samsung Galaxy S23 Ultra', 2, 'Điện thoại cao cấp với camera 108MP và bút S-Pen', 23990000.00, 0, '2025-04-24 19:17:43', '2025-04-24 19:34:02', 'Hết hàng', 'https://images.samsung.com/is/image/samsung/p6pim/vn/ls27fg812sexxv/gallery/vn-odyssey-oled-g8-27g81sf-ls27fg812sexxv-545183308?$684_547_PNG$', '{"camera": "Camera chính 108MP, Ultra-wide 12MP, Telephoto 10MP", "battery": "5000mAh, sạc nhanh 45W", "features": "Bút S-Pen, chống nước IP68, màn hình Dynamic AMOLED 2X", "configMemory": "12GB RAM, 256GB bộ nhớ trong", "connectivity": "5G, Wi-Fi 6E, Bluetooth 5.2, NFC", "designMaterials": "Khung nhôm, mặt lưng kính Gorilla Glass Victus+"}'),
	(2, 'iPhone 15 Pro Max', 2, 'iPhone cao cấp nhất của Apple với chip A17 Pro', 31990000.00, 0, '2025-04-24 19:17:43', '2025-04-24 19:34:39', 'Hết hàng', 'https://didongmoi.com.vn/upload/images/product/apple/iphone-15-pro-max-chinh-hang-4.jpg', '{"camera": "Camera chính 48MP, Ultra-wide 12MP, Telephoto 12MP với zoom quang học 5x", "battery": "4422mAh, sạc nhanh 27W, sạc không dây MagSafe 15W", "features": "Dynamic Island, Always-On display, Face ID, chống nước IP68", "configMemory": "8GB RAM, 512GB bộ nhớ trong", "connectivity": "5G, Wi-Fi 6E, Bluetooth 5.3, NFC, Ultra Wideband", "designMaterials": "Khung Titanium, mặt lưng kính Ceramic Shield"}'),
	(3, 'Xiaomi Redmi Note 12 Pro', 2, 'Điện thoại tầm trung với camera 108MP', 6990000.00, 0, '2025-04-24 19:17:43', '2025-04-24 19:35:24', 'Hết hàng', 'https://hanoicomputercdn.com/media/product/77196_may_choi_game_nintendo_switch_oled_white_cu_dep_full_box_phu_kien_1.jpg', '{"camera": "Camera chính 108MP, Ultra-wide 8MP, Macro 2MP", "battery": "5000mAh, sạc nhanh 67W", "features": "Màn hình AMOLED 120Hz, cảm biến vân tay dưới màn hình", "configMemory": "8GB RAM, 128GB bộ nhớ trong", "connectivity": "5G, Wi-Fi 6, Bluetooth 5.2, NFC", "designMaterials": "Khung nhựa, mặt lưng kính"}'),
	(4, 'MacBook Pro M3 Pro', 11, 'Laptop cao cấp với chip M3 Pro mạnh mẽ', 52990000.00, 0, '2025-04-24 19:17:43', '2025-04-24 19:37:07', 'Hết hàng', 'https://images.samsung.com/is/image/samsung/p6pim/vn/ls49dg910sexxv/gallery/vn-odyssey-oled-g9-g91sd-ls49dg910sexxv-543991822?$684_547_PNG$', '{"camera": "Camera FaceTime HD 1080p", "battery": "70Wh, thời lượng lên đến 18 giờ", "features": "Màn hình Liquid Retina XDR 14.2 inch, Touch ID, Magic Keyboard", "configMemory": "32GB RAM, 1TB SSD", "connectivity": "Wi-Fi 6E, Bluetooth 5.3, 3x Thunderbolt 4, HDMI, SD card slot", "designMaterials": "Khung nhôm, màn hình Liquid Retina XDR"}'),
	(5, 'Dell XPS 15', 11, 'Laptop mỏng nhẹ với màn hình OLED 4K', 45990000.00, 0, '2025-04-24 19:17:43', '2025-04-24 19:38:07', 'Hết hàng', 'https://images.samsung.com/is/image/samsung/p6pim/vn/2501/gallery/vn-galaxy-s25plus-ms936-ef-ms936cwegww-544458985?$684_547_PNG$', '{"camera": "Camera HD 720p", "battery": "86Wh, thời lượng lên đến 12 giờ", "features": "Màn hình OLED 4K, loa stereo, đèn nền bàn phím", "configMemory": "32GB RAM, 1TB SSD", "connectivity": "Wi-Fi 6, Bluetooth 5.2, 2x Thunderbolt 4, USB-C, SD card reader", "designMaterials": "Khung nhôm, mặt lưng carbon fiber"}'),
	(6, 'Sony WH-1000XM5', 16, 'Tai nghe chống ồn cao cấp', 8990000.00, 0, '2025-04-24 19:17:43', '2025-04-24 19:40:41', 'Hết hàng', 'https://hanoicomputercdn.com/media/product/59408_tai_nghe_gaming_rapoo_vh160_0004_5.jpg', '{"camera": "Không có", "battery": "30 giờ sử dụng, sạc nhanh 3 phút cho 3 giờ phát nhạc", "features": "Chống ồn chủ động, LDAC, DSEE Extreme, Speak-to-Chat", "configMemory": "Bộ nhớ trong cho EQ và cài đặt", "connectivity": "Bluetooth 5.2, NFC, jack 3.5mm", "designMaterials": "Vỏ nhựa cao cấp, đệm tai bằng da tổng hợp"}'),
	(7, 'JBL Flip 6', 17, 'Loa bluetooth di động chống nước', 2490000.00, 0, '2025-04-24 19:17:43', '2025-04-24 19:42:00', 'Hết hàng', 'https://hanoicomputercdn.com/media/product/38247_microlab_bluetooth_m300bt.png', '{"camera": "Không có", "battery": "4800mAh, thời gian phát 12 giờ", "features": "Chống nước IP67, JBL PartyBoost, EQ trong ứng dụng", "configMemory": "Không có", "connectivity": "Bluetooth 5.1", "designMaterials": "Vải chống nước, lưới thép, vỏ nhựa cứng"}'),
	(8, 'Samsung Family Hub Refrigerator', 43, 'Tủ lạnh thông minh với màn hình cảm ứng', 59990000.00, 0, '2025-04-24 19:17:43', '2025-04-24 19:42:33', 'Hết hàng', 'https://images.samsung.com/is/image/samsung/p6pim/vn/rs90f65d2fsv/gallery/vn-rs80f-9-inch-ai-home-rs90f65d2fsv-545198320?$684_547_PNG$', '{"camera": "Camera bên trong để theo dõi thực phẩm", "battery": "Không có (Kết nối điện)", "features": "Màn hình cảm ứng 21.5 inch, điều khiển từ xa qua ứng dụng, cảm biến nhiệt độ", "configMemory": "8GB bộ nhớ trong cho ứng dụng", "connectivity": "Wi-Fi, Bluetooth, SmartThings", "designMaterials": "Thép không gỉ, kính cường lực"}'),
	(9, 'Dyson V15 Detect', 23, 'Máy hút bụi không dây cao cấp với công nghệ laser', 16990000.00, 0, '2025-04-24 19:17:43', '2025-04-24 19:56:07', 'Hết hàng', 'https://1pro.vn/wp-content/uploads/2025/03/mba13-m4-skyblue-gallery1-202503-600x461.jpeg', '{"camera": "Cảm biến phân tích hạt bụi", "battery": "Pin Li-ion 60 phút sử dụng liên tục", "features": "Công nghệ phát hiện bụi bằng laser, màn hình LCD, cảm biến đo hạt bụi", "configMemory": "Bộ nhớ cho các chế độ hút", "connectivity": "Kết nối với ứng dụng Dyson Link", "designMaterials": "Nhựa PC ABS, hợp kim nhôm"}'),
	(10, 'iPad Pro M2 12.9"', 12, 'Máy tính bảng cao cấp với chip M2', 32990000.00, 0, '2025-04-24 19:17:44', '2025-04-24 19:58:03', 'Hết hàng', 'https://bizweb.dktcdn.net/thumb/1024x1024/100/459/953/products/ipad-pro-m1-11-wi-fi-silver-reseller-com-vn-1-364d28cf-ec02-4da1-afec-21332cb8b335-271705ac-3d7c-41e1-ab20-5d258b5d07bc.jpg', '{"camera": "Camera chính 12MP, Ultra-wide 10MP, LiDAR Scanner", "battery": "40.88Wh, thời lượng pin lên đến 10 giờ", "features": "Màn hình Liquid Retina XDR 12.9 inch, Face ID, hỗ trợ Apple Pencil 2", "configMemory": "16GB RAM, 512GB bộ nhớ trong", "connectivity": "5G, Wi-Fi 6E, Bluetooth 5.3, USB-C Thunderbolt 4", "designMaterials": "Khung nhôm, mặt lưng kính"}'),
	(11, 'Samsung Galaxy Tab S9 Ultra', 12, 'Máy tính bảng Android cao cấp với bút S-Pen', 24990000.00, 0, '2025-04-24 19:17:44', '2025-04-24 20:01:08', 'Hết hàng', 'https://images.samsung.com/is/image/samsung/p6pim/vn/ls27fg900xexxv/gallery/vn-odyssey-3d-g90xf-ls27fg900xexxv-545727279?$684_547_PNG$', '{"camera": "Camera kép 13MP + 8MP, camera trước kép 12MP + 12MP", "battery": "11200mAh, sạc nhanh 45W", "features": "Màn hình Dynamic AMOLED 2X 14.6 inch 120Hz, bút S-Pen, chống nước IP68", "configMemory": "12GB RAM, 256GB bộ nhớ trong", "connectivity": "5G, Wi-Fi 6E, Bluetooth 5.3, USB-C", "designMaterials": "Khung nhôm Armor, mặt lưng kính Gorilla Glass 5"}'),
	(12, 'LG OLED C3 65"', 21, 'TV OLED 4K với công nghệ WebOS', 45990000.00, 0, '2025-04-24 19:17:44', '2025-04-24 20:07:43', 'Hết hàng', 'https://kinghome.vn/data/products/1729875711_main_smart-tivi-samsung-neo-qled-4k-75-inch-qa75qn90dakxxv.jpg', '{"camera": "Không có (Tùy chọn camera riêng)", "battery": "Không có (Kết nối điện)", "features": "OLED evo, Dolby Vision IQ, Dolby Atmos, webOS 23, Game Optimizer", "configMemory": "8GB bộ nhớ trong cho ứng dụng", "connectivity": "Wi-Fi 6, Bluetooth 5.0, HDMI 2.1 x4, USB x3, eARC", "designMaterials": "Khung kim loại, chân đế nhôm"}'),
	(13, 'Samsung Neo QLED QN90C 75"', 21, 'TV Mini LED cao cấp', 69990000.00, 0, '2025-04-24 19:17:44', '2025-04-24 20:07:40', 'Hết hàng', 'https://kinghome.vn/data/products/1729875711_main_smart-tivi-samsung-neo-qled-4k-75-inch-qa75qn90dakxxv.jpg', '{"camera": "Không có (Hỗ trợ camera tùy chọn)", "battery": "Không có (Kết nối điện)", "features": "Mini LED, Quantum HDR 32x, Quantum Matrix Technology, Tizen OS", "configMemory": "8GB bộ nhớ trong cho ứng dụng", "connectivity": "Wi-Fi 6, Bluetooth 5.2, HDMI 2.1 x4, USB x3, eARC", "designMaterials": "Khung kim loại chống ăn mòn, chân đế kim loại"}'),
	(14, 'Sony PlayStation 5', 40, 'Máy chơi game thế hệ mới từ Sony', 14990000.00, 0, '2025-04-24 19:17:44', '2025-04-24 20:09:12', 'Hết hàng', 'https://product.hstatic.net/200000409445/product/template_photo_upweb_11200x1200-recovered_b063ab58053b4abbafedc206dbea2d54_master.jpg', '{"camera": "Không có (Hỗ trợ PlayStation Camera riêng)", "battery": "Không có (Kết nối điện)", "features": "Ray tracing, 3D Audio, hỗ trợ 8K, tay cầm DualSense với phản hồi xúc giác", "configMemory": "16GB GDDR6, SSD 825GB", "connectivity": "Wi-Fi 6, Bluetooth 5.1, HDMI 2.1, USB-A x3, USB-C", "designMaterials": "Nhựa ABS, khung kim loại"}'),
	(15, 'Meta Quest 3', 41, 'Kính thực tế ảo độc lập', 12990000.00, 0, '2025-04-24 19:17:44', '2025-04-24 20:09:41', 'Hết hàng', 'https://www.droidshop.vn/wp-content/uploads/2023/05/May-choi-game-Xbox-Series-X.jpg', '{"camera": "4 camera theo dõi chuyển động, camera màu RGB", "battery": "5000mAh, thời lượng pin 2-3 giờ", "features": "Màn hình LCD 2064x2208 mỗi mắt, tần số quét 90Hz, điều khiển Touch Plus", "configMemory": "8GB RAM, 256GB bộ nhớ trong", "connectivity": "Wi-Fi 6E, Bluetooth 5.2, USB-C", "designMaterials": "Nhựa ABS, đệm mặt bằng xốp có thể tháo rời"}'),
	(16, 'Sony Alpha A7 IV', 32, 'Máy ảnh mirrorless full-frame cao cấp', 55990000.00, 0, '2025-04-24 19:17:44', '2025-04-24 20:12:44', 'Hết hàng', 'https://hanoicomputercdn.com/media/product/73162_man_hinh_samsung_odyssey_g5_ls27cg510_850x850_1.jpg', '{"camera": "Cảm biến Full-frame 33MP, quay video 4K 60fps 10-bit", "battery": "Pin Z, thời lượng 610 ảnh mỗi lần sạc", "features": "IBIS 5 trục, kính ngắm điện tử 3.69M điểm ảnh, màn hình LCD xoay lật", "configMemory": "Khe cắm thẻ nhớ kép (SD/CFexpress Type A)", "connectivity": "Wi-Fi 5GHz, Bluetooth 5.0, USB-C, HDMI, jack 3.5mm", "designMaterials": "Khung hợp kim magiê, chống bụi và ẩm"}'),
	(17, 'GoPro Hero 11 Black', 31, 'Camera hành động chống nước', 10990000.00, 0, '2025-04-24 19:17:44', '2025-04-24 20:14:44', 'Hết hàng', 'https://apple.ngocnguyen.vn/cdn/images/202304/goods_img/iphone-13-quoc-te-99-P13192-1680928670361.jpg', '{"camera": "Cảm biến 27MP, quay video 5.3K 60fps, 4K 120fps", "battery": "1720mAh, thời lượng 137 phút ở 4K30", "features": "HyperSmooth 5.0, Horizon Lock, TimeWarp 3.0, chống nước 10m", "configMemory": "Hỗ trợ thẻ microSD lên đến 1TB", "connectivity": "Wi-Fi, Bluetooth, USB-C, HDMI (qua Media Mod)", "designMaterials": "Khung nhôm, kính cường lực, nhựa PC"}'),
	(18, 'Apple Watch Ultra 2', 48, 'Đồng hồ thông minh bền bỉ', 21990000.00, 0, '2025-04-24 19:17:44', '2025-04-24 20:17:28', 'Còn hàng', 'https://images.samsung.com/is/image/samsung/p6pim/vn/ls27fg812sexxv/gallery/vn-odyssey-oled-g8-27g81sf-ls27fg812sexxv-545183308?$684_547_PNG$', '{"camera": "Không có", "battery": "Thời lượng pin lên đến 36 giờ, chế độ tiết kiệm pin lên đến 72 giờ", "features": "Màn hình Always-On Retina LTPO OLED 2000 nits, GPS 2 băng tần, đo điện tâm đồ, cảm biến nhiệt độ", "configMemory": "32GB bộ nhớ trong", "connectivity": "LTE, Wi-Fi 6, Bluetooth 5.3, UWB", "designMaterials": "Titanium, kính sapphire, chống nước 100m"}'),
	(19, 'Samsung Galaxy Watch 6 Classic', 48, 'Đồng hồ thông minh với viền xoay', 7990000.00, 0, '2025-04-24 19:17:44', '2025-04-24 20:17:40', 'Còn hàng', 'https://product.hstatic.net/200000409445/product/template_photo_upweb_11200x1200-recovered_b063ab58053b4abbafedc206dbea2d54_master.jpg', '{"camera": "Không có", "battery": "425mAh, thời lượng pin lên đến 40 giờ", "features": "Màn hình Super AMOLED, viền xoay vật lý, đo điện tâm đồ, đo thành phần cơ thể", "configMemory": "16GB bộ nhớ trong", "connectivity": "LTE, Wi-Fi, Bluetooth 5.3, NFC", "designMaterials": "Thép không gỉ, kính sapphire, chống nước 5ATM+IP68"}'),
	(20, 'NVIDIA GeForce RTX 4090', 34, 'Card đồ họa cao cấp', 42990000.00, 0, '2025-04-24 19:17:44', '2025-04-24 20:21:12', 'Còn hàng', 'https://images.samsung.com/nvidia-rtx-4090.jpg', '{"camera": "Không có", "battery": "Không có (Yêu cầu nguồn 850W+)", "features": "DLSS 3, Ray Tracing thế hệ 3, Shader Model 7.0", "configMemory": "24GB GDDR6X, 384-bit", "connectivity": "PCIe 4.0 x16, 3x DisplayPort 1.4a, 1x HDMI 2.1", "designMaterials": "Khung nhôm, quạt kép đẩy áp, tản nhiệt vapor chamber"}'),
	(21, 'AMD Ryzen 9 7950X', 35, 'CPU hiệu năng cao', 15990000.00, 0, '2025-04-24 19:17:44', '2025-04-24 20:21:12', 'Còn hàng', 'https://images.samsung.com/amd-ryzen-7950x.jpg', '{"camera": "Không có", "battery": "Không có (TDP 170W)", "features": "Xung nhịp nền 4.5GHz, xung nhịp boost 5.7GHz, 64MB L3 cache", "configMemory": "16 nhân, 32 luồng", "connectivity": "Socket AM5, hỗ trợ PCIe 5.0, DDR5", "designMaterials": "IHS bằng nhôm mạ niken, đế đồng"}'),
	(22, 'ASUS ROG Rapture GT-AX11000', 27, 'Router gaming tốc độ cao', 12990000.00, 0, '2025-04-24 19:17:44', '2025-04-24 20:21:12', 'Còn hàng', 'https://images.samsung.com/asus-rog-rapture.jpg', '{"camera": "Không có", "battery": "Không có (Kết nối điện)", "features": "Tri-band Wi-Fi 6, 2.5G Gaming Port, AiMesh, AiProtection Pro", "configMemory": "1GB RAM, 256MB Flash", "connectivity": "1x 2.5G WAN/LAN, 4x Gigabit LAN, 1x Gigabit WAN, USB 3.0 x2", "designMaterials": "Nhựa ABS, 8 ăng-ten có thể tháo rời"}'),
	(23, 'TP-Link Deco X90', 26, 'Hệ thống Wi-Fi mesh cao cấp', 8990000.00, 0, '2025-04-24 19:17:44', '2025-04-24 20:21:12', 'Còn hàng', 'https://images.samsung.com/tp-link-deco-x90.jpg', '{"camera": "Không có", "battery": "Không có (Kết nối điện)", "features": "Wi-Fi 6 AX6600, AI-Driven Mesh, HomeShield, hỗ trợ 200 thiết bị", "configMemory": "1GB RAM", "connectivity": "1x 2.5G WAN/LAN, 1x Gigabit WAN/LAN, Bluetooth", "designMaterials": "Nhựa ABS, thiết kế tháp"}'),
	(24, 'Anker 737 PowerCore', 14, 'Pin dự phòng 24000mAh', 2490000.00, 0, '2025-04-24 19:17:44', '2025-04-24 20:21:12', 'Còn hàng', 'https://images.samsung.com/anker-737-powercore.jpg', '{"camera": "Không có", "battery": "24000mAh, sạc nhanh 140W", "features": "Màn hình LED thông minh, sạc nhanh PD 3.1, sạc được laptop", "configMemory": "Không có", "connectivity": "USB-C x2, USB-A x1", "designMaterials": "Nhôm, nhựa ABS"}'),
	(25, 'Apple MagSafe Charger', 49, 'Sạc không dây cho iPhone', 990000.00, 0, '2025-04-24 19:17:44', '2025-04-24 20:21:12', 'Còn hàng', 'https://images.samsung.com/apple-magsafe-charger.jpg', '{"camera": "Không có", "battery": "Không có (Kết nối điện)", "features": "Nam châm MagSafe, sạc không dây 15W cho iPhone, 7.5W cho AirPods", "configMemory": "Không có", "connectivity": "USB-C", "designMaterials": "Nhôm, nhựa, nam châm"}'),
	(26, 'Google Pixel 8 Pro', 2, 'Điện thoại với khả năng AI vượt trội', 21990000.00, 0, '2025-04-24 19:26:09', '2025-04-24 20:21:12', 'Còn hàng', 'https://images.samsung.com/google-pixel-8-pro.jpg', '{"camera": "Camera chính 50MP, Ultra-wide 48MP, Telephoto 48MP với zoom quang học 5x", "battery": "5000mAh, sạc nhanh 30W, sạc không dây 23W", "features": "Màn hình LTPO OLED 120Hz, chip Tensor G3, Android 14", "configMemory": "12GB RAM, 256GB bộ nhớ trong", "connectivity": "5G, Wi-Fi 7, Bluetooth 5.3, NFC, UWB", "designMaterials": "Khung nhôm, mặt lưng kính Gorilla Glass Victus 2"}'),
	(27, 'OPPO Find X7 Ultra', 2, 'Điện thoại cao cấp với 4 camera 50MP', 25990000.00, 0, '2025-04-24 19:26:09', '2025-04-24 20:21:12', 'Còn hàng', 'https://images.samsung.com/oppo-find-x7-ultra.jpg', '{"camera": "4 camera 50MP bao gồm periscope và zoom quang học 6x", "battery": "5000mAh, sạc nhanh 100W, sạc không dây 50W", "features": "Màn hình AMOLED vi cong 120Hz, cảm biến vân tay dưới màn hình", "configMemory": "16GB RAM, 512GB bộ nhớ trong", "connectivity": "5G, Wi-Fi 7, Bluetooth 5.4, NFC", "designMaterials": "Khung titanium, mặt lưng da thuộc cao cấp"}'),
	(28, 'Nothing Phone (2)', 2, 'Điện thoại với thiết kế đèn Glyph độc đáo', 14990000.00, 0, '2025-04-24 19:26:09', '2025-04-24 20:21:12', 'Còn hàng', 'https://images.samsung.com/nothing-phone-2.jpg', '{"camera": "Camera chính 50MP, Ultra-wide 50MP", "battery": "4700mAh, sạc nhanh 45W, sạc không dây 15W", "features": "Hệ thống đèn Glyph 2.0, màn hình OLED 120Hz", "configMemory": "12GB RAM, 256GB bộ nhớ trong", "connectivity": "5G, Wi-Fi 6E, Bluetooth 5.3, NFC", "designMaterials": "Khung nhôm, mặt lưng kính trong suốt"}'),
	(29, 'Motorola Razr 50 Ultra', 2, 'Điện thoại gập với màn hình ngoài lớn', 24990000.00, 0, '2025-04-24 19:26:09', '2025-04-24 20:21:12', 'Còn hàng', 'https://images.samsung.com/motorola-razr-50-ultra.jpg', '{"camera": "Camera chính 50MP, Ultra-wide 13MP", "battery": "4000mAh, sạc nhanh 45W, sạc không dây 15W", "features": "Màn hình gập 6.9 inch 165Hz, màn hình ngoài 3.6 inch OLED", "configMemory": "12GB RAM, 512GB bộ nhớ trong", "connectivity": "5G, Wi-Fi 6E, Bluetooth 5.3, NFC", "designMaterials": "Khung nhôm series 7000, mặt lưng kính mờ"}'),
	(30, 'Vivo X100 Pro', 2, 'Điện thoại chuyên chụp ảnh với ống kính ZEISS', 22990000.00, 0, '2025-04-24 19:26:09', '2025-04-24 20:21:12', 'Còn hàng', 'https://images.samsung.com/vivo-x100-pro.jpg', '{"camera": "Camera chính 50MP 1-inch, Ultra-wide 50MP, Telephoto 50MP với zoom quang học 4.3x", "battery": "5400mAh, sạc nhanh 100W, sạc không dây 50W", "features": "Màn hình AMOLED cong 120Hz, chip xử lý hình ảnh V3", "configMemory": "16GB RAM, 512GB bộ nhớ trong", "connectivity": "5G, Wi-Fi 7, Bluetooth 5.4, NFC", "designMaterials": "Khung nhôm, mặt lưng kính nano-ceramic"}'),
	(31, 'Lenovo Yoga 9i', 11, 'Laptop xoay gập với loa Bowers & Wilkins', 38990000.00, 0, '2025-04-24 19:26:09', '2025-04-24 20:21:12', 'Còn hàng', 'https://images.samsung.com/lenovo-yoga-9i.jpg', '{"camera": "Camera FHD với IR và khả năng nhận diện khuôn mặt", "battery": "75Wh, thời lượng lên đến 14 giờ", "features": "Màn hình OLED 14 inch 4K, bút Lenovo Active Pen, loa soundbar 360°", "configMemory": "32GB RAM, 1TB SSD PCIe Gen4", "connectivity": "Wi-Fi 6E, Bluetooth 5.3, 2x Thunderbolt 4, USB-A, HDMI", "designMaterials": "Khung nhôm CNC nguyên khối, bản lề kim loại"}'),
	(32, 'ASUS ROG Zephyrus G16', 11, 'Laptop gaming mỏng nhẹ với hiệu năng mạnh', 42990000.00, 0, '2025-04-24 19:26:09', '2025-04-24 20:21:12', 'Còn hàng', 'https://images.samsung.com/asus-rog-zephyrus-g16.jpg', '{"camera": "Camera FHD với khả năng làm mờ nền AI", "battery": "90Wh, thời lượng lên đến 8 giờ chơi game", "features": "Màn hình mini-LED 16 inch 240Hz, bàn phím RGB từng phím, hệ thống làm mát ARC Flow", "configMemory": "32GB RAM DDR5, 2TB SSD PCIe Gen4", "connectivity": "Wi-Fi 7, Bluetooth 5.4, 2x Thunderbolt 4, USB-A, HDMI 2.1", "designMaterials": "Khung hợp kim magiê, mặt lưng kim loại với họa tiết laze"}'),
	(33, 'HP Spectre x360 14', 11, 'Laptop xoay gập cao cấp với bút cảm ứng', 35990000.00, 0, '2025-04-24 19:26:09', '2025-04-24 20:21:12', 'Còn hàng', 'https://images.samsung.com/hp-spectre-x360-14.jpg', '{"camera": "Camera 5MP với tính năng theo dõi khuôn mặt", "battery": "66Wh, thời lượng lên đến 16 giờ", "features": "Màn hình OLED 3K2K cảm ứng tỉ lệ 3:2, bút HP Tilt Pen, cảm biến vân tay", "configMemory": "16GB RAM, 1TB SSD PCIe Gen4", "connectivity": "Wi-Fi 6E, Bluetooth 5.3, 2x Thunderbolt 4, USB-A, microSD", "designMaterials": "Khung nhôm nguyên khối, cạnh cắt kim cương"}'),
	(34, 'MSI Creator Z17 HX Studio', 11, 'Laptop dành cho nhà sáng tạo nội dung', 54990000.00, 0, '2025-04-24 19:26:09', '2025-04-24 20:21:12', 'Còn hàng', 'https://images.samsung.com/msi-creator-z17-hx.jpg', '{"camera": "Camera FHD IR với tính năng nhận diện khuôn mặt", "battery": "90Wh, thời lượng lên đến 11 giờ", "features": "Màn hình Mini LED 17 inch 165Hz, cảm ứng, bút MSI Pen, bàn phím RGB", "configMemory": "64GB RAM DDR5, 2TB SSD PCIe Gen5", "connectivity": "Wi-Fi 7, Bluetooth 5.4, Thunderbolt 4 x2, USB-A, HDMI, SD card reader", "designMaterials": "Khung nhôm CNC, tản nhiệt vapor chamber"}'),
	(35, 'Framework Laptop 16', 11, 'Laptop module hóa có thể nâng cấp dễ dàng', 39990000.00, 0, '2025-04-24 19:26:09', '2025-04-24 20:21:12', 'Còn hàng', 'https://images.samsung.com/framework-laptop-16.jpg', '{"camera": "Camera 1080p có thể tháo rời với cơ chế bảo mật vật lý", "battery": "85Wh có thể thay thế, thời lượng lên đến 12 giờ", "features": "Màn hình 16 inch 165Hz, 6 cổng mở rộng có thể hoán đổi, bàn phím có thể thay thế", "configMemory": "32GB RAM DDR5, 1TB SSD PCIe Gen4 (có thể nâng cấp)", "connectivity": "Tùy chọn module: Thunderbolt 4, USB-C, USB-A, HDMI, DisplayPort, Ethernet, microSD", "designMaterials": "Khung nhôm tái chế, thiết kế dễ sửa chữa và nâng cấp"}'),
	(36, 'Xiaomi Pad 6 Pro', 12, 'Máy tính bảng hiệu năng cao giá tốt', 10990000.00, 0, '2025-04-24 19:26:09', '2025-04-24 20:21:12', 'Còn hàng', 'https://images.samsung.com/xiaomi-pad-6-pro.jpg', '{"camera": "Camera sau 13MP, camera trước 8MP góc rộng", "battery": "8600mAh, sạc nhanh 67W", "features": "Màn hình LCD IPS 11 inch 144Hz, loa quad JBL, hỗ trợ bút cảm ứng", "configMemory": "12GB RAM, 256GB bộ nhớ trong", "connectivity": "Wi-Fi 6, Bluetooth 5.2, USB-C", "designMaterials": "Khung kim loại, mặt lưng kim loại nhôm"}'),
	(37, 'Google Pixel Tablet', 12, 'Máy tính bảng tích hợp Google Assistant', 14990000.00, 0, '2025-04-24 19:26:09', '2025-04-24 20:21:12', 'Còn hàng', 'https://images.samsung.com/google-pixel-tablet.jpg', '{"camera": "Camera sau 8MP, camera trước 8MP góc rộng", "battery": "7020mAh, sạc nhanh 15W", "features": "Màn hình LCD 11 inch, dock loa sạc, Google TV, tích hợp Nest Hub", "configMemory": "8GB RAM, 256GB bộ nhớ trong", "connectivity": "Wi-Fi 6, Bluetooth 5.2, USB-C, Pogo pin", "designMaterials": "Khung nhôm, mặt lưng nano-ceramic"}'),
	(38, 'Lenovo Tab P12 Pro', 12, 'Máy tính bảng với màn hình AMOLED lớn', 12990000.00, 0, '2025-04-24 19:26:09', '2025-04-24 20:21:12', 'Còn hàng', 'https://images.samsung.com/lenovo-tab-p12-pro.jpg', '{"camera": "Camera sau 13MP + 5MP, camera trước 8MP", "battery": "10200mAh, sạc nhanh 45W", "features": "Màn hình AMOLED 12.6 inch 120Hz, loa quad JBL, hỗ trợ Lenovo Precision Pen 3", "configMemory": "8GB RAM, 256GB bộ nhớ trong", "connectivity": "Wi-Fi 6, Bluetooth 5.2, USB-C", "designMaterials": "Khung nhôm nguyên khối, mặt lưng kim loại"}'),
	(39, 'Sony Bravia XR A95L', 21, 'TV OLED QD cao cấp', 79990000.00, 0, '2025-04-24 19:26:09', '2025-04-24 20:21:12', 'Còn hàng', 'https://images.samsung.com/sony-bravia-xr-a95l.jpg', '{"camera": "Không có (Hỗ trợ camera BRAVIA CAM tùy chọn)", "battery": "Không có (Kết nối điện)", "features": "QD-OLED, Cognitive Processor XR, Google TV, Acoustic Surface Audio+", "configMemory": "16GB bộ nhớ trong cho ứng dụng", "connectivity": "Wi-Fi 6, Bluetooth 5.3, HDMI 2.1 x2, eARC, USB x3, Ethernet", "designMaterials": "Khung kim loại, chân đế nhôm tối giản"}'),
	(40, 'Hisense U8N ULED', 21, 'TV Mini LED giá tốt', 23990000.00, 0, '2025-04-24 19:26:09', '2025-04-24 20:21:12', 'Còn hàng', 'https://images.samsung.com/hisense-u8n.jpg', '{"camera": "Không có", "battery": "Không có (Kết nối điện)", "features": "Mini LED, Quantum Dot, 144Hz, Dolby Vision IQ, IMAX Enhanced", "configMemory": "8GB bộ nhớ trong cho ứng dụng", "connectivity": "Wi-Fi 6, Bluetooth 5.2, HDMI 2.1 x4, eARC, USB x2, Ethernet", "designMaterials": "Khung kim loại, chân đế trung tâm kim loại"}'),
	(41, 'Samsung Odyssey OLED G9', 5, 'Màn hình gaming siêu rộng cong', 32990000.00, 0, '2025-04-24 19:26:09', '2025-04-24 20:21:12', 'Còn hàng', 'https://images.samsung.com/samsung-odyssey-oled-g9.jpg', '{"camera": "Không có", "battery": "Không có (Kết nối điện)", "features": "Màn hình OLED 49 inch cong 1800R, 240Hz, HDR10+, thời gian đáp ứng 0.03ms", "configMemory": "Không có", "connectivity": "HDMI 2.1 x2, DisplayPort 1.4, USB hub, KVM switch", "designMaterials": "Khung nhựa cao cấp, chân đế kim loại, đèn LED Infinity Core"}'),
	(42, 'Apple AirPods Pro 2', 16, 'Tai nghe true wireless cao cấp', 6490000.00, 0, '2025-04-24 19:26:09', '2025-04-24 20:21:12', 'Còn hàng', 'https://images.samsung.com/airpods-pro-2.jpg', '{"camera": "Không có", "battery": "6 giờ sử dụng, 30 giờ với hộp sạc", "features": "Chống ồn chủ động, âm thanh không gian, chip H2, điều khiển cảm ứng", "configMemory": "Không có", "connectivity": "Bluetooth 5.3, USB-C", "designMaterials": "Nhựa cao cấp, đệm tai silicone có 4 kích cỡ"}'),
	(43, 'Bose QuietComfort Ultra', 16, 'Tai nghe chống ồn đeo tai', 9990000.00, 0, '2025-04-24 19:26:09', '2025-04-24 20:21:12', 'Còn hàng', 'https://images.samsung.com/bose-qc-ultra.jpg', '{"camera": "Không có", "battery": "24 giờ sử dụng, sạc nhanh 15 phút cho 2.5 giờ sử dụng", "features": "Chống ồn đẳng cấp, âm thanh không gian Bose, cảm biến đeo, tự động dừng", "configMemory": "Không có", "connectivity": "Bluetooth 5.3, đa điểm, USB-C, jack 3.5mm", "designMaterials": "Nhựa cao cấp, da tổng hợp, khung kim loại"}'),
	(44, 'Sonos Arc', 17, 'Loa soundbar cao cấp hỗ trợ Dolby Atmos', 19990000.00, 0, '2025-04-24 19:26:09', '2025-04-24 20:21:12', 'Còn hàng', 'https://images.samsung.com/sonos-arc.jpg', '{"camera": "Không có", "battery": "Không có (Kết nối điện)", "features": "11 driver, Dolby Atmos, Trueplay tuning, Apple AirPlay 2", "configMemory": "Không có", "connectivity": "Wi-Fi 6, Ethernet, HDMI eARC", "designMaterials": "Khung nhôm, lưới vải chống xước"}'),
	(45, 'Bang & Olufsen Beoplay Portal', 16, 'Tai nghe gaming cao cấp', 14990000.00, 0, '2025-04-24 19:26:09', '2025-04-24 20:21:12', 'Còn hàng', 'https://images.samsung.com/bo-beoplay-portal.jpg', '{"camera": "Không có", "battery": "19 giờ sử dụng (BT+ANC), 42 giờ (BT)", "features": "Adaptive ANC, virtual boom arm, EQ tùy chỉnh, đệm tai làm mát", "configMemory": "Không có", "connectivity": "Bluetooth 5.2, USB-C không dây, jack 3.5mm", "designMaterials": "Nhôm đánh bóng, da cừu, foam memory"}'),
	(46, 'Garmin Enduro 3', 48, 'Đồng hồ thông minh siêu bền pin', 18990000.00, 0, '2025-04-24 19:26:09', '2025-04-24 20:21:12', 'Còn hàng', 'https://images.samsung.com/garmin-enduro-3.jpg', '{"camera": "Không có", "battery": "Thời lượng 90 ngày với sạc năng lượng mặt trời", "features": "Màn hình AMOLED, GPS đa băng tần, bản đồ TopoActive, đo SpO2, ECG", "configMemory": "32GB bộ nhớ trong", "connectivity": "Wi-Fi, Bluetooth, ANT+, NFC", "designMaterials": "Titan Grade 5, kính sapphire, dây đeo UltraFit nylon"}'),
	(47, 'HUAWEI Watch GT 4 Pro', 48, 'Đồng hồ thông minh cao cấp', 8990000.00, 0, '2025-04-24 19:26:09', '2025-04-24 20:21:12', 'Còn hàng', 'https://images.samsung.com/huawei-watch-gt4-pro.jpg', '{"camera": "Không có", "battery": "Thời lượng pin lên đến 14 ngày", "features": "Màn hình AMOLED 1.43 inch, 100+ chế độ thể thao, TruSleep 3.0, TruSeen 5.5+", "configMemory": "4GB bộ nhớ trong", "connectivity": "Bluetooth 5.2, NFC", "designMaterials": "Khung titanium, kính sapphire, dây đeo da/titanium"}'),
	(48, 'Fitbit Sense 2', 48, 'Đồng hồ thông minh theo dõi sức khỏe', 6990000.00, 0, '2025-04-24 19:26:09', '2025-04-24 20:21:12', 'Còn hàng', 'https://images.samsung.com/fitbit-sense-2.jpg', '{"camera": "Không có", "battery": "Thời lượng lên đến 6 ngày", "features": "Cảm biến EDA liên tục, ECG, theo dõi stress, đo nhiệt độ da, GPS tích hợp", "configMemory": "4GB bộ nhớ trong", "connectivity": "Bluetooth 5.0, NFC", "designMaterials": "Khung nhôm, kính Gorilla Glass 3, dây silicone"}'),
	(49, 'Canon EOS R6 Mark II', 32, 'Máy ảnh mirrorless full-frame', 59990000.00, 0, '2025-04-24 19:26:09', '2025-04-24 20:21:12', 'Còn hàng', 'https://images.samsung.com/canon-eos-r6-mk2.jpg', '{"camera": "Cảm biến Full-frame 24.2MP, quay video 4K 60fps 10-bit", "battery": "Pin LP-E6NH, thời lượng 580 ảnh mỗi lần sạc", "features": "IBIS 8 cấp độ, 40fps chụp liên tiếp, Eye AF động vật/người, màn hình LCD xoay lật", "configMemory": "Khe cắm thẻ nhớ kép SD UHS-II", "connectivity": "Wi-Fi 5GHz, Bluetooth 5.0, USB-C, HDMI, jack 3.5mm", "designMaterials": "Khung hợp kim magiê, chống bụi và ẩm"}'),
	(50, 'DJI Mavic 3 Pro', 31, 'Flycam cao cấp với 3 camera', 48990000.00, 0, '2025-04-24 19:26:09', '2025-04-24 20:21:12', 'Còn hàng', 'https://images.samsung.com/dji-mavic-3-pro.jpg', '{"camera": "Camera chính Hasselblad 4/3 CMOS 20MP, camera tele 70mm và 166mm", "battery": "5000mAh, thời gian bay lên đến 43 phút", "features": "Quay video 5.1K/50fps, ProRes 422 HQ, hỗ trợ D-Log, hệ thống truyền O3+", "configMemory": "8GB bộ nhớ trong, hỗ trợ thẻ microSD", "connectivity": "Wi-Fi 6, Bluetooth 5.2, OcuSync 3.0+", "designMaterials": "Khung composite, cánh quạt tiêu âm, cảm biến tránh vật cản toàn hướng"}'),
	(51, 'Insta360 X4', 31, 'Camera 360 độ 8K', 14990000.00, 0, '2025-04-24 19:26:09', '2025-04-24 20:21:12', 'Còn hàng', 'https://images.samsung.com/insta360-x4.jpg', '{"camera": "Cảm biến dual 8K, quay 360 độ 8K 30fps, 4K 120fps", "battery": "2290mAh, thời lượng 135 phút ở 5.7K 30fps", "features": "FlowState ổn định, chống nước 10m, AI editing, quay TimeShift", "configMemory": "Hỗ trợ thẻ microSD lên đến 2TB", "connectivity": "Wi-Fi 6, Bluetooth 5.2, USB-C", "designMaterials": "Khung nhôm, kính cường lực, chống sốc"}'),
	(52, 'Xbox Series X', 40, 'Máy chơi game thế hệ mới từ Microsoft', 13990000.00, 0, '2025-04-24 19:26:09', '2025-04-24 20:21:12', 'Còn hàng', 'https://images.samsung.com/xbox-series-x.jpg', '{"camera": "Không có (Hỗ trợ Kinect riêng)", "battery": "Không có (Kết nối điện)", "features": "4K 120Hz, ray tracing, tải nhanh, Quick Resume, hỗ trợ Dolby Vision/Atmos", "configMemory": "16GB GDDR6, SSD 1TB tùy chỉnh", "connectivity": "Wi-Fi 6, Bluetooth 5.1, HDMI 2.1, USB-A x3, Ethernet", "designMaterials": "Nhựa ABS, tản nhiệt vapor chamber"}'),
	(53, 'Nintendo Switch OLED', 40, 'Máy chơi game cầm tay với màn hình OLED', 8490000.00, 0, '2025-04-24 19:26:09', '2025-04-24 20:21:12', 'Còn hàng', 'https://images.samsung.com/nintendo-switch-oled.jpg', '{"camera": "Không có", "battery": "4310mAh, thời lượng 4.5-9 giờ tùy game", "features": "Màn hình OLED 7 inch, chân đế điều chỉnh, loa nâng cấp", "configMemory": "64GB bộ nhớ trong, hỗ trợ thẻ microSD", "connectivity": "Wi-Fi, Bluetooth, USB-C, cổng LAN (khi dock)", "designMaterials": "Nhựa cao cấp, kính cường lực, Joy-Con có thể tháo rời"}'),
	(54, 'PlayStation VR2', 41, 'Kính thực tế ảo cho PlayStation 5', 15990000.00, 0, '2025-04-24 19:26:09', '2025-04-24 20:21:12', 'Còn hàng', 'https://images.samsung.com/playstation-vr2.jpg', '{"camera": "4 camera theo dõi, camera IR cho theo dõi mắt", "battery": "Không có (Kết nối với PS5)", "features": "Màn hình OLED 2000x2040 mỗi mắt, tần số quét 120Hz, phản hồi xúc giác, theo dõi chuyển động mắt", "configMemory": "Không có", "connectivity": "USB-C kết nối với PS5", "designMaterials": "Nhựa cao cấp, đệm mặt có thể điều chỉnh, dây đeo cân bằng"}'),
	(55, 'Netgear Orbi WiFi 7', 26, 'Hệ thống mesh WiFi 7 cao cấp', 29990000.00, 0, '2025-04-24 19:30:25', '2025-04-24 20:21:12', 'Còn hàng', 'https://images.samsung.com/netgear-orbi-wifi7.jpg', '{"camera": "Không có", "battery": "Không có (Kết nối điện)", "features": "WiFi 7 BE19000, băng tần 320MHz, MLO, 10Gbps backhaul, bảo mật Armor", "configMemory": "2GB RAM, 512MB Flash", "connectivity": "1x 10G WAN, 1x 10G LAN, 4x Gigabit LAN, USB 3.0", "designMaterials": "Nhựa ABS cao cấp, thiết kế tản nhiệt đặc biệt"}'),
	(56, 'ASUS RT-BE96U', 27, 'Router WiFi 7 hiệu năng cao', 15990000.00, 0, '2025-04-24 19:30:25', '2025-04-24 20:21:12', 'Còn hàng', 'https://images.samsung.com/asus-rt-be96u.jpg', '{"camera": "Không có", "battery": "Không có (Kết nối điện)", "features": "WiFi 7 BE19000, Multi-Link Operation, AiMesh 2.0, AiProtection Pro", "configMemory": "2GB RAM, 512MB Flash", "connectivity": "1x 10G WAN/LAN, 1x 2.5G WAN/LAN, 4x Gigabit LAN, USB 3.2 Gen 2 x1", "designMaterials": "Nhựa ABS, 9 ăng-ten ngoài cao cấp"}'),
	(57, 'Synology DS1522+', 27, 'NAS hiệu năng cao cho doanh nghiệp nhỏ', 16990000.00, 0, '2025-04-24 19:30:25', '2025-04-24 20:21:12', 'Còn hàng', 'https://images.samsung.com/synology-ds1522.jpg', '{"camera": "Không có", "battery": "Không có (Kết nối điện)", "features": "5 khay ổ cứng, mã hóa phần cứng AES-NI, Snapshot Replication, Synology Drive", "configMemory": "8GB RAM DDR4 ECC (nâng cấp được đến 32GB)", "connectivity": "2x 1GbE RJ-45, 2x USB 3.2 Gen 1, slot mở rộng 10GbE", "designMaterials": "Khung kim loại, vỏ nhựa cao cấp, khay ổ cứng không cần công cụ"}'),
	(58, 'AMD Radeon RX 7900 XTX', 34, 'Card đồ họa cao cấp AMD', 24990000.00, 0, '2025-04-24 19:30:25', '2025-04-24 20:21:12', 'Còn hàng', 'https://images.samsung.com/amd-rx-7900-xtx.jpg', '{"camera": "Không có", "battery": "Không có (Yêu cầu nguồn 750W+)", "features": "Kiến trúc RDNA 3, ray tracing thế hệ 2, FSR 3.0, hỗ trợ AV1 encode/decode", "configMemory": "24GB GDDR6, 384-bit", "connectivity": "PCIe 4.0 x16, 2x DisplayPort 2.1, 1x HDMI 2.1a, USB-C", "designMaterials": "Tản nhiệt vapor chamber, quạt kép, đèn LED RGB"}'),
	(59, 'Intel Core i9-14900KS', 35, 'CPU cao cấp từ Intel', 18990000.00, 0, '2025-04-24 19:30:25', '2025-04-24 20:21:12', 'Còn hàng', 'https://images.samsung.com/intel-i9-14900ks.jpg', '{"camera": "Không có", "battery": "Không có (TDP 150W, turbo lên đến 320W)", "features": "Xung nhịp nền 3.2GHz, xung turbo 6.2GHz, 36MB L3 cache, hỗ trợ DDR5-5600", "configMemory": "24 nhân (8P+16E), 32 luồng", "connectivity": "Socket LGA 1700, hỗ trợ PCIe 5.0, Thunderbolt 4", "designMaterials": "IHS bạc mạ vàng, đế đồng"}'),
	(60, 'Corsair Dominator Titanium', 36, 'RAM DDR5 cao cấp', 5990000.00, 0, '2025-04-24 19:30:25', '2025-04-24 20:21:12', 'Còn hàng', 'https://images.samsung.com/corsair-dominator-titanium.jpg', '{"camera": "Không có", "battery": "Không có", "features": "Tản nhiệt nhôm CNC, đèn LED RGB Capellix thế hệ 2, iCUE support, XMP 3.0", "configMemory": "32GB (2x16GB) DDR5-8000 CL38", "connectivity": "PMIC tùy chỉnh, PCB 12 lớp", "designMaterials": "Nhôm CNC, bạc mạ rhodium"}'),
	(61, 'Samsung 990 PRO', 37, 'SSD NVMe Gen4 hiệu năng cao', 3990000.00, 0, '2025-04-24 19:30:25', '2025-04-24 20:21:12', 'Còn hàng', 'https://images.samsung.com/samsung-990-pro.jpg', '{"camera": "Không có", "battery": "Không có", "features": "Đọc 7450MB/s, ghi 6900MB/s, controller tự thiết kế, NAND 8th-gen V-NAND", "configMemory": "2TB PCIe 4.0 NVMe", "connectivity": "PCIe 4.0 x4, M.2 2280", "designMaterials": "Tản nhiệt nickel-coated, nhãn graphene, lớp bảo vệ EMI"}'),
	(62, 'NZXT Kraken Elite 360', 38, 'Tản nhiệt nước AIO cao cấp', 6490000.00, 0, '2025-04-24 19:30:25', '2025-04-24 20:21:12', 'Còn hàng', 'https://images.samsung.com/nzxt-kraken-elite-360.jpg', '{"camera": "Không có", "battery": "Không có", "features": "Màn hình LCD IPS 2.1 inch, 3 quạt 120mm RGB, bơm thế hệ 8, hỗ trợ phần mềm CAM", "configMemory": "Không có", "connectivity": "USB 2.0 internal header", "designMaterials": "Bơm nhựa ABS và nhôm, ống cao su bọc nylon, quạt khung nhựa cánh nhựa"}'),
	(63, 'LG CordZero A9', 23, 'Máy hút bụi không dây cao cấp', 14990000.00, 0, '2025-04-24 19:30:25', '2025-04-24 20:21:12', 'Còn hàng', 'https://images.samsung.com/lg-cordzero-a9.jpg', '{"camera": "Không có", "battery": "Pin kép, thời lượng 120 phút, sạc nhanh trạm sạc", "features": "Trạm giữ tự động làm sạch, đầu hút thông minh, lọc HEPA 5 lớp", "configMemory": "Bộ nhớ cho các chế độ hút", "connectivity": "Kết nối với ứng dụng LG ThinQ", "designMaterials": "Nhựa ABS cao cấp, nhôm nhẹ, lọc rửa được"}'),
	(64, 'Miele Complete C3', 23, 'Máy hút bụi truyền thống cao cấp', 12990000.00, 0, '2025-04-24 19:30:25', '2025-04-24 20:21:12', 'Còn hàng', 'https://images.samsung.com/miele-complete-c3.jpg', '{"camera": "Không có", "battery": "Không có (Kết nối điện)", "features": "Động cơ Vortex 890W, 6 chế độ hút, lọc HEPA AirClean, chức năng im lặng", "configMemory": "Không có", "connectivity": "Không có", "designMaterials": "Nhựa ABS cao cấp, vỏ cứng bền, bánh xe mềm"}'),
	(65, 'Philips Airfryer XXL', 42, 'Nồi chiên không dầu dung tích lớn', 6990000.00, 0, '2025-04-24 19:30:25', '2025-04-24 20:21:12', 'Còn hàng', 'https://images.samsung.com/philips-airfryer-xxl.jpg', '{"camera": "Không có", "battery": "Không có (Kết nối điện)", "features": "Công nghệ Twin TurboStar, dung tích 7.3L, công suất 2225W, màn hình cảm ứng", "configMemory": "Lưu trữ 7 chương trình nấu", "connectivity": "Bluetooth, kết nối ứng dụng NutriU", "designMaterials": "Nhựa chịu nhiệt cao cấp, khay không dính"}'),
	(66, 'Bosch Series 8 Washing Machine', 43, 'Máy giặt thông minh cửa trước', 24990000.00, 0, '2025-04-24 19:30:25', '2025-04-24 20:21:12', 'Còn hàng', 'https://images.samsung.com/bosch-series8-washer.jpg', '{"camera": "Không có", "battery": "Không có (Kết nối điện)", "features": "Công nghệ i-DOS tự định lượng, 4D Wash, động cơ EcoSilence Drive, 12kg", "configMemory": "Bộ nhớ cho các chương trình giặt", "connectivity": "Wi-Fi, HomeConnect app", "designMaterials": "Thép không gỉ, cửa kính cường lực, lồng giặt inox"}'),
	(67, 'KitchenAid Artisan Stand Mixer', 42, 'Máy trộn đứng cao cấp', 13990000.00, 0, '2025-04-24 19:30:25', '2025-04-24 20:21:12', 'Còn hàng', 'https://images.samsung.com/kitchenaid-artisan.jpg', '{"camera": "Không có", "battery": "Không có (Kết nối điện)", "features": "Động cơ DC công suất cao, 10 tốc độ, chảo 4.8L, hub phụ kiện đa năng", "configMemory": "Không có", "connectivity": "Không có", "designMaterials": "Thân kim loại đúc, phủ sơn tĩnh điện, chảo thép không gỉ"}'),
	(68, 'Belkin BoostCharge Pro', 49, 'Sạc không dây 3-in-1 với MagSafe', 2990000.00, 0, '2025-04-24 19:30:25', '2025-04-24 20:21:12', 'Còn hàng', 'https://images.samsung.com/belkin-boostcharge-pro.jpg', '{"camera": "Không có", "battery": "Không có (Kết nối điện)", "features": "MagSafe 15W cho iPhone, 5W cho AirPods, 5W cho Apple Watch, đế xoay và điều chỉnh", "configMemory": "Không có", "connectivity": "USB-C PD", "designMaterials": "Nhôm, silicone chống trượt, đèn LED trạng thái"}'),
	(69, 'Anker Prime Power Bank', 14, 'Pin dự phòng dung lượng cao', 3490000.00, 0, '2025-04-24 19:30:25', '2025-04-24 20:21:12', 'Còn hàng', 'https://images.samsung.com/anker-prime-power-bank.jpg', '{"camera": "Không có", "battery": "27000mAh, sạc nhanh 250W, sạc pin 1 giờ", "features": "Màn hình OLED thông minh, công nghệ GaN, sạc đồng thời 3 thiết bị", "configMemory": "Không có", "connectivity": "USB-C x3, PPS, QC 5.0, PD 3.1", "designMaterials": "Khung nhôm nguyên khối, vỏ phủ nano carbon"}'),
	(70, 'Apple Mac Mini M3 Pro', 13, 'Máy tính mini hiệu năng cao', 29990000.00, 0, '2025-04-24 19:30:25', '2025-04-24 20:21:12', 'Còn hàng', 'https://images.samsung.com/apple-mac-mini-m3-pro.jpg', '{"camera": "Không có", "battery": "Không có (Kết nối điện)", "features": "Chip M3 Pro 12-core CPU, 18-core GPU, Neural Engine 16-core", "configMemory": "32GB RAM, 1TB SSD", "connectivity": "Thunderbolt 4 x4, HDMI 2.1, Ethernet 10Gb, USB-A x2, Wi-Fi 6E, Bluetooth 5.3", "designMaterials": "Khung nhôm nguyên khối, màu midnight"}'),
	(71, 'Logitech MX Keys S', 30, 'Bàn phím không dây cao cấp', 2790000.00, 0, '2025-04-24 19:30:25', '2025-04-24 20:21:12', 'Còn hàng', 'https://images.samsung.com/logitech-mx-keys-s.jpg', '{"camera": "Không có", "battery": "Thời lượng lên đến 10 ngày với đèn nền, 5 tháng không đèn nền", "features": "Phím Perfect Stroke, đèn nền thông minh, ghép nối 3 thiết bị, phím Smart Actions", "configMemory": "Không có", "connectivity": "Bluetooth, Logi Bolt USB", "designMaterials": "Nhôm cao cấp, nhựa tái chế, phím lõm êm"}'),
	(72, 'ASUS ProArt PA32UCG', 5, 'Màn hình chuyên đồ họa cao cấp', 79990000.00, 0, '2025-04-24 19:30:25', '2025-04-24 20:21:12', 'Còn hàng', 'https://images.samsung.com/asus-proart-pa32ucg.jpg', '{"camera": "Không có", "battery": "Không có (Kết nối điện)", "features": "Mini LED 4K HDR 1600, 120Hz, Quantum Dot, Calman verified, Delta E < 1", "configMemory": "Không có", "connectivity": "HDMI 2.1 x2, DisplayPort 1.4, Thunderbolt 4, USB hub", "designMaterials": "Khung kim loại, chân đế chống rung, lọc ánh sáng xanh"}'),
	(73, 'Apple AirTag', 47, 'Thiết bị theo dõi vị trí', 790000.00, 0, '2025-04-24 19:30:25', '2025-04-24 20:21:12', 'Còn hàng', 'https://images.samsung.com/apple-airtag.jpg', '{"camera": "Không có", "battery": "Pin CR2032 thời lượng 1 năm", "features": "Chip U1 Ultra Wideband, kết nối Precision Finding, loa tích hợp", "configMemory": "Không có", "connectivity": "Bluetooth, NFC", "designMaterials": "Thép không gỉ, nhựa trắng, chống nước IP67"}'),
	(74, 'Elgato Stream Deck MK.2', 39, 'Bàn phím điều khiển cho streamer', 3990000.00, 0, '2025-04-24 19:30:25', '2025-04-24 20:21:12', 'Còn hàng', 'https://images.samsung.com/elgato-stream-deck-mk2.jpg', '{"camera": "Không có", "battery": "Không có (Kết nối USB)", "features": "15 phím LCD có thể tùy chỉnh, tích hợp với OBS, Twitch, YouTube", "configMemory": "Không có", "connectivity": "USB-C có thể tháo rời", "designMaterials": "Nhựa ABS, mặt trước nhôm, chân đế có thể thay đổi"}'),
	(75, 'SteelSeries Arctis Nova Pro', 39, 'Tai nghe gaming không dây cao cấp', 7990000.00, 0, '2025-04-24 19:30:25', '2025-04-24 20:21:12', 'Còn hàng', 'https://images.samsung.com/steelseries-arctis-nova-pro.jpg', '{"camera": "Không có", "battery": "Hệ thống 2 pin hotswap, thời lượng 44 giờ", "features": "ANC chủ động, âm thanh Hi-Fi 40 kHz, microphone AI, trạm DAC GameDAC Gen 2", "configMemory": "Không có", "connectivity": "2.4GHz, Bluetooth 5.0, USB-C, 3.5mm", "designMaterials": "Thép không gỉ, earcup bộ nhớ protein, đệm đầu ComfortMAX"}'),
	(76, 'Amazon Fire TV Cube', 45, 'Thiết bị streaming và điều khiển bằng giọng nói', 3490000.00, 0, '2025-04-24 19:30:25', '2025-04-24 20:21:12', 'Còn hàng', 'https://images.samsung.com/amazon-fire-tv-cube.jpg', '{"camera": "Không có", "battery": "Không có (Kết nối điện)", "features": "4K Ultra HD, Dolby Vision, Alexa tích hợp, điều khiển IR, xử lý octa-core", "configMemory": "16GB bộ nhớ trong", "connectivity": "Wi-Fi 6E, Bluetooth 5.0, HDMI eARC, Ethernet", "designMaterials": "Nhựa mờ cao cấp, lưới loa vải"}'),
	(77, 'GoPro Max 2', 31, 'Camera 360 độ chống rung cao cấp', 15990000.00, 0, '2025-04-24 19:30:25', '2025-04-24 20:21:12', 'Còn hàng', 'https://images.samsung.com/gopro-max-2.jpg', '{"camera": "Cảm biến dual 5.7K, 360 độ hoặc HERO mode", "battery": "1800mAh, thời lượng 85 phút quay 360 độ", "features": "HyperSmooth 5.0, 6 microphone, chống nước 10m, horizon lock 360 độ", "configMemory": "128GB bộ nhớ trong", "connectivity": "Wi-Fi 6, Bluetooth 5.2, USB-C, GPS", "designMaterials": "Khung nhôm, kính cường lực, chống sốc"}'),
	(78, 'Razer Basilisk V3 Pro', 39, 'Chuột gaming không dây cao cấp', 3990000.00, 0, '2025-04-24 19:30:25', '2025-04-24 20:21:12', 'Còn hàng', 'https://images.samsung.com/razer-basilisk-v3-pro.jpg', '{"camera": "Không có", "battery": "Thời lượng lên đến 90 giờ, hỗ trợ sạc không dây", "features": "Cảm biến Focus Pro 30K DPI, 11 nút lập trình, con lăn HyperScroll, RGB Chroma", "configMemory": "Không có", "connectivity": "Razer HyperSpeed Wireless, Bluetooth, USB-C", "designMaterials": "Nhựa ABS cao cấp, switch quang học gen 3, feet PTFE"}'),
	(79, 'LG StanbyMe Go', 21, 'TV di động với pin tích hợp', 26990000.00, 0, '2025-04-24 19:30:25', '2025-04-24 20:21:12', 'Còn hàng', 'https://images.samsung.com/lg-stanbyme-go.jpg', '{"camera": "Không có", "battery": "Thời lượng lên đến 3 giờ", "features": "Màn hình 27 inch Full HD, thiết kế vali, webOS, loa 20W", "configMemory": "8GB bộ nhớ trong", "connectivity": "Wi-Fi 5, Bluetooth 5.0, HDMI, USB", "designMaterials": "Vali chống sốc, chống nước, chân đế tích hợp"}'),
	(80, 'Zhiyun Smooth 5S', 31, 'Gimbal chống rung cho smartphone', 3490000.00, 0, '2025-04-24 19:30:25', '2025-04-24 20:21:12', 'Còn hàng', 'https://images.samsung.com/zhiyun-smooth-5s.jpg', '{"camera": "Không có", "battery": "2600mAh, thời lượng đến 12 giờ", "features": "Động cơ mạnh, núm điều khiển lấy nét và zoom, đèn fill 5W tích hợp", "configMemory": "Không có", "connectivity": "USB-C, Bluetooth, ZY app", "designMaterials": "Nhôm CNC, núm điều khiển kim loại, tay cầm silicon"}'),
	(81, 'Devialet Mania', 17, 'Loa bluetooth cao cấp với âm thanh 360 độ', 19900000.00, 0, '2025-04-24 19:30:25', '2025-04-24 20:21:12', 'Còn hàng', 'https://images.samsung.com/devialet-mania.jpg', '{"camera": "Không có", "battery": "10 giờ phát nhạc, sạc nhanh không dây", "features": "4 full-range driver, 2 subwoofer, Active Stereo Calibration, chống nước IPX4", "configMemory": "Không có", "connectivity": "Wi-Fi 5, Bluetooth 5.0, AirPlay 2, Spotify Connect", "designMaterials": "Khung nhôm mạ vàng, lưới loa dệt cao cấp"}'),
	(82, 'Leica Q3', 32, 'Máy ảnh compact full-frame cao cấp', 115990000.00, 0, '2025-04-24 19:30:25', '2025-04-24 20:21:12', 'Còn hàng', 'https://images.samsung.com/leica-q3.jpg', '{"camera": "Cảm biến full-frame 60MP, ống kính Summilux 28mm f/1.7 ASPH", "battery": "1800mAh, thời lượng 350 ảnh mỗi lần sạc", "features": "Màn hình cảm ứng lật 3 inch, EVF OLED 5.76M điểm ảnh, quay video 8K", "configMemory": "Khe cắm thẻ CFexpress Type B", "connectivity": "Wi-Fi 6, Bluetooth 5.0, USB-C", "designMaterials": "Khung magiê, vỏ nhôm, da thật, kính sapphire"}'),
	(83, 'Shure AONIC 50 Gen 2', 16, 'Tai nghe over-ear chất lượng studio', 8990000.00, 0, '2025-04-24 19:30:25', '2025-04-24 20:21:12', 'Còn hàng', 'https://images.samsung.com/shure-aonic-50-gen2.jpg', '{"camera": "Không có", "battery": "45 giờ sử dụng, sạc nhanh 15 phút cho 5 giờ phát nhạc", "features": "Driver 50mm Neodymium, ANC điều chỉnh, chế độ ambient, codec LDAC/aptX Lossless", "configMemory": "Không có", "connectivity": "Bluetooth 5.3, jack 3.5mm, USB-C audio", "designMaterials": "Khung nhôm, đệm tai memory foam bọc da cao cấp"}'),
	(84, 'BenQ X3000i', 44, 'Máy chiếu 4K chuyên gaming', 43990000.00, 0, '2025-04-24 19:30:25', '2025-04-24 20:21:12', 'Còn hàng', 'https://images.samsung.com/benq-x3000i.jpg', '{"camera": "Không có", "battery": "Không có (Kết nối điện)", "features": "4K UHD, 3000 lumens, 100% DCI-P3, input lag 4.16ms, chế độ 1080p 240Hz", "configMemory": "Không có", "connectivity": "HDMI 2.0b x2, eARC, USB 2.0 Type-A x2, RS-232, Android TV dongle", "designMaterials": "Nhựa cao cấp, tản nhiệt đồng, ống kính precision glass"}'),
	(85, 'Sennheiser Profile USB', 46, 'Microphone USB cho streamer và podcaster', 2990000.00, 0, '2025-04-24 19:30:25', '2025-04-24 20:21:12', 'Còn hàng', 'https://images.samsung.com/sennheiser-profile-usb.jpg', '{"camera": "Không có", "battery": "Không có (Kết nối USB)", "features": "Pattern cardioid, giảm tiếng ồn AI, núm điều chỉnh vật lý, giá treo shock-mount", "configMemory": "Không có", "connectivity": "USB-C, jack tai nghe 3.5mm không độ trễ", "designMaterials": "Thân kim loại, lưới kim loại, giá treo chống rung"}');

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
) ENGINE=InnoDB AUTO_INCREMENT=10004 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table quanlybanhang.users: ~39 rows (approximately)
INSERT INTO `users` (`id`, `username`, `password`, `full_name`, `email`, `phone`, `role`, `status`) VALUES
	(1, 'quang', '$argon2id$v=19$m=65536,t=2,p=1$ylC4mNIxzapo8ofq9O6xHw$PFHF9DhA3h6S8BpN5RI5+RlGpOQ/fWtrwj/WFxvjuog', 'Trịnh Đức Quang', 'quang.td.2430@aptechlearning.edu.vn', '0904700025', 'Admin', 'Unlock'),
	(2, 'nvk01', '$argon2id$v=19$m=65536,t=3,p=2$jIdw8PVlE9UbszsLtomCHQ$OmK/fzWKjxzSV/PgH95rPKndBAuEUPUsgbrMveR3+ag', 'Nguyễn Văn Khoa', 'nvk01@aptechlearning.edu.vn', '0904700026', 'Nhân viên kho', 'Unlock'),
	(3, 'ntn01', '$argon2id$v=19$m=65536,t=3,p=2$jIdw8PVlE9UbszsLtomCHQ$OmK/fzWKjxzSV/PgH95rPKndBAuEUPUsgbrMveR3+ag', 'Nguyễn Thị Ngọc', 'ntn01@aptechlearning.edu.vn', '0904700027', 'Nhân viên thu ngân', 'Unlock'),
	(4, 'nbs01', '$argon2id$v=19$m=65536,t=3,p=2$jIdw8PVlE9UbszsLtomCHQ$OmK/fzWKjxzSV/PgH95rPKndBAuEUPUsgbrMveR3+ag', 'Nguyễn Bảo Sơn', 'nbs01@aptechlearning.edu.vn', '0904700028', 'Nhân viên bán hàng', 'Unlock'),
	(5, 'nvk02', '$argon2id$v=19$m=65536,t=3,p=2$jIdw8PVlE9UbszsLtomCHQ$OmK/fzWKjxzSV/PgH95rPKndBAuEUPUsgbrMveR3+ag', 'Nguyễn Văn Khoa', 'nvk02@aptechlearning.edu.vn', '0904700029', 'Nhân viên kho', 'Unlock'),
	(6, 'ntn02', '$argon2id$v=19$m=65536,t=3,p=2$jIdw8PVlE9UbszsLtomCHQ$OmK/fzWKjxzSV/PgH95rPKndBAuEUPUsgbrMveR3+ag', 'Nguyễn Thị Ngọc', 'ntn02@aptechlearning.edu.vn', '0904700030', 'Nhân viên thu ngân', 'Unlock'),
	(7, 'nbs02', '$argon2id$v=19$m=65536,t=3,p=2$jIdw8PVlE9UbszsLtomCHQ$OmK/fzWKjxzSV/PgH95rPKndBAuEUPUsgbrMveR3+ag', 'Nguyễn Bảo Sơn', 'nbs02@aptechlearning.edu.vn', '0904700031', 'Nhân viên bán hàng', 'Unlock'),
	(8, 'nvk03', '$argon2id$v=19$m=65536,t=3,p=2$jIdw8PVlE9UbszsLtomCHQ$OmK/fzWKjxzSV/PgH95rPKndBAuEUPUsgbrMveR3+ag', 'Nguyễn Văn Khoa', 'nvk03@aptechlearning.edu.vn', '0904700032', 'Nhân viên kho', 'Unlock'),
	(9, 'ntn03', '$argon2id$v=19$m=65536,t=3,p=2$jIdw8PVlE9UbszsLtomCHQ$OmK/fzWKjxzSV/PgH95rPKndBAuEUPUsgbrMveR3+ag', 'Nguyễn Thị Ngọc', 'ntn03@aptechlearning.edu.vn', '0904700033', 'Nhân viên thu ngân', 'Unlock'),
	(10, 'nbs03', '$argon2id$v=19$m=65536,t=3,p=2$jIdw8PVlE9UbszsLtomCHQ$OmK/fzWKjxzSV/PgH95rPKndBAuEUPUsgbrMveR3+ag', 'Nguyễn Bảo Sơn', 'nbs03@aptechlearning.edu.vn', '0904700034', 'Nhân viên bán hàng', 'Unlock'),
	(11, 'nvk04', '$argon2id$v=19$m=65536,t=3,p=2$jIdw8PVlE9UbszsLtomCHQ$OmK/fzWKjxzSV/PgH95rPKndBAuEUPUsgbrMveR3+ag', 'Nguyễn Văn Khoa', 'nvk04@aptechlearning.edu.vn', '0904700035', 'Nhân viên kho', 'Unlock'),
	(12, 'ntn04', '$argon2id$v=19$m=65536,t=3,p=2$jIdw8PVlE9UbszsLtomCHQ$OmK/fzWKjxzSV/PgH95rPKndBAuEUPUsgbrMveR3+ag', 'Nguyễn Thị Ngọc', 'ntn04@aptechlearning.edu.vn', '0904700036', 'Nhân viên thu ngân', 'Unlock'),
	(13, 'nbs04', '$argon2id$v=19$m=65536,t=3,p=2$jIdw8PVlE9UbszsLtomCHQ$OmK/fzWKjxzSV/PgH95rPKndBAuEUPUsgbrMveR3+ag', 'Nguyễn Bảo Sơn', 'nbs04@aptechlearning.edu.vn', '0904700037', 'Nhân viên bán hàng', 'Unlock'),
	(14, 'nvk05', '$argon2id$v=19$m=65536,t=3,p=2$jIdw8PVlE9UbszsLtomCHQ$OmK/fzWKjxzSV/PgH95rPKndBAuEUPUsgbrMveR3+ag', 'Nguyễn Văn Khoa', 'nvk05@aptechlearning.edu.vn', '0904700038', 'Nhân viên kho', 'Unlock'),
	(15, 'ntn05', '$argon2id$v=19$m=65536,t=3,p=2$jIdw8PVlE9UbszsLtomCHQ$OmK/fzWKjxzSV/PgH95rPKndBAuEUPUsgbrMveR3+ag', 'Nguyễn Thị Ngọc', 'ntn05@aptechlearning.edu.vn', '0904700039', 'Nhân viên thu ngân', 'Unlock'),
	(16, 'nbs05', '$argon2id$v=19$m=65536,t=3,p=2$jIdw8PVlE9UbszsLtomCHQ$OmK/fzWKjxzSV/PgH95rPKndBAuEUPUsgbrMveR3+ag', 'Nguyễn Bảo Sơn', 'nbs05@aptechlearning.edu.vn', '0904700040', 'Nhân viên bán hàng', 'Unlock'),
	(17, 'nvk06', '$argon2id$v=19$m=65536,t=3,p=2$jIdw8PVlE9UbszsLtomCHQ$OmK/fzWKjxzSV/PgH95rPKndBAuEUPUsgbrMveR3+ag', 'Nguyễn Văn Khoa', 'nvk06@aptechlearning.edu.vn', '0904700041', 'Nhân viên kho', 'Unlock'),
	(18, 'ntn06', '$argon2id$v=19$m=65536,t=3,p=2$jIdw8PVlE9UbszsLtomCHQ$OmK/fzWKjxzSV/PgH95rPKndBAuEUPUsgbrMveR3+ag', 'Nguyễn Thị Ngọc', 'ntn06@aptechlearning.edu.vn', '0904700042', 'Nhân viên thu ngân', 'Unlock'),
	(19, 'nbs06', '$argon2id$v=19$m=65536,t=3,p=2$jIdw8PVlE9UbszsLtomCHQ$OmK/fzWKjxzSV/PgH95rPKndBAuEUPUsgbrMveR3+ag', 'Nguyễn Bảo Sơn', 'nbs06@aptechlearning.edu.vn', '0904700043', 'Nhân viên bán hàng', 'Unlock'),
	(20, 'nvk07', '$argon2id$v=19$m=65536,t=3,p=2$jIdw8PVlE9UbszsLtomCHQ$OmK/fzWKjxzSV/PgH95rPKndBAuEUPUsgbrMveR3+ag', 'Nguyễn Văn Khoa', 'nvk07@aptechlearning.edu.vn', '0904700044', 'Nhân viên kho', 'Unlock'),
	(21, 'ntn07', '$argon2id$v=19$m=65536,t=3,p=2$jIdw8PVlE9UbszsLtomCHQ$OmK/fzWKjxzSV/PgH95rPKndBAuEUPUsgbrMveR3+ag', 'Nguyễn Thị Ngọc', 'ntn07@aptechlearning.edu.vn', '0904700045', 'Nhân viên thu ngân', 'Unlock'),
	(22, 'nbs07', '$argon2id$v=19$m=65536,t=3,p=2$jIdw8PVlE9UbszsLtomCHQ$OmK/fzWKjxzSV/PgH95rPKndBAuEUPUsgbrMveR3+ag', 'Nguyễn Bảo Sơn', 'nbs07@aptechlearning.edu.vn', '0904700046', 'Nhân viên bán hàng', 'Unlock'),
	(23, 'nvk08', '$argon2id$v=19$m=65536,t=3,p=2$jIdw8PVlE9UbszsLtomCHQ$OmK/fzWKjxzSV/PgH95rPKndBAuEUPUsgbrMveR3+ag', 'Nguyễn Văn Khoa', 'nvk08@aptechlearning.edu.vn', '0904700047', 'Nhân viên kho', 'Unlock'),
	(24, 'ntn08', '$argon2id$v=19$m=65536,t=3,p=2$jIdw8PVlE9UbszsLtomCHQ$OmK/fzWKjxzSV/PgH95rPKndBAuEUPUsgbrMveR3+ag', 'Nguyễn Thị Ngọc', 'ntn08@aptechlearning.edu.vn', '0904700048', 'Nhân viên thu ngân', 'Unlock'),
	(25, 'nbs08', '$argon2id$v=19$m=65536,t=3,p=2$jIdw8PVlE9UbszsLtomCHQ$OmK/fzWKjxzSV/PgH95rPKndBAuEUPUsgbrMveR3+ag', 'Nguyễn Bảo Sơn', 'nbs08@aptechlearning.edu.vn', '0904700049', 'Nhân viên bán hàng', 'Unlock'),
	(26, 'nvk09', '$argon2id$v=19$m=65536,t=3,p=2$jIdw8PVlE9UbszsLtomCHQ$OmK/fzWKjxzSV/PgH95rPKndBAuEUPUsgbrMveR3+ag', 'Nguyễn Văn Khoa', 'nvk09@aptechlearning.edu.vn', '0904700050', 'Nhân viên kho', 'Unlock'),
	(27, 'ntn09', '$argon2id$v=19$m=65536,t=3,p=2$jIdw8PVlE9UbszsLtomCHQ$OmK/fzWKjxzSV/PgH95rPKndBAuEUPUsgbrMveR3+ag', 'Nguyễn Thị Ngọc', 'ntn09@aptechlearning.edu.vn', '0904700051', 'Nhân viên thu ngân', 'Unlock'),
	(28, 'nbs09', '$argon2id$v=19$m=65536,t=3,p=2$jIdw8PVlE9UbszsLtomCHQ$OmK/fzWKjxzSV/PgH95rPKndBAuEUPUsgbrMveR3+ag', 'Nguyễn Bảo Sơn', 'nbs09@aptechlearning.edu.vn', '0904700052', 'Nhân viên bán hàng', 'Unlock'),
	(29, 'nvk10', '$argon2id$v=19$m=65536,t=3,p=2$jIdw8PVlE9UbszsLtomCHQ$OmK/fzWKjxzSV/PgH95rPKndBAuEUPUsgbrMveR3+ag', 'Nguyễn Văn Khoa', 'nvk10@aptechlearning.edu.vn', '0904700053', 'Nhân viên kho', 'Unlock'),
	(30, 'ntn10', '$argon2id$v=19$m=65536,t=3,p=2$jIdw8PVlE9UbszsLtomCHQ$OmK/fzWKjxzSV/PgH95rPKndBAuEUPUsgbrMveR3+ag', 'Nguyễn Thị Ngọc', 'ntn10@aptechlearning.edu.vn', '0904700054', 'Nhân viên thu ngân', 'Unlock'),
	(31, 'nbs10', '$argon2id$v=19$m=65536,t=3,p=2$jIdw8PVlE9UbszsLtomCHQ$OmK/fzWKjxzSV/PgH95rPKndBAuEUPUsgbrMveR3+ag', 'Nguyễn Bảo Sơn', 'nbs10@aptechlearning.edu.vn', '0904700055', 'Nhân viên bán hàng', 'Unlock'),
	(32, 'nvk11', '$argon2id$v=19$m=65536,t=3,p=2$jIdw8PVlE9UbszsLtomCHQ$OmK/fzWKjxzSV/PgH95rPKndBAuEUPUsgbrMveR3+ag', 'Nguyễn Văn Khoa', 'nvk11@aptechlearning.edu.vn', '0904700056', 'Nhân viên kho', 'Unlock'),
	(33, 'ntn11', '$argon2id$v=19$m=65536,t=3,p=2$jIdw8PVlE9UbszsLtomCHQ$OmK/fzWKjxzSV/PgH95rPKndBAuEUPUsgbrMveR3+ag', 'Nguyễn Thị Ngọc', 'ntn11@aptechlearning.edu.vn', '0904700057', 'Nhân viên thu ngân', 'Unlock'),
	(34, 'nbs11', '$argon2id$v=19$m=65536,t=3,p=2$jIdw8PVlE9UbszsLtomCHQ$OmK/fzWKjxzSV/PgH95rPKndBAuEUPUsgbrMveR3+ag', 'Nguyễn Bảo Sơn', 'nbs11@aptechlearning.edu.vn', '0904700058', 'Nhân viên bán hàng', 'Unlock'),
	(35, 'nvk12', '$argon2id$v=19$m=65536,t=3,p=2$jIdw8PVlE9UbszsLtomCHQ$OmK/fzWKjxzSV/PgH95rPKndBAuEUPUsgbrMveR3+ag', 'Nguyễn Văn Khoa', 'nvk12@aptechlearning.edu.vn', '0904700059', 'Nhân viên kho', 'Unlock'),
	(36, 'ntn12', '$argon2id$v=19$m=65536,t=3,p=2$jIdw8PVlE9UbszsLtomCHQ$OmK/fzWKjxzSV/PgH95rPKndBAuEUPUsgbrMveR3+ag', 'Nguyễn Thị Ngọc', 'ntn12@aptechlearning.edu.vn', '0904700060', 'Nhân viên thu ngân', 'Unlock'),
	(37, 'nbs12', '$argon2id$v=19$m=65536,t=3,p=2$jIdw8PVlE9UbszsLtomCHQ$OmK/fzWKjxzSV/PgH95rPKndBAuEUPUsgbrMveR3+ag', 'Nguyễn Bảo Sơn', 'nbs12@aptechlearning.edu.vn', '0904700061', 'Nhân viên bán hàng', 'Unlock'),
	(38, 'nvk13', '$argon2id$v=19$m=65536,t=3,p=2$jIdw8PVlE9UbszsLtomCHQ$OmK/fzWKjxzSV/PgH95rPKndBAuEUPUsgbrMveR3+ag', 'Nguyễn Văn Khoa', 'nvk13@aptechlearning.edu.vn', '0904700062', 'Nhân viên kho', 'Unlock'),
	(39, 'ntn13', '$argon2id$v=19$m=65536,t=3,p=2$jIdw8PVlE9UbszsLtomCHQ$OmK/fzWKjxzSV/PgH95rPKndBAuEUPUsgbrMveR3+ag', 'Nguyễn Thị Ngọc', 'ntn13@aptechlearning.edu.vn', '0904700063', 'Nhân viên thu ngân', 'Unlock');

-- Dumping structure for table quanlybanhang.warehouse_transactions
CREATE TABLE IF NOT EXISTS `warehouse_transactions` (
  `id` int NOT NULL AUTO_INCREMENT,
  `product_id` int NOT NULL,
  `quantity` int NOT NULL,
  `unit_price` decimal(18,2) NOT NULL,
  `type` enum('Nhập Kho','Xuất Kho') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `note` text,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_by` int DEFAULT NULL,
  `transaction_code` varchar(20) DEFAULT NULL,
  `source_transaction_code` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `product_id` (`product_id`),
  KEY `fk_created_by` (`created_by`),
  CONSTRAINT `fk_created_by` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `warehouse_transactions_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1001 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table quanlybanhang.warehouse_transactions: ~0 rows (approximately)

-- Dumping structure for view quanlybanhang.warehouse_transaction_details
-- Creating temporary table to overcome VIEW dependency errors
CREATE TABLE `warehouse_transaction_details` (
	`id` INT(10) NOT NULL,
	`product_id` INT(10) NOT NULL,
	`quantity` INT(10) NOT NULL,
	`unit_price` DECIMAL(18,2) NOT NULL,
	`type` ENUM('Nhập Kho','Xuất Kho') NOT NULL COLLATE 'utf8mb4_0900_ai_ci',
	`note` TEXT NULL COLLATE 'utf8mb4_0900_ai_ci',
	`created_at` TIMESTAMP NULL,
	`updated_at` TIMESTAMP NULL,
	`transaction_code` VARCHAR(20) NULL COLLATE 'utf8mb4_0900_ai_ci',
	`created_by` INT(10) NULL,
	`source_transaction_code` VARCHAR(50) NULL COLLATE 'utf8mb4_0900_ai_ci',
	`product_name` VARCHAR(255) NOT NULL COLLATE 'utf8mb4_0900_ai_ci',
	`sell_price` DECIMAL(18,2) NOT NULL,
	`category_name` VARCHAR(100) NOT NULL COLLATE 'utf8mb4_0900_ai_ci',
	`created_by_name` VARCHAR(100) NULL COLLATE 'utf8mb4_0900_ai_ci'
) ENGINE=MyISAM;

-- Dumping structure for view quanlybanhang.order_summary
-- Removing temporary table and create final VIEW structure
DROP TABLE IF EXISTS `order_summary`;
CREATE ALGORITHM=UNDEFINED SQL SECURITY DEFINER VIEW `order_summary` AS select `o`.`id` AS `order_id`,`o`.`order_date` AS `order_date`,`o`.`status` AS `status`,`o`.`total_price` AS `total_price`,`o`.`shipping_fee` AS `shipping_fee`,`o`.`employee_id` AS `employee_id`,`o`.`note` AS `note`,`c`.`id` AS `customer_id`,`c`.`name` AS `customer_name`,group_concat(`p`.`id` order by `od`.`id` ASC separator ',') AS `product_ids`,group_concat(replace(`p`.`name`,',',' ') order by `od`.`id` ASC separator ', ') AS `product_names`,group_concat(`p`.`image_url` order by `od`.`id` ASC separator ', ') AS `product_images`,group_concat(`od`.`quantity` order by `od`.`id` ASC separator ',') AS `product_quantities`,group_concat(`od`.`price` order by `od`.`id` ASC separator ',') AS `product_prices`,`o`.`export_status` AS `export_status` from (((`orders` `o` join `customers` `c` on((`o`.`customer_id` = `c`.`id`))) join `order_details` `od` on((`o`.`id` = `od`.`order_id`))) join `products` `p` on((`od`.`product_id` = `p`.`id`))) group by `o`.`id`;

-- Dumping structure for view quanlybanhang.warehouse_transaction_details
-- Removing temporary table and create final VIEW structure
DROP TABLE IF EXISTS `warehouse_transaction_details`;
CREATE ALGORITHM=UNDEFINED SQL SECURITY DEFINER VIEW `warehouse_transaction_details` AS select `wt`.`id` AS `id`,`wt`.`product_id` AS `product_id`,`wt`.`quantity` AS `quantity`,`wt`.`unit_price` AS `unit_price`,`wt`.`type` AS `type`,`wt`.`note` AS `note`,`wt`.`created_at` AS `created_at`,`wt`.`updated_at` AS `updated_at`,`wt`.`transaction_code` AS `transaction_code`,`wt`.`created_by` AS `created_by`,`wt`.`source_transaction_code` AS `source_transaction_code`,`p`.`name` AS `product_name`,`p`.`price` AS `sell_price`,`c`.`name` AS `category_name`,`u`.`full_name` AS `created_by_name` from (((`warehouse_transactions` `wt` join `products` `p` on((`wt`.`product_id` = `p`.`id`))) join `categories` `c` on((`p`.`category_id` = `c`.`id`))) join `users` `u` on((`wt`.`created_by` = `u`.`id`)));

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
