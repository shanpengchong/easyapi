CREATE TABLE if not exists `host` (
  `id` bigint(20) NOT NULL,
  `create_time` bigint(20) DEFAULT NULL,
  `update_time` bigint(20) DEFAULT NULL,
  `deleted` int(1) DEFAULT NULL,
  `name` varchar(100) DEFAULT NULL,
  `host` varchar(255) DEFAULT NULL,
  `port` int(5) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;