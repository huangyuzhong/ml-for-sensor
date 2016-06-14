SET FOREIGN_KEY_CHECKS=0;

--用户信息
  drop table if exists users;
  create table `users`(
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NULL,
  `password` VARCHAR(500) NULL,
  `user_name` VARCHAR(255) NULL,
  `mobile` VARCHAR(255) NULL,
  `head_icon` VARCHAR(255) NULL,
  `create_date` DATETIME NULL,
  `gender` VARCHAR(255) NULL,
  `email` VARCHAR(255) NULL,
  PRIMARY KEY (`id`)
  );

--用户的角色信息
  drop table if exists roles;
  create table `roles`(
  `id` INT NOT NULL AUTO_INCREMENT,
  `user_name` VARCHAR(255) NULL,
  `role_name` VARCHAR(255) NULL,
  `role_auth_id` INT NULL,
  PRIMARY KEY (`id`)
  );

--角色权限
  drop table if exists role_authority;
  create table `role_authority`(
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NULL,
  PRIMARY KEY (`id`)
  );

--企业管理员绑定的公司信息
  drop table if exists company;
  create table `company`(
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NULL,
  `address` VARCHAR(255) NULL,
  `manager_user_id` INT NOT NULL,
  `business_user_id` INT NULL,
  `email` VARCHAR(255) NULL,
  `telephone` VARCHAR(255) NULL,
  `contract_no` VARCHAR(255) NULL,
  `sign_date` DATETIME NULL,
  `contract_end_date` DATETIME NULL,
  `background_url` VARCHAR(255) NULL,
  PRIMARY KEY (`id`)
  );

  drop table if exists buildings;
  create table `buildings`(
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NULL,
  `x_location` FLOAT NULL,
  `y_location` FLOAT NULL,
  `device_num` INT NULL DEFAULT 0,
  `create_date` DATETIME NULL,
  `company_id` INT NULL,
  PRIMARY KEY (`id`)
  );

  drop table if exists buildings_daily;
  create table `buildings_daily`(
  `id` INT NOT NULL AUTO_INCREMENT,
  `build_id` INT NULL,
  `start_time` DATETIME NULL,
  `end_time` DATETIME NULL,
  `alert_num` INT NULL DEFAULT 0,
  PRIMARY KEY (`id`)
  );

  drop table if exists floors;
  create table `floors` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `floor_num` INT NULL,
  `name` VARCHAR(255) NULL,
  `build_id` INT NOT NULL,
  `device_num` INT NULL DEFAULT 0,
  `create_date` DATETIME NULL,
  PRIMARY KEY (`id`)
  );

  drop table if exists floors_daily;
  create table `floors_daily`(
  `id` INT NOT NULL AUTO_INCREMENT,
  `floor_id` INT NULL,
  `start_time` DATETIME NULL,
  `end_time` DATETIME NULL,
  `alert_num` INT NULL DEFAULT 0,
  PRIMARY KEY (`id`)
  );

  drop table if exists room;
  create table `room`(
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NULL,
  `floor_id` INT NULL,
  `x_location` FLOAT NULL,
  `y_location` FLOAT NULL,
  `device_num` INT NULL DEFAULT 0,
  `create_date` DATETIME NULL,
  PRIMARY KEY (`id`)
  );

--房间内设备每天的报警数量信息
  drop table if exists room_daily;
  create table `room_daily`(
  `id` INT NOT NULL AUTO_INCREMENT,
  `room_id` INT NOT NULL,
  `start_time` DATETIME NULL,
  `end_time` DATETIME NULL,
  `alert_num` INT NULL DEFAULT 0,
  PRIMARY KEY (`id`)
  );

--监测的类型
  drop table if exists inspect_type;
  create table `inspect_type` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NULL,
  PRIMARY KEY (`id`)
  );

--设备的类型
  drop table if exists device_type;
  create table `device_type`(
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NULL,
  `logo_url` VARCHAR(255) NULL,
  PRIMARY KEY (`id`)
  );

--设备的检测项判定
  drop table if exists device_inspect;
  create table `device_inspect`(
  `id` INT NOT NULL,
  `device_type_id` INT NULL,
  `inspect_type_id` INT NULL,
  PRIMARY KEY (`id`)
  );

--监控终端
  drop table if exists monitor_device;
  create table `monitor_device`(
  `id` INT NOT NULL AUTO_INCREMENT,
  `number` VARCHAR(255) NULL,
  );

--设备信息
  drop table if exists device;
  create table `device`(
  `id` INT NOT NULL AUTO_INCREMENT,
  `number` VARCHAR(255) NULL,
  `name` VARCHAR(255) NULL,
  `device_type_id` INT NULL,
  `create_date` DATETIME NULL,
  `creator` VARCHAR(255) NULL,
  `purchase_date` DATETIME NULL,
  `photo` VARCHAR(255) NULL,
  `manager_user_id` INT NULL,
  

  );

  drop table if exists file;
  create table `file`(
  `id` INT NOT NULL AUTO_INCREMENT,
  `type` VARCHAR(255) NULL,
  `description` VARCHAR(500) NULL,
  `enable` INT NULL DEFAULT 0,
  `create_date` DATETIME NULL,
  PRIMARY KEY (`id`)
  );



SET FOREIGN_KEY_CHECKS=1;